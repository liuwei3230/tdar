package org.tdar.search.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tdar.core.bean.DisplayOrientation;
import org.tdar.core.bean.Indexable;
import org.tdar.core.bean.Obfuscatable;
import org.tdar.core.bean.collection.ResourceCollection;
import org.tdar.core.bean.coverage.LatitudeLongitudeBox;
import org.tdar.core.bean.entity.ResourceCreator;
import org.tdar.core.bean.entity.TdarUser;
import org.tdar.core.bean.resource.Resource;
import org.tdar.core.bean.resource.ResourceAccessType;
import org.tdar.core.bean.resource.Status;
import org.tdar.core.bean.resource.file.InformationResourceFile;
import org.tdar.core.configuration.TdarConfiguration;
import org.tdar.core.dao.resource.DatasetDao;
import org.tdar.core.serialize.coverage.PLatitudeLongitudeBox;
import org.tdar.core.serialize.resource.PInformationResource;
import org.tdar.core.serialize.resource.PProject;
import org.tdar.core.serialize.resource.PResource;
import org.tdar.core.service.Context;
import org.tdar.core.service.ObfuscationService;
import org.tdar.core.service.ProxyConstructionService;
import org.tdar.search.bean.SolrSearchObject;
import org.tdar.search.query.QueryFieldNames;
import org.tdar.search.query.SearchResultHandler;
import org.tdar.utils.PersistableUtils;

@Component
public class ProjectionTransformer<I extends Indexable> {

    private static final TdarConfiguration CONFIG = TdarConfiguration.getInstance();

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DatasetDao datasetDao;

    public boolean isProjected(SolrSearchObject<I> results) {
        if (CollectionUtils.isEmpty(results.getDocumentList())) {
            return true;
        }
        SolrDocument doc = results.getDocumentList().get(0);

        // we only start storing this properly in obsidian & we only project it in resources
        if (doc.getFieldValue(QueryFieldNames.SUBMITTER_ID) != null) {
            return true;
        }
        return false;
    }

