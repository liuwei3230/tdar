package org.tdar.core.bean.projects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name="pm_task_one_off")
public class OneOffTask extends AbstractTask {

    private static final long serialVersionUID = -4254290630269268596L;

    @NotNull
    @Column(length = 512)
    @Length(max = 512)
    private String name;

    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    private String description;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
