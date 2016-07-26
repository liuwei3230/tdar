package org.tdar.search.index;

import java.util.List;

import org.tdar.core.bean.Indexable;
import org.tdar.core.bean.Persistable;

public class RightsContainer implements Persistable,Indexable {

    private static final long serialVersionUID = -1199913140126848387L;

    @Override
    public void setScore(Float score) {
    }

    @Override
    public Float getScore() {
        return null;
    }

    @Override
    public Long getId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setId(Long number) {
        
    }

    @Override
    public List<?> getEqualityFields() {
        return null;
    }

}
