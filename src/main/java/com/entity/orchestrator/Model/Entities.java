package com.entity.orchestrator.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Service
public class Entities {
    Map<String,Entity> entityMap=new HashMap<>();

    public Map<String, Entity> getEntityMap() {
        return entityMap;
    }

    public void setEntityMap(Map<String, Entity> entityMap) {
        this.entityMap = entityMap;
    }
}
