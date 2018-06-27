package org.tdar.core.serialize.resource.sensory;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.StringUtils;
import org.tdar.core.bean.AbstractSequenced;
import org.tdar.core.bean.resource.sensory.ScannerTechnologyType;

/**
 * Represents a sensory-data scan.
 * 
 * @author abrin
 * 
 */
public class SensoryDataScan extends AbstractSequenced<SensoryDataScan> {
    private String filename;
    private String transformationMatrix;
    private String monumentName;
    private Long pointsInScan;
    private String scanNotes;
    private ScannerTechnologyType scannerTechnology;
    private String triangulationDetails;
    private String resolution;
    private String tofReturn;
    private String phaseFrequencySettings;
    private String phaseNoiseSettings;
    private String cameraExposureSettings;
    private Date scanDate;
    private boolean matrixApplied;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getTransformationMatrix() {
        return transformationMatrix;
    }

    public void setTransformationMatrix(String transformationMatrix) {
        this.transformationMatrix = transformationMatrix;
    }

    public String getMonumentName() {
        return monumentName;
    }

    public void setMonumentName(String monumentName) {
        this.monumentName = monumentName;
    }

    public Long getPointsInScan() {
        return pointsInScan;
    }

    public void setPointsInScan(Long pointsInScan) {
        this.pointsInScan = pointsInScan;
    }

    public String getScanNotes() {
        return scanNotes;
    }

    public void setScanNotes(String scanNotes) {
        this.scanNotes = scanNotes;
    }

    public ScannerTechnologyType getScannerTechnology() {
        return scannerTechnology;
    }

    public void setScannerTechnology(ScannerTechnologyType scannerTechnology) {
        this.scannerTechnology = scannerTechnology;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getTriangulationDetails() {
        return triangulationDetails;
    }

    public void setTriangulationDetails(String triangulationDetails) {
        this.triangulationDetails = triangulationDetails;
    }

    public String getTofReturn() {
        return tofReturn;
    }

    public void setTofReturn(String tofReturn) {
        this.tofReturn = tofReturn;
    }

    public String getPhaseFrequencySettings() {
        return phaseFrequencySettings;
    }

    public void setPhaseFrequencySettings(String phaseFrequencySettings) {
        this.phaseFrequencySettings = phaseFrequencySettings;
    }

    public String getPhaseNoiseSettings() {
        return phaseNoiseSettings;
    }

    public void setPhaseNoiseSettings(String phaseNoiseSettings) {
        this.phaseNoiseSettings = phaseNoiseSettings;
    }

    public String getCameraExposureSettings() {
        return cameraExposureSettings;
    }

    public void setCameraExposureSettings(String cameraExposureSettings) {
        this.cameraExposureSettings = cameraExposureSettings;
    }

    public Date getScanDate() {
        return scanDate;
    }

    public void setScanDate(Date scanDate) {
        this.scanDate = scanDate;
    }

    public boolean isMatrixApplied() {
        return matrixApplied;
    }

    public void setMatrixApplied(boolean matrixApplied) {
        this.matrixApplied = matrixApplied;
    }

    @Override
    public String toString() {
        return filename + " (" + getId() + " )";
    }
}
