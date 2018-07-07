package org.tdar.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tdar.core.bean.Persistable;
import org.tdar.core.bean.citation.RelatedComparativeCollection;
import org.tdar.core.bean.citation.SourceCollection;
import org.tdar.core.bean.collection.ResourceCollection;
import org.tdar.core.bean.coverage.CoverageDate;
import org.tdar.core.bean.coverage.LatitudeLongitudeBox;
import org.tdar.core.bean.entity.AuthorizedUser;
import org.tdar.core.bean.entity.Creator;
import org.tdar.core.bean.entity.Creator.CreatorType;
import org.tdar.core.bean.entity.permissions.Permissions;
import org.tdar.core.bean.integration.DataIntegrationWorkflow;
import org.tdar.core.bean.entity.Institution;
import org.tdar.core.bean.entity.Person;
import org.tdar.core.bean.entity.ResourceCreator;
import org.tdar.core.bean.entity.TdarUser;
import org.tdar.core.bean.keyword.HierarchicalKeyword;
import org.tdar.core.bean.keyword.Keyword;
import org.tdar.core.bean.resource.BookmarkedResource;
import org.tdar.core.bean.resource.CategoryVariable;
import org.tdar.core.bean.resource.CodingRule;
import org.tdar.core.bean.resource.CodingSheet;
import org.tdar.core.bean.resource.Dataset;
import org.tdar.core.bean.resource.Document;
import org.tdar.core.bean.resource.InformationResource;
import org.tdar.core.bean.resource.Ontology;
import org.tdar.core.bean.resource.OntologyNode;
import org.tdar.core.bean.resource.Resource;
import org.tdar.core.bean.resource.ResourceAnnotation;
import org.tdar.core.bean.resource.ResourceNote;
import org.tdar.core.bean.resource.datatable.DataTable;
import org.tdar.core.bean.resource.datatable.DataTableColumn;
import org.tdar.core.bean.resource.file.InformationResourceFile;
import org.tdar.core.dao.base.GenericDao;
import org.tdar.core.dao.external.auth.InternalTdarRights;
import org.tdar.core.exception.TdarAuthorizationException;
import org.tdar.core.serialize.citation.PRelatedComparativeCollection;
import org.tdar.core.serialize.citation.PSourceCollection;
import org.tdar.core.serialize.collection.PResourceCollection;
import org.tdar.core.serialize.coverage.PCoverageDate;
import org.tdar.core.serialize.coverage.PLatitudeLongitudeBox;
import org.tdar.core.serialize.entity.PAuthorizedUser;
import org.tdar.core.serialize.entity.PCreator;
import org.tdar.core.serialize.entity.PInstitution;
import org.tdar.core.serialize.entity.PPerson;
import org.tdar.core.serialize.entity.PResourceCreator;
import org.tdar.core.serialize.entity.PTdarUser;
import org.tdar.core.serialize.integration.PDataIntegrationWorkflow;
import org.tdar.core.serialize.keyword.PCultureKeyword;
import org.tdar.core.serialize.keyword.PExternalKeywordMapping;
import org.tdar.core.serialize.keyword.PGeographicKeyword;
import org.tdar.core.serialize.keyword.PHierarchicalKeyword;
import org.tdar.core.serialize.keyword.PInvestigationType;
import org.tdar.core.serialize.keyword.PKeyword;
import org.tdar.core.serialize.keyword.PMaterialKeyword;
import org.tdar.core.serialize.keyword.POtherKeyword;
import org.tdar.core.serialize.keyword.PSiteNameKeyword;
import org.tdar.core.serialize.keyword.PSiteTypeKeyword;
import org.tdar.core.serialize.keyword.PTemporalKeyword;
import org.tdar.core.serialize.resource.PBookmarkedResource;
import org.tdar.core.serialize.resource.PCategoryVariable;
import org.tdar.core.serialize.resource.PCodingRule;
import org.tdar.core.serialize.resource.PCodingSheet;
import org.tdar.core.serialize.resource.PDataset;
import org.tdar.core.serialize.resource.PDocument;
import org.tdar.core.serialize.resource.PInformationResource;
import org.tdar.core.serialize.resource.POntology;
import org.tdar.core.serialize.resource.POntologyNode;
import org.tdar.core.serialize.resource.PProject;
import org.tdar.core.serialize.resource.PResource;
import org.tdar.core.serialize.resource.PResourceAnnotation;
import org.tdar.core.serialize.resource.PResourceAnnotationKey;
import org.tdar.core.serialize.resource.PResourceNote;
import org.tdar.core.serialize.resource.datatable.PDataTable;
import org.tdar.core.serialize.resource.datatable.PDataTableColumn;
import org.tdar.core.serialize.resource.datatable.PDataTableRelationship;
import org.tdar.core.serialize.resource.file.PInformationResourceFile;
import org.tdar.core.serialize.resource.file.PInformationResourceFileVersion;
import org.tdar.core.service.external.AuthorizationService;
import org.tdar.core.service.resource.ResourceService;

@Service
public class ProxyConstructionService {

    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private ResourceService resourceService;

