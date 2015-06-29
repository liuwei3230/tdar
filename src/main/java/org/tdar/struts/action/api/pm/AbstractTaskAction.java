package org.tdar.struts.action.api.pm;

import org.tdar.core.bean.projects.AbstractTask;
import org.tdar.core.dao.external.auth.InternalTdarRights;
import org.tdar.struts.action.AbstractPersistableController.RequestType;
import org.tdar.struts.action.AuthenticationAware;
import org.tdar.struts.action.PersistableLoadingAction;
import org.tdar.struts.action.TdarActionException;

public abstract class AbstractTaskAction extends AuthenticationAware.Base implements PersistableLoadingAction<AbstractTask> {

    private Long id;
    private AbstractTask task;


    private static final long serialVersionUID = 1379361441783196359L;

    @Override
    public void prepare() throws Exception {
        prepareAndLoad(this, RequestType.VIEW);
    }

    public AbstractTask getTask() {
        return task;
    }

    public void setTask(AbstractTask task) {
        this.task = task;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean authorize() throws TdarActionException {
        return true;
    }

    @Override
    public AbstractTask getPersistable() {
        return task;
    }

    @Override
    public Class<AbstractTask> getPersistableClass() {
        return AbstractTask.class;
    }

    @Override
    public void setPersistable(AbstractTask persistable) {
        this.task = persistable;
    }

    @Override
    public InternalTdarRights getAdminRights() {
        // TODO Auto-generated method stub
        return null;
    }

}
