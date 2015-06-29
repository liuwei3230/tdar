package org.tdar.core.dao.project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tdar.core.bean.collection.ResourceCollection;
import org.tdar.core.bean.projects.ResourceStatusWrapper;
import org.tdar.core.bean.projects.TaskStatus;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect
public class TaskStatusResult implements Serializable {

    private static final long serialVersionUID = -5165934159755075939L;

    private List<ResourceStatusWrapper> results = new ArrayList<>();
    
    private ResourceCollection collection;
    
    private Map<TaskStatus, Integer> aggregate = new HashMap<>();

    public List<ResourceStatusWrapper> getResults() {
        return results;
    }

    public void setResults(List<ResourceStatusWrapper> results) {
        this.results = results;
    }

    public ResourceCollection getCollection() {
        return collection;
    }

    public void setCollection(ResourceCollection collection) {
        this.collection = collection;
    }

    public Map<TaskStatus, Integer> getAggregate() {
        return aggregate;
    }

    public void setAggregate(Map<TaskStatus, Integer> aggregate) {
        this.aggregate = aggregate;
    }
    
}
