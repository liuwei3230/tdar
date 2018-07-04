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
    
    protected Double getCenterLong(Double minLong, Double maxLong) {
        // print out in degrees
        if (maxLong < minLong) {
            // logger.debug("min:" + minLong);
            // logger.debug("max:" + maxLong);

            // min is one side of the dateline and max is on the other
            if (maxLong < 0 && minLong > 0) {
                // convert the eastern side to a positive number as if we go to 360º
                double offsetRight = (-180d - maxLong) * -1d + 180d;
                // get the distance 1/2 way
                double ret = (offsetRight + minLong) / 2d;
                // logger.debug("min: {} offset:{} max: {}", minLong, offsetRight, maxLong);
                // logger.debug("toReturn:" + ret);
                // if we're greater than 180º, then subtract 360º to get the negative variant
                if (ret > 180) {
                    ret += -360d;
                }
                // logger.debug("to return: {}", ret);
                return ret;
            }

            Double tmp = (minLong + maxLong * -1d + 180d) / 2d;
            if (tmp > 180) {
                tmp = 180 - tmp;
            }
            return tmp;
        }

        return (minLong + maxLong) / 2d;
        /*
         * // http://stackoverflow.com/questions/4656802/midpoint-between-two-latitude-and-longitude
         * double dLon = Math.toRadians(maxLong - minLong);
         * 
         * double minLong_ = Math.toRadians(minLong);
         * 
         * double Bx = Math.cos(0.0) * Math.cos(dLon);
         * double By = Math.cos(0.0) * Math.sin(dLon);
         * double lon3 = minLong_ + Math.atan2(By, Math.cos(0.0) + Bx);
         * double degrees = Math.toDegrees(lon3);
         * if (degrees > 180) {
         * return -180 + degrees - 180;
         * }
         * return degrees;
         * 
         */
    }


    public void setScale(Integer scale) {
        this.scale = scale;
    }

    protected Double getCenterLat(Double double1, Double double2) {
        return (double1 + double2) / 2d;
    }

    public Double getCenterLongitude() {
        return getCenterLong(getWest(), getEast());
    }

    public Double getCenterLatitude() {
        return getCenterLat(getNorth(), getSouth());
    }

    
}
