package com.virhon.fintech.gl.api;

import com.virhon.fintech.gl.exception.LedgerException;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class RequestValidator {
    public void checkNotNullAllFields()
            throws LedgerException, IntrospectionException, InvocationTargetException, IllegalAccessException {
        for (Field f: getClass().getDeclaredFields()) {
            Object value = new PropertyDescriptor(f.getName(), this.getClass()).getReadMethod().invoke(this);
            if (value == null) {
                throw LedgerException.notNullValue(f.getName());
            }
        }
    }
}
