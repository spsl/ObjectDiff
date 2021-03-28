package com.github.spsl.objectdiff.core;

import java.util.Set;

public class DifferClassWrapper {

    private Class<?> differClass;

    private Set<Class<?>> dependClasses;

    public Class<?> getDifferClass() {
        return differClass;
    }

    public void setDifferClass(Class<?> differClass) {
        this.differClass = differClass;
    }

    public Set<Class<?>> getDependClasses() {
        return dependClasses;
    }

    public void setDependClasses(Set<Class<?>> dependClasses) {
        this.dependClasses = dependClasses;
    }
}
