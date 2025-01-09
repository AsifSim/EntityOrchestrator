package com.entity.orchestrator.Model;

import com.entity.orchestrator.Validations.PostStartupValidations;
import flink.generic.db.Model.Condition;
import flink.generic.db.Service.GenericQueryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
        return serv.read(entity,new ArrayList<>(),new ArrayList<>(),"spriced_meritor");
    }

    public List<Map<String,Object>> fetchColumns(List<String> columns){
        return serv.read(entity,columns,new ArrayList<>(),"spriced_meritor");
    }

    public List<Map<String,Object>> fetchColumnsWithLimit(List<String> columns,Integer limit){
        return serv.read(entity,columns,new ArrayList<>(),"spriced_meritor",limit);
    }

    public List<Map<String,Object>> filterColumns(List<Condition> conditions){
        return serv.read(entity,new ArrayList<>(),new ArrayList<>(),"spriced_meritor");
    }

    public List<Map<String,Object>> filterColumns(List<String> columns,List<Condition> conditions){
        return serv.read(entity,columns,new ArrayList<>(),"spriced_meritor");
    }

    public List<Map<String,Object>> filterColumnsWithLimit(List<String> columns,List<Condition> conditions, Integer limit){
        return serv.read(entity,columns,new ArrayList<>(),"spriced_meritor",limit);
    }

    public List<Map<String,Object>> filterColumnsWithLimit(List<Condition> conditions, Integer limit){
        return serv.read(entity,new ArrayList<>(),new ArrayList<>(),"spriced_meritor",limit);
    }

    public List<Map<String,Object>> fetch(List<String> columns, List<Condition> conditions, Integer... limit){
        return serv.read(entity,new ArrayList<>(),new ArrayList<>(),"spriced_meritor",limit);
    }

    public List<Map<String,Object>> fetch(List<String> columns, List<Condition> conditions, String orderByColumn, Boolean isDesc, Integer... limit){
        return serv.read(entity,new ArrayList<>(),new ArrayList<>(),"spriced_meritor", orderByColumn, isDesc, limit);
    }

    public Integer insert(List<Map<String, Object>> valuesList){
        ValidationResponse status=postStartupValidations.postStartupValidations(entity,valuesList);
        if(status.getError()!=null)return -1;
        return serv.insert(entity,valuesList,"spriced_meritor");
    }

    public Integer update(Map<String, Object> values, List<Condition> conditions){
//        postStartupValidations.postStartupValidations(entity,values);
        return serv.update(entity,values, conditions, "spriced_meritor");
    }

    public Integer upsert(List<Map<String, Object>> valuesList, String uniqueKeyColumn){
        postStartupValidations.postStartupValidations(entity,valuesList);
        return serv.upsert(entity,valuesList, uniqueKeyColumn, "spriced_meritor");
    }
}
