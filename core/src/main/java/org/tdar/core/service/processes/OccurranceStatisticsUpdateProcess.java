package org.tdar.core.service.processes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tdar.core.service.EntityService;
import org.tdar.core.service.GenericKeywordService;

@Component
@Scope("prototype")
public class OccurranceStatisticsUpdateProcess extends AbstractScheduledProcess {

    private static final long serialVersionUID = 8726938824021007982L;

    @Autowired
    private transient GenericKeywordService genericKeywordService;

    @Autowired
    private transient EntityService entityService;

    private boolean run = false;

    public void execute() {
        run = true;
        genericKeywordService.updateOccurranceValues();
        entityService.updatePersonOcurrances();
    }

    public boolean isEnabled() {
        return true;
    }

    public String getDisplayName() {
        return "Weekly Ocurrence Count Info";
    }

    public boolean isCompleted() {
        return run;
    }

    public boolean isSingleRunProcess() {
        return false;
    }

}
