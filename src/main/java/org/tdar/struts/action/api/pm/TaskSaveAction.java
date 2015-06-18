package org.tdar.struts.action.api.pm;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tdar.core.service.project.ProjectTaskService;
import org.tdar.struts.action.TdarActionException;


@ParentPackage("secured")
@Namespace("/pm/task/")
@Component
@Scope("prototype")
public class TaskSaveAction extends AbstractTaskAction {

    private static final long serialVersionUID = 5961466478802961388L;
    private ProjectTaskService projectTaskService;

    @Override
    @Action("save")
    public String execute() throws Exception {
        projectTaskService.saveForController(getTask());
        return SUCCESS;
    }
    
    @Override
    public boolean authorize() throws TdarActionException {
        // TODO Auto-generated method stub
        return super.authorize();
    }
}
