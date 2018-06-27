package org.tdar.core.serialize.keyword;

import javax.xml.bind.annotation.XmlRootElement;

import org.tdar.core.bean.keyword.KeywordType;

/**
 * Represents the type of Investigation or research described by the resource.
 * 
 * @author Matt Cordial
 * @version $Rev$
 */
@XmlRootElement
public class PInvestigationType extends AbstractKeyword<PInvestigationType> implements ControlledKeyword {

    public PInvestigationType() {
    }

    public PInvestigationType(String string) {
        setLabel(string);
    }

    @Override
    public String getUrlNamespace() {
        return KeywordType.INVESTIGATION_TYPE.getUrlNamespace();
    }

}
