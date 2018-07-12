package org.tdar.core.serialize.billing;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdar.core.bean.HasDateCreated;
import org.tdar.core.bean.HasDateUpdated;
import org.tdar.core.bean.HasName;
import org.tdar.core.bean.HasStatus;
import org.tdar.core.bean.Updatable;
import org.tdar.core.bean.Validatable;
import org.tdar.core.bean.billing.TransactionStatus;
import org.tdar.core.bean.entity.TdarUser;
import org.tdar.core.bean.resource.Addressable;
import org.tdar.core.bean.resource.Status;
import org.tdar.core.serialize.PAbstractPersistable;
import org.tdar.core.serialize.entity.PAuthorizedUser;
import org.tdar.core.serialize.entity.PTdarUser;
import org.tdar.core.serialize.resource.PResource;
import org.tdar.utils.MathUtils;
import org.tdar.utils.jaxb.converters.JaxbPersistableConverter;
import org.tdar.utils.json.JsonAccountFilter;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * $Id$
 * 
 * An Account maintains a set of Invoices and is the entity against which people can charge resource uploads. It also tracks a set of users who can charge
 * against those invoices.
 * 
 * @author TDAR
 * @version $Rev$
 */
public class PBillingAccount extends PAbstractPersistable implements HasStatus, Addressable, HasName, Validatable, HasDateUpdated, HasDateCreated {

    @Transient
    private final transient Logger logger = LoggerFactory.getLogger(getClass());

    @JsonView({JsonAccountFilter.class })
    private String name;
    private String description;
    @JsonView({JsonAccountFilter.class })
    private Status status = Status.ACTIVE;
    private Date dateCreated = new Date();
    private Date lastModified = new Date();
    private PTdarUser owner;
    private PTdarUser modifiedBy;
    private Date expires = new Date();
    private Set<PInvoice> invoices = new HashSet<>();
    private Set<PCoupon> coupons = new HashSet<>();
    private Set<PResource> resources = new HashSet<>();
    private List<PAccountUsageHistory> usageHistory = new ArrayList<>();
    private Set<PAuthorizedUser> authorizedUsers = new LinkedHashSet<>();

    private transient Long totalResources = 0L;
    private transient Long totalFiles = 0L;
    private transient Long totalSpaceInBytes = 0L;

    private Long filesUsed = 0L;
    private Long spaceUsedInBytes = 0L;
    private Long resourcesUsed = 0L;
    @JsonView({JsonAccountFilter.class })
    private Integer daysFilesExpireAfter = 60;
    @JsonView({JsonAccountFilter.class })
    private Boolean fullService = false;
    @JsonView({JsonAccountFilter.class })
    private Boolean initialReview = false;
    @JsonView({JsonAccountFilter.class })
    private Boolean externalReview = false;

    public PBillingAccount() {
    }

    public PBillingAccount(String name) {
        this.name = name;
    }

    /**
     * @return the invoices
     */
    public Set<PInvoice> getInvoices() {
        return invoices;
    }

