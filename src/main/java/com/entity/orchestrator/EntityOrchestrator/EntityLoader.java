package com.entity.orchestrator.EntityOrchestrator;

import com.entity.orchestrator.Model.Entities;
import com.entity.orchestrator.Model.Entity;
import com.entity.orchestrator.Validations.PostStartupValidations;
import com.entity.orchestrator.Validations.StartupValidations;
import com.fasterxml.jackson.databind.ObjectMapper;
import flink.generic.db.Application;
import flink.generic.db.Model.Condition;
import flink.generic.db.Service.GenericQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EntityLoader{
    @Autowired
    GenericQueryService serv;
    @Autowired
    StartupValidations startupValidate;

    @Autowired
    PostStartupValidations postStartupValidations;

    @Value("${RESOURCE_DIRECTORY_PATH}")
    String directoryPath;

    @Autowired
    Entities entities;

    private static final Logger logger = LoggerFactory.getLogger(EntityLoader.class);

    public void entityLoader() {
        logger.info("=============Inside Class {}, Function {}", this.getClass().getSimpleName(), (new Object() {}.getClass().getEnclosingMethod().getName()));
        logger.info("Parameter(directoryPath = {})", directoryPath);
        ObjectMapper objectMapper = new ObjectMapper();
        logger.info("objectMapper has been instatntiated");
        List<Entity> entityList =new ArrayList<>();
        File folder = new File(directoryPath);
        logger.info("folder = {}",folder.listFiles().toString());
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        logger.info("files = {}",files!=null?files.toString():null);
        if (files != null) {
            logger.info("variable files is not null");
            entityList.addAll(Arrays.stream(files).map(file -> {
                        try {
                            logger.info("For file: {}", file.getName());
                            Entity newEntity = objectMapper.readValue(file, Entity.class);
                            logger.info("Entity {} has been instantiated",newEntity.getEntity());
                            newEntity.setServ(serv);
                            logger.info("Have added the GenericQuery for the entity created");
                            newEntity.setPostStartupValidations(postStartupValidations);
                            logger.info("Have added the postStartupValidations for the entity created");
                            return newEntity;
                        } catch (Exception e) {
                            logger.error("Error reading file {}: {}", file.getName(), e.getMessage());
                            return null;
                        }}).filter(Objects::nonNull).collect(Collectors.toList()));
        }
        Map<String, Entity> entityMap = entityList.stream().collect(Collectors.toMap(Entity::getEntity, e -> e));
        logger.info("EntityMap = {}",entityMap.toString());
        entityList.stream().filter(e -> e.getRelationshipEntity() != null)
                .forEach(e -> {
                    logger.info("RelationShipEntity is not null");
                    List<Entity> relationList = e.getRelationshipEntity().stream().map(x -> entityMap.get(x.getEntity())).collect(Collectors.toList());
                    logger.info("relationList = {}",relationList.toString());
                    e.setRelationshipEntity(relationList);
                    logger.info("Relationship has been added");
                });
        logger.info("========="+entityMap.get("person").getEntity()+"=============="+entityMap.get("vehicle").getEntity());
        logger.info("=========Starting validations");
        startupValidate(entityList);
        //=============The code below is only for testing Insert operation====================
        List<Map<String,Object>> dataToInsert=new ArrayList<>();
        Map<String,Object> hm = new HashMap<>();
        hm.put("uname","Nikhitha");
        hm.put("age","2");
        hm.put("contact","8768767");
        hm.put("vehicle","500035");
        dataToInsert.add(hm);
        System.out.println("============Insert========="+entityMap.get("person").insert(dataToInsert));
        logger.info("=============Going out of {}", (new Object() {}.getClass().getEnclosingMethod().getName()));
    }

    public void startupValidate(List<Entity> entityList){
        logger.info("=============Inside Class {}, Function {}", this.getClass().getSimpleName(), (new Object() {}.getClass().getEnclosingMethod().getName()));
        logger.info("Parameter(entityList = {})", entityList.toString());
        entityList.stream().forEach(entity -> {
            Integer status = startupValidate.startupValidations(entity);
            logger.info("status = {}",status);
            if (status == 0) {
                logger.info("\n" +
                        "                                                      __        _   _   _   __   __ \n" +
                        " \\  /   _.  |  o   _|   _.  _|_  o   _   ._    _    (_   | |  /   /   |_  (_   (_  \n" +
                        "  \\/   (_|  |  |  (_|  (_|   |_  |  (_)  | |  _>    __)  |_|  \\_  \\_  |_  __)  __) \n" +
                        "                                                                                  ");
                logger.info("Adding entity to the entities class");
                entities.getEntityMap().put(entity.getEntity(), entity);
                logger.info("Entity has been added");
            } else {
                logger.info("\n" +
                        "                                                      _         ___       _   _  \n" +
                        " \\  /   _.  |  o   _|   _.  _|_  o   _   ._    _    |_   /\\    |   |   |_  | \\ \n" +
                        "  \\/   (_|  |  |  (_|  (_|   |_  |  (_)  | |  _>    |   /--\\  _|_  |_  |_  |_/ \n" +
                        "                                                                              ");
            }
        });
        logger.info("=============Going out of {}", (new Object() {}.getClass().getEnclosingMethod().getName()));
    }
}
