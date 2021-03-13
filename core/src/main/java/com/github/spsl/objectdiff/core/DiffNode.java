package com.github.spsl.objectdiff.core;

import java.util.List;


public class DiffNode {

    private DiffNode parentNode;

    private List<DiffNode> childNodes;

    private String path;

    private Object originValue;

    private Object targetValue;

    private DiffState state;

    public DiffState getState() {
        return state;
    }

    public void setState(DiffState state) {
        this.state = state;
    }

    public DiffNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(DiffNode parentNode) {
        this.parentNode = parentNode;
    }

    public List<DiffNode> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(List<DiffNode> childNodes) {
        this.childNodes = childNodes;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Object getOriginValue() {
        return originValue;
    }

    public void setOriginValue(Object originValue) {
        this.originValue = originValue;
    }

    public Object getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(Object targetValue) {
        this.targetValue = targetValue;
    }
}
