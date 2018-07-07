package org.tdar.core.serialize.billing;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.validator.constraints.Length;
import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.bean.FieldLength;
import org.tdar.core.bean.TdarGroup;

import com.fasterxml.jackson.annotation.JsonIgnore;

/*
 * An activity represents a specific thing that can be "charged"
 * 
 *  THIS CLASS IS DESIGNED TO BE IMMUTABLE -- SET ONCE, NEVER CAN CHANGE
 */
public class PBillingActivity extends AbstractPersistable implements Comparable<PBillingActivity> {

    private static final long BYTES_IN_MB = 1_048_576L;

    public enum BillingActivityType {
        PRODUCTION,
        TEST;
    }

    private String name;
    private Integer numberOfHours = 0;
    private Long numberOfMb = 0L;
    private Long numberOfResources = 0L;
    private Long numberOfFiles = 0L;
    private BillingActivityType activityType = BillingActivityType.PRODUCTION;
    private Integer order;
    private PBillingActivityModel model;
    private Long minAllowedNumberOfFiles = 0L;
    private Long displayNumberOfMb;
    private Long displayNumberOfResources;
    private Long displayNumberOfFiles;
    private Float price;
    private String currency;
    private Boolean active = Boolean.FALSE;
    private TdarGroup group;

    public PBillingActivity() {
    }


    public Integer getNumberOfHours() {
        return numberOfHours;
    }

    public void setNumberOfHours(Integer numberOfHours) {
        this.numberOfHours = numberOfHours;
    }

    public Long getNumberOfMb() {
        return numberOfMb;
    }

    public Long getNumberOfBytes() {
        return getNumberOfMb() * BYTES_IN_MB;
    }

    public void setNumberOfMb(Long numberOfMb) {
        this.numberOfMb = numberOfMb;
    }

    public Long getNumberOfResources() {
        return numberOfResources;
    }

    public void setNumberOfResources(Long numberOfResources) {
        this.numberOfResources = numberOfResources;
    }

    public Long getNumberOfFiles() {
        return numberOfFiles;
    }

    public void setNumberOfFiles(Long numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean enabled) {
        this.active = enabled;
    }

    public TdarGroup getGroup() {
        return group;
    }

    public void setGroup(TdarGroup group) {
        this.group = group;
    }

    public Long getDisplayNumberOfFiles() {
        return displayNumberOfFiles;
    }

    public void setDisplayNumberOfFiles(Long displayNumberOfFiles) {
        this.displayNumberOfFiles = displayNumberOfFiles;
    }

    public Long getDisplayNumberOfResources() {
        return displayNumberOfResources;
    }

    public void setDisplayNumberOfResources(Long displayNumberOfResources) {
        this.displayNumberOfResources = displayNumberOfResources;
    }

    public Long getDisplayNumberOfMb() {
        return displayNumberOfMb;
    }

    public void setDisplayNumberOfMb(Long displayNumberOfMb) {
        this.displayNumberOfMb = displayNumberOfMb;
    }

    public Long getMinAllowedNumberOfFiles() {
        return minAllowedNumberOfFiles;
    }

    public void setMinAllowedNumberOfFiles(Long minAllowedNumberOfFiles) {
        this.minAllowedNumberOfFiles = minAllowedNumberOfFiles;
    }

    @Override
    public String toString() {
        return getName();
    }

    @JsonIgnore
    public PBillingActivityModel getModel() {
        return model;
    }

    public void setModel(PBillingActivityModel model) {
        this.model = model;
    }

    public boolean supportsFileLimit() {
        return (getNumberOfFiles() != null) && (getNumberOfFiles() > 0);
    }

    public boolean isProduction() {
        return getActivityType() == BillingActivityType.PRODUCTION;
    }

    public BillingActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(BillingActivityType activityType) {
        this.activityType = activityType;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    public int compareTo(PBillingActivity o) {
        if (!Objects.equals(getOrder(), o.getOrder())) {
            return ObjectUtils.compare(getOrder(), o.getOrder());
        } else {
            return ObjectUtils.compare(getName(), o.getName());
        }
    }

    private boolean isNullOrZero(Number number) {
        return (number == null) || (number.floatValue() == 0.0);
    }

    public boolean isSpaceOnly() {
        return (isNullOrZero(getNumberOfHours()) && isNullOrZero(getNumberOfResources())
                && (getNumberOfBytes() != null) && (getNumberOfBytes() > 0)
                && isNullOrZero(getNumberOfFiles()));

    }

    public boolean isFilesOnly() {
        return (isNullOrZero(getNumberOfHours()) && isNullOrZero(getNumberOfResources())
                && (getNumberOfFiles() != null) && (getNumberOfFiles() > 0)
                && isNullOrZero(getNumberOfBytes()));
    }
}
