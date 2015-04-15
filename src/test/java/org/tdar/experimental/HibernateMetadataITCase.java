package org.tdar.experimental;

import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;
import org.junit.Test;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.tdar.core.bean.AbstractIntegrationTestCase;
import org.tdar.core.bean.resource.Document;

import javax.sql.DataSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;

/**
 * A playground for using hibernate's metadata API
 *
 * http://docs.jboss.org/hibernate/core/4.3/manual/en-US/html/ch11.html#objectstate-metadata
 */
public class HibernateMetadataITCase extends AbstractIntegrationTestCase{

    @Autowired
    SessionFactory sf;

    @Autowired
    @Qualifier("tdarMetadataDataSource")
    DataSource ds;

    @Test
    public void testOneToManyScan() {
        ClassMetadata meta = sf.getClassMetadata(Document.class);
        for(Type type : meta.getPropertyTypes()) {
            getLogger().debug(
                    "assoc:{}\t col:{}\t name:{}",
                    type.isAssociationType(), type.isAssociationType(), type.getName());
        }
    }
}
