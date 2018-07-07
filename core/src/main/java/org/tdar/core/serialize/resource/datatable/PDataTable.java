package org.tdar.core.serialize.resource.datatable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.serialize.resource.PDataset;
import org.tdar.core.bean.resource.datatable.DataTableColumnRelationshipType;
import org.tdar.utils.PersistableUtils;
import org.tdar.utils.jaxb.converters.JaxbPersistableConverter;
import org.tdar.utils.json.JsonIntegrationDetailsFilter;
import org.tdar.utils.json.JsonIntegrationFilter;

import com.amazonaws.services.dynamodbv2.datamodeling.unmarshallers.StringUnmarshaller;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * $Id$
 * <p>
 * A DataTable belonging to a Dataset and carrying a list of ordered DataTableColumns and descriptive metadata.
 * </p>
 * 
 * @author <a href='Yan.Qi@asu.edu'>Yan Qi</a>
 * @version $Revision$
 */
@XmlRootElement
public class PDataTable extends AbstractPersistable {

    private PDataset dataset;
    private String name;
    private String displayName;
    private String description;
    private List<PDataTableColumn> dataTableColumns = new ArrayList<PDataTableColumn>();
    private Integer importOrder;

    private transient Map<String, PDataTableColumn> nameToColumnMap;
    private transient Map<Long, PDataTableColumn> idToColumnMap;
    private transient Map<String, PDataTableColumn> displayNameToColumnMap;
    private transient int dataTableColumnHashCode = -1;

    @XmlElement(name = "resourceRef")
    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    public PDataset getDataset() {
        return dataset;
    }

    public void setDataset(PDataset dataset) {
        this.dataset = dataset;
    }

    @JsonView(JsonIntegrationFilter.class)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElementWrapper(name = "dataTableColumns")
    @XmlElement(name = "dataTableColumn")
    @JsonView(JsonIntegrationDetailsFilter.class)
    public List<PDataTableColumn> getDataTableColumns() {
        return dataTableColumns;
    }

    public void setDataTableColumns(List<PDataTableColumn> dataTableColumns) {
        this.dataTableColumns = dataTableColumns;
    }

    /**
     * Get the data table columns sorted in the ascending order of column names.
     * 
     * @return
     */
    @XmlTransient
    public List<PDataTableColumn> getSortedDataTableColumns() {
        return getSortedDataTableColumns(new Comparator<PDataTableColumn>() {
            @Override
            public int compare(PDataTableColumn a, PDataTableColumn b) {
                int comparison = a.compareTo(b);
                if (comparison == 0) {
                    return a.getDisplayName().compareTo(b.getDisplayName());
                }
                return comparison;
            }
        });
    }

    /**
     * Get the data table columns sorted in the ascending order of sequence_number which should be the import order if available
     * 
     * @return
     */
    @XmlTransient
    @JsonView(JsonIntegrationFilter.class)
    public List<PDataTableColumn> getSortedDataTableColumnsByImportOrder() {
        return getSortedDataTableColumns(new Comparator<PDataTableColumn>() {
            @Override
            public int compare(PDataTableColumn a, PDataTableColumn b) {
                return ObjectUtils.compare(a.getSequenceNumber(), b.getSequenceNumber());
            }
        });
    }

    public List<PDataTableColumn> getSortedDataTableColumns(Comparator<PDataTableColumn> comparator) {
        ArrayList<PDataTableColumn> sortedDataTableColumns = new ArrayList<PDataTableColumn>(dataTableColumns);
        Collections.sort(sortedDataTableColumns, comparator);
        return sortedDataTableColumns;
    }

