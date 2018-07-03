package org.tdar.struts.action.codingSheet;

import java.util.Set;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tdar.core.bean.resource.CodingSheet;
import org.tdar.core.serialize.resource.PCodingSheet;
import org.tdar.core.service.resource.CodingSheetService;
import org.tdar.core.service.resource.DataTableService;
import org.tdar.struts.action.resource.AbstractSupportingResourceViewAction;
import org.tdar.struts_base.action.TdarActionException;

@Component
@Scope("prototype")
@ParentPackage("default")
@Namespace("/coding-sheet")
public class CodingSheetViewAction extends AbstractSupportingResourceViewAction<PCodingSheet> {

    private static final long serialVersionUID = 3034924577588283512L;

    @Override
    public Class<PCodingSheet> getPersistableClass() {
        return PCodingSheet.class;
    }

    @Autowired
    private transient DataTableService dataTableService;
    @Autowired
    private CodingSheetService codingSheetService;
    private Set<String> missingCodingKeys;
    private boolean okToMap;

    @Override
    protected void loadCustomViewMetadata() throws TdarActionException {
        // TODO Auto-generated method stub
        super.loadCustomViewMetadata();
        CodingSheet sheet = getGenericService().find(CodingSheet.class, getId());
        setMissingCodingKeys(dataTableService.getMissingCodingKeys(sheet, getTablesUsingResource()));
        okToMap = codingSheetService.isOkToMapOntology(sheet);
    }

    public boolean isOkToMapOntology() {
        return okToMap;
    }

    public Set<String> getMissingCodingKeys() {
        return missingCodingKeys;
    }

    public void setMissingCodingKeys(Set<String> missingCodingKeys) {
        this.missingCodingKeys = missingCodingKeys;
    }

}
