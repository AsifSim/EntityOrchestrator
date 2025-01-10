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

import java.util.*;
import java.util.stream.Collectors;

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
        List<String> notNull = entity.getAttributes().stream()
                .filter(attribute -> !(boolean) attribute.get("allowNull"))
                .map(attribute -> attribute.get("name").toString()).collect(Collectors.toList());
        long startTime = System.currentTimeMillis();
        logger.info("Going to execute the fetch query from entity: {} at: {}ms",entity.getEntity(),startTime);
        List<Map<String,Object>> fetchResult=entity.fetchColumns(notNull);
        long endTime = System.currentTimeMillis();
        logger.info("The fetch query from entity: {} has been executed at: {}ms",entity.getEntity(),endTime);
        logger.info("Total time taken = {}ms",endTime-startTime);
        Optional<ValidationResponse> validationResponse = fetchResult.stream().flatMap(record -> notNull.stream()
                        .filter(column -> record.get(column) == null)
                        .map(column -> {
                            logger.info("=============Going out of {}", (new Object() {}.getClass().getEnclosingMethod().getName()));
                            return new ValidationResponse(false, "The non-null column " + column + " is null for primaryKey " + record.get(entity.getPrimary()));
                        })
                ).findFirst();
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
        Optional<ValidationResponse> validationResponse = fetchResult.stream().map(record -> {
                    String primaryValue = record.get(entity.getPrimary()).toString();
                    unique.put(primaryValue, unique.getOrDefault(primaryValue, 0) + 1);
                    if (unique.get(primaryValue) > 1) {
                        logger.info("=============Going out of {}", (new Object() {}.getClass().getEnclosingMethod().getName()));
                        return new ValidationResponse(false, "The unique column " + entity.getPrimary() + " contains duplicate value " + primaryValue);
                    }return null;
                }).filter(Objects::nonNull).findFirst();
        if (validationResponse.isPresent()) return validationResponse.get();
        logger.info("=============Going out of {}", (new Object() {}.getClass().getEnclosingMethod().getName()));
        return new ValidationResponse(true,null);
    }
}
