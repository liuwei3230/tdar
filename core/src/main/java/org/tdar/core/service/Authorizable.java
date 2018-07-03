package org.tdar.core.service;

import org.tdar.core.bean.entity.TdarUser;

import com.opensymphony.xwork2.TextProvider;

public interface Authorizable<T> extends TextProvider{

    public boolean authorize(T t, TdarUser user) throws Exception;
}
