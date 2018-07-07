package org.tdar.core.serialize.billing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.Length;
import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.bean.FieldLength;

public class PBillingActivityModel extends AbstractPersistable {

    /*
     * A 1:1 representation of a billing model and a set of activites. As a billing model changes, a new version should be published with new activities. At
     * that point, a new invoice may need to be generated which makes the billing model "whole" for previous customers.
     * 
     * e.g.
     * Model 1: assumes at Resources are "Free" (2010)
     * Model 2: charges per Resource. (2014)
     * 
     * In order to implement Model 2, you will need to issue an invoice for every user which "credits" them for all of the previous resources they've used in
     * the time between 2010 and 2014.
     */

    private Integer version;
    private Boolean countingSpace = true;
    private Boolean countingFiles = true;
    private Boolean countingResources = true;
    private Boolean active = false;
    private String description;
    private Date dateCreated;
    private List<PBillingActivity> activities = new ArrayList<PBillingActivity>();

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getCountingSpace() {
        return countingSpace;
    }

    public void setCountingSpace(Boolean countingSpace) {
        this.countingSpace = countingSpace;
    }

    public Boolean getCountingFiles() {
        return countingFiles;
    }

    public void setCountingFiles(Boolean countingFiles) {
        this.countingFiles = countingFiles;
    }

    public Boolean getCountingResources() {
        return countingResources;
    }

    public void setCountingResources(Boolean countingResources) {
        this.countingResources = countingResources;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public List<PBillingActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<PBillingActivity> activities) {
        this.activities = activities;
    }

    @Override
    public String toString() {
        return String.format("%s r:%s f:%s s:%s", getVersion(), countingFiles, countingResources, countingSpace);
    }

}
