package com.github.spsl.objectdiff.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public abstract class BaseDiffer implements Differ {

    private final Map<String, AtomicReference<Differ>> differMap = new ConcurrentHashMap<>();

    protected boolean existDiffer(String differTypeName) {
        return differMap.containsKey(differTypeName);
    }

    public void setDiffer(String differName, AtomicReference<Differ> differReference) {
        differMap.put(differName, differReference);
    }

    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, Integer a, Integer b) {
        return immutableObjectDiff(parentNode, propertyName, a, b);
    }
    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, Short a, Short b) {
        return immutableObjectDiff(parentNode, propertyName, a, b);
    }
    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, Long a, Long b) {
        return immutableObjectDiff(parentNode, propertyName, a, b);
    }
    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, Double a, Double b) {
        return immutableObjectDiff(parentNode, propertyName, a, b);
    }
    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, Float a, Float b) {
        return immutableObjectDiff(parentNode, propertyName, a, b);
    }
    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, Byte a, Byte b) {
        return immutableObjectDiff(parentNode, propertyName, a, b);
    }
    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, String a, String b) {
        return immutableObjectDiff(parentNode, propertyName, a, b);
    }
    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, Boolean a, Boolean b) {
        return immutableObjectDiff(parentNode, propertyName, a, b);
    }
    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, Character a, Character b) {
        return immutableObjectDiff(parentNode, propertyName, a, b);
    }

    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, byte a, byte b) {
        return immutableObjectDiff(parentNode, propertyName, a, b);
    }

    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, char a, char b) {
        return immutableObjectDiff(parentNode, propertyName, a, b);
    }
    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, short a, short b) {
        return immutableObjectDiff(parentNode, propertyName, a, b);
    }
    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, int a, int b) {
        return immutableObjectDiff(parentNode, propertyName, a, b);
    }
    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, float a, float b) {
        return immutableObjectDiff(parentNode, propertyName, a, b);
    }
    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, long a, long b) {
        return immutableObjectDiff(parentNode, propertyName, a, b);
    }
    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, double a, double b) {
        return immutableObjectDiff(parentNode, propertyName, a, b);
    }
    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, boolean a, boolean b) {
        return immutableObjectDiff(parentNode, propertyName, a, b);
    }

    protected Optional<DiffNode> immutableObjectDiff(DiffNode parentNode, String propertyName, Object a, Object b) {
        if (Objects.equals(a,b)) {
            return Optional.empty();
        }

        DiffNode node = new DiffNode();
        node.setPath(propertyName);
        node.setOriginValue(a);
        node.setTargetValue(b);
        node.setParentNode(parentNode);

        if (a == null) {
            node.setState(DiffState.ADDED);
        } else if (b == null) {
            node.setState(DiffState.DELETED);
        } else {
            node.setState(DiffState.CHANGED);
        }
        return Optional.of(node);
    }

    protected Optional<DiffNode> customObjectDiff(String customTypeName, DiffNode parentNode, String propertyName, Object a, Object b) {
        AtomicReference<Differ> differReference = differMap.get(customTypeName);
        if (Objects.isNull(differReference) || differReference.get() == null) {
            return immutableObjectDiff(parentNode, propertyName, a, b);
        }
        return differReference.get().diff(parentNode, propertyName, a, b);
    }

    protected Optional<DiffNode> listDiff(DiffNode parentNode, String propertyName, List<?> a, List<?> b) {
        return collectionDiff(parentNode, propertyName, a, b);
    }

    protected Optional<DiffNode> mapDiff(String valueTypeName,
                                         DiffNode parentNode,
                                         String propertyName,
                                         Map<?, ?> a,
                                         Map<?, ?> b) {

        if (Objects.equals(a, b)) {
            return Optional.empty();
        }

        DiffNode diffNode = new DiffNode();
        diffNode.setParentNode(parentNode);
        diffNode.setPath(propertyName);
        diffNode.setOriginValue(a);
        diffNode.setTargetValue(b);

        if (a == null) {
            diffNode.setState(DiffState.ADDED);
            return Optional.of(diffNode);
        } else if (b == null) {
            diffNode.setState(DiffState.DELETED);
            return Optional.of(diffNode);
        }

        diffNode.setState(DiffState.CHANGED);


        List<DiffNode> childNodeList = new ArrayList<>();
        a.forEach((k, v) -> {
            Optional<DiffNode> diffNodeOptional = customObjectDiff(valueTypeName, diffNode, String.valueOf(k), v, b.get(k));
            diffNodeOptional.ifPresent(childNodeList::add);
        });

        b.forEach((k, v) -> {
            if (!a.containsKey(k)) {
                Optional<DiffNode> diffNodeOptional = customObjectDiff(valueTypeName, diffNode, String.valueOf(k), null, v);
                diffNodeOptional.ifPresent(childNodeList::add);
            }
        });

        if (childNodeList.isEmpty()) {
            return Optional.empty();
        }
        diffNode.setChildNodes(childNodeList);
        return Optional.of(diffNode);
    }

    protected Optional<DiffNode> collectionDiff(DiffNode parentNode, String propertyName, Collection<?> a, Collection<?> b) {
        if (Objects.equals(a, b)) {
            return Optional.empty();
        }

        DiffNode diffNode = new DiffNode();
        diffNode.setParentNode(parentNode);
        diffNode.setPath(propertyName);
        diffNode.setOriginValue(a);
        diffNode.setTargetValue(b);

        if (a == null) {
            diffNode.setState(DiffState.ADDED);
            return Optional.of(diffNode);
        } else if (b == null) {
            diffNode.setState(DiffState.DELETED);
            return Optional.of(diffNode);
        }

        diffNode.setState(DiffState.CHANGED);

        // 遍历出添加的子项，删除的子项

        List<DiffNode> childChangedList = new ArrayList<>();
        b.forEach(item -> {
            if (!a.contains(item)) {

                immutableObjectDiff(diffNode, "", null, item)
                        .ifPresent(childChangedList::add);
            }
        });
        a.forEach(item -> {
            if (!b.contains(item)) {
                immutableObjectDiff(diffNode, "", item, null)
                        .ifPresent(childChangedList::add);
            }
        });
        if (childChangedList.isEmpty()) {
            return Optional.empty();
        }
        diffNode.setChildNodes(childChangedList);
        return Optional.of(diffNode);
    }

    protected Optional<DiffNode> primitiveArrayDiff(DiffNode parentNode, String propertyName, Object[] a, Object[] b) {
        return listDiff(parentNode, propertyName, a == null ? null : Arrays.asList(a), b == null ? null : Arrays.asList(b));
    }
}
