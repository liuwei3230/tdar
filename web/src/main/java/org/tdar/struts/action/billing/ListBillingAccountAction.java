package org.tdar.struts.action.billing;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tdar.core.bean.TdarGroup;
import org.tdar.core.bean.billing.Invoice;
import org.tdar.core.serialize.billing.PBillingAccount;
import org.tdar.core.service.Context;
import org.tdar.core.service.billing.BillingAccountService;
import org.tdar.struts.action.AbstractAuthenticatableAction;
import org.tdar.struts.interceptor.annotation.HttpsOnly;
import org.tdar.struts_base.interceptor.annotation.RequiresTdarUserGroup;
import org.tdar.utils.PersistableUtils;
import org.tdar.web.service.WebLoadingService;

import com.opensymphony.xwork2.Preparable;

@Component
@Scope("prototype")
@ParentPackage("secured")
@Namespace("/billing")
@RequiresTdarUserGroup(TdarGroup.TDAR_BILLING_MANAGER)
@HttpsOnly
public class ListBillingAccountAction extends AbstractAuthenticatableAction implements Preparable {

    public static final String LIST = "list";

    private static final long serialVersionUID = -987101577591701115L;

    private List<PBillingAccount> accounts = new ArrayList<>();
    @Autowired
    private transient WebLoadingService webLoadingService;

    @Autowired
    private BillingAccountService accountService;

    @Action(value = LIST)
    public String list() {
        return SUCCESS;
    }

    public List<PBillingAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<PBillingAccount> accounts) {
        this.accounts = accounts;
    }

    @Override
    public void prepare() throws Exception {
        getAccounts().addAll(webLoadingService.proxy(accountService.findAll(), new Context(getAuthenticatedUser())));
        
    }

}
