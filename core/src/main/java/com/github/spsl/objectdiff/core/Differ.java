package com.github.spsl.objectdiff.core;

import java.util.Optional;

public interface Differ {

    Optional<DiffNode> diff(Object from, Object to);

}
