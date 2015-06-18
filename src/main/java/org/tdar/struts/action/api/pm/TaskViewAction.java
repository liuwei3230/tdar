package org.tdar.struts.action.api.pm;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * Get the details for a task
 * @author abrin
 *
 */

@ParentPackage("secured")
@Namespace("/pm/task")
@Component
@Scope("prototype")
public class TaskViewAction extends AbstractTaskAction {

    private static final long serialVersionUID = 1679611841820858545L;

    @Override
    @Action("{id}")
    public String execute() throws Exception {
        // TODO Auto-generated method stub
        return super.execute();
    }
}
