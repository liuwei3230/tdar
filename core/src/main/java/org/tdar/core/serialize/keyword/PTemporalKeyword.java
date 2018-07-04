package org.tdar.core.serialize.keyword;

import javax.xml.bind.annotation.XmlRootElement;

import org.tdar.core.bean.keyword.KeywordType;

/**
 * Temporal term coverage
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Revision$
 */

@XmlRootElement
public class PTemporalKeyword extends PAbstractKeyword<PTemporalKeyword>  {

    public PTemporalKeyword(String string) {
        this.setLabel(string);
    }

    public PTemporalKeyword() {
    }

    @Override
    public String getUrlNamespace() {
        return KeywordType.TEMPORAL_KEYWORD.getUrlNamespace();
    }

}
