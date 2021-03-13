package com.github.spsl.objectdiff.core;

import java.util.Optional;

public interface Differ {

    Optional<DiffNode> diff(DiffNode parentNode, String propertyName, Object origin, Object target);

}
