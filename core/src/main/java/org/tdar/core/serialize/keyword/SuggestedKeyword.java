package org.tdar.core.serialize.keyword;

/**
 * An interface to manage suggested keywords
 * 
 * @version $Rev$
 */
public interface SuggestedKeyword extends PKeyword {

    public boolean isApproved();

    public void setApproved(boolean approved);
}
