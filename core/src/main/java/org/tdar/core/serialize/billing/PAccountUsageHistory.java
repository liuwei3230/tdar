package org.tdar.core.serialize.billing;

import java.util.Date;

import org.tdar.core.serialize.PAbstractPersistable;
import org.tdar.utils.MathUtils;

/**
 * Keeps track of the Account Usage History by period of time. This snapshot can be used in billing processes to track changes.
 * 
 * @author abrin
 *
 */
public class PAccountUsageHistory extends PAbstractPersistable {

    private Long filesUsed = 0L;
    private Long spaceUsedInBytes = 0L;
    private Long resourcesUsed = 0L;
    private Date date;

    public PAccountUsageHistory() {

    }


    public Long getFilesUsed() {
        return filesUsed;
    }

    public void setFilesUsed(Long filesUsed) {
        this.filesUsed = filesUsed;
    }

    public Long getSpaceUsedInBytes() {
        return spaceUsedInBytes;
    }

    public void setSpaceUsedInBytes(Long spaceUsedInBytes) {
        this.spaceUsedInBytes = spaceUsedInBytes;
    }

    public Long getResourcesUsed() {
        return resourcesUsed;
    }

    public void setResourcesUsed(Long resourcesUsed) {
        this.resourcesUsed = resourcesUsed;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getSpaceUsedInMb() {
        return MathUtils.divideByRoundUp(spaceUsedInBytes, MathUtils.ONE_MB);
    }
}
