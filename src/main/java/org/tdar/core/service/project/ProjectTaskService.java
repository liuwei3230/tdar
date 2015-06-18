package org.tdar.core.service.project;

import java.io.Serializable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tdar.core.bean.projects.AbstractTask;

@Service
public class ProjectTaskService implements Serializable {

    private static final long serialVersionUID = -8219904956809567795L;

    @Transactional(readOnly=false)
    public void saveForController(AbstractTask task) {
        // TODO Auto-generated method stub
        
    }

    
}
