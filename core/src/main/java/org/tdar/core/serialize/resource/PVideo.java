package org.tdar.core.serialize.resource;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import org.tdar.core.bean.resource.ResourceType;

@XmlRootElement(name = "Pvideo")
/**
 * Represents a "video" resource.
 * 
 * @author abrin
 *
 */
public class PVideo extends PInformationResource {

    public PVideo() {
        setResourceType(ResourceType.VIDEO);
    }

    private Integer fps;
    private Integer kbps;

    private String videoCodec;
    private String audioCodec;
    private String audioChannels;
    private Integer audioKbps;
    private Integer width;
    private Integer height;
    private Integer sampleFrequency;

    public Integer getFps() {
        return fps;
    }

    public void setFps(Integer fps) {
        this.fps = fps;
    }

    public Integer getKbps() {
        return kbps;
    }

    public void setKbps(Integer kbps) {
        this.kbps = kbps;
    }

    public String getVideoCodec() {
        return videoCodec;
    }

    public void setVideoCodec(String videoCodec) {
        this.videoCodec = videoCodec;
    }

    public String getAudioCodec() {
        return audioCodec;
    }

    public void setAudioCodec(String audioCodec) {
        this.audioCodec = audioCodec;
    }

    public String getAudioChannels() {
        return audioChannels;
    }

    public void setAudioChannels(String audioChannels) {
        this.audioChannels = audioChannels;
    }

    public Integer getAudioKbps() {
        return audioKbps;
    }

    public void setAudioKbps(Integer audioKbps) {
        this.audioKbps = audioKbps;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getSampleFrequency() {
        return sampleFrequency;
    }

    public void setSampleFrequency(Integer sampleFrequency) {
        this.sampleFrequency = sampleFrequency;
    }

    @Override
    @Transient
    public boolean isSupportsThumbnails() {
        return true;
    }
}
