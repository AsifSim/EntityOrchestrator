package com.entity.orchestrator.Model;

import com.entity.orchestrator.EntityOrchestrator.EntityLoader;
import com.entity.orchestrator.Validations.PostStartupValidations;
import flink.generic.db.DTO.Request.*;
import flink.generic.db.Model.Condition;
import flink.generic.db.Service.GenericQueryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Service
public class Entity {
    @Autowired
    GenericQueryService serv;

    @Autowired
    PostStartupValidations postStartupValidations;

    String entity;
    String primary;
    List<Entity> relationshipEntity;
    List<Map<String,Object>> attributes;

    private static final Logger logger = LoggerFactory.getLogger(Entity.class);

    public List<Entity> getRelationshipEntity() {
        return relationshipEntity;
    }

    public void setRelationshipEntity(List<Entity> relationshipEntity) {
        this.relationshipEntity = relationshipEntity;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public List<Map<String, Object>> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Map<String, Object>> attributes) {
        this.attributes = attributes;
    }

    public GenericQueryService getServ() {
        return serv;
    }

    public void setServ(GenericQueryService serv) {
        this.serv = serv;
    }

    public PostStartupValidations getPostStartupValidations() {
        return postStartupValidations;
    }

    public void setPostStartupValidations(PostStartupValidations postStartupValidations) {
        this.postStartupValidations = postStartupValidations;
    }

    public List<Map<String,Object>> fetchAll(){
        return serv.read(new ReadRequest(entity,new ArrayList<>(),new ArrayList<>(),null,"spriced_meritor",null,true));//entity,new ArrayList<>(),new ArrayList<>(),"spriced_meritor");
    }

    public List<Map<String,Object>> fetchColumns(List<String> columns){
        return serv.read(new ReadRequest(entity,columns,new ArrayList<>(),null,"spriced_meritor",null,true));
    }

    public List<Map<String,Object>> fetchColumnsWithLimit(List<String> columns,Integer limit){
        return serv.read(new ReadRequest(entity,columns,new ArrayList<>(),limit,"spriced_meritor",null,true));
    }

    public List<Map<String,Object>> filterColumns(List<Condition> conditions){
        return serv.read(new ReadRequest(entity,new ArrayList<>(),conditions,null,"spriced_meritor",null,true));
    }

    public List<Map<String,Object>> filterColumns(List<String> columns,List<Condition> conditions){
        return serv.read(new ReadRequest(entity,columns,new ArrayList<>(),null,"spriced_meritor",null,true));
    }

    public List<Map<String,Object>> filterColumnsWithLimit(List<String> columns,List<Condition> conditions, Integer limit){
        return serv.read(new ReadRequest(entity,columns,new ArrayList<>(),limit,"spriced_meritor",null,true));
    }

    public List<Map<String,Object>> filterColumnsWithLimit(List<Condition> conditions, Integer limit){
        return serv.read(new ReadRequest(entity,new ArrayList<>(),new ArrayList<>(),limit,"spriced_meritor",null,true));
    }

    public List<Map<String,Object>> fetch(List<String> columns, List<Condition> conditions, Integer... limit){
        if(limit==null) return serv.read(new ReadRequest(entity,columns,conditions,null,"spriced_meritor",null,true));
        return serv.read(new ReadRequest(entity,columns,conditions,limit[0],"spriced_meritor",null,true));
    }

    public List<Map<String,Object>> fetch(List<String> columns, List<Condition> conditions, String orderByColumn, Boolean isDesc, Integer... limit){
        if(limit==null) return serv.read(new ReadRequest(entity,columns,conditions,null,"spriced_meritor",orderByColumn,isDesc));
        return serv.read(new ReadRequest(entity,columns,conditions,limit[0],"spriced_meritor",orderByColumn,isDesc));
    }

    public Integer insert(List<Map<String, Object>> valuesList){
        ValidationResponse status=postStartupValidations.postStartupValidations(entity,valuesList);
        if(status.getError()!=null)return -1;
        for(Map<String,Object> value: valuesList){
            value.put(getPrimary(),count(new ArrayList<>())+1);
        }
        long startTime = System.currentTimeMillis();
        logger.info("Going to execute insert query for "+entity+" at "+startTime+"ms");
        Integer records=serv.insert(new InsertRequest(entity,valuesList,"spriced_meritor"));
        long endtime = System.currentTimeMillis();
        logger.info("The insert query for "+entity+" has been executed at "+endtime+"ms");
        logger.info("Total time taken = {}ms",endtime-startTime);
        return records;
    }

    public Integer update(Map<String, Object> values, List<Condition> conditions){
//        postStartupValidations.postStartupValidations(entity,values);
        return serv.update(new UpdateRequest(entity,values, conditions, "spriced_meritor"));
    }

    public Integer upsert(List<Map<String, Object>> valuesList, String uniqueKeyColumn){
        postStartupValidations.postStartupValidations(entity,valuesList);
        return serv.upsert(new UpsertRequest(entity,valuesList, uniqueKeyColumn, "spriced_meritor"));
    }

    public Integer count(List<Condition> conditions){
        return serv.count(new CountRequest(entity,conditions,"spriced_meritor"));
    }
}
