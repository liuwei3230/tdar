package org.tdar.core.serialize.coverage;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.Range;
import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.bean.HasResource;
import org.tdar.core.bean.Validatable;
import org.tdar.core.bean.coverage.CoverageType;
import org.tdar.core.bean.resource.Resource;
import org.tdar.utils.json.JsonLookupFilter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

public class PCoverageDate extends AbstractPersistable implements HasResource<Resource>, Validatable {

    private Integer startDate;
    private Integer endDate;
    private CoverageType dateType;
    private boolean startDateApproximate;
    private boolean endDateApproximate;
    @JsonView(JsonLookupFilter.class)
    private String description;

    public PCoverageDate() {
    }

    public PCoverageDate(CoverageType type) {
        setDateType(type);
    }

    public PCoverageDate(CoverageType type, Integer start, Integer end) {
        setDateType(type);
        setStartDate(start);
        setEndDate(end);
    }

    public Integer getStartDate() {
        return startDate;
    }

    public void setStartDate(Integer startDate) {
        this.startDate = startDate;
    }

    public Integer getEndDate() {
        return endDate;
    }

    public void setEndDate(Integer endDate) {
        this.endDate = endDate;
    }

    public void copyDatesFrom(PCoverageDate coverageDate) {
        setStartDate(coverageDate.getStartDate());
        setEndDate(coverageDate.getEndDate());
    }

    @Override
    public boolean isValid() {
        return validate(startDate, endDate);
    }

    @Override
    public boolean isValidForController() {
        if ((dateType == null) || (startDate == null) || (endDate == null)) {
            return false;
        } else {
            return validate(startDate, endDate);
        }
    }

    protected boolean validate(Integer start, Integer end) {
        return getDateType().validate(start, end);
    }

    public void setDateType(CoverageType dateType) {
        this.dateType = dateType;
    }

    @JsonIgnore
    @XmlTransient
    public void setDateType(String dateType) {
        this.dateType = CoverageType.valueOf(dateType);
    }

    public CoverageType getDateType() {
        return dateType;
    }

    @Override
    public String toString() {
        if (getDateType() == null) {
            return String.format("%s: %s - %s", getDateType(), getStartDate(), getEndDate());
        }
        return String.format("%s: %s - %s", getDateType().getLabel(), getStartDate(), getEndDate());
    }

    /**
     * @param startDateApproximate
     *            the startDateApproximate to set
     */
    public void setStartDateApproximate(boolean startDateApproximate) {
        this.startDateApproximate = startDateApproximate;
    }

    /**
     * @return the startDateApproximate
     */
    public boolean isStartDateApproximate() {
        return startDateApproximate;
    }

    /**
     * @param endDateApproximate
     *            the endDateApproximate to set
     */
    public void setEndDateApproximate(boolean endDateApproximate) {
        this.endDateApproximate = endDateApproximate;
    }

    /**
     * @return the endDateApproximate
     */
    public boolean isEndDateApproximate() {
        return endDateApproximate;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    // package private
    @Transient
    Range<Integer> getRange() {
        Range<Integer> range = Range.between(startDate, endDate);
        return range;
    }

    // return true if the supplied covereageDate completely falls within this date range
    public boolean contains(PCoverageDate coverageDate) {
        return (dateType == coverageDate.getDateType())
                && getRange().containsRange(coverageDate.getRange());
    }

    // return true if start or end (or both) falls within this coverageDate
    public boolean overlaps(PCoverageDate coverageDate) {
        return (dateType == coverageDate.getDateType())
                && getRange().isOverlappedBy(coverageDate.getRange());
    }

    // is this date even worth 'evaluating'
    @Transient
    public boolean isInitialized() {
        if ((getStartDate() == null) && (getEndDate() == null)) {
            return false;
        }
        return true;
    }
}
