package com.github.spsl.objectdiff.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractDiffer implements Differ {

    private final Map<String, AtomicReference<Differ>> differMap = new ConcurrentHashMap<>();

    protected boolean existDiffer(String differTypeName) {
        return differMap.containsKey(differTypeName);
    }

    public void setDiffer(String differName, AtomicReference<Differ> differReference) {
        differMap.put(differName, differReference);
    }

    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, Integer from, Integer to) {
        return immutableObjectDiff(parentNode, propertyName, from, to);
    }

    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, Short from, Short to) {
        return immutableObjectDiff(parentNode, propertyName, from, to);
    }

    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, Long from, Long to) {
        return immutableObjectDiff(parentNode, propertyName, from, to);
    }

    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, Double from, Double to) {
        return immutableObjectDiff(parentNode, propertyName, from, to);
    }

    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, Float from, Float to) {
        return immutableObjectDiff(parentNode, propertyName, from, to);
    }

    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, Byte from, Byte to) {
        return immutableObjectDiff(parentNode, propertyName, from, to);
    }

    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, String from, String to) {
        return immutableObjectDiff(parentNode, propertyName, from, to);
    }

    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, Boolean from, Boolean to) {
        return immutableObjectDiff(parentNode, propertyName, from, to);
    }

    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, Character from, Character to) {
        return immutableObjectDiff(parentNode, propertyName, from, to);
    }

    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, byte from, byte to) {
        return immutableObjectDiff(parentNode, propertyName, from, to);
    }

    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, char from, char to) {
        return immutableObjectDiff(parentNode, propertyName, from, to);
    }

    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, short from, short to) {
        return immutableObjectDiff(parentNode, propertyName, from, to);
    }

    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, int from, int to) {
        return immutableObjectDiff(parentNode, propertyName, from, to);
    }

    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, float from, float to) {
        return immutableObjectDiff(parentNode, propertyName, from, to);
    }

    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, long from, long to) {
        return immutableObjectDiff(parentNode, propertyName, from, to);
    }

    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, double from, double to) {
        return immutableObjectDiff(parentNode, propertyName, from, to);
    }

    protected Optional<DiffNode> primitiveDiff(DiffNode parentNode, String propertyName, boolean from, boolean to) {
        return immutableObjectDiff(parentNode, propertyName, from, to);
    }

    protected Optional<DiffNode> immutableObjectDiff(DiffNode parentNode, String propertyName, Object from, Object to) {
        if (Objects.equals(from,to)) {
            return Optional.empty();
        }

        DiffNode node = new DiffNode();
        node.setPath(propertyName);
        node.setOriginValue(from);
        node.setTargetValue(to);
        node.setParentNode(parentNode);

        if (from == null) {
            node.setState(State.ADDED);
        } else if (to == null) {
            node.setState(State.DELETED);
        } else {
            node.setState(State.CHANGED);
        }
        return Optional.of(node);
    }

    protected Optional<DiffNode> customObjectDiff(String customTypeName, DiffNode parentNode, String propertyName, Object a, Object to) {
        AtomicReference<Differ> differReference = differMap.get(customTypeName);
        if (Objects.isNull(differReference) || differReference.get() == null) {
            return immutableObjectDiff(parentNode, propertyName, a, to);
        }
        return differReference.get().diff(parentNode, propertyName, a, to);
    }

    protected Optional<DiffNode> listDiff(DiffNode parentNode, String propertyName, List<?> a, List<?> to) {
        return collectionDiff(parentNode, propertyName, a, to);
    }

    protected Optional<DiffNode> mapDiff(String valueTypeName,
                                         DiffNode parentNode,
                                         String propertyName,
                                         Map<?, ?> from,
                                         Map<?, ?> to) {

        if (Objects.equals(from, to)) {
            return Optional.empty();
        }

        DiffNode diffNode = new DiffNode();
        diffNode.setParentNode(parentNode);
        diffNode.setPath(propertyName);
        diffNode.setOriginValue(from);
        diffNode.setTargetValue(to);

        if (from == null) {
            diffNode.setState(State.ADDED);
            return Optional.of(diffNode);
        } else if (to == null) {
            diffNode.setState(State.DELETED);
            return Optional.of(diffNode);
        }

        diffNode.setState(State.CHANGED);


        List<DiffNode> childNodeList = new ArrayList<>();
        from.forEach((k, v) -> {
            Optional<DiffNode> diffNodeOptional = customObjectDiff(valueTypeName, diffNode, String.valueOf(k), v, to.get(k));
            diffNodeOptional.ifPresent(childNodeList::add);
        });

        to.forEach((k, v) -> {
            if (!from.containsKey(k)) {
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

    protected Optional<DiffNode> collectionDiff(DiffNode parentNode, String propertyName, Collection<?> from, Collection<?> to) {
        if (Objects.equals(from, to)) {
            return Optional.empty();
        }

        DiffNode diffNode = new DiffNode();
        diffNode.setParentNode(parentNode);
        diffNode.setPath(propertyName);
        diffNode.setOriginValue(from);
        diffNode.setTargetValue(to);

        if (from == null) {
            diffNode.setState(State.ADDED);
            return Optional.of(diffNode);
        } else if (to == null) {
            diffNode.setState(State.DELETED);
            return Optional.of(diffNode);
        }

        diffNode.setState(State.CHANGED);

        // 遍历出添加的子项，删除的子项

        List<DiffNode> childChangedList = new ArrayList<>();
        to.forEach(item -> {
            if (!from.contains(item)) {

                immutableObjectDiff(diffNode, "", null, item)
                        .ifPresent(childChangedList::add);
            }
        });
        from.forEach(item -> {
            if (!to.contains(item)) {
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

    protected Optional<DiffNode> primitiveArrayDiff(DiffNode parentNode, String propertyName, Object[] from, Object[] to) {
        return listDiff(parentNode, propertyName, from == null ? null : Arrays.asList(from), to == null ? null : Arrays.asList(to));
    }
}