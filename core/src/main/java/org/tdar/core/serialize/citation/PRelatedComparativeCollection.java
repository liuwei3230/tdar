package org.tdar.core.serialize.citation;

/**
 * Relation.comparativeCollection (resource level; repeatable) - If
 * identifications were made using specific comparative collections, list them
 * here.
 * 
 * @author <a href='mailto:Yan.Qi@asu.edu'>Yan Qi</a>
 * @version $Revision$
 */
public class PRelatedComparativeCollection extends Citation {


    public PRelatedComparativeCollection() {
    }

    public PRelatedComparativeCollection(String text) {
        setText(text);
    }

}
