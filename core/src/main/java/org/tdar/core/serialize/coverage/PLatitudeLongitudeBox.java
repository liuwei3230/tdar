package org.tdar.core.serialize.coverage;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.bean.HasResource;
import org.tdar.core.bean.Obfuscatable;
import org.tdar.core.bean.keyword.GeographicKeyword;
import org.tdar.core.bean.resource.Resource;

/**
 * $Id$
 * 
 * Encapsulates min/max lat-long pairs representing the approximate spatial
 * coverage of a Resource.
 * 
 * @author <a href='Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Revision$
 */

@XmlRootElement
public class PLatitudeLongitudeBox extends AbstractPersistable {

    public static final double MAX_LATITUDE = 90d;
    public static final double MIN_LATITUDE = -90d;

    public static final double MAX_LONGITUDE = 180d;
    public static final double MIN_LONGITUDE = -180d;
    public static final int LATITUDE = 1;
    public static final int LONGITUDE = 2;
    private transient int hash = -1;
    public static final double ONE_MILE_IN_DEGREE_MINUTES = 0.01472d;

    private final transient Logger logger = LoggerFactory.getLogger(getClass());


    private Double south;
    private Double north;
    private Double west;
    private Double east;

    // used in testing and management
    private transient Set<GeographicKeyword> geographicKeywords;
    private Integer scale;
    public Set<GeographicKeyword> getGeographicKeywords() {
        return geographicKeywords;
    }

    public void setGeographicKeywords(Set<GeographicKeyword> geographicKeywords) {
        this.geographicKeywords = geographicKeywords;
    }

    public Integer getScale() {
        return scale;
    }

    public boolean isObfuscatedObjectDifferent() {
        return obfuscatedObjectDifferent;
    }

    private boolean obfuscatedObjectDifferent;

    public PLatitudeLongitudeBox() {
    }

    public PLatitudeLongitudeBox(Double west, Double south, Double east, Double north) {
        this.west = west;
        this.south = south;
        this.north = north;
        this.east = east;
    }

    public Double getSouth() {
        return south;
    }

    public void setSouth(Double south) {
        this.south = south;
    }

    public Double getNorth() {
        return north;
    }

    public void setNorth(Double north) {
        this.north = north;
    }

    public Double getWest() {
        return west;
    }

    public void setWest(Double west) {
        this.west = west;
    }

    public Double getEast() {
        return east;
    }

    public void setEast(Double east) {
        this.east = east;
    }

    public void setObfuscatedObjectDifferent(boolean obfuscatedObjectDifferent) {
        this.obfuscatedObjectDifferent = obfuscatedObjectDifferent;
        
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public Double getCenterLongitude() {
        // TODO Auto-generated method stub
        return null;
    }

    public Double getCenterLatitude() {
        // TODO Auto-generated method stub
        return null;
    }

    
}
