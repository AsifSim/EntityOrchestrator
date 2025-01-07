package com.entity.orchestrator.Model;

//import com.spriced.workflow.DataReading;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Entity {
//    @Autowired
//    DataReading serv;
    String entity;
    String primary;
    Entity relationshipEntity;
    List<Map<String,Object>> attributes;

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

    public Entity getRelationshipEntity() {
        return relationshipEntity;
    }

    public void setRelationshipEntity(Entity relationshipEntity) {
        this.relationshipEntity = relationshipEntity;
    }

    public List<Map<String, Object>> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Map<String, Object>> attributes) {
        this.attributes = attributes;
    }

//    public List<Map<String,Object>> fetchAll(){
//        DataReading dataRead=new DataReading();
//        return serv.read(this.entity,new ArrayList<>(),new ArrayList<>(),"spriced_meritor");
//    }
}
