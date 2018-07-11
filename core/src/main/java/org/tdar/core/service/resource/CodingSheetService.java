package org.tdar.core.service.resource;

import java.io.IOException;
import java.util.List;

import org.tdar.core.bean.entity.TdarUser;
import org.tdar.core.bean.resource.CodingRule;
import org.tdar.core.bean.resource.CodingSheet;
import org.tdar.core.bean.resource.Ontology;
import org.tdar.core.bean.resource.file.InformationResourceFileVersion;
import org.tdar.core.parser.CodingSheetParserException;
import org.tdar.core.serialize.resource.PCodingSheet;
import org.tdar.filestore.WorkflowContext;

public interface CodingSheetService {

    List<CodingSheet> findSparseCodingSheetList();

    void ingestCodingSheet(CodingSheet codingSheet, WorkflowContext ctx);

    void reconcileOntologyReferencesOnRulesAndDataTableColumns(CodingSheet codingSheet, Ontology ontology_);

    List<PCodingSheet> findAllUsingOntology(Long id);

    List<String> updateCodingSheetMappings(CodingSheet codingSheet, TdarUser authenticatedUser, List<CodingRule> incomingRules);

    boolean isOkToMapOntology(PCodingSheet pCodingSheet);

    void parseUpload(CodingSheet sheet, InformationResourceFileVersion version) throws IOException, CodingSheetParserException;

    List<CodingSheet> findAll();

    List<CodingRule> addSpecialCodingRules(CodingSheet codingSheet, List<CodingRule> codingRules);

}