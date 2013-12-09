package org.tdar.core.dao;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: jimdevos
 * Date: 12/4/13
 * Time: 4:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class TdarHibernateInterceptor extends EmptyInterceptor{

    Logger logger = LoggerFactory.getLogger(TdarHibernateInterceptor.class);

    @Override
    public boolean onSave(Object entity,
                          Serializable id,
                          Object[] state,
                          String[] propertyNames,
                          Type[] types) {
        int len = state.length;

        logger.trace("hibernate interceptor, reporting for duty");
        for(int i = 0; i < len; i++) {

            logger.trace("entclass:{} id:{} property:{} type:{} val:{}",
                    entity.getClass().getSimpleName(),
                    id,
                    propertyNames[i],
                    types[i],
                    state[i]
            );

            Type type = types[i];
            if(StringType.INSTANCE.equals(type)) {
                if("".equals(state[i])) {
                    logger.trace("setting empty string to null:{}.{}", entity.getClass().getSimpleName(), propertyNames[i]);
                    state[i] = null;
                }
            }
        }

        return false;

    }

}