    /**
     * @param invoices
     *            the invoices to set
     */
    public void setInvoices(Set<PInvoice> invoices) {
        this.invoices = invoices;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the dateCreated
     */
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * @param dateCreated
     *            the dateCreated to set
     */
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * @return the lastModified
     */
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * @param lastModified
     *            the lastModified to set
     */
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * @return the resources
     */

    @XmlElementWrapper(name = "resources")
    @XmlElement(name = "resource")
    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    // FIXME: THIS IS A POTENTIAL ISSUE FOR PERFORMANCE WHEREBY IT COULD BE LINKED TO THOUSANDS OF THINGS
    public Set<PResource> getResources() {
        return resources;
    }

    @Transient
    public Set<PResource> getFlaggedResources() {
        Set<PResource> flagged = new HashSet<>();
        for (PResource resource : getResources()) {
            if (resource.getStatus().isFlaggedForBilling()) {
                flagged.add(resource);
            }
        }
        return flagged;
    }

    /**
     * @param resources
     *            the resources to set
     */
    public void setResources(Set<PResource> resources) {
        this.resources = resources;
    }

    private void initTotals() {
        resetTransientTotals();
        for (PInvoice invoice : getInvoices()) {
            if (invoice.getTransactionStatus() != TransactionStatus.TRANSACTION_SUCCESSFUL) {
                continue;
            }
            totalResources += invoice.getTotalResources();
            totalFiles += invoice.getTotalNumberOfFiles();
            totalSpaceInBytes += invoice.getTotalSpaceInBytes();
        }

        logger.trace(String.format("Totals: %s r %s f %s b", totalResources, totalFiles, totalSpaceInBytes));
    }

    public void resetTransientTotals() {
        totalFiles = 0L;
        totalResources = 0L;
        totalSpaceInBytes = 0L;
    }

    public Long getTotalNumberOfResources() {
        initTotals();
        return totalResources;
    }

    public Long getTotalNumberOfFiles() {
        initTotals();
        return totalFiles;
    }

    public Long getTotalSpaceInMb() {
        initTotals();
        return MathUtils.divideByRoundUp(totalSpaceInBytes, MathUtils.ONE_MB);
    }

    public Long getTotalSpaceInBytes() {
        initTotals();
        return totalSpaceInBytes;
    }

    @JsonView({JsonAccountFilter.class })
    public Long getAvailableNumberOfFiles() {
        Long totalFiles = getTotalNumberOfFiles();
        return totalFiles - getFilesUsed();
    }

    public Long getAvailableSpaceInBytes() {
        Long totalSpace = getTotalSpaceInBytes();
        logger.trace("total space: {} , used {} ", totalSpace, getSpaceUsedInBytes());
        return totalSpace - getSpaceUsedInBytes();
    }

    @JsonView({JsonAccountFilter.class })
    public Long getAvailableSpaceInMb() {
        return MathUtils.divideByRoundDown(getAvailableSpaceInBytes(), (double) MathUtils.ONE_MB);
    }

    public Long getAvailableResources() {
        Long totalResources = getTotalNumberOfResources();
        return totalResources - getResourcesUsed();
    }


    @JsonView({JsonAccountFilter.class })
    @XmlAttribute(name = "ownerRef")
    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    public PTdarUser getOwner() {
        return owner;
    }

    public void setOwner(PTdarUser owner) {
        this.owner = owner;
    }

    public PTdarUser getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(PTdarUser modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    /**
     * @return the status
     */
    @Override
    public Status getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getFilesUsed() {
        return filesUsed;
    }

    public Float getTotalCost() {
        Float total = 0f;
        for (PInvoice invoice : invoices) {
            TransactionStatus status2 = invoice.getTransactionStatus();
            if (status2.isComplete() && !status2.isInvalid()) {
                total += invoice.getTotal();
            }
        }
        return total;
    }

    public void setFilesUsed(Long filesUsed) {
        this.filesUsed = filesUsed;
    }

    public Long getSpaceUsedInBytes() {
        return spaceUsedInBytes;
    }

    public Long getSpaceUsedInMb() {
        return MathUtils.divideByRoundUp(spaceUsedInBytes, MathUtils.ONE_MB);
    }

    public void setSpaceUsedInBytes(Long spaceUsed) {
        this.spaceUsedInBytes = spaceUsed;
    }

    public Long getResourcesUsed() {
        return resourcesUsed;
    }

    public void setResourcesUsed(Long resourcesUsed) {
        this.resourcesUsed = resourcesUsed;
    }

    @Override
    public String getUrlNamespace() {
        return "billing";
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public Date getDateUpdated() {
        return lastModified;
    }

    public Set<PCoupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(Set<PCoupon> coupons) {
        this.coupons = coupons;
    }

    public void reset() {
        setStatus(Status.ACTIVE);
        setSpaceUsedInBytes(0L);
        setFilesUsed(0L);
        initTotals();
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", getName(), getId());
    }

    @Override
    @JsonView({JsonAccountFilter.class })
    public String getDetailUrl() {
        return String.format("/%s/%s", getUrlNamespace(), getId());
    }

    public List<PAccountUsageHistory> getUsageHistory() {
        return usageHistory;
    }

    public void setUsageHistory(List<PAccountUsageHistory> history) {
        this.usageHistory = history;
    }

    public String usedString() {
        return String.format("f: %s s: %s", filesUsed, spaceUsedInBytes);
    }

    public String availableString() {
        return String.format("f: %s s: %s", totalFiles - filesUsed, totalSpaceInBytes - spaceUsedInBytes);
    }

    @Override
    @Transient
    @XmlTransient
    public boolean isDeleted() {
        return status == Status.DELETED;
    }

    @Override
    @Transient
    @XmlTransient
    public boolean isActive() {
        return status == Status.ACTIVE;
    }

    @Override
    @Transient
    @XmlTransient
    public boolean isDraft() {
        return status == Status.DRAFT;
    }

    @Override
    public boolean isDuplicate() {
        return status == Status.DUPLICATE;
    }

    @Override
    @Transient
    @XmlTransient
    public boolean isFlagged() {
        return status == Status.FLAGGED;
    }

    
    @JsonView({JsonAccountFilter.class })
    @XmlElementWrapper(name = "authorizedUsers")
    @XmlElement(name = "authorizedUser")
    public Set<PAuthorizedUser> getAuthorizedUsers() {
        return authorizedUsers;
    }

    public void setAuthorizedUsers(Set<PAuthorizedUser> authorizedUsers) {
        this.authorizedUsers = authorizedUsers;
    }

    @Override
    public boolean isValidForController() {
        return isValid();
    }

    @Override
    public boolean isValid() {
        return StringUtils.isNotBlank(name);
    }

    public Boolean getFullService() {
        return fullService;
    }

    public void setFullService(Boolean fullService) {
        this.fullService = fullService;
    }

    public Boolean getInitialReview() {
        return initialReview;
    }

    public void setInitialReview(Boolean initialReview) {
        this.initialReview = initialReview;
    }

    public Boolean getExternalReview() {
        return externalReview;
    }

    public void setExternalReview(Boolean externalReview) {
        this.externalReview = externalReview;
    }

    public Integer getDaysFilesExpireAfter() {
        return daysFilesExpireAfter;
    }

    public void setDaysFilesExpireAfter(Integer daysFilesExpireAfter) {
        this.daysFilesExpireAfter = daysFilesExpireAfter;
    }

}
