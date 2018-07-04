package org.tdar.core.serialize.keyword;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.tdar.core.bean.keyword.KeywordType;
import org.tdar.utils.json.JsonLookupFilter;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Material Type keyword (controlled).
 * 
 * @author Matt Cordial
 * @version $Rev$
 */
@XmlRootElement
public class PMaterialKeyword extends PAbstractKeyword<PMaterialKeyword> implements ControlledKeyword, SuggestedKeyword {

    private boolean approved;

    public PMaterialKeyword() {
    }

    public PMaterialKeyword(String string) {
        this.setLabel(string);
    }

    @Override
    public String getUrlNamespace() {
        return KeywordType.MATERIAL_TYPE.getUrlNamespace();
    }

    @XmlAttribute
    @Override
    @JsonView(JsonLookupFilter.class)
    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

}
