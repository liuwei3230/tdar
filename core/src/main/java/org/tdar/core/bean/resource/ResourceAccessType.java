/**
 * $Id$
 * 
 * @author $Author$
 * @version $Revision$
 */
package org.tdar.core.bean.resource;

import org.tdar.core.bean.HasLabel;
import org.tdar.core.bean.Localizable;
import org.tdar.core.bean.resource.file.InformationResourceFile;
import org.tdar.utils.MessageHelper;

/**
 * Describes the aggregate restrictions on all of the files on the InformationResource.
 * 
 * @author Adam Brin
 * 
 */
public enum ResourceAccessType implements HasLabel, Localizable {
    CITATION("Citation Only"),
    PUBLICALLY_ACCESSIBLE("Publicly Accessible Files"),
    PARTIALLY_RESTRICTED("Some Files Restricted"),
    RESTRICTED(
            "Restricted Files");

    private String label;

    ResourceAccessType(String label) {
        this.setLabel(label);
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getLocaleKey() {
        return MessageHelper.formatLocalizableKey(this);
    }

    private void setLabel(String label) {
        this.label = label;
    }

    public static ResourceAccessType getResourceAccessType(InformationResource ir) {
        int totalFiles = 0;
        int publicFiles = 0;
        for (InformationResourceFile irf : ir.getInformationResourceFiles()) {
            if (irf.isDeleted()) {
                continue;
            }
            totalFiles++;
            if (irf.getRestriction().isRestricted()) {
                continue;
            }
            publicFiles++;
        }
        if (totalFiles > 0) {
            if (publicFiles == 0) {
                return ResourceAccessType.RESTRICTED;
            }
            if (publicFiles == totalFiles) {
                return ResourceAccessType.PUBLICALLY_ACCESSIBLE;
            }
            return ResourceAccessType.PARTIALLY_RESTRICTED;
        }
        return ResourceAccessType.CITATION;
    }

    public static boolean isPublicallyAccessible(InformationResource dataset) {
        return PUBLICALLY_ACCESSIBLE == getResourceAccessType(dataset);
    }

}
