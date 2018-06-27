package org.tdar.core.serialize.keyword;

import org.apache.commons.lang3.StringUtils;
import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.bean.RelationType;
import org.tdar.core.bean.Validatable;
import org.tdar.core.bean.util.UrlUtils;
import org.tdar.utils.json.JsonLookupFilter;

import com.fasterxml.jackson.annotation.JsonView;

public class ExternalKeywordMapping extends AbstractPersistable implements Validatable {


    public ExternalKeywordMapping() {
    }

    public ExternalKeywordMapping(String relationUrl, RelationType type) {
        this.relation = relationUrl;
        this.relationType = type;
    }
    @JsonView(JsonLookupFilter.class)
    private String relation;
    @JsonView(JsonLookupFilter.class)
    private String label;
    @JsonView(JsonLookupFilter.class)
    private RelationType relationType;

    public RelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    @Override
    public String toString() {
        return String.format("%s %s (%s)", relation, relationType, getId());
    }

    @Override
    public boolean isValidForController() {
        if (StringUtils.isBlank(relation) || relationType == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getRelationHost() {
        try {
            return UrlUtils.getHost(relation);
        } catch (Exception e) {
            logger.warn("url is not valid: {}", relation);
        }
        return null;
    }
}
