package org.tdar.core.serialize.resource;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.tdar.core.bean.resource.ResourceType;
import org.tdar.core.bean.resource.sensory.ScannerTechnologyType;
import org.tdar.core.serialize.resource.sensory.SensoryDataImage;
import org.tdar.core.serialize.resource.sensory.SensoryDataScan;

@XmlRootElement(name = "sensoryData")
public class PSensoryData extends PDataset {

    private String monumentNumber;
    private String surveyLocation; // FIXME: remove this field
    private Date surveyDateBegin;
    private Date surveyDateEnd;
    private String surveyConditions;
    private String scannerDetails;
    private String companyName;
    private boolean turntableUsed;
    private String rgbDataCaptureInfo;
    private String estimatedDataResolution;
    private Long totalScansInProject;
    private String finalDatasetDescription;
    private String additionalProjectNotes;
    private String planimetricMapFilename;
    private String controlDataFilename;

    public enum RgbCapture {
        NA("None"),
        INTERNAL("Internal"),
        EXTERNAL("External");
        String label;

        RgbCapture(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private RgbCapture rgbCapture;
    private String registeredDatasetName;
    private Integer scansUsed;
    private Integer scansTotalAcquired;
    private Double registrationErrorUnits;
    private Long finalRegistrationPoints;
    private String registrationMethod;
    private String preMeshDatasetName;
    private Long preMeshPoints;
    private boolean premeshOverlapReduction;
    private boolean premeshSmoothing;
    private boolean premeshSubsampling;
    private boolean premeshColorEditions;
    private String pointDeletionSummary;
    private String meshDatasetName;
    private boolean meshHolesFilled;
    private boolean meshSmoothing;
    private boolean meshColorEditions;
    private boolean meshHealingDespiking;
    private Long meshTriangleCount;
    private boolean meshRgbIncluded;
    private boolean meshdataReduction;
    private String meshAdjustmentMatrix;
    private String meshProcessingNotes;
    private String decimatedMeshDataset;
    private Long decimatedMeshOriginalTriangleCount;
    private Long decimatedMeshTriangleCount;
    private boolean rgbPreservedFromOriginal;
    private Set<SensoryDataScan> sensoryDataScans = new LinkedHashSet<SensoryDataScan>();
    private Set<SensoryDataImage> sensoryDataImages = new LinkedHashSet<SensoryDataImage>();
    private ScannerTechnologyType scannerTechnology;
    private String cameraDetails;

    public PSensoryData() {
        setResourceType(ResourceType.SENSORY_DATA);
    }

    public String getMonumentNumber() {
        return monumentNumber;
    }

    public void setMonumentNumber(String monumentNumber) {
        this.monumentNumber = monumentNumber;
    }

    public String getSurveyLocation() {
        return surveyLocation;
    }

    public void setSurveyLocation(String surveyLocation) {
        this.surveyLocation = surveyLocation;
    }

    public String getSurveyConditions() {
        return surveyConditions;
    }

    public void setSurveyConditions(String surveyConditions) {
        this.surveyConditions = surveyConditions;
    }

    public String getScannerDetails() {
        return scannerDetails;
    }

    public void setScannerDetails(String scannerDetails) {
        this.scannerDetails = scannerDetails;
    }

    public String getCompanyName() {
        return companyName;
    }

    public boolean isTurntableUsed() {
        return turntableUsed;
    }

    public void setTurntableUsed(boolean isTurntableUsed) {
        this.turntableUsed = isTurntableUsed;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRgbDataCaptureInfo() {
        return rgbDataCaptureInfo;
    }

    public void setRgbDataCaptureInfo(String rgbDataCaptureInfo) {
        this.rgbDataCaptureInfo = rgbDataCaptureInfo;
    }

    public String getEstimatedDataResolution() {
        return estimatedDataResolution;
    }

    public void setEstimatedDataResolution(String estimatedDataResolution) {
        this.estimatedDataResolution = estimatedDataResolution;
    }

    public Long getTotalScansInProject() {
        return totalScansInProject;
    }

    public void setTotalScansInProject(Long totalScansInProject) {
        this.totalScansInProject = totalScansInProject;
    }

    public String getFinalDatasetDescription() {
        return finalDatasetDescription;
    }

    public void setFinalDatasetDescription(String finalDatasetDescription) {
        this.finalDatasetDescription = finalDatasetDescription;
    }

    public String getAdditionalProjectNotes() {
        return additionalProjectNotes;
    }

    public void setAdditionalProjectNotes(String additionalProjectNotes) {
        this.additionalProjectNotes = additionalProjectNotes;
    }

    public String getRegisteredDatasetName() {
        return registeredDatasetName;
    }

    public void setRegisteredDatasetName(String registeredDatasetName) {
        this.registeredDatasetName = registeredDatasetName;
    }

    public Integer getScansUsed() {
        return scansUsed;
    }

    public void setScansUsed(Integer scansUsed) {
        this.scansUsed = scansUsed;
    }

    public Integer getScansTotalAcquired() {
        return scansTotalAcquired;
    }

    public void setScansTotalAcquired(Integer scansTotalAcquired) {
        this.scansTotalAcquired = scansTotalAcquired;
    }

    public Double getRegistrationErrorUnits() {
        return registrationErrorUnits;
    }

    public void setRegistrationErrorUnits(Double registrationErrorUnits) {
        this.registrationErrorUnits = registrationErrorUnits;
    }

    public Long getFinalRegistrationPoints() {
        return finalRegistrationPoints;
    }

    public void setFinalRegistrationPoints(Long finalRegistrationPoints) {
        this.finalRegistrationPoints = finalRegistrationPoints;
    }

    public String getPreMeshDatasetName() {
        return preMeshDatasetName;
    }

    public void setPreMeshDatasetName(String preMeshDatasetName) {
        this.preMeshDatasetName = preMeshDatasetName;
    }

    public Long getPreMeshPoints() {
        return preMeshPoints;
    }

    public void setPreMeshPoints(Long preMeshPoints) {
        this.preMeshPoints = preMeshPoints;
    }

    public String getMeshDatasetName() {
        return meshDatasetName;
    }

    public void setMeshDatasetName(String meshDatasetName) {
        this.meshDatasetName = meshDatasetName;
    }

    public Long getMeshTriangleCount() {
        return meshTriangleCount;
    }

    public void setMeshTriangleCount(Long meshTriangleCount) {
        this.meshTriangleCount = meshTriangleCount;
    }

    public String getMeshProcessingNotes() {
        return meshProcessingNotes;
    }

    public void setMeshProcessingNotes(String meshProcessingNotes) {
        this.meshProcessingNotes = meshProcessingNotes;
    }

    public String getDecimatedMeshDataset() {
        return decimatedMeshDataset;
    }

    public void setDecimatedMeshDataset(String decimatedMeshDataset) {
        this.decimatedMeshDataset = decimatedMeshDataset;
    }

    public Long getDecimatedMeshOriginalTriangleCount() {
        return decimatedMeshOriginalTriangleCount;
    }

    public void setDecimatedMeshOriginalTriangleCount(Long decimatedMeshOriginalTriangleCount) {
        this.decimatedMeshOriginalTriangleCount = decimatedMeshOriginalTriangleCount;
    }

    public Long getDecimatedMeshTriangleCount() {
        return decimatedMeshTriangleCount;
    }

    public void setDecimatedMeshTriangleCount(Long decimatedMeshTringleCount) {
        this.decimatedMeshTriangleCount = decimatedMeshTringleCount;
    }

    public Date getSurveyDateBegin() {
        return surveyDateBegin;
    }

    public void setSurveyDateBegin(Date surveyDate) {
        this.surveyDateBegin = surveyDate;
    }

    public Date getSurveyDateEnd() {
        return surveyDateEnd;
    }

    public void setSurveyDateEnd(Date surveyDateEnd) {
        this.surveyDateEnd = surveyDateEnd;
    }

    public boolean isPremeshOverlapReduction() {
        return premeshOverlapReduction;
    }

    public void setPremeshOverlapReduction(boolean premeshOverlapReduction) {
        this.premeshOverlapReduction = premeshOverlapReduction;
    }

    public boolean isPremeshSmoothing() {
        return premeshSmoothing;
    }

    public void setPremeshSmoothing(boolean premeshSmoothing) {
        this.premeshSmoothing = premeshSmoothing;
    }

    public boolean isMeshColorEditions() {
        return meshColorEditions;
    }

    public void setMeshColorEditions(boolean meshColorEditions) {
        this.meshColorEditions = meshColorEditions;
    }

    public boolean isMeshHealingDespiking() {
        return meshHealingDespiking;
    }

    public void setMeshHealingDespiking(boolean meshHealingDespiking) {
        this.meshHealingDespiking = meshHealingDespiking;
    }

    public boolean isPremeshSubsampling() {
        return premeshSubsampling;
    }

    public void setPremeshSubsampling(boolean premeshSubSampling) {
        this.premeshSubsampling = premeshSubSampling;
    }

    public boolean isPremeshColorEditions() {
        return premeshColorEditions;
    }

    public void setPremeshColorEditions(boolean premeshColorEditions) {
        this.premeshColorEditions = premeshColorEditions;
    }

    public String getPointDeletionSummary() {
        return pointDeletionSummary;
    }

    public void setPointDeletionSummary(String pointDeletionSummary) {
        this.pointDeletionSummary = pointDeletionSummary;
    }

    public boolean isMeshHolesFilled() {
        return meshHolesFilled;
    }

    public void setMeshHolesFilled(boolean meshHolesFilled) {
        this.meshHolesFilled = meshHolesFilled;
    }

    public boolean isMeshSmoothing() {
        return meshSmoothing;
    }

    public void setMeshSmoothing(boolean meshSmoothing) {
        this.meshSmoothing = meshSmoothing;
    }

    public boolean isMeshRgbIncluded() {
        return meshRgbIncluded;
    }

    public void setMeshRgbIncluded(boolean meshRgbIncluded) {
        this.meshRgbIncluded = meshRgbIncluded;
    }

    public boolean isMeshdataReduction() {
        return meshdataReduction;
    }

    public void setMeshdataReduction(boolean meshdataReduction) {
        this.meshdataReduction = meshdataReduction;
    }

    public String getMeshAdjustmentMatrix() {
        return meshAdjustmentMatrix;
    }

    public void setMeshAdjustmentMatrix(String meshAdjustmentMatrix) {
        this.meshAdjustmentMatrix = meshAdjustmentMatrix;
    }

    public boolean isRgbPreservedFromOriginal() {
        return rgbPreservedFromOriginal;
    }

    public void setRgbPreservedFromOriginal(boolean rgbPreservedFromOriginal) {
        this.rgbPreservedFromOriginal = rgbPreservedFromOriginal;
    }

    @XmlElementWrapper(name = "sensoryDataScans")
    @XmlElement(name = "sensoryDataScan")
    public Set<SensoryDataScan> getSensoryDataScans() {
        return sensoryDataScans;
    }

    public void setSensoryDataScans(LinkedHashSet<SensoryDataScan> sensoryDataScans) {
        this.sensoryDataScans = sensoryDataScans;
    }

    @XmlElementWrapper(name = "sensoryDataImages")
    @XmlElement(name = "sensoryDataImage")
    public Set<SensoryDataImage> getSensoryDataImages() {
        return sensoryDataImages;
    }

    public void setSensoryDataImages(LinkedHashSet<SensoryDataImage> sensoryDataImages) {
        this.sensoryDataImages = sensoryDataImages;
    }

    public String getPlanimetricMapFilename() {
        return planimetricMapFilename;
    }

    public void setPlanimetricMapFilename(String planimetricMapFilename) {
        this.planimetricMapFilename = planimetricMapFilename;
    }

    public String getControlDataFilename() {
        return controlDataFilename;
    }

    public void setControlDataFilename(String controlDataFilename) {
        this.controlDataFilename = controlDataFilename;
    }

    public String getRegistrationMethod() {
        return registrationMethod;
    }

    public void setRegistrationMethod(String registrationMethod) {
        this.registrationMethod = registrationMethod;
    }

    @Override
    public boolean isSupportsThumbnails() {
        return true;
    }

    public ScannerTechnologyType getScannerTechnology() {
        return scannerTechnology;
    }

    public void setScannerTechnology(ScannerTechnologyType scannerTechnology) {
        this.scannerTechnology = scannerTechnology;
    }

    public RgbCapture getRgbCapture() {
        return rgbCapture;
    }

    public void setRgbCapture(RgbCapture rgbCapture) {
        this.rgbCapture = rgbCapture;
    }

    public String getCameraDetails() {
        return cameraDetails;
    }

    public void setCameraDetails(String cameraDetails) {
        this.cameraDetails = cameraDetails;
    }

    @Override
    public boolean isHasBrowsableImages() {
        return true;
    }
}
