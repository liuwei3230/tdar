package org.tdar.struts.action.pm;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tdar.core.bean.collection.ResourceCollection;
import org.tdar.core.bean.projects.ProjectWorkflow;
import org.tdar.core.dao.external.auth.InternalTdarRights;
import org.tdar.struts.action.AbstractPersistableController.RequestType;
import org.tdar.struts.action.AuthenticationAware;
import org.tdar.struts.action.PersistableLoadingAction;
import org.tdar.struts.action.TdarActionException;

@ParentPackage("secured")
@Namespace("/pm/workflow")
@Component
@Scope("prototype")
public class WorkflowOverviewAction extends AuthenticationAware.Base implements PersistableLoadingAction<ProjectWorkflow> {

    private static final long serialVersionUID = -1641073964697923384L;
    private ResourceCollection collection;
    private ProjectWorkflow workflow;
    private Long id;

    @Override
    public void prepare() throws Exception {
        prepareAndLoad(this, RequestType.VIEW);
    }

    @Override
    @Action("view")
    public String execute() throws Exception {
        // TODO Auto-generated method stub
        return super.execute();
    }

    public ResourceCollection getCollection() {
        return collection;
    }

    public void setCollection(ResourceCollection collection) {
        this.collection = collection;
    }

    public ProjectWorkflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(ProjectWorkflow workflow) {
        this.workflow = workflow;
    }

    @Override
    public boolean authorize() throws TdarActionException {
        return true;
    }

    @Override
    public ProjectWorkflow getPersistable() {
        return workflow;
    }

    @Override
    public Class<ProjectWorkflow> getPersistableClass() {
        return ProjectWorkflow.class;
    }

    @Override
    public void setPersistable(ProjectWorkflow persistable) {
        this.workflow = persistable;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public InternalTdarRights getAdminRights() {
        return null;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
