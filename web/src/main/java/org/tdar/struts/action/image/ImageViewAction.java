package org.tdar.struts.action.image;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tdar.core.serialize.resource.PImage;
import org.tdar.struts.action.resource.AbstractResourceViewAction;

@Component
@Scope("prototype")
@ParentPackage("default")
@Namespace("/image")
public class ImageViewAction extends AbstractResourceViewAction<PImage> {

    private static final long serialVersionUID = -5329847561530865453L;

    @Override
    public Class<PImage> getPersistableClass() {
        return PImage.class;
    }
}
