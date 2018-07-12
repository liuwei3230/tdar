package org.tdar.core.serialize.billing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.bean.HasDateCreated;
import org.tdar.core.bean.HasDateUpdated;
import org.tdar.core.bean.Updatable;
import org.tdar.core.bean.billing.TransactionStatus;
import org.tdar.core.dao.external.payment.PaymentMethod;
import org.tdar.core.serialize.entity.PAddress;
import org.tdar.core.serialize.entity.PTdarUser;
import org.tdar.utils.MathUtils;
import org.tdar.utils.PersistableUtils;
import org.tdar.utils.json.JsonLookupFilter;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * $Id$
 * 
 * Represents a financial transaction to purchase space and number of resources for a given Account.
 * 
 * @author TDAR
 * @version $Rev$
 */
public class PInvoice extends AbstractPersistable implements HasDateUpdated, HasDateCreated {

    private final transient Logger logger = LoggerFactory.getLogger(getClass());

    private Date dateCreated;
    private String transactionId;
    @JsonView(JsonLookupFilter.class)
    private PaymentMethod paymentMethod;
    private Long billingPhone;
    private String accountType;
    private Date transactionDate;
    private PTdarUser owner;
    private PCoupon coupon;
    private PBillingTransactionLog response;
    private PTdarUser transactedBy;
    private PAddress address;
    private List<PBillingItem> items = new ArrayList<>();
    private Long numberOfFiles = 0L;
    private Long numberOfMb = 0L;
    @JsonView(JsonLookupFilter.class)
    private Float total;
    private String invoiceNumber;
    private String otherReason;
    @JsonView(JsonLookupFilter.class)
    private TransactionStatus transactionStatus = TransactionStatus.PREPARED;

