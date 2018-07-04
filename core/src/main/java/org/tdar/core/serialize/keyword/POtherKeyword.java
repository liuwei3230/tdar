package org.tdar.core.serialize.keyword;

import javax.xml.bind.annotation.XmlRootElement;

import org.tdar.core.bean.keyword.KeywordType;

/**
 * Represents a "general" or non-specific keyword
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
@XmlRootElement
public class POtherKeyword extends PAbstractKeyword<POtherKeyword> {


    public POtherKeyword() {
    }

    public POtherKeyword(String name) {
        setLabel(name);
    }

    @Override
    public String getUrlNamespace() {
        return KeywordType.OTHER_KEYWORD.getUrlNamespace();
    }

}
