package com.entity.orchestrator.EntityOrchestrator;

import com.entity.orchestrator.Model.Entities;
import com.entity.orchestrator.Model.Entity;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.spriced.workflow.DataReading;
import flink.generic.db.Service.GenericQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EntityLoader{
    @Autowired
    GenericQueryService serv;
    public Entities entityLoader(String directoryPath) {
        ObjectMapper objectMapper = new ObjectMapper();
        Entities entities = new Entities();
        List<Entity> entityList =new ArrayList<>();
        File folder = new File(directoryPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

        if (files != null) {
            for (File file : files) {
                try {
                    System.out.println("For file "+file.getName());
                    Entity entity = objectMapper.readValue(file, Entity.class);
                    entityList.add(entity);
                } catch (Exception e) {
                    System.err.println("Error reading file " + file.getName() + ": " + e.getMessage());
                }
            }
        }

        Map<String,Entity> entityMap=new HashMap<>();
        for(Entity e: entityList)entityMap.put(e.getEntity(),e);
        for(Entity e: entityList){
            if(e.getRelationshipEntity()!=null){
                e.setRelationshipEntity(entityMap.get(e.getRelationshipEntity().getEntity()));
            }
        }

        for(Entity e: entityList){
            System.out.println("=============The entities created are(entity = "+e.getEntity()+", primaryKey = "+e.getPrimary()+", relatedTo = "+e.getRelationshipEntity()+", Attributes = "+e.getAttributes().toString());
            if(e.getRelationshipEntity()!=null)System.out.println("The related Entity(entity = "+e.getRelationshipEntity().getEntity()+", PrimaryKey = "+e.getRelationshipEntity().getPrimary()+", Attributes = "+e.getRelationshipEntity().getAttributes());
        }
        serv.read(entityList.get(0).getEntity(),new ArrayList<>(),new ArrayList<>(),"spriced_meritor");
//        System.out.println("===========Read operation from entity = "+entityMap.get("person").fetchAll().toString());
        return entities;
    }
}
