package org.tdar.core.serialize.resource;

import javax.xml.bind.annotation.XmlRootElement;

import org.tdar.core.bean.resource.ResourceType;

/**
 * $Id$
 * <p>
 * Represents any type of geospatial object, ShapeFile, GeoDatabase, and Georectified image
 * </p>
 * 
 * @author Adam Brin
 * @version $Revision: 543$
 */
@XmlRootElement(name = "geospatial")
public class Geospatial extends PDataset {


    public Geospatial() {
        setResourceType(ResourceType.GEOSPATIAL);
    }

    private String currentnessUpdateNotes;
    private String spatialReferenceSystem;
    private String mapSource;
    private String scale;

    @Override
    public boolean isSupportsThumbnails() {
        return true;
    }

    @Override
    public boolean isHasBrowsableImages() {
        return true;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getSpatialReferenceSystem() {
        return spatialReferenceSystem;
    }

    public void setSpatialReferenceSystem(String spatialReferenceSystem) {
        this.spatialReferenceSystem = spatialReferenceSystem;
    }

    public String getCurrentnessUpdateNotes() {
        return currentnessUpdateNotes;
    }

    public void setCurrentnessUpdateNotes(String currentnessUpdateNotes) {
        this.currentnessUpdateNotes = currentnessUpdateNotes;
    }

    public String getMapSource() {
        return mapSource;
    }

    public void setMapSource(String mapSource) {
        this.mapSource = mapSource;
    }

}
