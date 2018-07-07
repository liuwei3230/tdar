package org.tdar.core.serialize.billing;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.bean.Validatable;
import org.tdar.core.exception.TdarValidationException;

/**
 * an Activity + quantity for a financial transaction. Multiple activities may be associated with a single financial transaction.
 * 
 */
public class PBillingItem extends AbstractPersistable implements Validatable {

    private final transient Logger logger = LoggerFactory.getLogger(getClass());
    private PBillingActivity activity;
    private Integer quantity = 0;

    public PBillingItem() {
    }

    public PBillingItem(PBillingActivity activity, int quantity) {
        this.activity = activity;
        this.quantity = quantity;
    }

    public PBillingActivity getActivity() {
        return activity;
    }

    public void setActivity(PBillingActivity activity) {
        this.activity = activity;
    }

    public Integer getQuantity() {
        if ((quantity == null) || (quantity < 1)) {
            return 0;
        }
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    @XmlTransient
    public boolean isValidForController() {
        if (getActivity() == null) {
            throw new TdarValidationException("billingItem.specify_activity");
        }
        if (getQuantity() < 1) {
            throw new TdarValidationException("billingItem.non_zero_value");
        }
        return true;
    }

    @Override
    @XmlTransient
    public boolean isValid() {
        return isValidForController();
    }

    public Float getSubtotal() {
        return activity.getPrice() * getQuantity().floatValue();
    }

    @Override
    public String toString() {
        return String.format("%s %s ($%s)", getQuantity(), getActivity(), getSubtotal());
    }
}
