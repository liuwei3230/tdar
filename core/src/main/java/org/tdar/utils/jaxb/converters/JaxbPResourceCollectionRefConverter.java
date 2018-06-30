package org.tdar.utils.jaxb.converters;

import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tdar.core.bean.Persistable;
import org.tdar.core.bean.collection.ResourceCollection;
import org.tdar.core.dao.base.GenericDao;
import org.tdar.core.serialize.collection.PResourceCollection;
import org.tdar.core.service.ReflectionService;
import org.tdar.utils.PersistableUtils;

/**
 * This class is meant to help with serialization of ResourceCollections it provides a method for handling different cases separately:
 * * Internal Resource Collections
 * * Un-persisted Resource Collections
 * * Persisted Resource Collections
 * 
 * Persisted resource collections can be given a "reference" instead of a full XML representation.
 * 
 * @author abrin
 *
 */
@Component
public class JaxbPResourceCollectionRefConverter extends javax.xml.bind.annotation.adapters.XmlAdapter<Persistable, PResourceCollection> {
    @SuppressWarnings("unused")
    private final transient Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private GenericDao genericDao;

    @Autowired
    private ReflectionService reflectionService;

    public Persistable marshal(PResourceCollection d) throws Exception {
        if (d == null) {
            return null;
        }
        PResourceCollection rc = d;
        if (HibernateProxy.class.isAssignableFrom(d.getClass())) {
            rc = (PResourceCollection) ((HibernateProxy) d).getHibernateLazyInitializer().getImplementation();
        }
        if (PersistableUtils.isTransient(rc)) {
            return rc;
        }
        return new JAXBPersistableRef(rc.getId(), d.getClass());
    }

    @Override
    public PResourceCollection unmarshal(Persistable ref_) throws Exception {
        if (ref_ instanceof PResourceCollection) {
            return (PResourceCollection) ref_;
        }

        if (ref_ == null || !(ref_ instanceof JAXBPersistableRef)) {
            return null;
        }
        JAXBPersistableRef ref = (JAXBPersistableRef) ref_;
        PResourceCollection rc_ = new PResourceCollection();
        rc_.setId(ref.getId());
        // rc = null;
        return rc_;
    }

}
