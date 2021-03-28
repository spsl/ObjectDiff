package com.github.spsl.objectdiff.core;

import java.util.Optional;

public class ObjectEqualsDiffer extends AbstractDiffer {
    @Override
    public Optional<DiffNode> doDiff(DiffNode parentNode, String propertyName, Object origin, Object target) {
        return immutableObjectDiff(parentNode, propertyName, origin, target);
    }
}
