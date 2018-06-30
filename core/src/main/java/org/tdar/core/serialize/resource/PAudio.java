package org.tdar.core.serialize.resource;

import javax.xml.bind.annotation.XmlRootElement;

import org.tdar.core.bean.resource.ResourceType;

/**
 * The elements on this resource are inspired by:
 * http://guides.archaeologydataservice.ac.uk/g2gp/Audio_3
 * Not done yet are:
 * Transcript
 * Also, not yet resolved: we should try to automatically extract some of the fields from the audio file: are the ones below enough?
 * Note: Depending on how we implement length, it should likely be kept on the InformationResourceFile
 * (unless it differs between the archival and non-archival). In that case, the derivative should live on the version.
 * Until I get a better understanding from actual users as to how the audio is to work I'm not populating the fields below. I'm going to simply put a very
 * long descriptive string into the audio codec that displays all of the information suggested by the guide above, and show that on the form.
 * 
 * @author Martin Paulo
 */
@XmlRootElement(name = "Paudio")
public class PAudio extends PInformationResource {

    private String software;
    private Integer bitDepth;
    private Integer bitRate;
    private Integer sampleRate;
    private String audioCodec;

    public PAudio() {
        setResourceType(ResourceType.AUDIO);
    }

    /**
     * @return The software (or device) used to create the file.
     */
    public String getSoftware() {
        return software;
    }

    /**
     * @param software
     *            The software (or device) used to create the file.
     */
    public void setSoftware(String software) {
        this.software = software;
    }

    /**
     * @return The bit depth of the recording E.g. 16 or 24 bit.
     */
    public Integer getBitDepth() {
        return bitDepth;
    }

    /**
     * @param bitDepth
     *            The bit depth of the recording E.g. 16 or 24 bit.
     */
    public void setBitDepth(Integer bitDepth) {
        this.bitDepth = bitDepth;
    }

    /**
     * @return The bit rate Optional, often recorded as kbps
     */
    public Integer getBitRate() {
        return bitRate;
    }

    /**
     * @param bitRate
     *            The bit rate Optional, often recorded as kbps
     */
    public void setBitRate(Integer bitRate) {
        this.bitRate = bitRate;
    }

    /**
     * @return The sample rate (KHz) E.g. 44.1kHz
     */
    public Integer getSampleRate() {
        return sampleRate;
    }

    /**
     * @param sampleRate
     *            The sample rate (KHz) E.g. 44.1kHz
     */
    public void setSampleRate(Integer sampleRate) {
        this.sampleRate = sampleRate;
    }

    /**
     * @return The codec used in creating the file e.g. FLAC or AAC.
     */
    public String getAudioCodec() {
        return audioCodec;
    }

    /**
     * @param audioCodec
     *            The codec used in creating the file e.g. FLAC or AAC.
     */
    public void setAudioCodec(String audioCodec) {
        this.audioCodec = audioCodec;
    }

    @Override
    public PAudio getTransientCopyForWorkflow() {
        final PAudio result = new PAudio();
        result.setId(this.getId());
        return result;
    }

    @Override
    public void updateFromTransientResource(final PInformationResource transientAudio) {
        if (transientAudio == null) {
            // Should never be here, so perhaps we should do more than return?
            return;
        }
        this.audioCodec = ((PAudio) transientAudio).getAudioCodec();
    }

}
