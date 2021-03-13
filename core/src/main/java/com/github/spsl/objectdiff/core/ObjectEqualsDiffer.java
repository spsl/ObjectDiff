package com.github.spsl.objectdiff.core;

import java.util.Optional;

public class ObjectEqualsDiffer extends BaseDiffer {
    @Override
    public Optional<DiffNode> diff(DiffNode parentNode, String propertyName, Object origin, Object target) {
        return immutableObjectDiff(parentNode, propertyName, origin, target);
    }
}
