package org.tdar.core.service.project;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tdar.core.bean.collection.ResourceCollection;
import org.tdar.core.bean.projects.AbstractTask;
import org.tdar.core.bean.projects.ResourceStatusWrapper;
import org.tdar.core.dao.project.ProjectResourceStatusDao;
import org.tdar.core.dao.project.TaskStatusResult;

@Service
public class ProjectResourceStatusService implements Serializable {

    private static final long serialVersionUID = 6441235719673488955L;

    @Autowired
    private ProjectResourceStatusDao projectResourceStatusDao;

    @Transactional(readOnly=false)
    public void updateStatusesForResource(AbstractTask task, ResourceCollection collection, List<ResourceStatusWrapper> changes) {
        projectResourceStatusDao.updateProjectResourceStatusForTask(task, collection, changes);
    }
    
    @Transactional(readOnly=true)
    public TaskStatusResult listProjectResourceStatus(ResourceCollection collection, AbstractTask task) {
        return projectResourceStatusDao.getProjectResourceStatusForTask(collection, task);
    }
    
}
