package org.tdar.core.serialize.collection;

import java.util.ArrayList;
import java.util.List;

import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.bean.entity.TdarUser;
import org.tdar.core.bean.entity.permissions.Permissions;

public class RequestCollection extends AbstractPersistable {

    private List<Long> collections = new ArrayList<>();
    private Permissions permission;
    private String name;
    private String descriptionRequest;
    private String descriptionResponse;
    private TdarUser contact;

    public List<Long> getCollections() {
        return collections;
    }

    public void setCollections(List<Long> collections) {
        this.collections = collections;
    }

    public Permissions getPermission() {
        return permission;
    }

    public void setPermission(Permissions permission) {
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TdarUser getContact() {
        return contact;
    }

    public void setContact(TdarUser contact) {
        this.contact = contact;
    }

    public String getDescriptionRequest() {
        return descriptionRequest;
    }

    public void setDescriptionRequest(String descriptionRequest) {
        this.descriptionRequest = descriptionRequest;
    }

    public String getDescriptionResponse() {
        return descriptionResponse;
    }

    public void setDescriptionResponse(String descriptionResponse) {
        this.descriptionResponse = descriptionResponse;
    }
}
