package org.tdar.core.service.project;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tdar.core.bean.entity.TdarUser;
import org.tdar.core.bean.projects.AbstractTask;
import org.tdar.core.bean.projects.Comment;
import org.tdar.core.service.GenericService;

@Service
public class CommentService extends GenericService {

    @Transactional(readOnly=false)
    public void saveForController(AbstractTask task, Comment com, TdarUser authenticatedUser) {
        task.getComments().add(com);
        saveOrUpdate(task);
    }

}
