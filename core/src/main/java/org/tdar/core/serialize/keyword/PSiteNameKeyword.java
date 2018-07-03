package org.tdar.core.serialize.keyword;

import javax.xml.bind.annotation.XmlRootElement;

import org.tdar.core.bean.keyword.KeywordType;

/**
 * Lists the name of the site in the resource
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
@XmlRootElement
public class PSiteNameKeyword extends AbstractKeyword<PSiteNameKeyword> {

    public PSiteNameKeyword() {
    }

    public PSiteNameKeyword(String label) {
        this.setLabel(label);
    }

    public String getSiteCode() {
        return getLabel();
    }

    @Override
    public String getUrlNamespace() {
        return KeywordType.SITE_NAME_KEYWORD.getUrlNamespace();
    }

}
