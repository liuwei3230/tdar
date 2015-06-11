package org.tdar.core.bean.projects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.tdar.core.bean.FieldLength;

@Entity
@Table(name = "pm_task_project")
public class ProjectTask extends AbstractTask {

    private static final long serialVersionUID = -5811970129767276663L;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", length = FieldLength.FIELD_LENGTH_50)
    private TaskType type;

}
