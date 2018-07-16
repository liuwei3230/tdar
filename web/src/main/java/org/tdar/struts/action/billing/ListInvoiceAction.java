package org.tdar.struts.action.billing;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tdar.core.bean.TdarGroup;
import org.tdar.core.bean.billing.Invoice;
import org.tdar.core.serialize.billing.PInvoice;
import org.tdar.core.service.Context;
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
public class ListInvoiceAction extends AbstractAuthenticatableAction  implements Preparable {

    private static final long serialVersionUID = 5946241615531835007L;
    private static final String LIST_INVOICES = "listInvoices";
    private List<PInvoice> invoices = new ArrayList<>();
    @Autowired
    private transient WebLoadingService webLoadingService;

    @Action(value = LIST_INVOICES, results = { @Result(name = SUCCESS, location = "list-invoices.ftl") })
    public String listInvoices() {
        return SUCCESS;
    }

    public List<PInvoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<PInvoice> invoices) {
        this.invoices = invoices;
    }

    @Override
    public void prepare() throws Exception {
        getInvoices().addAll(webLoadingService.proxy(getGenericService().findAll(Invoice.class), new Context(getAuthenticatedUser())));
        PersistableUtils.sortByCreatedDate(getInvoices());
        
    }

}
