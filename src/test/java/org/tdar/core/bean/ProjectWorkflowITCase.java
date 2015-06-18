package org.tdar.core.bean;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.tdar.core.dao.GenericDao;
import org.tdar.core.service.GenericService;

public class ProjectWorkflowITCase extends AbstractIntegrationTestCase {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    GenericService genericService;

    @Autowired
    GenericDao genericDao;


}
