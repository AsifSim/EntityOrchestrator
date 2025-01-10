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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Entity> entityList =new ArrayList<>();
        File folder = new File(directoryPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        logger.info("files = {}",files!=null?files.toString():null);
        if (files != null) {
            logger.info("files is not null");
            for (File file : files) {
                try {
                    logger.info("For file: {}",file.getName());
                    Entity newEntity = objectMapper.readValue(file, Entity.class);
                    newEntity.setServ(serv);
                    newEntity.setPostStartupValidations(postStartupValidations);
                    entityList.add(newEntity);
                } catch (Exception e) {
                    System.err.println("Error reading file " + file.getName() + ": " + e.getMessage());
                }
            }
        }
        Map<String,Entity> entityMap=new HashMap<>();
        for(Entity e: entityList)entityMap.put(e.getEntity(),e);
        logger.info("EntityMap = {}",entityMap.toString());
        for(Entity e: entityList) {
            if(e.getRelationshipEntity()!=null){
                logger.info("RelationShipEntity is not null");
                List<Entity> relationList=new ArrayList<>();
                for(Entity x: e.getRelationshipEntity()) relationList.add(entityMap.get(x.getEntity()));
                e.setRelationshipEntity(relationList);
            }
        }
        logger.info("=======================================================================");
        for(Entity e: entityList){
            logger.info("The entity created = (entity = "+e.getEntity()+", primaryKey = "+e.getPrimary()+", relatedTo = "+e.getRelationshipEntity()+", Attributes = "+e.getAttributes().toString());
            logger.info("---------------------------The related Entities------------------------");
            if(e.getRelationshipEntity()!=null){
                for(Entity x: e.getRelationshipEntity()){
                    if(x!=null)logger.info("entity = "+x.getEntity()+", PrimaryKey = "+x.getPrimary()+", Attributes = "+x.getAttributes());
                }
            }
            logger.info("=======================================================================");
        }
        logger.info("========="+entityMap.get("person").getEntity()+"=============="+entityMap.get("vehicle").getEntity());
        entities.setEntityMap(entityMap);

        System.out.println(entities.getEntityMap().get("person").fetchAll());
        System.out.println("count = "+entities.getEntityMap().get("person").count(new ArrayList<>(List.of(new Condition("uname","Deepak","=","AND")))));
        for(Entity entity: entityList){
            Integer status=startupValidate.startupValidations(entity);
            if(status==0){
                logger.info("\n"+
                        "                                                      __        _   _   _   __   __ \n" +
                        " \\  /   _.  |  o   _|   _.  _|_  o   _   ._    _    (_   | |  /   /   |_  (_   (_  \n" +
                        "  \\/   (_|  |  |  (_|  (_|   |_  |  (_)  | |  _>    __)  |_|  \\_  \\_  |_  __)  __) \n" +
                        "                                                                                  ");
                entities.setEntityMap(entityMap);
            }
            else logger.info("\n"+
                    "                                                      _         ___       _   _  \n" +
                    " \\  /   _.  |  o   _|   _.  _|_  o   _   ._    _    |_   /\\    |   |   |_  | \\ \n" +
                    "  \\/   (_|  |  |  (_|  (_|   |_  |  (_)  | |  _>    |   /--\\  _|_  |_  |_  |_/ \n" +
                    "                                                                              ");
        }

        //=============The code below is only for testing Insert operation====================
        List<Map<String,Object>> dataToInsert=new ArrayList<>();
        Map<String,Object> hm = new HashMap<>();
        hm.put("uname","Nikhitha");
        hm.put("age","2");
        hm.put("contact","8768767");
        hm.put("vehicle","500035");
        dataToInsert.add(hm);
        System.out.println("============Insert========="+entityMap.get("person").insert(dataToInsert));
    }
}
