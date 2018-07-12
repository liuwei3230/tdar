package org.tdar.web.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tdar.core.bean.HasName;
import org.tdar.core.bean.HasStatus;
import org.tdar.core.bean.Persistable;
import org.tdar.core.bean.billing.BillingAccount;
import org.tdar.core.bean.entity.TdarUser;
import org.tdar.core.bean.keyword.Keyword;
import org.tdar.core.bean.resource.Status;
import org.tdar.core.dao.base.GenericDao;
import org.tdar.core.dao.external.auth.InternalTdarRights;
import org.tdar.core.exception.StatusCode;
import org.tdar.core.serialize.billing.PBillingAccount;
import org.tdar.core.serialize.keyword.PKeyword;
import org.tdar.core.service.Authorizable;
import org.tdar.core.service.ProxyConstructionService;
import org.tdar.core.service.billing.BillingAccountService;
import org.tdar.core.service.external.AuthorizationService;
import org.tdar.struts.action.AbstractPersistableController.RequestType;
import org.tdar.struts_base.action.PersistableLoadingAction;
import org.tdar.struts_base.action.TdarActionException;
import org.tdar.struts_base.action.TdarActionSupport;
import org.tdar.utils.PersistableUtils;

@Service
public class WebLoadingService {
    private final transient Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ProxyConstructionService proxyConstructionService;
    @Autowired
    GenericDao genericDao;

    @Autowired
    BillingAccountService billingAccountService;
    
    @Autowired
    AuthorizationService authorizationService;
    
    public <T extends Persistable,R> R load(Class<T> hcls, Class<R> vcls, Long id, TdarUser user, InternalTdarRights rights, RequestType requestType,
            Authorizable<T> support) throws Exception {
        if (PersistableUtils.isNullOrTransient(id)) {
            abort(StatusCode.UNKNOWN_ERROR,
                    support.getText("abstractPersistableController.cannot_recognize_request", hcls.getSimpleName()));
        }
        R q = null;
        try {
            q = proxyConstructionService.load(hcls, id, support, user);
        } catch (TdarActionException tae) {
            throw tae;
        } catch (Throwable t) {
            logger.error("{}", t,t);
            abort(StatusCode.BAD_REQUEST,
                    support.getText("abstractPersistableController.cannot_recognize_request", hcls.getSimpleName()));
        }
        T r = genericDao.find(hcls, id);  
        checkValidRequest(rights, user, support, (T)r);
        return q;
    }

    /**
     * Check that the request is valid. In general, this should be able to used as is, though, it's possible to either (a) override the entire method or (b)
     * implement authorize() differently.
     * 
     * @param pc
     * @throws Exception 
     */
    protected <P extends Persistable> void checkValidRequest(InternalTdarRights adminRole, TdarUser user,
        Authorizable<P> support, P r) throws Exception {
        if (r == null) {
            logger.debug("Dealing with transient persistable {}", r);
            // persistable is null, so the lookup failed (aka not found)
            abort(StatusCode.NOT_FOUND, support.getText("abstractPersistableController.not_found"));
        }

        // the admin rights check -- on second thought should be the fastest way to execute as it pulls from cached values
        if (authorizationService.can(adminRole, user)) {
            return;
        }

        // call the locally defined "authorize" method for more specific checks
        if (support.authorize(r, user)) {
            return;
        }

        // default is to be an error
        String errorMessage = support.getText("abstractPersistableController.no_permissions");
        // addActionError(errorMessage);
        abort(StatusCode.FORBIDDEN, TdarActionSupport.FORBIDDEN, errorMessage);
    }

    private <P extends Persistable> void logRequest(PersistableLoadingAction<P> pc, RequestType type, P p) {
        String status = "";
        String name = "";
        if (p instanceof HasStatus) {
            status = ((HasStatus) p).getStatus().toString();
        }

        if (pc.getAuthenticatedUser() != null) {
            // don't log anonymous users
            name = pc.getAuthenticatedUser().getUsername();
        } else {
            return;
        }

        if (StringUtils.isBlank(name)) {
            name = "anonymous";
        }
        String title = "";
        if (p != null && p instanceof HasName) {
            title = ((HasName) pc.getPersistable()).getName();
        }
        logger.info(String.format("%s is %s %s (%s): %s - %s", name, type.getLabel(), pc.getClass().getSimpleName(), pc.getId(), status, title));
    }

    /**
     * Throw an exception with a status code
     * 
     * @param statusCode
     * @param errorMessage
     * @throws TdarActionException
     */
    protected void abort(StatusCode statusCode, String errorMessage) throws TdarActionException {
        logger.debug("abort: {}", statusCode);
        throw new TdarActionException(statusCode, errorMessage);
    }

    protected void abort(StatusCode statusCode, String response, String errorMessage) throws TdarActionException {
        logger.debug("abort: {}", statusCode);
        throw new TdarActionException(statusCode, response, errorMessage);
    }

    public List<PBillingAccount> listAvailableAccountsForUser(TdarUser authenticatedUser, Status ...statuses) {
        List<BillingAccount> acts = billingAccountService.listAvailableAccountsForUser(authenticatedUser, statuses);
        List<PBillingAccount> toReturn = new ArrayList<>();
        for (BillingAccount act : acts) {
            toReturn.add(proxyConstructionService.constructShallowAccount(act));
        }
        return toReturn;
    }


}
