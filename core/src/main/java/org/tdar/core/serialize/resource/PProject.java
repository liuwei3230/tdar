package org.tdar.core.serialize.resource;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.tdar.core.bean.DisplayOrientation;
import org.tdar.core.bean.SortOption;
import org.tdar.core.bean.Sortable;
import org.tdar.core.bean.resource.HasStatic;
import org.tdar.core.bean.resource.ResourceType;

/**
 * Represents a Project. Projects allow for inheritance of metadata from the project to resources within the project and thus simplifying metadata entry.
 * 
 * @author <a href='Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Revision$
 */

@XmlRootElement(name = "project")
public class PProject extends PResource implements Sortable, HasStatic {


    public static final PProject NULL = new PProject() {

        @Override
        public String getDescription() {
            return "";
        }

        @Override
        public Long getId() {
            return -1L;
        }

        @Override
        public String getTitle() {
            return "";
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public boolean isStatic() {
            return true;
        }

    };

    /**
     * Instantiate a transient project instance with the specified ID and Title.
f     * 
     * @param id
     * @param title
     */
    public PProject(Long id, String title) {
        setId(id);
        setTitle(title);
        setResourceType(ResourceType.PROJECT);
    }

    public PProject() {
        setResourceType(ResourceType.PROJECT);
    }

    private SortOption sortBy = SortOption.RESOURCE_TYPE;
    private SortOption secondarySortBy;
    private DisplayOrientation orientation = DisplayOrientation.LIST;

    @Transient
    public String getProjectTitle() {
        return getTitle();
    }

    @Transient
    // return the title without "The" as a prefix or "Project" as suffix
    public String getCoreTitle() {
        return getTitle().trim().replaceAll("^[T|t]he\\s", "").replaceAll("\\s[P|p]roject$", "");
    }

    @Override
    public SortOption getSortBy() {
        return sortBy;
    }

    public void setSortBy(SortOption sortBy) {
        this.sortBy = sortBy;
    }

    public DisplayOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(DisplayOrientation orientation) {
        this.orientation = orientation;
    }

    public SortOption getSecondarySortBy() {
        return secondarySortBy;
    }

    public void setSecondarySortBy(SortOption secondarySortBy) {
        this.secondarySortBy = secondarySortBy;
    }

    @Override
    @XmlTransient
    @Transient
    public boolean isStatic() {
        return false;
    }

}
