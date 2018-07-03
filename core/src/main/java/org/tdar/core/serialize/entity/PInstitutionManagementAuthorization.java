package org.tdar.core.serialize.entity;

import javax.xml.bind.annotation.XmlRootElement;

import org.tdar.core.bean.AbstractPersistable;

@XmlRootElement(name = "Pinstitution_authorization")
/**
 * Class to manage users who can edit institutions. The goal here is to allow users to "apply" to own an institution, and then add a workflow for staff to
 * authorize editing.
 * 
 * @author abrin
 *
 */
public class PInstitutionManagementAuthorization extends AbstractPersistable {

    private PTdarUser user;
    private PInstitution institution;
    private boolean authorized = false;
    private String reason;

    public PInstitutionManagementAuthorization() {

    }

    public PInstitutionManagementAuthorization(PInstitution institution, PTdarUser user) {
        this.institution = institution;
        this.user = user;
    }

    public PTdarUser getUser() {
        return user;
    }

    public void setUser(PTdarUser user) {
        this.user = user;
    }

    public PInstitution getInstitution() {
        return institution;
    }

    public void setInstitution(PInstitution institution) {
        this.institution = institution;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
