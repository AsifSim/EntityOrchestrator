package com.entity.orchestrator.Validations;

import com.entity.orchestrator.Model.Entities;
import com.entity.orchestrator.Model.Entity;
import com.entity.orchestrator.Model.ValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostStartupValidations {
    @Autowired
    Entities entities;
    private static final Logger logger = LoggerFactory.getLogger(StartupValidations.class);

    public ValidationResponse postStartupValidations(String entity, List<Map<String, Object>> valuesList){
        logger.info("=============Inside Class {}, Function {}", this.getClass().getSimpleName(), (new Object() {}.getClass().getEnclosingMethod().getName()));
        logger.info("Parameter(entity = {}, valuesList = {})", entity,valuesList.toString());
        ValidationResponse validationResponse=nonNullValidation(entities.getEntityMap().get(entity),valuesList);
        logger.info("Non null validation status: {}",validationResponse.isValidationResult());
        logger.info("error: {}",validationResponse.getError());
//        validationResponse=uniqueness(entity);//primary key should get auto incremented
//        logger.info("Unique constraint status: "+validationResponse.isValidationResult()+"\nerror: "+validationResponse.getError());
        logger.info("=============Going out of {}", (new Object() {}.getClass().getEnclosingMethod().getName()));
        return validationResponse;
    }

//    public Integer postStartupValidations(String entity, Map<String, Object> valuesList){
//        logger.info("=============Inside Class {}, Function {}", this.getClass().getSimpleName(), (new Object() {}.getClass().getEnclosingMethod().getName()));
//        logger.info("Parameter(entity = {}, valuesList = {})", entity,valuesList.toString());
//        ValidationResponse validationResponse=nonNullValidation(entities.getEntityMap().get(entity),valuesList);
//        logger.info("Non null validation status: {}",validationResponse.isValidationResult());
//        logger.info("error: {}",validationResponse.getError());
////        validationResponse=uniqueness(entity);
//        logger.info("Unique constraint status: "+validationResponse.isValidationResult()+"\nerror: "+validationResponse.getError());
//        logger.info("=============Going out of {}", (new Object() {}.getClass().getEnclosingMethod().getName()));
//    }

    public ValidationResponse nonNullValidation(Entity entity, List<Map<String, Object>> valuesList){
        logger.info("=============Inside Class {}, Function {}", this.getClass().getSimpleName(), (new Object() {}.getClass().getEnclosingMethod().getName()));
        logger.info("Parameter(entityName = {}, Attributes = {}, PrimaryKey = {}, RelatedEntities = {})", entity.getEntity(),entity.getAttributes().toString(),entity.getPrimary(),entity.getRelationshipEntity());
        List<String> notNull= new ArrayList<>();
        for(Map<String,Object> attribute: entity.getAttributes()){
            if(!(boolean)attribute.get("allowNull")) notNull.add(attribute.get("name").toString());
        }
        for(Map<String,Object> values:valuesList){
            for(String attribute: notNull)if(values.get(attribute)==null)return new ValidationResponse(false,"The non null attribute: "+attribute+" is null");
        }
        logger.info("=============Going out of {}", (new Object() {}.getClass().getEnclosingMethod().getName()));
        return new ValidationResponse(true,null);
    }

//    public ValidationResponse nonNullValidation(Entity entity, Map<String, Object> valuesList){
//        logger.info("=============Inside Class {}, Function {}", this.getClass().getSimpleName(), (new Object() {}.getClass().getEnclosingMethod().getName()));
//        logger.info("Parameter(entityName = {}, Attributes = {}, PrimaryKey = {}, RelatedEntities = {})", entity.getEntity(),entity.getAttributes().toString(),entity.getPrimary(),entity.getRelationshipEntity());
//        List<String> notNull= new ArrayList<>();
//        for(Map<String,Object> attribute: entity.getAttributes()){
//            if(!(boolean)attribute.get("allowNull")) notNull.add(attribute.get("name").toString());
//        }
//        for(Map<String,Object> values:valuesList){
//            for(String attribute: notNull)if(values.get(attribute)==null)return new ValidationResponse(false,"The non null attribute: "+attribute+" is null");
//        }
//        logger.info("=============Going out of {}", (new Object() {}.getClass().getEnclosingMethod().getName()));
//        return new ValidationResponse(true,null);
//    }

    public ValidationResponse uniqueness(Entity entity){
        logger.info("=============Inside Class {}, Function {}", this.getClass().getSimpleName(), (new Object() {}.getClass().getEnclosingMethod().getName()));
        logger.info("Parameter(entityName = {}, Attributes = {}, PrimaryKey = {}, RelatedEntities = {})", entity.getEntity(),entity.getAttributes().toString(),entity.getPrimary(),entity.getRelationshipEntity());
        long startTime = System.currentTimeMillis();
        logger.info("Going to execute the fetch query from entity: {} at: {}ms",entity.getEntity(),startTime);
        List<Map<String,Object>> fetchResult=entity.fetchColumns(new ArrayList<>(List.of(entity.getPrimary())));
        long endTime = System.currentTimeMillis();
        logger.info("The fetch query from entity: {} has been executed at: {}ms",entity.getEntity(),endTime);
        logger.info("Total time taken = {}ms",endTime-startTime);
        logger.info("fetchedResult = {}",fetchResult.toString());
        HashMap<String,Integer> unique=new HashMap<>();
        for(Map<String,Object> record: fetchResult){
            unique.put(record.get(entity.getPrimary()).toString(),unique.getOrDefault(record.get(entity.getPrimary()),0)+1);
            if(unique.get(record.get(entity.getPrimary()).toString())>1){
                logger.info("=============Going out of {}", (new Object() {}.getClass().getEnclosingMethod().getName()));
                return new ValidationResponse(false,"The unique column "+entity.getPrimary()+" contains duplicate value "+record.get(entity.getPrimary()));
            }
        }
        logger.info("=============Going out of {}", (new Object() {}.getClass().getEnclosingMethod().getName()));
        return new ValidationResponse(true,null);
    }
}
