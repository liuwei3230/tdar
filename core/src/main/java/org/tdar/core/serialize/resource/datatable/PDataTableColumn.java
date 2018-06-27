package org.tdar.core.serialize.resource.datatable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdar.core.bean.AbstractSequenced;
import org.tdar.core.serialize.resource.PCategoryVariable;
import org.tdar.core.serialize.resource.CodingRule;
import org.tdar.core.serialize.resource.PCodingSheet;
import org.tdar.core.serialize.resource.POntology;
import org.tdar.core.serialize.resource.POntologyNode;
import org.tdar.core.bean.resource.datatable.DataTableColumnEncodingType;
import org.tdar.core.bean.resource.datatable.DataTableColumnType;
import org.tdar.core.bean.resource.datatable.MeasurementUnit;
import org.tdar.utils.PersistableUtils;
import org.tdar.utils.jaxb.converters.JaxbPersistableConverter;
import org.tdar.utils.json.JsonIdNameFilter;
import org.tdar.utils.json.JsonIntegrationDetailsFilter;
import org.tdar.utils.json.JsonIntegrationFilter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * Metadata for a column in a data table.
 * 
 * 
 * @author <a href='mailto:allen.lee@asu.edu'>Allen Lee</a>
 * @version $Revision$
 */
@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class PDataTableColumn extends AbstractSequenced<PDataTableColumn> {

    private final transient Logger logger = LoggerFactory.getLogger(getClass());

    public static final String TDAR_ID_COLUMN = "id_row_tdar";

    @XmlTransient
    public boolean isStatic() {
        return false;
    }

    private PDataTable dataTable;
    private String name;
    private String displayName;
    private String description;
    private DataTableColumnType columnDataType = DataTableColumnType.VARCHAR;
    private DataTableColumnEncodingType columnEncodingType;
    private PCategoryVariable categoryVariable;
    private transient POntology transientOntology;
    private PCodingSheet defaultCodingSheet;
    private MeasurementUnit measurementUnit;
    private boolean mappingColumn = false;
    private String delimiterValue;
    private boolean ignoreFileExtension = true;
    private boolean visible = true;
    private Map<Long, List<String>> ontologyNodeIdToValuesMap;

    @Column(name = "import_order")
    private Integer importOrder;

    @Transient
    private Integer length = -1;

    // XXX: only used for data transfer from the web layer.
    @Transient
    private transient PCategoryVariable tempSubCategoryVariable;

    @XmlElement(name = "dataTableRef")
    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    public PDataTable getDataTable() {
        return dataTable;
    }

    public void setDataTable(PDataTable dataTable) {
        this.dataTable = dataTable;
    }

    @JsonView({ JsonIntegrationDetailsFilter.class, JsonIdNameFilter.class })
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataTableColumnType getColumnDataType() {
        return columnDataType;
    }

    public void setColumnDataType(DataTableColumnType type) {
        this.columnDataType = type;
    }

    @JsonView(JsonIntegrationDetailsFilter.class)
    public DataTableColumnEncodingType getColumnEncodingType() {
        return columnEncodingType;
    }

    public void setColumnEncodingType(DataTableColumnEncodingType columnEncodingType) {
        this.columnEncodingType = columnEncodingType;
    }

    public PCategoryVariable getCategoryVariable() {
        return categoryVariable;
    }

    @JsonView(JsonIntegrationDetailsFilter.class)
    public Long getCategoryVariableId() {
        if (PersistableUtils.isTransient(categoryVariable))
            return null;
        return categoryVariable.getId();
    }

    public void setCategoryVariable(PCategoryVariable categoryVariable) {
        this.categoryVariable = categoryVariable;
    }

    @XmlElement(name = "codingSheetRef")
    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    public PCodingSheet getDefaultCodingSheet() {
        return defaultCodingSheet;
    }

    public void setDefaultCodingSheet(PCodingSheet defaultCodingSheet) {
        this.defaultCodingSheet = defaultCodingSheet;
        if (defaultCodingSheet != null) {
            setColumnEncodingType(DataTableColumnEncodingType.CODED_VALUE);
        }
    }

    public MeasurementUnit getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(MeasurementUnit measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    @Transient
    public Map<String, POntologyNode> getValueToOntologyNodeMap() {
        PCodingSheet codingSheet = getDefaultCodingSheet();
        if (codingSheet == null) {
            return Collections.emptyMap();
        }
        return codingSheet.getTermToOntologyNodeMap();
    }

    @Transient
    public Map<String, List<Long>> getValueToOntologyNodeIdMap() {
        PCodingSheet codingSheet = getDefaultCodingSheet();
        if (codingSheet == null) {
            return Collections.emptyMap();
        }
        return codingSheet.getTermToOntologyNodeIdMap();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("%s - %s %s", name, columnDataType, getId());
    }

    @JsonView(value = { JsonIntegrationFilter.class, JsonIntegrationDetailsFilter.class })
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getLength() {
        return length;
    }

    public String getDelimiterValue() {
        if (StringUtils.isEmpty(delimiterValue)) {
            return null;
        }
        return delimiterValue;
    }

    public void setDelimiterValue(String delimiterValue) {
        this.delimiterValue = delimiterValue;
    }

    public boolean isIgnoreFileExtension() {
        return ignoreFileExtension;
    }

    public void setIgnoreFileExtension(boolean ignoreFileExtension) {
        this.ignoreFileExtension = ignoreFileExtension;
    }

    public void copyUserMetadataFrom(PDataTableColumn column) {
        if (StringUtils.isNotBlank(column.getDisplayName())) { // NOT NULLABLE FIELD
            setDisplayName(column.getDisplayName());
        }
        setDescription(column.getDescription());
        // XXX: this should be set by the dataset conversion process
        // if (column.getColumnDataType() != null) { // NOT NULLABLE FIELD
        // setColumnDataType(column.getColumnDataType());
        // }

        if (getColumnDataType().isNumeric()) {
            setMeasurementUnit(column.getMeasurementUnit());
        }
        if (column.getColumnEncodingType() != null) { // NOT NULLABLE FIELD
            setColumnEncodingType(column.getColumnEncodingType());
        }
    }

    public void copyMappingMetadataFrom(PDataTableColumn column) {
        setMappingColumn(column.isMappingColumn());
        setDelimiterValue(column.getDelimiterValue());
        setIgnoreFileExtension(column.isIgnoreFileExtension());
    }

    public PCategoryVariable getTempSubCategoryVariable() {
        return tempSubCategoryVariable;
    }

    public void setTempSubCategoryVariable(PCategoryVariable tempSubCategoryVariable) {
        this.tempSubCategoryVariable = tempSubCategoryVariable;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }


    public boolean isMappingColumn() {
        return mappingColumn;
    }

    public void setMappingColumn(boolean mappingColumn) {
        this.mappingColumn = mappingColumn;
    }

    public boolean hasDifferentMappingMetadata(PDataTableColumn column) {
        logger.debug("delim: '{}' - '{}'", getDelimiterValue(), column.getDelimiterValue());
        if (!StringUtils.equals(getDelimiterValue(), column.getDelimiterValue())) {
            return true;
        }
        logger.debug("extension: {} - {}", isIgnoreFileExtension(), column.isIgnoreFileExtension());
        if (!Objects.equals(isIgnoreFileExtension(), column.isIgnoreFileExtension())) {
            return true;
        }
        logger.debug("mapping: {} - {}", isMappingColumn(), column.isMappingColumn());
        if (!Objects.equals(isMappingColumn(), column.isMappingColumn())) {
            return true;
        }
        return false;
    }

    @Transient
    @XmlTransient
    public String getJsSimpleName() {
        return getName().replaceAll("[\\s\\,\"\']", "_");
    }

    public Set<String> getMappedDataValues(POntologyNode node) {
        Set<String> values = new HashSet<>();
        for (CodingRule rule : getDefaultCodingSheet().getCodingRules()) {
            if (Objects.equals(node, rule.getOntologyNode())) {
                values.add(rule.getTerm());
            }
        }
        return values;
    }

    @XmlTransient
    @Transient
    public Set<String> getUnmappedDataValues() {
        Set<String> values = new HashSet<>();
        if (getDefaultCodingSheet() == null || CollectionUtils.isEmpty(getDefaultCodingSheet().getCodingRules())) {
            return values;
        }
        for (CodingRule rule : getDefaultCodingSheet().getCodingRules()) {
            if (rule.getOntologyNode() == null) {
                values.add(rule.getTerm());
            }
        }
        return values;
    }

    /**
     * Return true if this column has a mapped ontology and has a mapped coding sheet that is not invalid.
     * 
     * @return
     */
    @Deprecated
    public boolean isActuallyMapped() {
        if (PersistableUtils.isNullOrTransient(getMappedOntology()) && PersistableUtils.isNullOrTransient(getDefaultCodingSheet())) {
            return false;
        }

        for (CodingRule rule : getDefaultCodingSheet().getCodingRules()) {
            if (rule != null && rule.getOntologyNode() != null) {
                return true;
            }
        }
        return false;
    }

    @XmlElement(name = "mappedOntologyRef")
    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    @Deprecated()
    public POntology getMappedOntology() {
        if (getDefaultCodingSheet() != null && getDefaultCodingSheet().getDefaultOntology() != null) {
            return getDefaultCodingSheet().getDefaultOntology();
        }
        return null;
    }

    @Deprecated
    public void setMappedOntology(POntology ont) {
        logger.warn("setting mappedOntology does nothing...");
    }

    @JsonView(JsonIntegrationDetailsFilter.class)
    public Long getMappedOntologyId() {
        if (getMappedOntology() == null) {
            return null;
        }
        return getMappedOntology().getId();
    }

    @XmlTransient
    public POntology getTransientOntology() {
        return transientOntology;
    }

    public void setTransientOntology(POntology transientOntology) {
        this.transientOntology = transientOntology;
    }

    @Transient
    @XmlTransient
    public boolean isFilenameColumn() {
        return this.getColumnEncodingType().isFilename();
    }

    @Transient
    @XmlTransient
    @JsonIgnore
    public String getPrettyDisplayName() {
        String displayName = getDisplayName().replaceAll(" (?i)Ontology", "");
        displayName = StringUtils.replace(displayName, "  ", " ");
        return displayName;
    }

    public Integer getImportOrder() {
        return importOrder;
    }

    public void setImportOrder(Integer importOrder) {
        this.importOrder = importOrder;
    }
}