    @Autowired
    private GenericDao genericDao;
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());


    @Transactional(readOnly = true)
    public <R extends PResource, T extends Resource> R constructResource(T resource, Class<R> cls,
            TdarUser viewer,
            boolean forceObfuscate)
            throws InstantiationException, IllegalAccessException {
        if (resource == null) {
            return null;
        }
        return constructResource(resource, cls.newInstance(), viewer, forceObfuscate);
    }

    @Transactional(readOnly = true)
    public <R extends PResource, T extends Resource> R constructResource(T resource, R r,
            TdarUser viewer,
            boolean forceObfuscate)
            throws InstantiationException, IllegalAccessException {
        boolean viewConfidentialinfo = authorizationService.canDo(viewer, resource, InternalTdarRights.VIEW_AND_DOWNLOAD_CONFIDENTIAL_INFO, Permissions.VIEW_ALL);
        logger.debug("user: {}/ viewconf:{}", viewer, viewConfidentialinfo);
        Context ctx = new Context(viewer);
        ctx.setAdmin(false);
        ctx.setAbleToSeeConfidentialFiles(viewConfidentialinfo);
        ctx.setViewUnobfuscatedLat(viewConfidentialinfo);
        ctx.setPersonEmailObfuscated(true);
        ctx.setInstitutionEmailObfuscated(true);
        r.setCultureKeywords(convertKeywords(resource, resource.getActiveCultureKeywords(), PCultureKeyword.class, ctx));
        r.setSiteTypeKeywords(convertKeywords(resource, resource.getActiveSiteTypeKeywords(), PSiteTypeKeyword.class, ctx));
        r.setOtherKeywords(convertKeywords(resource, resource.getActiveOtherKeywords(), POtherKeyword.class, ctx));
        r.setSiteNameKeywords(convertKeywords(resource, resource.getActiveSiteNameKeywords(), PSiteNameKeyword.class, ctx));
        r.setMaterialKeywords(convertKeywords(resource, resource.getActiveMaterialKeywords(), PMaterialKeyword.class, ctx));
        r.setInvestigationTypes(convertKeywords(resource, resource.getActiveInvestigationTypes(), PInvestigationType.class, ctx));
        r.setGeographicKeywords(convertKeywords(resource, resource.getActiveGeographicKeywords(), PGeographicKeyword.class, ctx));
        r.setManagedGeographicKeywords(convertKeywords(resource, resource.getActiveManagedGeographicKeywords(), PGeographicKeyword.class, ctx));
        r.setTemporalKeywords(convertKeywords(resource, resource.getActiveTemporalKeywords(), PTemporalKeyword.class, ctx));
        // r.setResourceRevisionLog((resource.getResourceRevisionLog(());
        r.setSourceCollections(convertSourceCollections(resource.getActiveSourceCollections(), ctx));
        r.setResourceNotes(convertResourceNotes(resource.getActiveResourceNotes(), ctx));
        r.setRelatedComparativeCollections(convertRelatedComparativeCollections(resource.getActiveRelatedComparativeCollections(), ctx));
        r.setId(resource.getId());
        r.setTitle(resource.getTitle());
        r.setDescription(resource.getDescription());
        r.setDateCreated(resource.getDateCreated());
        r.setSubmitter(convertUser(resource.getSubmitter(), ctx));
        r.setLatitudeLongitudeBoxes(convertLatLong(resource.getActiveLatitudeLongitudeBoxes(), ctx));
        r.setResourceType(resource.getResourceType());
        r.setUrl(resource.getUrl());
        r.setUpdatedBy(convertUser(resource.getUpdatedBy(), ctx));
        r.setDateUpdated(resource.getDateUpdated());
        r.setStatus(resource.getStatus());
        r.setResourceCreators(convertResourrceCreator(resource.getActiveResourceCreators(), ctx));
        r.setResourceAnnotations(convertResourceAnnotation(resource.getActiveResourceAnnotations()));
        r.setCoverageDates(convertCoverageDates(resource.getActiveCoverageDates()));
        r.setUnmanagedResourceCollections(convertResourceCollections(resource.getUnmanagedResourceCollections(), 1, ctx));
        r.setManagedResourceCollections(convertResourceCollections(resource.getManagedResourceCollections(), 1, ctx));
        r.setExternalId(resource.getExternalId());
        r.setTransientAccessCount(resource.getTransientAccessCount());
        r.setUploader(convertUser(resource.getUploader(), ctx));
        r.setAccount(resource.getAccount());
        r.setPreviousStatus(resource.getPreviousStatus());
        r.setSpaceInBytesUsed(resource.getSpaceInBytesUsed());
        r.setFilesUsed(resource.getFilesUsed());
        r.setPreviousSpaceInBytesUsed(resource.getPreviousSpaceInBytesUsed());
        r.setPreviousFilesUsed(resource.getPreviousFilesUsed());
        r.setBookmarkedResources(createBookmarked(resource.getBookmarkedResources()));
        r.setFormattedDescription(resource.getFormattedDescription());
        r.setAuthorizedUsers(convertAuthorizedUsers(resource.getAuthorizedUsers(), ctx));

        if (resource instanceof InformationResource) {
            PInformationResource ir = (PInformationResource) r;
            InformationResource iresource = (InformationResource) resource;
            ir.setMetadataLanguage(iresource.getMetadataLanguage());
            ir.setResourceLanguage(iresource.getResourceLanguage());
            ir.setCopyrightHolder(createCreator(iresource.getCopyrightHolder(), ctx));
            ir.setLicenseType(iresource.getLicenseType());
            ir.setLicenseText(iresource.getLicenseText());
            ir.setDate(iresource.getDate());
            ir.setDateNormalized(iresource.getDateNormalized());
            ir.setResourceProviderInstitution(createInstitution(iresource.getResourceProviderInstitution(), ctx));
            ir.setProject(constructResource(iresource.getProject(), PProject.class, ctx.getUser(), false));
            ir.setExternalReference(iresource.isExternalReference());
            ir.setLastUploaded(iresource.getLastUploaded());
            ir.setInformationResourceFiles(convertInformationResourceFiles(iresource.getInformationResourceFiles(), ctx));
            ir.setInheritingInvestigationInformation(iresource.isInheritingInvestigationInformation());
            ir.setInheritingSiteInformation(iresource.isInheritingSiteInformation());
            ir.setInheritingMaterialInformation(iresource.isInheritingMaterialInformation());
            ir.setInheritingOtherInformation(iresource.isInheritingOtherInformation());
            ir.setInheritingCulturalInformation(iresource.isInheritingCulturalInformation());
            ir.setInheritingSpatialInformation(iresource.isInheritingSpatialInformation());
            ir.setInheritingTemporalInformation(iresource.isInheritingTemporalInformation());
            ir.setCopyLocation(iresource.getCopyLocation());
            // ir.setMappedDataKeyColumn(iresource.getMappedDataKeyColumn());
            ir.setMappedDataKeyValue(iresource.getMappedDataKeyValue());
            ir.setInheritingNoteInformation(iresource.isInheritingNoteInformation());
            ir.setInheritingIdentifierInformation(iresource.isInheritingIdentifierInformation());
            ir.setInheritingCollectionInformation(iresource.isInheritingCollectionInformation());
            ir.setPublisher(createInstitution(iresource.getPublisher(), ctx));
            ir.setPublisherLocation(iresource.getPublisherLocation());
            ir.setInheritingIndividualAndInstitutionalCredit(iresource.isInheritingIndividualAndInstitutionalCredit());
            ir.setDoi(iresource.getDoi());
            // ir.setFileProxies(iresource.getFileProxies());
            ir.setTransientAccessType(iresource.getTransientAccessType());

            if (ctx.isMappedMetadaataIncluded()) {
                Map<DataTableColumn, String> map = resourceService.getMappedDataForInformationResource((InformationResource) resource, true);
                map.entrySet().forEach(e -> {
                    ir.getMappedData().put(convertDataTableColumn(e.getKey()), e.getValue());

                });
            }

            if (ir instanceof PDataset) {
                PDataset doc = (PDataset) ir;
                Dataset doc_ = (Dataset) iresource;
                doc.setDataTables(convertDataTables(doc_.getDataTables()));
                doc.setRelationships(convertRelationships(doc_.getRelationships()));
            }
            if (ir instanceof PCodingSheet) {
                PCodingSheet doc = (PCodingSheet) ir;
                CodingSheet doc_ = (CodingSheet) iresource;
                doc.setCodingRules(convertCodingRules(doc_.getCodingRules()));
                doc.setCategoryVariable(convertCategoryVariable(doc_.getCategoryVariable()));
                doc.setAssociatedDataTableColumns(convertDataTableColumns(doc_.getAssociatedDataTableColumns()));
                doc.setDefaultOntology(createShellResource(doc_.getDefaultOntology(), POntology.class));
                doc.setGenerated(doc_.isGenerated());
            }
            if (ir instanceof POntology) {
                POntology doc = (POntology) ir;
                Ontology doc_ = (Ontology) iresource;
                doc.setCategoryVariable(convertCategoryVariable(doc_.getCategoryVariable()));
                doc_.getOntologyNodes().forEach(on -> {
                    doc.getOntologyNodes().add(convertOntologyNode(on));
                });
            }
            if (ir instanceof PDocument) {
                PDocument doc = (PDocument) ir;
                Document doc_ = (Document) iresource;
                doc.setDocumentType(doc_.getDocumentType());
                doc.setDocumentType(doc_.getDocumentType());
                doc.setSeriesName(doc_.getSeriesName());
                doc.setSeriesNumber(doc_.getSeriesNumber());
                doc.setVolume(doc_.getVolume());
                doc.setEdition(doc_.getEdition());
                doc.setIsbn(doc_.getIsbn());
                doc.setNumberOfVolumes(doc_.getNumberOfVolumes());
                doc.setNumberOfPages(doc_.getNumberOfPages());
                doc.setStartPage(doc_.getStartPage());
                doc.setEndPage(doc_.getEndPage());
                doc.setBookTitle(doc_.getBookTitle());
                doc.setIssn(doc_.getIssn());
                doc.setJournalName(doc_.getJournalName());
                doc.setJournalNumber(doc_.getJournalNumber());
                doc.setDegree(doc_.getDegree());
                doc.setDocumentSubType(doc_.getDocumentSubType());

            }

        }

        return r;
    }

    private PDataTableColumn convertDataTableColumn(DataTableColumn dtc_) {
        PDataTableColumn c = new PDataTableColumn();
        c.setName(dtc_.getName());
        c.setColumnDataType(dtc_.getColumnDataType());
        c.setColumnEncodingType(dtc_.getColumnEncodingType());
        c.setMeasurementUnit(dtc_.getMeasurementUnit());
        c.setDescription(dtc_.getDescription());
        c.setDisplayName(dtc_.getDisplayName());
        c.setLength(dtc_.getLength());
        c.setDelimiterValue(dtc_.getDelimiterValue());
        c.setIgnoreFileExtension(dtc_.isIgnoreFileExtension());
        c.setVisible(dtc_.isVisible());
        c.setMappingColumn(dtc_.isMappingColumn());
        c.setCategoryVariable(convertCategoryVariable(dtc_.getCategoryVariable()));
        c.setTempSubCategoryVariable(convertCategoryVariable(dtc_.getTempSubCategoryVariable()));
        c.setDefaultCodingSheet(createShellResource(dtc_.getDefaultCodingSheet(), PCodingSheet.class));
        c.setMappedOntology(createShellResource(dtc_.getMappedOntology(), POntology.class));
        c.setTransientOntology(createShellResource(dtc_.getTransientOntology(), POntology.class));
        c.setImportOrder(dtc_.getImportOrder());
        return c;
    }

    private Set<PDataTableColumn> convertDataTableColumns(Collection<DataTableColumn> list) {
        HashSet<PDataTableColumn> toReturn = new HashSet<>();
        list.forEach(dtc_ -> {
            toReturn.add(convertDataTableColumn(dtc_));
        });
        return toReturn;
    }

    private Set<PCodingRule> convertCodingRules(Set<CodingRule> codingRules) {
        if (CollectionUtils.isEmpty(codingRules)) {
            return Collections.EMPTY_SET;
        }
        Set<PCodingRule> toReturn = new HashSet<>();
        codingRules.forEach(cr_ -> {
            PCodingRule cr = new PCodingRule();
            cr.setCode(cr_.getCode());
            cr.setTerm(cr_.getTerm());
            cr.setDescription(cr_.getDescription());
            cr.setOntologyNode(convertOntologyNode(cr_.getOntologyNode()));
            cr.setCount(cr_.getCount());
            toReturn.add(cr);
        });
        return toReturn;
    }

    public POntologyNode convertOntologyNode(OntologyNode ontologyNode) {
        if (ontologyNode == null) {
            return null;
        }
        POntologyNode n = new POntologyNode();
        n.setIntervalStart(ontologyNode.getIntervalStart());
        n.setIntervalEnd(ontologyNode.getIntervalEnd());
        n.setIri(ontologyNode.getIri());
        n.setUri(ontologyNode.getUri());
        n.setIndex(ontologyNode.getIndex());
        n.setDisplayName(ontologyNode.getDisplayName());
        n.setDescription(ontologyNode.getDescription());
        n.setImportOrder(ontologyNode.getImportOrder());
        n.setSynonyms(ontologyNode.getSynonyms());
        n.setParent(ontologyNode.isParent());
        n.setSynonym(ontologyNode.isSynonym());
        n.setMappedDataValues(ontologyNode.isMappedDataValues());
        return n;
    }

    private Set<PDataTableRelationship> convertRelationships(Set<org.tdar.core.bean.resource.datatable.DataTableRelationship> relationships) {
        // TODO Auto-generated method stub
        return null;
    }

    private Set<PDataTable> convertDataTables(Set<DataTable> dataTables) {
        if (CollectionUtils.isEmpty(dataTables)) {
            return Collections.EMPTY_SET;
        }
        Set<PDataTable> toReturn = new HashSet<>();
        dataTables.forEach(dt_ -> {
            PDataTable table = convertDataTable(dt_);
            toReturn.add(table);
        });
        return toReturn;
    }

    private PDataTable convertDataTable(DataTable dt_) {
        PDataTable dt = new PDataTable();
        dt.setDescription(dt_.getDescription());
        dt.setDisplayName(dt_.getDisplayName());
        dt.setId(dt_.getId());
        dt.setImportOrder(dt_.getImportOrder());
        dt.setDataTableColumns(new ArrayList<>(convertDataTableColumns(dt_.getDataTableColumns())));
        return dt;
    }

    public <P extends PResource, R extends Resource> P createShellResource(R r, Class<P> class1) {
        try {
            if (r == null) {
                if (PProject.class.isAssignableFrom(class1)) {
                    return (P) PProject.NULL;
                }
                return null;
            }
            P p = class1.newInstance();
            p.setId(r.getId());
            p.setTitle(r.getTitle());
            p.setDateCreated(r.getDateCreated());
            p.setDescription(r.getDescription());
            p.setUrl(r.getUrl());
            return p;
        } catch (InstantiationException | IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private PCategoryVariable convertCategoryVariable(CategoryVariable categoryVariable) {
        if (categoryVariable == null) {
            return null;
        }
        PCategoryVariable pcv = new PCategoryVariable();
        pcv.setId(categoryVariable.getId());
        pcv.setLabel(categoryVariable.getLabel());
        pcv.setName(categoryVariable.getName());
        pcv.setType(categoryVariable.getType());
        if (categoryVariable.getParent() != null) {
            pcv.setParent(convertCategoryVariable(categoryVariable.getParent()));
        }
        return pcv;
    }

    private Set<PInformationResourceFile> convertInformationResourceFiles(
            Collection<InformationResourceFile> informationResourceFiles, Context ctx) {
        if (CollectionUtils.isEmpty(informationResourceFiles)) {
            return Collections.EMPTY_SET;
        }
        Set<PInformationResourceFile> toReturn = new HashSet<>();
        informationResourceFiles.forEach(irf_ -> {
            if (!irf_.isDeleted()) {
                PInformationResourceFile irf = new PInformationResourceFile();
                irf.setViewable(true);
                if (irf_.isConfidential() && !ctx.isAbleToSeeConfidentialFiles()) {
                    irf_.setViewable(false);
                }

                // irf.setInformationResource(irf_.getInformationResource());
                irf.setInformationResourceFileType(irf_.getInformationResourceFileType());
                irf.setLatestVersion(irf_.getLatestVersion());
                // irf.setInformationResourceFileVersions(irf_.getInformationResourceFileVersions());
                irf.setStatus(irf_.getStatus());
                irf.setViewable(irf_.isViewable());
                irf.setTransientDownloadCount(irf_.getTransientDownloadCount());
                irf.setNumberOfParts(irf_.getNumberOfParts());
                irf.setWorkflowContext(irf_.getWorkflowContext());
                irf.setDateMadePublic(irf_.getDateMadePublic());
                irf.setRestriction(irf_.getRestriction());
                irf.setErrorMessage(irf_.getErrorMessage());
                irf.setPartOfComposite(irf_.isPartOfComposite());
                irf.setDescription(irf_.getDescription());
                irf.setFileCreatedDate(irf_.getFileCreatedDate());
                irf.setFilename(irf_.getFilename());
                irf.setDeleted(irf_.isDeleted());
                irf.setPreservationNote(irf_.getPreservationNote());
                // irf.setPreservationStatus(irf_.getPreservationStatus());
                irf_.getInformationResourceFileVersions().forEach(irfv_ -> {
                    PInformationResourceFileVersion irfv = new PInformationResourceFileVersion();
                    irfv.setInformationResourceFile(irf);
                    irfv.setId(irfv_.getId());
                    irfv.setFilename(irfv_.getFilename());
                    irfv.setVersion(irfv_.getVersion());
                    irfv.setMimeType(irfv_.getMimeType());
                    irfv.setFormat(irfv_.getFormat());
                    irfv.setExtension(irfv_.getExtension());
                    irfv.setPremisId(irfv_.getPremisId());
                    irfv.setFilestoreId(irfv_.getFilestoreId());
                    irfv.setChecksum(irfv_.getChecksum());
                    irfv.setChecksumType(irfv_.getChecksumType());
                    irfv.setDateCreated(irfv_.getDateCreated());
                    irfv.setFileType(irfv_.getFileType());
                    irfv.setFileVersionType(irfv_.getFileVersionType());
                    irfv.setWidth(irfv_.getWidth());
                    irfv.setHeight(irfv_.getHeight());
                    irfv.setFileLength(irfv_.getFileLength());
                    irfv.setPath(irfv_.getPath());
                    irfv.setInformationResourceFileId(irfv_.getInformationResourceFileId());
                    irfv.setInformationResourceId(irfv_.getInformationResourceId());
                    irfv.setViewable(irfv_.isViewable());
                    irfv.setTotalTime(irfv_.getTotalTime());
                    irfv.setUncompressedSizeOnDisk(irfv_.getUncompressedSizeOnDisk());
                    irfv.setPrimaryFile(irfv_.isPrimaryFile());
                    irfv.setTransientFile(irfv_.getTransientFile());
                    irf.getInformationResourceFileVersions().add(irfv);
                });
                toReturn.add(irf);
            }

        });
        return toReturn;
    }

    private Set<PRelatedComparativeCollection> convertRelatedComparativeCollections(
            Set<RelatedComparativeCollection> relatedComparativeCollections, Context ctx) {
        if (CollectionUtils.isEmpty(relatedComparativeCollections)) {
            return Collections.EMPTY_SET;
        }
        Set<PRelatedComparativeCollection> toReturn = new HashSet<>();
        relatedComparativeCollections.forEach(rcc_ -> {
            PRelatedComparativeCollection rcc = new PRelatedComparativeCollection();
            rcc.setId(rcc_.getId());
            rcc.setText(rcc_.getText());
            toReturn.add(rcc);
        });
        return toReturn;
    }

    private Set<PResourceNote> convertResourceNotes(Set<ResourceNote> resourceNotes, Context ctx) {
        if (CollectionUtils.isEmpty(resourceNotes)) {
            return Collections.EMPTY_SET;
        }
        Set<PResourceNote> toReturn = new HashSet<>();
        resourceNotes.forEach(rcc_ -> {
            PResourceNote rcc = new PResourceNote();
            rcc.setId(rcc_.getId());
            rcc.setNote(rcc_.getNote());
            rcc.setType(rcc_.getType());
            toReturn.add(rcc);
        });
        return toReturn;
    }

    private Set<PSourceCollection> convertSourceCollections(Set<SourceCollection> sourceCollections, Context ctx) {
        if (CollectionUtils.isEmpty(sourceCollections)) {
            return Collections.EMPTY_SET;
        }
        Set<PSourceCollection> toReturn = new HashSet<>();
        sourceCollections.forEach(rcc_ -> {
            PSourceCollection rcc = new PSourceCollection();
            rcc.setId(rcc_.getId());
            rcc.setText(rcc_.getText());
            toReturn.add(rcc);
        });
        return toReturn;
    }

    private Set<PAuthorizedUser> convertAuthorizedUsers(Set<AuthorizedUser> authorizedUsers, Context ctx) {
        if (CollectionUtils.isEmpty(authorizedUsers)) {
            return Collections.EMPTY_SET;
        }
        Set<PAuthorizedUser> toReturn = new HashSet<>();
        authorizedUsers.forEach(au_ -> {
            PAuthorizedUser au = new PAuthorizedUser();
            au.setGeneralPermission(au_.getGeneralPermission());
            au.setUser(convertUser(au_.getUser(), ctx));
            toReturn.add(au);
        });
        return toReturn;
    }

    private Set<PBookmarkedResource> createBookmarked(Set<BookmarkedResource> bookmarkedResources) {
        // TODO Auto-generated method stub
        return null;
    }

    private Set<PResourceCollection> convertResourceCollections(Set<ResourceCollection> collections, int depth, Context ctx) {
        if (CollectionUtils.isEmpty(collections)) {
            return Collections.EMPTY_SET;
        }
        Set<PResourceCollection> toReturn = new HashSet<>();
        collections.forEach(rc_ -> {
            PResourceCollection rc = convertResourceCollection(rc_, depth, ctx);
            if ((rc_.isActive() || rc_.isDraft()) && rc != null) {
                toReturn.add(rc);
            }
        });
        return toReturn;
    }

    private PResourceCollection convertResourceCollection(ResourceCollection rc_, int depth_, Context ctx) {
        int depth = depth_ - 1;
        if (depth < 0 || rc_ == null) {
            return null;
        }

        boolean viewable = authorizationService.canView(ctx.getUser(), rc_);
        logger.debug("viewable: {} / {} / {}", viewable, ctx.getUser(), rc_);
        if (!viewable) {
            return null;
        }
        PResourceCollection rc = new PResourceCollection();
        rc.setAlternateParent(convertResourceCollection(rc_.getAlternateParent(), depth, ctx));
        rc.setParent(convertResourceCollection(rc_.getParent(), depth, ctx));
        rc.setHidden(rc_.isHidden());
        rc.setId(rc_.getId());
        rc.setName(rc_.getName());
        rc.setDescription(rc_.getDescription());
        rc.setFormattedDescription(rc_.getFormattedDescription());
        rc.setSortBy(rc_.getSortBy());
        rc.setOrientation(rc_.getOrientation());
        rc.setSecondarySortBy(rc_.getSecondarySortBy());
        rc.setAuthorizedUsers(convertAuthorizedUsers(rc_.getAuthorizedUsers(), ctx));
        rc.setOwner(convertUser(rc_.getOwner(), ctx));
        rc.setDateCreated(rc_.getDateCreated());
        rc.setDateUpdated(rc_.getDateUpdated());
        rc.setUpdater(convertUser(rc_.getUpdater(), ctx));
        rc.setResourceIds(rc_.getResourceIds());
        rc.setSystemManaged(rc_.isSystemManaged());
        rc.setStatus(rc_.getStatus());
        rc.setParentIds(rc_.getParentIds());
        rc.setAlternateParentIds(rc_.getAlternateParentIds());
        // rc.setTransientChildren(rc_.getTransientChildren());
        rc.setUnmanagedResourceIds(rc_.getUnmanagedResourceIds());
        rc.setVerified(rc_.getVerified());
        return rc;
    }

    private Set<PCoverageDate> convertCoverageDates(Set<CoverageDate> coverageDates) {
        if (CollectionUtils.isEmpty(coverageDates)) {
            return Collections.EMPTY_SET;
        }
        Set<PCoverageDate> toReturn = new HashSet<>();
        coverageDates.forEach(cd_ -> {
            PCoverageDate cd = new PCoverageDate();
            cd.setDateType(cd_.getDateType());
            cd.setDescription(cd_.getDescription());
            cd.setStartDate(cd_.getStartDate());
            cd.setEndDate(cd_.getEndDate());
            cd.setStartDateApproximate(cd_.isStartDateApproximate());
            cd.setEndDateApproximate(cd_.isEndDateApproximate());
            toReturn.add(cd);
        });
        return toReturn;
    }

    private Set<PResourceAnnotation> convertResourceAnnotation(Set<ResourceAnnotation> resourceAnnotations) {
        if (CollectionUtils.isEmpty(resourceAnnotations)) {
            return Collections.EMPTY_SET;
        }
        Set<PResourceAnnotation> toReturn = new HashSet<>();
        resourceAnnotations.forEach(ra_ -> {
            PResourceAnnotation ra = new PResourceAnnotation();
            ra.setId(ra_.getId());
            PResourceAnnotationKey rak = new PResourceAnnotationKey();
            rak.setAnnotationDataType(ra_.getResourceAnnotationKey().getAnnotationDataType());
            rak.setId(ra_.getResourceAnnotationKey().getId());
            rak.setFormatString(ra_.getResourceAnnotationKey().getFormatString());
            rak.setKey(ra_.getResourceAnnotationKey().getKey());
            ra.setResourceAnnotationKey(rak);
            ra.setValue(ra_.getValue());
        });
        return toReturn;
    }

    private Set<PResourceCreator> convertResourrceCreator(Collection<ResourceCreator> resourceCreators, Context ctx) {
        if (CollectionUtils.isEmpty(resourceCreators)) {
            return Collections.EMPTY_SET;
        }
        Set<PResourceCreator> toReturn = new HashSet<>();
        resourceCreators.forEach(rc_ -> {
            Creator creator = rc_.getCreator();
            if (creator.isActive() || creator.isDuplicate()) {
                PResourceCreator rc = new PResourceCreator();
                rc.setId(rc_.getId());
                rc.setRole(rc_.getRole());
                rc.setSequenceNumber(rc_.getSequenceNumber());
                rc.setCreator(createCreator(creator, ctx));
                toReturn.add(rc);
            }
        });
        return toReturn;
    }

    private PCreator<?> createCreator(Creator<?> creator, Context ctx) {
        if (creator == null) {
            return null;
        }
        if (creator.getCreatorType() == CreatorType.PERSON) {
            return createPerson((Person) creator, ctx);
        } else {
            return createInstitution((Institution) creator, ctx);
        }
    }

    private PPerson createPerson(Person creator, Context ctx) {
        if (!creator.isActive() && !creator.isDuplicate() || creator == null) {
            return null;
        }
        PPerson person = new PPerson();
        updateperson(creator, person, ctx);
        return person;
    }

    private void updateperson(Person creator, PPerson person, Context ctx) {
        person.setFirstName(creator.getFirstName());
        person.setLastName(creator.getLastName());
        if (ctx.isPersonEmailObfuscated() == false && ctx.getUserId() != person.getId()) {
            person.setEmail(creator.getEmail());
        }
        person.setInstitution(createInstitution(creator.getInstitution(), ctx));
        updateCreator(person, creator);
    }

    private PInstitution createInstitution(Institution creator, Context ctx) {
        if (creator == null || !creator.isActive() && !creator.isDuplicate()) {
            return null;
        }
        PInstitution institution = new PInstitution();
        updateCreator(institution, creator);
        institution.setName(creator.getName());
        if (ctx.isInstitutionEmailObfuscated() == true) {
            institution.setEmail(creator.getEmail());
        }
        return institution;
    }

    private void updateCreator(PCreator institution, Creator creator) {
        institution.setId(creator.getId());
        institution.setUrl(creator.getUrl());
        institution.setDescription(creator.getDescription());
        institution.setDateCreated(creator.getDateCreated());
        institution.setDateUpdated(creator.getDateUpdated());
        institution.setStatus(creator.getStatus());
    }

    private Set<PLatitudeLongitudeBox> convertLatLong(Collection<LatitudeLongitudeBox> latitudeLongitudeBoxes, Context ctx) {
        if (CollectionUtils.isEmpty(latitudeLongitudeBoxes)) {
            return Collections.EMPTY_SET;
        }
        logger.debug("obfuscate: {}", ctx.isViewUnobfuscatedLat());
        Set<PLatitudeLongitudeBox> toReturn = new HashSet<>();
        latitudeLongitudeBoxes.forEach(llb_ -> {
            PLatitudeLongitudeBox llb = new PLatitudeLongitudeBox();
            llb.setScale(llb_.getScale());
            llb.setObfuscatedObjectDifferent(llb_.isObfuscatedObjectDifferent());
            if (ctx.isViewUnobfuscatedLat()) {
                llb.setNorth(llb_.getNorth());
                llb.setSouth(llb_.getSouth());
                llb.setEast(llb_.getEast());
                llb.setWest(llb_.getWest());
            } else {
                llb.setNorth(llb_.getObfuscatedNorth());
                llb.setSouth(llb_.getObfuscatedSouth());
                llb.setEast(llb_.getObfuscatedEast());
                llb.setWest(llb_.getObfuscatedWest());
            }
            toReturn.add(llb);
        });
        return toReturn;
    }

    private PTdarUser convertUser(TdarUser creator, Context ctx) {
        if (creator == null || !creator.isActive() && !creator.isDuplicate()) {
            return null;
        }
        PTdarUser user = new PTdarUser();
        updateperson(creator, user, ctx);
        user.setUsername(creator.getUsername());
        return user;
    }

    private <K extends PKeyword> Set<K> convertKeywords(Resource resource,
            Set<? extends Keyword> keywords, Class<K> cls, Context ctx) {
        if (CollectionUtils.isEmpty(keywords)) {
            return Collections.EMPTY_SET;
        }
        Set<K> toReturn = new HashSet<>();
        keywords.forEach(k_ -> {
            if (k_ != null) {

                K k;
                try {
                    k = createKeyword(cls, k_, 1, ctx);
                    toReturn.add(k);
                } catch (InstantiationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        return toReturn;
    }

    private <K extends PKeyword, L extends Keyword> K createKeyword(Class<K> cls, L k_, int depth, Context ctx)
            throws InstantiationException, IllegalAccessException {
        K k = cls.newInstance();
        k.setLabel(k_.getLabel());
        k.setId(k_.getId());
        k.setDefinition(k_.getDefinition());
        k.setStatus(k_.getStatus());
        k_.getAssertions().forEach(a_ -> {
            PExternalKeywordMapping a = new PExternalKeywordMapping();
            a.setId(a_.getId());
            a.setLabel(a_.getLabel());
            a.setRelation(a_.getRelation());
            a.setRelationType(a_.getRelationType());
            k.getAssertions().add(a);
        });
        if (k instanceof PHierarchicalKeyword) {
            L parent = (L) ((HierarchicalKeyword) k_).getParent();
            if (parent != null) {
                ((PHierarchicalKeyword) k).setParent((PHierarchicalKeyword) createKeyword(cls, parent, 1, ctx));
            }
        }

        return k;
    }

    public PKeyword consructKeyword(Keyword keyword) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (keyword == null) {
            return null;
        }
        Class<? extends PKeyword> serializedClass = (Class<? extends PKeyword>) getSerializedClass(keyword.getClass());
        Context ctx = new Context(null);
        return createKeyword(serializedClass, keyword, 1, ctx);
    }

    public <I, J extends Persistable> I construct(J j, Class<J> class1, TdarUser authenticatedUser, boolean b)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        // HACK
        Class<?> cls = getSerializedClass(class1);
        if (PResource.class.isAssignableFrom(cls)) {
            return (I) constructResource((Resource) j, (PResource) cls.newInstance(), authenticatedUser, false);
        }
        Context ctx = new Context(authenticatedUser);
        if (PResourceCollection.class.isAssignableFrom(cls)) {
            return (I) convertResourceCollection((ResourceCollection) j, 1, ctx);
        }
        if (PCreator.class.isAssignableFrom(cls)) {
            return (I) createCreator((Creator<?>) j, ctx);
        }
        if (PDataIntegrationWorkflow.class.isAssignableFrom(cls)) {
            return (I) createIntegration((DataIntegrationWorkflow) j, ctx);
        }
        return null;
    }

    private PDataIntegrationWorkflow createIntegration(DataIntegrationWorkflow work_, Context ctx) {
        boolean viewable = authorizationService.canView(ctx.getUser(), work_);
        logger.debug("viewable: {} / {} / {}", viewable, ctx.getUser(), work_);
        if (!viewable) {
            return null;
        }

        PDataIntegrationWorkflow work = new PDataIntegrationWorkflow();
        work.setTitle(work_.getTitle());
        work.setId(work_.getId());
        work.setDescription(work_.getDescription());
        work.setJsonData(work_.getJsonData());
        work.setSubmitter(work_.getSubmitter());
        work.setDateUpdated(work_.getDateUpdated());
        work.setDateCreated(work_.getDateCreated());
        work.setHidden(work_.isHidden());
        work.setAuthorizedUsers(work_.getAuthorizedUsers());
        work.setEditable(work_.isEditable());
        return work;
    }

    private <J extends Persistable> Class<?> getSerializedClass(Class<J> class1) throws ClassNotFoundException {
        String cname = class1.getCanonicalName();
        cname = StringUtils.replace(cname, ".bean.", ".serialize.");
        String toReturn = StringUtils.substringBeforeLast(cname, ".") + ".P" + StringUtils.substringAfterLast(cname, ".");
        Class<?> cls = Class.forName(toReturn);
        return cls;
    }

    public Collection<? extends PResourceCreator> convertResourceCreators(Collection<ResourceCreator> findAll, Context ctx) {
        return convertResourrceCreator(findAll, ctx);
    }

    public PResourceCollection createShellCollection(ResourceCollection rc, Context ctx) {
        return convertResourceCollection(rc, 1, ctx);
    }

    public PTdarUser createShellTdarUser(TdarUser find, Context ctx) {
        return convertUser(find, ctx);
    }

    public Set<PLatitudeLongitudeBox> createShellLatitudeLongitue(List<LatitudeLongitudeBox> findAll, Context ctx) {
        return convertLatLong(findAll, ctx);
    }

    public Collection<? extends PInformationResourceFile> createInformationResourceFiles(List<InformationResourceFile> findAll, Context ctx) {
        return convertInformationResourceFiles(findAll, ctx);
    }

    /**
     * P p = null;
     * Class
     * <P>
     * persistableClass = pc.getPersistableClass();
     * 
     * // get the ID
     * Long id = pc.getId();
     * getLogger().trace("{} {}", persistableClass, id);
     * // if we're not null or transient, somehow we've been initialized wrongly
     * if (PersistableUtils.isNotNullOrTransient(pc.getPersistable())) {
     * getLogger().error("item id should not be set yet -- persistable.id:{}\t controller.id:{}", pc.getPersistable().getId(), id);
     * }
     * // if the ID is not set, don't try and load/set it
     * else if (PersistableUtils.isNotNullOrTransient(id)) {
     * p = getGenericService().find(persistableClass, id);
     * pc.setPersistable(p);
     * }
     * 
     * logRequest(pc, type, p);
     * checkValidRequest(pc);
     * 
     * @param class1
     * @param id
     * @param msg
     * @param user
     * @return
     * @throws Exception
     */
    public <P extends Persistable, Q> Q load(Class<P> class1, Long id, Authorizable<Resource> support, TdarUser user)
            throws Exception {
        if (id == null) {
            throw new TdarAuthorizationException("error.file_not_found", Arrays.asList(id));
        }
        if (Resource.class.isAssignableFrom(class1)) {
            Resource r = (Resource) genericDao.find(class1, id);
            if (r == null) {
                return null;
            }
            support.setStatus(r.getStatus());

            if (support != null && support.authorize(r, user) == false) {
                throw new TdarAuthorizationException("error.file_not_found", Arrays.asList(id));
            }
            return (Q) constructResource(r, r.getResourceType().getProxyClass(), user, false);
        }
        return null;
    }

    public PDataTable loadDataTable(Long dataTableId) {
        DataTable dataTable = genericDao.find(DataTable.class, dataTableId);
        return convertDataTable(dataTable);
    }

}
