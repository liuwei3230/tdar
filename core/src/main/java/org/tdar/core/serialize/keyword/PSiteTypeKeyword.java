package org.tdar.core.serialize.keyword;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.tdar.core.bean.keyword.KeywordType;
import org.tdar.utils.json.JsonLookupFilter;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Describes the type of site in the resource
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */

@XmlRootElement
public class PSiteTypeKeyword extends PHierarchicalKeyword<PSiteTypeKeyword> implements SuggestedKeyword {

    private boolean approved;

    public PSiteTypeKeyword() {
    }

    public PSiteTypeKeyword(String string) {
        this.setLabel(string);
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
        return KeywordType.SITE_TYPE_KEYWORD.getUrlNamespace();
    }

}
