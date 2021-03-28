package com.github.spsl.objectdiff.core;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DifferClassFactory {

    private static final DifferClassFactory INSTANCE = new DifferClassFactory();

    private DifferClassFactory() {

    }

    public static DifferClassFactory getInstance() {
        return INSTANCE;
    }

    private final Map<Class, DifferClassWrapper> map = new ConcurrentHashMap<>();

    private final DifferClassGenerator differClassGenerator = new JavassistDifferClassGenerator();


    public DifferClassWrapper getDifferClassWrapper(Class<?> type) {
        assert type != null;

        DifferClassWrapper wrapper = map.get(type);

        if (wrapper == null) {
            wrapper = differClassGenerator.generator(type);
            DifferClassWrapper preWrapper = map.putIfAbsent(type, wrapper);
            if (preWrapper == null) {
                generateDependClass(wrapper.getDependClasses());
            }
            return map.get(type);
        }
        return wrapper;
    }

    private void generateDependClass(Set<Class<?>> dependClasses) {
        if (dependClasses == null || dependClasses.isEmpty()) {
            return;
        }

        dependClasses.forEach(this::getDifferClassWrapper);
    }

}
