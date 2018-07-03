package org.tdar.core.serialize.resource;

import org.tdar.core.bean.Viewable;

public interface ConfidentialViewable extends Viewable {

    public boolean isConfidentialViewable();

    public void setConfidentialViewable(boolean editable);
}
