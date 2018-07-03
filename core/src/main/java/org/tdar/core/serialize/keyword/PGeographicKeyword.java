package org.tdar.core.serialize.keyword;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.tdar.core.bean.keyword.GeographicKeyword.Level;
import org.tdar.core.bean.keyword.KeywordType;

/**
 * $Id$
 * 
 * Spatial coverage - geographic or jurisdictional terms (e.g., city, county,
 * state/province/department, country).
 * 
 * See http://www.getty.edu/research/conducting_research/vocabularies/ and
 * http://geonames.usgs.gov/pls/gnispublic/
 * 
 * @author <a href='Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Revision$
 */
@XmlRootElement
public class PGeographicKeyword extends AbstractKeyword<PGeographicKeyword> {

    public PGeographicKeyword(String string) {
        this.setLabel(string);
    }

    public PGeographicKeyword() {
    }

    private Level level;
    private String code;

    /**
     * @param level
     *            the level to set
     */
    public void setLevel(Level level) {
        this.level = level;
    }

    /**
     * @return the level
     */
    @XmlAttribute
    public Level getLevel() {
        return level;
    }

    public static String getFormattedLabel(String label, Level level) {
        StringBuffer toReturn = new StringBuffer();
        toReturn.append(label.trim()).append(" (").append(level.getLabel()).append(")");
        return toReturn.toString();
    }

    @Override
    public String getUrlNamespace() {
        return KeywordType.GEOGRAPHIC_KEYWORD.getUrlNamespace();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
