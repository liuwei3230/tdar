package org.tdar.core.serialize.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.StringUtils;
import org.tdar.core.bean.Obfuscatable;
import org.tdar.core.bean.entity.UserAffiliation;
import org.tdar.utils.json.JsonAdminLookupFilter;

import com.fasterxml.jackson.annotation.JsonView;

@XmlRootElement(name = "Puser")
public class PTdarUser extends PPerson {

    private String username;
    private PInstitution proxyInstitution;
    private String proxyNote;
    private Date lastLogin;
    private UserAffiliation affiliation;
    private Date penultimateLogin;
    private Long totalLogins = 0L;
    private Long totalDownloads = 0L;
    private Boolean contributor = Boolean.FALSE;
    private String contributorReason;
    private String userAgent;
    private Integer tosVersion = 0;
    private Integer contributorAgreementVersion = 0;
    private Date dismissedNotificationsDate;
    private Boolean newResourceSavedAsDraft = Boolean.FALSE;

    public PTdarUser() {
    }

    public PTdarUser(String firstName, String lastName, String email, String username, Long id) {
        this(firstName, lastName, email, username);
        setId(id);
    }

    public PTdarUser(String firstName, String lastName, String email) {
        super(firstName, lastName, email);
    }

    public PTdarUser(String firstName, String lastName, String email, String username) {
        super(firstName, lastName, email);
        this.username = username;
    }

    public PTdarUser(PPerson person, String username) {
        super(person.getFirstName(), person.getLastName(), person.getEmail());
        this.setUsername(username);
    }

    public Boolean isContributor() {
        return contributor;
    }

    public Boolean getContributor() {
        return contributor;
    }

    public void setContributor(Boolean contributor) {
        this.contributor = contributor;
    }

    @XmlTransient
    public String getContributorReason() {
        return contributorReason;
    }

    public void setContributorReason(String contributorReason) {
        this.contributorReason = contributorReason;
    }

    public Long getTotalLogins() {
        if (totalLogins == null) {
            return 0L;
        }
        return totalLogins;
    }

    public void setTotalLogins(Long totalLogins) {
        this.totalLogins = totalLogins;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.penultimateLogin = getLastLogin();
        this.lastLogin = lastLogin;
    }

    public void incrementLoginCount() {
        totalLogins = getTotalLogins() + 1;
    }

    public Date getPenultimateLogin() {
        return penultimateLogin;
    }

    public void setPenultimateLogin(Date penultimateLogin) {
        this.penultimateLogin = penultimateLogin;
    }

    @Override
    public Set<Obfuscatable> obfuscate() {
        Set<Obfuscatable> results = new HashSet<>();
        setObfuscated(true);
        results.addAll(super.obfuscate());
        setObfuscatedObjectDifferent(true);
        setContributor(false);
        setObfuscated(true);
        setLastLogin(null);
        setPenultimateLogin(null);
        setTotalLogins(null);
        return results;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonView(JsonAdminLookupFilter.class)
    @Override
    public boolean isRegistered() {
        return true;
    }

    @Override
    public boolean isDedupable() {
        return false;
    }

    public PInstitution getProxyInstitution() {
        return proxyInstitution;
    }

    public void setProxyInstitution(PInstitution proxyInstitution) {
        this.proxyInstitution = proxyInstitution;
    }

    public String getProxyNote() {
        return proxyNote;
    }

    public void setProxyNote(String proxyNote) {
        this.proxyNote = proxyNote;
    }

    public Integer getContributorAgreementVersion() {
        return contributorAgreementVersion;
    }

    public void setContributorAgreementVersion(Integer contributorAgreementVersion) {
        this.contributorAgreementVersion = contributorAgreementVersion;
    }

    public Integer getTosVersion() {
        return tosVersion;
    }

    public void setTosVersion(Integer tosVersion) {
        this.tosVersion = tosVersion;
    }

    public UserAffiliation getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(UserAffiliation affiliation) {
        this.affiliation = affiliation;
    }

    public Date getDismissedNotificationsDate() {
        return dismissedNotificationsDate;
    }

    public void updateDismissedNotificationsDate() {
        setDismissedNotificationsDate(new Date());
    }

    public void setDismissedNotificationsDate(Date dismissedNotificationsDate) {
        this.dismissedNotificationsDate = dismissedNotificationsDate;
    }

    public Boolean getNewResourceSavedAsDraft() {
        return newResourceSavedAsDraft;
    }

    public void setNewResourceSavedAsDraft(Boolean newResourceSavedAsDraft) {
        this.newResourceSavedAsDraft = newResourceSavedAsDraft;
    }

    public Long getTotalDownloads() {
        return totalDownloads;
    }

    public void setTotalDownloads(Long totalDownloads) {
        this.totalDownloads = totalDownloads;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

}
