package org.tdar.core.dao.project;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Component;
import org.tdar.core.bean.collection.ResourceCollection;
import org.tdar.core.bean.projects.AbstractTask;
import org.tdar.core.bean.projects.ProjectTask;
import org.tdar.core.bean.projects.ResourceStatusWrapper;
import org.tdar.core.bean.projects.TaskStatus;
import org.tdar.core.bean.resource.ResourceProxy;
import org.tdar.core.bean.resource.ResourceType;
import org.tdar.core.bean.resource.Status;
import org.tdar.core.dao.GenericDao;
import org.tdar.utils.PersistableUtils;

@Component
public class ProjectResourceStatusDao extends GenericDao {

    public void updateProjectResourceStatusForTask(AbstractTask task, ResourceCollection collection, List<ResourceStatusWrapper> changes) {
        List<ResourceStatusWrapper> updates = new ArrayList<>();
        List<ResourceStatusWrapper> inserts = new ArrayList<>();
        for (ResourceStatusWrapper change : changes) {
            if (PersistableUtils.isNullOrTransient(change)) {
                inserts.add(change);
            } else {
                updates.add(change);
            }
        }
        
        // insert into pm_task_resources (task_id, status, resource_id) select 2, 'ACTIVE', id from resource where id in (...);
        
    }
    
    public TaskStatusResult getProjectResourceStatusForTask(ResourceCollection collection, AbstractTask task) {
        Long collectionId = collection.getId();
        Long taskId = task.getId();
        String sql = "select r.id, r.status, r.title, r.resource_type, "
                + "coalesce(tr.id, -1) as rsid, coalesce(tr.status,'ACTIVE') as tstatus, coalesce(tr.task_id,2) as tid from "
                + "resource r left join  pm_task_resource tr on r.id=tr.resource_id where r.id "
                + "in (select resource_id from collection_resource where collection_id in "
                + "(select collection_id from collection_parents where collection_id=:collectionId or parent_id=:collectionId)) "
                + "and (task_id=:taskId or task_id is null) order by r.id";
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.addScalar("r.id", LongType.INSTANCE);
        query.addScalar("r.title", StringType.INSTANCE);
        query.addScalar("r.resource_type", StringType.INSTANCE);
        query.addScalar("tid", LongType.INSTANCE);
        query.addScalar("rsid", LongType.INSTANCE);
        query.addScalar("tstatus", StringType.INSTANCE);
        
        
        query.setLong("taskId", taskId);
        query.setLong("collectionId", collectionId);
        TaskStatusResult toReturn = new TaskStatusResult();
        List<Object> results = new ArrayList<>();
        for (Object result_ : results) {
            Object[] result = (Object[]) result_;
            Long resourceId = (Long) result[0];
            Status resourceStatus = (Status) result[1];
            String title = (String) result[2];
            ResourceType type = ResourceType.valueOf((String) result[3]);
            ResourceProxy rp = new ResourceProxy();
            rp.setId(resourceId);
            rp.setTitle(title);
            rp.setResourceType(type);
            rp.setStatus(resourceStatus);
            ResourceStatusWrapper rsw = new ResourceStatusWrapper();
            rsw.setId((Long) result[4]);
            TaskStatus taskStatus = (TaskStatus) result[5];
            Integer acc = toReturn.getAggregate().get(taskStatus);
            if (acc == null) {
                acc = 0;
            }
            toReturn.getAggregate().put(taskStatus, acc + 1);
            rsw.setStatus(taskStatus);
            toReturn.getResults().add(rsw);
            try {
                rsw.setResource(rp.generateResource());
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return toReturn;
    }
}
