package org.tdar.struts.action.api.pm;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tdar.core.dao.project.TaskStatusResult;
import org.tdar.core.service.project.ProjectResourceStatusService;

/**
 * List the statuses for resources in a task
 * @author abrin
 *
 */
@ParentPackage("secured")
@Namespace("/pm/task/status")
@Component
@Scope("prototype")
public class StatusListAction extends AbstractStatusAction {

    private static final long serialVersionUID = 7760948959909073425L;

    @Autowired
    private ProjectResourceStatusService projectResourceStatusService;

    private TaskStatusResult result;

    @Override
    @Action("save")
    public String execute() throws Exception {
        setResult(projectResourceStatusService.listProjectResourceStatus(getCollection(), getTask()));
        // make JSON
        return SUCCESS;
    }

    public TaskStatusResult getResult() {
        return result;
    }

    public void setResult(TaskStatusResult result) {
        this.result = result;
    }
}
