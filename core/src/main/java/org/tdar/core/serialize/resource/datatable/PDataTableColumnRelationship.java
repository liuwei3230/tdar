package org.tdar.core.serialize.resource.datatable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.tdar.core.bean.AbstractPersistable;
import org.tdar.utils.jaxb.converters.JaxbPersistableConverter;

/**
 * Represents a relationship between two data-tables via columns
 * 
 * @author abrin
 * 
 */
public class PDataTableColumnRelationship extends AbstractPersistable {

    private PDataTableColumn localColumn;
    private PDataTableColumn foreignColumn;

    /**
     * @return the localColumn
     */
    @XmlElement(name = "localColumnRef")
    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    public PDataTableColumn getLocalColumn() {
        return localColumn;
    }

    /**
     * @param localColumn
     *            the localColumn to set
     */
    public void setLocalColumn(PDataTableColumn localColumn) {
        this.localColumn = localColumn;
    }

    /**
     * @return the foreignColumn
     */
    @XmlElement(name = "foreignColumnRef")
    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    public PDataTableColumn getForeignColumn() {
        return foreignColumn;
    }

    /**
     * @param foreignColumn
     *            the foreignColumn to set
     */
    public void setForeignColumn(PDataTableColumn foreignColumn) {
        this.foreignColumn = foreignColumn;
    }
}
