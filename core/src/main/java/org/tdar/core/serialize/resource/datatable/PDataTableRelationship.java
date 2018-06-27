package org.tdar.core.serialize.resource.datatable;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.bean.resource.datatable.DataTableColumnRelationshipType;

/**
 * This Class represents a Primary or Foreign Key relationship between two data tables
 * These relationships can represent a single column foreign key, or multiple column
 * foreign key, as well as more basic primary keys.
 */
public class PDataTableRelationship extends AbstractPersistable {
    private DataTableColumnRelationshipType type;
    private Set<PDataTableColumnRelationship> columnRelationships = new HashSet<PDataTableColumnRelationship>();

    public void setType(DataTableColumnRelationshipType type) {
        this.type = type;
    }

    public DataTableColumnRelationshipType getType() {
        return type;
    }

    @XmlElementWrapper(name = "columnRelationships")
    @XmlElement(name = "columnRelationship")
    public Set<PDataTableColumnRelationship> getColumnRelationships() {
        return columnRelationships;
    }

    public void setColumnRelationships(Set<PDataTableColumnRelationship> columnRelationships) {
        this.columnRelationships = columnRelationships;
    }

    @XmlTransient
    public PDataTable getForeignTable() {
        // try {
        PDataTableColumnRelationship relationship = getColumnRelationships().iterator().next();
        return relationship.getForeignColumn().getDataTable();
        // } catch (Exception e) {
        // }
        // return null;
    }

    @XmlTransient
    public PDataTable getLocalTable() {
        // try {
        PDataTableColumnRelationship relationship = getColumnRelationships().iterator().next();
        return relationship.getLocalColumn().getDataTable();
        // } catch (Exception e) {
        //
        // }
        // return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getType().name());
        sb.append(" - ").append(getLocalTable().getName()).append(" (");
        for (PDataTableColumnRelationship rel : getColumnRelationships()) {
            sb.append(rel.getLocalColumn().getName());
            sb.append("<==>");
            sb.append(rel.getForeignColumn().getName());
            sb.append(" ");
        }
        sb.append(")");
        return sb.toString();
    }

}
