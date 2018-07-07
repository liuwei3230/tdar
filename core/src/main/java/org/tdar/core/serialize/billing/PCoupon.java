package org.tdar.core.serialize.billing;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.serialize.entity.PTdarUser;

/**
 * A coupon or 'credit' for space or files in tDAR.
 * 
 * @author abrin
 * 
 */
public class PCoupon extends AbstractPersistable {

    private Long numberOfMb = 0L;
    private Long numberOfFiles = 0L;
    private String code;
    private Date dateCreated = new Date();
    private Date dateExpires;
    private Date dateRedeemed;
    private Set<Long> resourceIds = new HashSet<>();
    private PTdarUser user;

    public Long getNumberOfMb() {
        return numberOfMb;
    }

    public void setNumberOfMb(Long numberOfMb) {
        this.numberOfMb = numberOfMb;
    }

    public Long getNumberOfFiles() {
        return numberOfFiles;
    }

    public void setNumberOfFiles(Long numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateExpires() {
        return dateExpires;
    }

    public void setDateExpires(Date dateExpires) {
        this.dateExpires = dateExpires;
    }

    @Override
    public String toString() {
        return String.format("coupon[f=%s s=%s c=%s]", numberOfFiles, numberOfMb, code);
    }

    public PTdarUser getUser() {
        return user;
    }

    public void setUser(PTdarUser user) {
        this.user = user;
    }

    public Date getDateRedeemed() {
        return dateRedeemed;
    }

    public void setDateRedeemed(Date dateRedeemed) {
        this.dateRedeemed = dateRedeemed;
    }

    public Set<Long> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(Set<Long> resourceIds) {
        this.resourceIds = resourceIds;
    }
}
