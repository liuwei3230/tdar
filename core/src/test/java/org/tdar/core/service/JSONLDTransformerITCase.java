package org.tdar.core.service;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.tdar.core.bean.AbstractIntegrationTestCase;
import org.tdar.core.bean.RelationType;
import org.tdar.core.bean.entity.Creator;
import org.tdar.core.bean.entity.Creator.CreatorType;
import org.tdar.core.bean.keyword.CultureKeyword;
import org.tdar.core.bean.keyword.ExternalKeywordMapping;
import org.tdar.core.bean.keyword.GeographicKeyword;
import org.tdar.core.bean.resource.Resource;
import org.tdar.core.serialize.entity.PCreator;
import org.tdar.core.serialize.keyword.PKeyword;
import org.tdar.core.serialize.resource.PResource;
import org.tdar.transform.jsonld.SchemaOrgCreatorTransformer;
import org.tdar.transform.jsonld.SchemaOrgKeywordTransformer;
import org.tdar.transform.jsonld.SchemaOrgResourceTransformer;

public class JSONLDTransformerITCase extends AbstractIntegrationTestCase {

    private static final String HTTP_WWW_TEST_COM = "http://www.test.com";

    protected transient Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SerializationService serializationService;
    @Autowired
    GenericService genericService;
    @Autowired
    ProxyConstructionService proxyConstructionService;
    
    @Test
    @Rollback
    public void testResources() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        SchemaOrgResourceTransformer transformer = new SchemaOrgResourceTransformer();

        for (Resource r : genericService.findAll(Resource.class)) {
            if (r == null) {
                continue;
            }
            logger.debug("//  {} - {}", r.getId(), r.getResourceType());
            PResource rr = proxyConstructionService.constructResource(r, r.getResourceType().getProxyClass(), null, true);
            logger.debug(transformer.convert(serializationService, rr));
        }
    }

    @Test
    @Rollback
    public void testJsonLDExtensions() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        SchemaOrgResourceTransformer transformer = new SchemaOrgResourceTransformer();
        Resource r = generateDocumentWithUser();
        GeographicKeyword gk = new GeographicKeyword();
        gk.setLabel("Petra");
        ExternalKeywordMapping map = new ExternalKeywordMapping();
        map.setRelation("http://www.petra.com");
        map.setRelationType(RelationType.DCTERMS_RELATION);
        gk.getAssertions().add(map);
        r.getGeographicKeywords().add(gk);
        genericService.saveOrUpdate(map);
        genericService.saveOrUpdate(gk);
        genericService.saveOrUpdate(r);
        PResource rr = proxyConstructionService.constructResource(r, r.getResourceType().getProxyClass(), null, true);
        String json = transformer.convert(serializationService, rr);
        logger.debug(json);
        assertTrue(json.contains("petra"));
    }

    @Test
    @Rollback
    public void testCreators() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        SchemaOrgCreatorTransformer transformer = new SchemaOrgCreatorTransformer();

        for (Creator<?> r : genericService.findAll(Creator.class)) {
            logger.debug("//  {} - {}", r.getId(), r.getCreatorType());
            PCreator construct = proxyConstructionService.construct(r, Creator.class, null, false);
            String json = transformer.convert(serializationService, construct, null);
            logger.debug(json);
            if (r.getCreatorType() == CreatorType.PERSON) {
                assertTrue("contains type", StringUtils.containsIgnoreCase(json, r.getCreatorType().name()));
            } else {
                assertTrue("contains type", StringUtils.containsIgnoreCase(json, "Organization"));
            }
            // have to remove quotes
            assertTrue("contains name", StringUtils.contains(json, r.getProperName().replace("\"", "")));
        }
    }

    @Test
    @Rollback
    public void testKeywords() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        SchemaOrgKeywordTransformer transformer = new SchemaOrgKeywordTransformer();
        CultureKeyword random = genericService.find(CultureKeyword.class, 4L);
        ExternalKeywordMapping assertion = new ExternalKeywordMapping(HTTP_WWW_TEST_COM, RelationType.DCTERMS_IS_REPLACED_BY);
        random.getAssertions().add(assertion);
        PKeyword rr = proxyConstructionService.constructKeyword(random);
        String json = transformer.convert(serializationService, rr);
        logger.debug(json);
        assertTrue("json contains URL", json.contains(HTTP_WWW_TEST_COM));
        assertTrue("json contains short term", json.contains(RelationType.DCTERMS_IS_REPLACED_BY.getShortTerm()));
    }
}
