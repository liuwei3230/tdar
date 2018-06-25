package org.tdar.balk.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.annotations.NamedQuery;
import org.springframework.stereotype.Component;
import org.tdar.balk.bean.PollType;

@Component
@NamedQuery(
        name = "cursor.latest",
        query = "select cursor from DropboxState where type=:type order by lastPolled desc")
public class CursorDao {

    @PersistenceContext
    private EntityManager manager;

    public String getLatestCursorFor(PollType type) {
        String query = "cursor.latest";
        Query query2 = manager.createQuery("select cursor from DropboxState where type=:type order by lastPolled desc");
        query2.setParameter("type", type);
        query2.setFirstResult(0);
        query2.setMaxResults(1);
        return (String) query2.getSingleResult();
    }


}
