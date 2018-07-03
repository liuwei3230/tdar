package org.tdar.core.serialize.keyword;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.tdar.core.bean.keyword.KeywordType;
import org.tdar.utils.json.JsonLookupFilter;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Represents a Culture described by a resource.
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */

@XmlRootElement
public class PCultureKeyword extends PHierarchicalKeyword<PCultureKeyword> implements SuggestedKeyword {

    private boolean approved;

    public PCultureKeyword() {
    }

    public PCultureKeyword(String string) {
        setLabel(string);
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

    @Override
    public String getUrlNamespace() {
        return KeywordType.CULTURE_KEYWORD.getUrlNamespace();
    }

}