    /**
     * <p>
     * List all the columns in this table and any left-joined tables (including recursively left-joined)
     * <p>
     * Consider the following very unlikely scenario:
     * 
     * <pre>
     *   B
     *  / \
     * A   C-E-A
     *  \ /
     *   D
     * Table A reference Table B & D, B & D reference the same column in C, and C references E, which in turn references A...
     * </pre>
     * <p>
     * What still needs doing in this method is:
     * <ol>
     * <li>Adding a filter that excludes columns that are already in the result set (B-C and D-C). It would be nice if we could include them both using aliases
     * but that's not possible at this moment in time.
     * <li>to stop recursing if we detect tables that have already been visited (but if the columns referenced are not in the list of columns, to still add
     * them)
     * </ol>
     * <p>
     * There is an implicit assumption that the referenced foreign key's are in fact primary keys...
     * 
     * <p>
     * What still needs doing to support this method is:
     * <ol>
     * <li>The screen that displays the resultant tables/columns might need to be enhanced to ensure that the user doesn't become confused by columns with the
     * same name in multiple tables.
     * <li>The code that generates the SQL queries needs to be updated to perform the required joins.
     * </ol>
     * 
     * @return list of columns
     */
    @XmlTransient
    public List<PDataTableColumn> getLeftJoinColumns() {
        ArrayList<PDataTableColumn> leftJoinColumns = new ArrayList<PDataTableColumn>(getSortedDataTableColumns());
        for (PDataTableRelationship r : getRelationships()) {
            // Include fields from related tables unless they're on the "many" side of a one-to-many relationship
            if (this.equals(r.getLocalTable()) && (r.getType() != DataTableColumnRelationshipType.ONE_TO_MANY)) {
                // this is the "local" table in a many-to-one or one-to-one relationship,
                // so including the "foreign" table's fields will not increase the cardinality of this query
                leftJoinColumns.addAll(r.getForeignTable().getLeftJoinColumns());
            } else if (this.equals(r.getForeignTable()) && (r.getType() != DataTableColumnRelationshipType.MANY_TO_ONE)) {
                // this is the "foreign" table in a one-to-many or one-to-one relationship,
                // so including the "local" table's fields will not increase the cardinality of this query
                leftJoinColumns.addAll(r.getLocalTable().getLeftJoinColumns());
            }
        }
        return leftJoinColumns;
    }

    /**
     * The relationships in which this dataset is the local table
     * 
     * @return the set of relationships
     */
    @XmlTransient
    @Transient
    public Set<PDataTableRelationship> getRelationships() {
        Set<PDataTableRelationship> relationships = new HashSet<PDataTableRelationship>();
        for (PDataTableRelationship r : dataset.getRelationships()) {
            // return the relationship if this table is either the relationship's foreign or local table
            if (this.equals(r.getLocalTable()) || this.equals(r.getForeignTable())) {
                relationships.add(r);
            }
        }
        return relationships;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(name)) {
            builder.append(name);
        } else {
            builder.append("unnamed");
        }
        builder.append(" - ").append(getId());
        return builder.toString();
    }

    @Transient
    public List<String> getColumnNames() {
        List<String> columns = new ArrayList<String>();
        for (PDataTableColumn column : getDataTableColumns()) {
            columns.add(column.getName());
        }
        return columns;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonView(value = { JsonIntegrationFilter.class, JsonIntegrationDetailsFilter.class })
    public String getDisplayName() {
        return displayName;
    }

    @JsonView(JsonIntegrationDetailsFilter.class)
    public String getDatasetTitle() {
        return getDataset().getTitle();
    }

    @JsonView(JsonIntegrationDetailsFilter.class)
    public Long getDatasetId() {
        return getDataset().getId();
    }

    public String getInternalName() {
        return getName().replaceAll("^((\\w+)_)(\\d+)(_?)", "");
    }

    @Transient
    public List<PDataTableColumn> getColumnsWithOntologyMappings() {
        List<PDataTableColumn> columns = new ArrayList<>();
        for (PDataTableColumn column : getDataTableColumns()) {
            if (column.getMappedOntology() != null) {
                columns.add(column);
            }
        }
        return columns;
    }

    @Transient
    public List<PDataTableColumn> getFilenameColumns() {
        List<PDataTableColumn> columns = new ArrayList<>();
        for (PDataTableColumn column : getDataTableColumns()) {
            if (column.isFilenameColumn()) {
                columns.add(column);
            }
        }
        return columns;
    }

    public Integer getImportOrder() {
        return importOrder;
    }

    public void setImportOrder(Integer importOrder) {
        this.importOrder = importOrder;
    }

}
