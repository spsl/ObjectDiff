package com.github.spsl.objectdiff.core;

import java.util.List;


public class DiffNode {

    private DiffNode parentNode;

    private List<DiffNode> childNodes;

    private String property;

    private String fullPath;

    private Object originValue;

    private Object targetValue;

    private State state;

    public State getState() {
        return state;
    }

    public void visit(Visitor visitor) {
        if (visitor == null) {
            throw new NullPointerException();
        }

        visit("/", visitor);
    }


    private void visit(String parentPath, Visitor visitor) {
        String fullPath;
        if ("/".equals(parentPath)) {
            fullPath = parentPath;
        } else if (this.property != null && !"".equals(this.property.trim())){
            fullPath = parentPath;
        } else {
            fullPath = parentPath + this.getProperty();
        }
        visitor.visit(fullPath,this);
        if (childNodes != null) {
            childNodes.forEach(item -> item.visit(fullPath, visitor));
        }
    }

    public void setState(State state) {
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

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
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

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }
}
