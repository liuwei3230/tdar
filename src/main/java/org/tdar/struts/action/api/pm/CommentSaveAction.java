package org.tdar.struts.action.api.pm;

import java.util.Date;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tdar.core.bean.projects.AbstractTask;
import org.tdar.core.bean.projects.Comment;
import org.tdar.core.service.project.CommentService;
import org.tdar.struts.action.AuthenticationAware;
import org.tdar.struts.interceptor.annotation.PostOnly;

import com.opensymphony.xwork2.Preparable;

@ParentPackage("secured")
@Namespace("/pm/comment")
@Component
@Scope("prototype")
public class CommentSaveAction extends AuthenticationAware.Base implements Preparable {

    private static final long serialVersionUID = -9019469502706484541L;

    @Autowired
    CommentService commentService;
    
    private String comment;
    private Long taskId;
    private AbstractTask task;
    
    public void prepare() {
        task = getGenericService().find(AbstractTask.class, taskId);
    }
    
    @Override
    public void validate() {
        super.validate();
        
    }
    
    @Override
    @PostOnly
    @Action("save")
    public String execute() throws Exception {
        Comment com = new Comment();
        com.setComment(comment);
        com.setCommentor(getAuthenticatedUser());
        com.setDateCreated(new Date());
        commentService.saveForController(task, com, getAuthenticatedUser());
        return SUCCESS;
    }
}