    @Autowired
    ProxyConstructionService proxyConstructionService;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public I transformResource(SearchResultHandler<I> resultHandler, SolrDocument doc, I r, ObfuscationService obfuscationService) throws InstantiationException, IllegalAccessException {
        PResource r_ = ((Resource)r).getResourceType().getProxyClass().newInstance();
        // set basic fields
        Object value = doc.getFieldValue(QueryFieldNames.ID);
        if (value instanceof String) {
            r_.setId(Long.parseLong((String) value));
        } 
        if (value instanceof Long) {
            r_.setId((Long) value);
        }
        r_.setStatus(Status.valueOf((String) doc.getFieldValue(QueryFieldNames.STATUS)));
        r_.setTitle((String) doc.getFieldValue(QueryFieldNames.NAME));
        r_.setDescription((String) doc.getFieldValue(QueryFieldNames.DESCRIPTION));
        TdarUser user = resultHandler.getAuthenticatedUser();
        Context ctx = new Context(user);
        ctx.setAbleToSeeConfidentialFiles(false);
        // set collections
        logger.trace("begin collection");
        Collection<Long> collectionIds = (Collection<Long>) (Collection) doc.getFieldValues(QueryFieldNames.RESOURCE_COLLECTION_MANAGED_IDS);
        for (ResourceCollection rc : datasetDao.findAll(ResourceCollection.class, collectionIds)) {
            r_.getManagedResourceCollections().add(proxyConstructionService.createShellCollection( (ResourceCollection) rc, ctx));
        }

        Collection<Long> uCollectionIds = (Collection<Long>) (Collection) doc.getFieldValues(QueryFieldNames.RESOURCE_COLLECTION_UNMANAGED_IDS);
        for (ResourceCollection rc : datasetDao.findAll(ResourceCollection.class, uCollectionIds)) {
            r_.getUnmanagedResourceCollections().add(proxyConstructionService.createShellCollection( (ResourceCollection) rc, ctx));
        }

//        logger.trace("begin authorized users");
//        r_.getAuthorizedUsers().addAll(datasetDao.findAllAuthorizedUsersForResource(r_.getId()));

        logger.trace("begin submitter");
        // handle submitter
        Long submitterId = (Long) doc.getFieldValue(QueryFieldNames.SUBMITTER_ID);
        r_.setSubmitter(proxyConstructionService.createShellTdarUser(datasetDao.find(TdarUser.class, submitterId), ctx));

        // only display for map
        Collection<Long> llIds = (Collection<Long>) (Collection) doc.getFieldValues(QueryFieldNames.ACTIVE_LATITUDE_LONGITUDE_BOXES_IDS);
        Set<PLatitudeLongitudeBox> findAll = null;
        DisplayOrientation orientation = resultHandler.getOrientation();
        if (orientation == null) {
            orientation = DisplayOrientation.LIST_FULL;
        }
        if (orientation == DisplayOrientation.MAP) {
            logger.trace("begin latLong");
            findAll = proxyConstructionService.createShellLatitudeLongitue(datasetDao.findAll(LatitudeLongitudeBox.class, llIds), ctx);
            r_.getLatitudeLongitudeBoxes().addAll(findAll);
        }

        // creators
        Collection<Long> cIds = (Collection<Long>) (Collection) doc.getFieldValues(QueryFieldNames.RESOURCE_CREATOR_ROLE_IDS);
        logger.trace("{}: creator: {}", r_.getId(), cIds);
        if (orientation == DisplayOrientation.LIST_FULL) {
            logger.trace("begin resource creator");
            r_.getResourceCreators().addAll(proxyConstructionService.convertResourceCreators(datasetDao.findAll(ResourceCreator.class, cIds),ctx));
            logger.debug("{}", r_.getResourceCreators());
        }

        if (r_ instanceof PInformationResource) {
            // add file info
            PInformationResource ir = (PInformationResource) r_;

            String fieldValue = (String) doc.getFieldValue(QueryFieldNames.RESOURCE_ACCESS_TYPE);
            if (fieldValue != null) {
                ir.setTransientAccessType(ResourceAccessType.valueOf(fieldValue));
            }
            Collection<Long> fileIds = (Collection<Long>) (Collection) doc.getFieldValues(QueryFieldNames.FILE_IDS);
            if (orientation == DisplayOrientation.GRID || orientation == DisplayOrientation.MAP) {
                logger.trace("begin file");
                ir.getInformationResourceFiles().addAll(proxyConstructionService.createInformationResourceFiles(datasetDao.findAll(InformationResourceFile.class, fileIds), ctx));
            }
            logger.trace("begin project");

            // setup project
            String ptitle = (String) doc.getFieldValue(QueryFieldNames.PROJECT_TITLE);
            ir.setDate((Integer) doc.getFieldValue(QueryFieldNames.DATE));
            if (StringUtils.isNotBlank(ptitle)) {
                PProject project = new PProject();
                project.setTitle(ptitle);
                project.setId((Long) doc.getFieldValue(QueryFieldNames.PROJECT_ID));
                ir.setProject(project);
            }

            if (ir.isInheritingSpatialInformation()) {
                if (findAll == null) {
                    logger.trace("begin inherited LLB");
                    findAll = proxyConstructionService.createShellLatitudeLongitue(datasetDao.findAll(LatitudeLongitudeBox.class, llIds), ctx);
                }
                ir.getProject().getLatitudeLongitudeBoxes().addAll(findAll);
            }
            
        }

        logger.trace("begin obfuscation");

//        obfuscationService.getAuthenticationAndAuthorizationService().applyTransientViewableFlag(r_, user, collectionIds);
//
//        if (CONFIG.obfuscationInterceptorDisabled()
//                && PersistableUtils.isNullOrTransient(user)) {
//            obfuscationService.obfuscate((Obfuscatable) r_, user);
//        }
        logger.debug("{}",r_);
        return (I) r_;
    }

}
