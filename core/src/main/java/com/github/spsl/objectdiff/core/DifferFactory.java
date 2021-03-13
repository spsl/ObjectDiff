package com.github.spsl.objectdiff.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DifferFactory {

    private static final DifferFactory INSTANCE = new DifferFactory();

    private DifferFactory() {

    }

    public static DifferFactory getInstance() {
        return INSTANCE;
    }

    private static final Map<Class, Differ> map = new ConcurrentHashMap<>();

    private final DifferClassGenerator differClassGenerator = new JavassistDifferClassGenerator();

    public Differ getDiffer(Class<?> type) {
        return differClassGenerator.generator(type);
    }

}
