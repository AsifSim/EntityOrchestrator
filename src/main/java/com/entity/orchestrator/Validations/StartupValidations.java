package com.entity.orchestrator.Validations;

import com.entity.orchestrator.EntityOrchestrator.EntityLoader;
import com.entity.orchestrator.Model.Entity;
import com.entity.orchestrator.Model.ValidationResponse;
import flink.generic.db.Model.Condition;
import flink.generic.db.Service.GenericQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StartupValidations {
    @Autowired
    GenericQueryService serv;
    private static final Logger logger = LoggerFactory.getLogger(StartupValidations.class);

    public Integer startupValidations(Entity entity){
        logger.info("=============Inside Class {}, Function {}", this.getClass().getSimpleName(), (new Object() {}.getClass().getEnclosingMethod().getName()));
        logger.info("Parameter(entityName = {}, Attributes = {}, PrimaryKey = {}, RelatedEntities = {})", entity.getEntity(),entity.getAttributes().toString(),entity.getPrimary(),entity.getRelationshipEntity());
        ValidationResponse validationResponse=nonNullValidation(entity);
        if(validationResponse.getError()!=null)
        {
            logger.info("error: {}",validationResponse.getError());
            return -1;
        }
        logger.info("Non null validation status: {}",validationResponse.isValidationResult());
        validationResponse=uniqueness(entity);
        if(validationResponse.getError()!=null)
        {
            logger.info("error: {}",validationResponse.getError());
            return -1;
        }
        logger.info("Unique constraint status: "+validationResponse.isValidationResult()+"\nerror: "+validationResponse.getError());
        logger.info("=============Going out of {}", (new Object() {}.getClass().getEnclosingMethod().getName()));
        return 0;
    }

    public ValidationResponse nonNullValidation(Entity entity){
        logger.info("=============Inside Class {}, Function {}", this.getClass().getSimpleName(), (new Object() {}.getClass().getEnclosingMethod().getName()));
        logger.info("Parameter(entityName = {}, Attributes = {}, PrimaryKey = {}, RelatedEntities = {})", entity.getEntity(),entity.getAttributes().toString(),entity.getPrimary(),entity.getRelationshipEntity());
        List<String> notNull= new ArrayList<>();
        for(Map<String,Object> attribute: entity.getAttributes()){
            if(!(boolean)attribute.get("allowNull")) notNull.add(attribute.get("name").toString());
        }
        long startTime = System.currentTimeMillis();
        logger.info("Going to execute the fetch query from entity: {} at: {}ms",entity.getEntity(),startTime);
        List<Map<String,Object>> fetchResult=entity.fetchColumns(notNull);
        long endTime = System.currentTimeMillis();
        logger.info("The fetch query from entity: {} has been executed at: {}ms",entity.getEntity(),endTime);
        logger.info("Total time taken = {}ms",endTime-startTime);
//        logger.info("fetchedResult = {}",fetchResult.toString());
        for(Map<String,Object> record: fetchResult){
            for(String column: notNull){
                if(record.get(column)==null){
                    logger.info("=============Going out of {}", (new Object() {}.getClass().getEnclosingMethod().getName()));
                    return new ValidationResponse(false,"The non null column "+column+" is null for primaryKey"+record.get(entity.getPrimary()));
                }
            }
        }
        logger.info("=============Going out of {}", (new Object() {}.getClass().getEnclosingMethod().getName()));
        return new ValidationResponse(true,null);
    }

    public ValidationResponse uniqueness(Entity entity){
        logger.info("=============Inside Class {}, Function {}", this.getClass().getSimpleName(), (new Object() {}.getClass().getEnclosingMethod().getName()));
        logger.info("Parameter(entityName = {}, Attributes = {}, PrimaryKey = {}, RelatedEntities = {})", entity.getEntity(),entity.getAttributes().toString(),entity.getPrimary(),entity.getRelationshipEntity());
        long startTime = System.currentTimeMillis();
        logger.info("Going to execute the fetch query from entity: {} at: {}ms",entity.getEntity(),startTime);
        List<Map<String,Object>> fetchResult=entity.fetchColumns(new ArrayList<>(List.of(entity.getPrimary())));
        long endTime = System.currentTimeMillis();
        logger.info("The fetch query from entity: {} has been executed at: {}ms",entity.getEntity(),endTime);
        logger.info("Total time taken = {}ms",endTime-startTime);
//        logger.info("fetchedResult = {}",fetchResult.toString());
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