    public PInvoice() {
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
     * @return the transactionId
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * @param transactionId
     *            the transactionId to set
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public PAddress getAddress() {
        return address;
    }

    public void setAddress(PAddress address) {
        this.address = address;
    }

    public PTdarUser getOwner() {
        return owner;
    }

    public void setOwner(PTdarUser person) {
        this.owner = person;
    }

    public List<PBillingItem> getItems() {
        return items;
    }

    public void setItems(List<PBillingItem> items) {
        this.items = items;
    }

    public Float getTotal() {
        if (total == null) {
            return getCalculatedCost();
        }
        return total;
    }

    /* not sure if this can be 'private', but ideally only called by finalize method and hibernate internally */
    private void setTotal(Float total) {
        this.total = total;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod transactionType) {
        this.paymentMethod = transactionType;
    }

    public Long getTotalResources() {
        initTotals();
        return totalResources;
    }

    public Long getTotalSpaceInMb() {
        initTotals();
        return totalSpaceInMb;
    }

    public Long getTotalSpaceInBytes() {
        return getTotalSpaceInMb() * MathUtils.ONE_MB;
    }

    public Long getTotalNumberOfFiles() {
        initTotals();
        return totalFiles;
    }

    @SuppressWarnings("unchecked")
    private <T> T coalesce(T... items) {
        for (T i : items) {
            if (i != null) {
                return i;
            }
        }
        return null;
    }

    private void initTotals() {
        if (!initialized) {
            // if (coupon != null) {
            // calculatedCost -= coupon.getNumberOfDollars();
            // }

            Long discountedSpace = 0L;
            Long discountedFiles = 0L;
            if (coupon != null) {
                discountedFiles = coalesce(coupon.getNumberOfFiles(), 0L);
                discountedSpace = coalesce(coupon.getNumberOfMb(), 0L);
            }

            for (PBillingItem item : getItems()) {
                PBillingActivity activity = item.getActivity();
                Long numberOfFiles = coalesce(activity.getNumberOfFiles(), 0L);
                Long space = coalesce(activity.getNumberOfMb(), 0L);
                Long numberOfResources = coalesce(activity.getNumberOfResources(), 0L);

                if ((numberOfFiles > 0L) && (discountedFiles > 0L)) {
                    couponValue += activity.getPrice() * discountedFiles;
                    discountedFiles = 0L;
                }

                if ((space > 0L) && (discountedSpace > 0L)) {
                    couponValue += activity.getPrice() * discountedSpace;
                    discountedSpace = 0L;
                }
                long quantity = item.getQuantity().longValue();
                totalFiles += numberOfFiles * quantity;
                totalSpaceInMb += space * quantity;

                if (numberOfResources != null) {
                    totalResources += numberOfResources * quantity;
                }
                calculatedCost += item.getSubtotal();
                logger.trace("{}", this);
            }
            calculatedCost -= couponValue;
            if (calculatedCost < 0) {
                calculatedCost = 0f;
            }
            initialized = true;
        }
    }

    public void resetTransientValues() {
        totalResources = 0L;
        totalSpaceInMb = 0L;
        calculatedCost = 0F;
        totalResources = 0L;
        couponValue = 0F;
        initialized = false;
    }

    @JsonView(JsonLookupFilter.class)
    private transient Long totalResources = 0L;
    @JsonView(JsonLookupFilter.class)
    private transient Long totalSpaceInMb = 0L;
    @JsonView(JsonLookupFilter.class)
    private transient Long totalFiles = 0L;
    @JsonView(JsonLookupFilter.class)
    private transient Float calculatedCost = 0F;
    private transient boolean initialized = false;
    private transient Float couponValue = 0f;


    public void setBillingPhone(Long billingPhone) {
        this.billingPhone = billingPhone;
    }

    public Long getBillingPhone() {
        return billingPhone;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getOtherReason() {
        return otherReason;
    }

    public void setOtherReason(String otherReason) {
        this.otherReason = otherReason;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public boolean isCancelled() {
        if (TransactionStatus.TRANSACTION_CANCELLED.equals(getTransactionStatus()) || TransactionStatus.TRANSACTION_FAILED.equals(getTransactionStatus())) {
            return true;
        }
        return false;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public PTdarUser getTransactedBy() {
        return transactedBy;
    }

    public void setTransactedBy(PTdarUser transactedBy) {
        this.transactedBy = transactedBy;
    }

    public boolean isModifiable() {
        return transactionStatus.isModifiable();
    }

    public Float getCalculatedCost() {
        initTotals();
        return calculatedCost;
    }

    public void setCalculatedCost(Float calculatedCost) {
        this.calculatedCost = calculatedCost;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Long getNumberOfFiles() {
        return numberOfFiles;
    }

    public void setNumberOfFiles(Long numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }

    public Long getNumberOfMb() {
        return numberOfMb;
    }

    public void setNumberOfMb(Long numberOfMb) {
        this.numberOfMb = numberOfMb;
    }

    public void markFinal() {
        setTotal(getCalculatedCost());
    }

    public PBillingTransactionLog getResponse() {
        return response;
    }

    public void setResponse(PBillingTransactionLog response) {
        this.response = response;
    }

    @Transient
    public boolean isProxy() {
        if (PersistableUtils.isNullOrTransient(owner) || PersistableUtils.isNullOrTransient(transactedBy)) {
            return false;
        }
        return ObjectUtils.notEqual(owner.getId(), transactedBy.getId());
    }

    public Date getDateUpdated() {
        return dateCreated;
    }

    public PCoupon getCoupon() {
        return coupon;
    }

    public void setCoupon(PCoupon coupon) {
        this.coupon = coupon;
    }

    public Float getCouponValue() {
        return couponValue;
    }

    public void setCouponValue(Float couponValue) {
        this.couponValue = couponValue;
    }

    /**
     * NOTE: Returns transient data, not the actual data stored in this object. If invoked before
     * getCalculatedCost() or initTotals() is called, all these values will be zero/empty (except the ID).
     * 
     * Consider emitting persistable values in addition to transient values.
     */
    @Override
    public String toString() {
        return String.format("%s files, %s mb, %s resources [calculated cost: $%s] %s (id: %d)",
                totalFiles, totalSpaceInMb, totalResources, calculatedCost, coupon, getId());
    }

    public boolean hasValidValue() {
        logger.trace("files: {} space: {}", getNumberOfFiles(), getNumberOfMb());
        if (isLessThan(getNumberOfFiles(), 1) && isLessThan(getNumberOfMb(), 1) && (getCoupon() == null)) {
            return false;
        }
        return true;
    }

    public void setDefaultPaymentMethod() {
        if (paymentMethod == null) {
            setPaymentMethod(PaymentMethod.CREDIT_CARD);
        }
    }

    private boolean isLessThan(Long val, long comp) {
        if (val == null) {
            return false;
        }
        return val.longValue() < comp;
    }

}
