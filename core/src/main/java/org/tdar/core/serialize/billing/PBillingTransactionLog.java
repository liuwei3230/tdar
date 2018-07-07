package org.tdar.core.serialize.billing;

import java.util.Date;

import org.tdar.core.bean.AbstractPersistable;

/**
 * A JSON Object that represents the result of a financial transaction. Could be successful or failed.
 * 
 * @author abrin
 * 
 */
public class PBillingTransactionLog extends AbstractPersistable {

    private String responseInJson;
    private Date dateCreated;
    private String transactionId;

    public PBillingTransactionLog() {
    }

    public PBillingTransactionLog(String jsonResponse, String transactionId) {
        setResponseInJson(jsonResponse);
        setDateCreated(new Date());
        setTransactionId(transactionId);

    }

    public String getResponseInJson() {
        return responseInJson;
    }

    public void setResponseInJson(String responseInJson) {
        this.responseInJson = responseInJson;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return String.format("TransactionLog (%s) - %s", getId(), getTransactionId());
    }

}
