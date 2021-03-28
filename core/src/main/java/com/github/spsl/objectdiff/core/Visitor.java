package com.github.spsl.objectdiff.core;

public interface Visitor {
    void visit(String fullPath, DiffNode node);
}
