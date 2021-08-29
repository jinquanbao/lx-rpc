package com.laoxin.rpc.http.handler;


import java.lang.annotation.Annotation;


public class MethodParamter {

    private int paramterIndex;

    private Class<? extends Annotation> annotationType;

    private String patamterName;

    public MethodParamter(int paramterIndex) {
        this.paramterIndex = paramterIndex;
    }

    public MethodParamter(int paramterIndex, Class<? extends Annotation> annotationType, String patamterName) {
        this.paramterIndex = paramterIndex;
        this.annotationType = annotationType;
        this.patamterName = patamterName;
    }

    public int getParamterIndex() {
        return paramterIndex;
    }

    public void setParamterIndex(int paramterIndex) {
        this.paramterIndex = paramterIndex;
    }

    public Class<? extends Annotation> getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(Class<? extends Annotation> annotationType) {
        this.annotationType = annotationType;
    }

    public String getPatamterName() {
        return patamterName;
    }

    public void setPatamterName(String patamterName) {
        this.patamterName = patamterName;
    }
}
