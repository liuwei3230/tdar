package org.tdar.core.serialize.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.collections4.CollectionUtils;
import org.tdar.core.bean.resource.ResourceType;
import org.tdar.core.serialize.resource.datatable.PDataTable;
import org.tdar.core.serialize.resource.datatable.PDataTableColumn;
import org.tdar.core.serialize.resource.datatable.PDataTableRelationship;
import org.tdar.utils.PersistableUtils;

/**
 * A Dataset information resource can currently be an Excel file, Access MDB file, or plaintext CSV file.
 * 
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
@XmlRootElement(name = "Pdataset")
public class PDataset extends PInformationResource {

    private Set<PDataTable> dataTables = new LinkedHashSet<PDataTable>();
    private Set<PDataTableRelationship> relationships = new HashSet<PDataTableRelationship>();
    public PDataset() {
        setResourceType(ResourceType.DATASET);
    }

    private transient Map<String, PDataTable> nameToTableMap;
    private transient Map<String, PDataTable> genericNameToTableMap;
    private transient int dataTableHashCode = -1;

    @XmlElementWrapper(name = "dataTables")
    @XmlElement(name = "dataTable")
    public Set<PDataTable> getDataTables() {
        return dataTables;
    }

    @XmlTransient
    public List<PDataTable> getSortedDataTables() {
        List<PDataTable> tables = new ArrayList<>(dataTables);
        Collections.sort(tables, new Comparator<PDataTable>() {

            @Override
            public int compare(PDataTable o1, PDataTable o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return tables;
    }

    @XmlTransient
    public List<PDataTable> getImportSortedDataTables() {
        List<PDataTable> tables = new ArrayList<>(dataTables);
        Collections.sort(tables, new Comparator<PDataTable>() {

            @Override
            public int compare(PDataTable o1, PDataTable o2) {
                if (o1.getImportOrder() != null && o2.getImportOrder() != null) {
                    return o1.getImportOrder().compareTo(o2.getImportOrder());
                }
                return o1.getName().compareTo(o2.getName());
            }
        });
        return tables;
    }

    public void setDataTables(Set<PDataTable> dataTables) {
        this.dataTables = dataTables;
    }

    /**
     * @param string
     * @return
     */
    public PDataTable getDataTableByName(String name) {
        if ((nameToTableMap == null) || !Objects.equals(dataTableHashCode, getDataTables().hashCode())) {
            initializeNameToTableMap();
        }
        // NOTE: IF the HashCode is not implemented properly, on DataTableColumn, this may get out of sync
        return nameToTableMap.get(name);
    }

    /**
     * @param string
     * @return
     */
    public PDataTable getDataTableById(Long id) {
        for (PDataTable datatable : getDataTables()) {
            if (Objects.equals(datatable.getId(), id)) {
                return datatable;
            }
        }
        return null;
    }

    private void initializeNameToTableMap() {
        nameToTableMap = new HashMap<String, PDataTable>();
        genericNameToTableMap = new HashMap<String, PDataTable>();

        for (PDataTable dt : getDataTables()) {
            nameToTableMap.put(dt.getName(), dt);
            String simpleName = dt.getName().replaceAll("^((\\w+_)(\\d+)(_?))", "");
            genericNameToTableMap.put(simpleName, dt);
        }

    }

    @Transient
    public PDataTable getDataTableByGenericName(String name) {
        if ((genericNameToTableMap == null) || !Objects.equals(dataTableHashCode, getDataTables().hashCode())) {
            initializeNameToTableMap();
        }
        // NOTE: IF the HashCode is not implemented properly, on DataTableColumn, this may get out of sync
        return genericNameToTableMap.get(name);
    }

    public void setRelationships(Set<PDataTableRelationship> relationships) {
        this.relationships = relationships;
    }

    public Set<PDataTableRelationship> getRelationships() {
        return relationships;
    }

    public boolean hasMappingColumns() {
        if (CollectionUtils.isEmpty(getDataTables())) {
            return false;
        }
        for (PDataTable dt : getDataTables()) {
            if (CollectionUtils.isEmpty(dt.getDataTableColumns())) {
                return false;
            }
            for (PDataTableColumn col : dt.getDataTableColumns()) {
                if (col.isMappingColumn()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasCodingColumns() {
        if (CollectionUtils.isEmpty(getDataTables())) {
            return false;
        }
        for (PDataTable dt : getDataTables()) {
            if (CollectionUtils.isEmpty(dt.getDataTableColumns())) {
                return false;
            }
            for (PDataTableColumn col : dt.getDataTableColumns()) {
                if (PersistableUtils.isNotNullOrTransient(col.getDefaultCodingSheet())) {
                    return true;
                }
            }
        }
        return false;
    }
}
