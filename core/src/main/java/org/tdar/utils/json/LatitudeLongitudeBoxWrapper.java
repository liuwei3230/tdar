package org.tdar.utils.json;

import java.io.Serializable;

import org.tdar.core.bean.resource.Resource;
import org.tdar.core.serialize.coverage.PLatitudeLongitudeBox;
import org.tdar.core.serialize.resource.PResource;
import org.tdar.core.service.FeedSearchHelper;
import org.tdar.core.service.GeoRssMode;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(getterVisibility = Visibility.PUBLIC_ONLY)
public class LatitudeLongitudeBoxWrapper implements Serializable {

    private static final long serialVersionUID = 9175448938321937035L;

    private double south;
    private double north;
    private double west;
    private double east;
    private double centerLatitude;
    private double centerLongitude;
    private GeoRssMode mode = GeoRssMode.ENVELOPE;
    private PResource resource;

    private Class<?> jsonView = null;
    private boolean spatial;

    public LatitudeLongitudeBoxWrapper(PResource resource, FeedSearchHelper helper) {
        this.jsonView = helper.getJsonFilter();
        if (helper.getGeoMode() != null) {
            this.mode = helper.getGeoMode();
        }
        if (resource != null) {
            this.resource = resource;
            PLatitudeLongitudeBox llb = resource.getFirstActiveLatitudeLongitudeBox();
            if (llb != null) {
//                if (helper.isOverrideAndObfuscate() == true || resource.isLatLongVisible()) {
//                    setSpatial(true);
//                    this.centerLatitude = llb.getCenterLatitude();
//                    this.centerLongitude = llb.getCenterLongitude();
//                    this.south = llb.getSouth();
//                    this.west = llb.getWest();
//                    this.north = llb.getNorth();
//                    this.east = llb.getEast();
//                }
//
//                if (helper.isOverrideAndObfuscate() == false && resource.isConfidentialViewable()) {
                    setSpatial(true);
                    this.south = llb.getSouth();
                    this.west = llb.getWest();
                    this.north = llb.getNorth();
                    this.east = llb.getEast();
                    this.centerLatitude = llb.getCenterLatitude();
                    this.centerLongitude = llb.getCenterLongitude();
//
//                }
            }
        }
    }

    public double getSouth() {
        return south;
    }

    public void setSouth(double minLatitude) {
        this.south = minLatitude;
    }

    public double getNorth() {
        return north;
    }

    public void setNorth(double maxLatitude) {
        this.north = maxLatitude;
    }

    public double getWest() {
        return west;
    }

    public void setWest(double minLongitude) {
        this.west = minLongitude;
    }

    public double getEast() {
        return east;
    }

    public void setEast(double maxLongitude) {
        this.east = maxLongitude;
    }

    public PResource getResource() {
        return resource;
    }

    public void setResource(PResource resource) {
        this.resource = resource;
    }

    public GeoRssMode getMode() {
        return mode;
    }

    public void setMode(GeoRssMode mode) {
        this.mode = mode;
    }

    public double getCenterLongitude() {
        return centerLongitude;
    }

    public void setCenterLongitude(double centerLongitude) {
        this.centerLongitude = centerLongitude;
    }

    public double getCenterLatitude() {
        return centerLatitude;
    }

    public void setCenterLatitude(double centerLatitude) {
        this.centerLatitude = centerLatitude;
    }

    public boolean isSpatial() {
        return spatial;
    }

    public void setSpatial(boolean spatial) {
        this.spatial = spatial;
    }

    public Class<?> getJsonView() {
        return jsonView;
    }

    public void setJsonView(Class<?> jsonView) {
        this.jsonView = jsonView;
    }

}
