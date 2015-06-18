package org.tdar.struts.action.api.pm;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tdar.core.bean.projects.ResourceStatusWrapper;
import org.tdar.core.service.project.ProjectResourceStatusService;
import org.tdar.struts.interceptor.annotation.PostOnly;

@ParentPackage("secured")
@Namespace("/pm/task/status")
@Component
@Scope("prototype")
public class StatusUpdateAction extends AbstractStatusAction {

    private static final long serialVersionUID = -4753288253646995352L;

    @Autowired
    private ProjectResourceStatusService projectResourceStatusService;

    private List<ResourceStatusWrapper> wrappers;
    
    @PostOnly
    @Override
    @Action("save")
    public String execute() throws Exception {
        projectResourceStatusService.listProjectResourceStatus(getCollection(), getTask());
        return SUCCESS;
    }

    public List<ResourceStatusWrapper> getWrappers() {
        return wrappers;
    }

    public void setWrappers(List<ResourceStatusWrapper> wrappers) {
        this.wrappers = wrappers;
    }
    
}
