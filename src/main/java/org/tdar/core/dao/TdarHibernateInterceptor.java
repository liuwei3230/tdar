package org.tdar.core.dao;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * User: jimdevos
 * Date: 12/4/13
 * Time: 4:06 PM
 */
public class TdarHibernateInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = 7011287186638725852L;

    Logger logger = LoggerFactory.getLogger(TdarHibernateInterceptor.class);

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        int len = state.length;

        logger.trace("hibernate interceptor, reporting for duty");
        for(int i = 0; i < len; i++) {

            String className = entity.getClass().getSimpleName();
            if (logger.isTraceEnabled()) {
                logger.trace("entclass:{} id:{} property:{} type:{} val:{}", className, id, propertyNames[i], types[i], state[i] );
            }
            
            Type type = types[i];
            if(StringType.INSTANCE.equals(type)) {
                if("".equals(state[i])) {
                    if (logger.isTraceEnabled()) {
                      logger.trace("setting empty string to null:{}.{}", className, propertyNames[i]);
                    }
                    state[i] = null;
                }
            }
        }

        return false;

    }

}
