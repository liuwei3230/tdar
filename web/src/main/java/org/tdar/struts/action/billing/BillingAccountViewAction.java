package org.tdar.struts.action.billing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tdar.core.bean.TdarGroup;
import org.tdar.core.bean.billing.BillingAccount;
import org.tdar.core.bean.billing.BillingActivityModel;
import org.tdar.core.bean.billing.Invoice;
import org.tdar.core.bean.entity.Person;
import org.tdar.core.bean.entity.TdarUser;
import org.tdar.core.bean.resource.Status;
import org.tdar.core.dao.external.auth.InternalTdarRights;
import org.tdar.core.serialize.billing.PBillingAccount;
import org.tdar.core.serialize.billing.PCoupon;
import org.tdar.core.serialize.billing.PInvoice;
import org.tdar.core.serialize.entity.PTdarUser;
import org.tdar.core.serialize.resource.PResource;
import org.tdar.core.service.Authorizable;
import org.tdar.core.service.billing.BillingAccountService;
import org.tdar.core.service.external.AuthorizationService;
import org.tdar.struts.action.AbstractAuthenticatableAction;
import org.tdar.struts.action.AbstractPersistableController.RequestType;
import org.tdar.struts.interceptor.annotation.HttpsOnly;
import org.tdar.struts_base.action.TdarActionException;
import org.tdar.struts_base.action.TdarActionSupport;
import org.tdar.utils.PersistableUtils;
import org.tdar.web.service.WebLoadingService;

import com.opensymphony.xwork2.Preparable;

@Component
@Scope("prototype")
@ParentPackage("secured")
@Namespace("/billing")

@Results(value = {
        @Result(name = TdarActionSupport.SUCCESS, location = "view.ftl"),
        @Result(name = TdarActionSupport.BAD_SLUG, type = TdarActionSupport.TDAR_REDIRECT,
                location = "${id}/${persistable.slug}${slugSuffix}", params = { "ignoreParams", "id,keywordPath,slug", "statusCode", "301" }),
        @Result(name = TdarActionSupport.INPUT, type = TdarActionSupport.HTTPHEADER, params = { "error", "404" }),
        @Result(name = TdarActionSupport.DRAFT, location = "/WEB-INF/content/errors/resource-in-draft.ftl")
})
@HttpsOnly
public class BillingAccountViewAction<R extends PBillingAccount> extends AbstractAuthenticatableAction implements Preparable, Authorizable<BillingAccount> {

    private static final long serialVersionUID = 3896385613294762404L;

    private Long invoiceId;
    private List<PBillingAccount> accounts = new ArrayList<>();
    private List<PInvoice> invoices = new ArrayList<>();
    private List<PResource> resources = new ArrayList<>();
    private List<PCoupon> coupons = new ArrayList<>();
    // private BillingAccountGroup accountGroup;
    private List<PTdarUser> authorizedMembers = new ArrayList<>();
    private Integer quantity = 1;

    private Long numberOfFiles = 0L;
    private Long numberOfMb = 0L;
    private Date expires = new DateTime().plusYears(1).toDate();
    private Long id;
    @Autowired
    private transient BillingAccountService accountService;
    @Autowired
    private transient AuthorizationService authorizationService;

    private boolean editable;

    @Autowired
    WebLoadingService webLoadingService;

    private PBillingAccount account;

    @HttpsOnly
    @Actions(value = {
            @Action(value = "{id}")
    })
    @SkipValidation
    public String view() throws TdarActionException {
        return SUCCESS;
    }

    public Invoice getInvoice() {
        return getGenericService().find(Invoice.class, invoiceId);
    }

    public boolean isEditable() {
        return editable;
    }

    @Override
    public void prepare() throws Exception {
        setAccount(webLoadingService.load(BillingAccount.class, getId(), getAuthenticatedUser(), InternalTdarRights.VIEW_BILLING_INFO, RequestType.VIEW , this));
        getLogger().debug("{}", account);
        if (PersistableUtils.isNullOrTransient(getAccount())) {
            addActionError(getText("error.object_does_not_exist"));
            return;
        }
        setAccounts(webLoadingService.listAvailableAccountsForUser(getAuthenticatedUser()));
        // setAccountGroup(accountService.getAccountGroup(getAccount()));
        getAccount().getAuthorizedUsers().forEach(au -> {
            getAuthorizedMembers().add(au.getUser());
        });
        getResources().addAll(getAccount().getResources());
        PersistableUtils.sortByUpdatedDate(getResources());
        setInvoices(accountService.getInvoicesForAccount(getAccount()));
        setCoupons(new ArrayList<>(getAccount().getCoupons()));
        PersistableUtils.sortByCreatedDate(getInvoices());
    }

    public PBillingAccount getAccount() {
        return account;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public List<PBillingAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<PBillingAccount> accounts) {
        this.accounts = accounts;
    }

    public Person getBlankPerson() {
        return new Person();
    }

    public List<PTdarUser> getAuthorizedMembers() {
        return authorizedMembers;
    }

    public void setAuthorizedMembers(List<PTdarUser> authorizedMembers) {
        this.authorizedMembers = authorizedMembers;
    }

    public boolean isBillingAdmin() {
        return authorizationService.isMember(getAuthenticatedUser(), TdarGroup.TDAR_BILLING_MANAGER);
    }

    public BillingActivityModel getBillingActivityModel() {
        return accountService.getLatestActivityModel();
    }

    public List<PResource> getResources() {
        return resources;
    }

    public void setResources(List<PResource> resources) {
        this.resources = resources;
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

    public Date getExipres() {
        return getExpires();
    }

    public void setExipres(Date exipres) {
        this.setExpires(exipres);
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public List<PInvoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<PInvoice> invoices) {
        this.invoices = invoices;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public List<PCoupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<PCoupon> coupons) {
        this.coupons = coupons;
    }

    @Override
    public boolean authorize(BillingAccount t, TdarUser user) throws Exception {
        getLogger().info("isViewable {} {}", getAuthenticatedUser(), t);
        boolean canView = authorizationService.canViewBillingAccount(user, t);
        if (canView == true) {
            editable = authorizationService.canEditAccount(user , t);
        }
        return canView;
    }

    @Override
    public void setStatus(Status status) {
        // TODO Auto-generated method stub

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAccount(PBillingAccount account) {
        this.account = account;
    }

}