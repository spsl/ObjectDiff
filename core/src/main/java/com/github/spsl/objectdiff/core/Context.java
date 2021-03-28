package com.github.spsl.objectdiff.core;

import java.util.HashSet;
import java.util.Set;

public class Context {

    private Set<String> excludeProperties;

    private Set<Tuple2<Object, Object>> tracker;

    public Context() {
        tracker = new HashSet<>();
        excludeProperties = new HashSet<>();
    }

    public Set<String> getExcludeProperties() {
        return excludeProperties;
    }

    public Set<Tuple2<Object, Object>> getTracker() {
        return tracker;
    }

}
