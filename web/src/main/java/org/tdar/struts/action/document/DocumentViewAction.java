package org.tdar.struts.action.document;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tdar.core.serialize.resource.PDocument;
import org.tdar.struts.action.resource.AbstractResourceViewAction;

@Component
@Scope("prototype")
@ParentPackage("default")
@Namespace("/document")
public class DocumentViewAction extends AbstractResourceViewAction<PDocument> {

    private static final long serialVersionUID = 2384325295193047858L;

    @Override
    public Class<PDocument> getPersistableClass() {
        return PDocument.class;
    }


}
