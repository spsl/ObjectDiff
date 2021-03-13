package com.github.spsl.objectdiff.core;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class JavassistDifferGenerator implements DifferGenerator{

    JavassistDifferGenerator() {
        System.out.println("");
    }

    private final Map<Class<?>, BaseDiffer> cachedDifferMap = new ConcurrentHashMap<>();

    @Override
    public Differ generator(Class<?> clazz) {
        Set<Class<?>> dependTypeSet = new HashSet<>();

        BaseDiffer differ = doGenerator(clazz, dependTypeSet);
        if (differ == null) {
            return new ObjectEqualsDiffer();
        }
        if (!dependTypeSet.isEmpty()) {
            dependTypeSet.forEach(type -> {
                if (!differ.existDiffer(type.getName())) {
                    AtomicReference<Differ> reference = new AtomicReference<>();
                    differ.setDiffer(type.getName(), reference);
                    Differ dependDiffer = DifferFactory.getInstance().getDiffer(type);
                    reference.set(dependDiffer);
                }
            });
        }
        return differ;
    }

    private synchronized BaseDiffer doGenerator(Class<?> clazz, Set<Class<?>> dependTypeSet) {

        try {
            BaseDiffer differ = cachedDifferMap.get(clazz);

            if (differ != null) {
                return differ;
            }

            differ = cachedDifferMap.get(clazz);
            if (differ != null) {
                return differ;
            }

            ClassPool classPool = ClassPool.getDefault();
            String clazzName = clazz.getName();
            String name = clazz.getSimpleName() + "$differ";


            CtClass differClass = classPool.makeClass(name);

            differClass.setSuperclass(classPool.getCtClass(BaseDiffer.class.getName()));
            StringBuilder methodBuilder = new StringBuilder();

            methodBuilder.append(getMethodSign());
            methodBuilder.append(String.format("    %s origin = (%s) from;\n", clazzName, clazzName));
            methodBuilder.append(String.format("    %s target = (%s) to;\n", clazzName, clazzName));
            methodBuilder.append(getIfEqual());
            methodBuilder.append(getDiffNodeInit());
            methodBuilder.append(getNullCheck());
            methodBuilder.append("  java.util.List childNodeList = new java.util.ArrayList();\n");
            methodBuilder.append("  java.util.Optional optional = java.util.Optional.empty();\n");

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                // 判断field类型
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), clazz);
                String getterMethodName = propertyDescriptor.getReadMethod().getName();

                Class<?> fieldType = field.getType();
                if (isPrimitiveType(fieldType)) {
                    methodBuilder.append(getPrimitiveDiff(field.getName(), getterMethodName));
                    methodBuilder.append(getCheckOptional());
                    methodBuilder.append("\n");
                } else if (fieldType.isArray()) {
                    methodBuilder.append(getArrayDiff(field.getName(), getterMethodName));
                    methodBuilder.append(getCheckOptional());
                    methodBuilder.append("\n");
                } else if (isMapType(fieldType)) {
                    Type type = field.getGenericType();
                    String valueTypeName = Object.class.getName();
                    if (type instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) type;
                        Type valueType = parameterizedType.getActualTypeArguments()[1];
                        valueTypeName = valueType.getTypeName();
                        dependTypeSet.add(Class.forName(valueTypeName));
                    }
                    methodBuilder.append(getMapDiff(valueTypeName, field.getName(), getterMethodName));
                    methodBuilder.append(getCheckOptional());
                    methodBuilder.append("\n");
                } else if (isSetType(fieldType)) {
                    methodBuilder.append(getCollectionDiff(field.getName(), getterMethodName));
                    methodBuilder.append(getCheckOptional());
                    methodBuilder.append("\n");
                } else if (isListType(fieldType)) {
                    methodBuilder.append(getListDiff(field.getName(), getterMethodName));
                    methodBuilder.append(getCheckOptional());
                    methodBuilder.append("\n");
                } else if (isCollectionType(fieldType)) {
                    methodBuilder.append(getCollectionDiff(field.getName(), getterMethodName));
                    methodBuilder.append(getCheckOptional());
                    methodBuilder.append("\n");
                } else {
                    // customType
                    // 还需要处理生成的类
                    methodBuilder.append(getCustomTypeDiff(field.getType().getName(), field.getName(), getterMethodName));
                    methodBuilder.append(getCheckOptional());
                    methodBuilder.append("\n");
                    dependTypeSet.add(field.getType());
                }
            }
            methodBuilder.append(getResultCheck());
            methodBuilder.append("}\n");


            CtMethod m = CtNewMethod.make(methodBuilder.toString(), differClass);
            differClass.addMethod(m);

            Class<?> invokerClass = differClass.toClass();

            BaseDiffer result = (BaseDiffer) invokerClass.getConstructor().newInstance();

            cachedDifferMap.putIfAbsent(clazz, result);

            return cachedDifferMap.get(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private String getMethodSign() {
        return String.format("public java.util.Optional diff(%s parent, java.lang.String propertyName, java.lang.Object from, java.lang.Object to) {\n", DiffNode.class.getName());
    }

    private String getIfEqual() {
        return  "   if (java.util.Objects.equals(origin, target)) {\n" +
                "       return java.util.Optional.empty();\n" +
                "   }\n";
    }

    private String getDiffNodeInit() {
        String s =  "    %s diffNode = new %s();\n" +
                    "    diffNode.setOriginValue(origin);\n" +
                    "    diffNode.setTargetValue(target);\n" +
                    "    diffNode.setParentNode(parent);\n" +
                    "    diffNode.setPath(propertyName);\n";
        return String.format(s, DiffNode.class.getName(), DiffNode.class.getName());
    }

    private String getNullCheck() {
        String s = "    if (origin == null) {\n" +
                    "       diffNode.setState(%s.%s);\n" +
                    "       return java.util.Optional.of(diffNode);\n" +
                    "   } else if (target == null) {\n" +
                    "       diffNode.setState(%s.%s);\n" +
                    "       return java.util.Optional.of(diffNode);\n" +
                    "   }\n";
        return String.format(s, DiffState.class.getName(), DiffState.ADDED.name(),  DiffState.class.getName(), DiffState.DELETED.name());
    }

    private String getResultCheck() {
        String s =  "   if (childNodeList.isEmpty()) {\n" +
                    "       return java.util.Optional.empty();\n" +
                    "   }\n" +
                    "   diffNode.setState(%s.%s);\n" +
                    "   diffNode.setChildNodes(childNodeList);\n" +
                    "   return java.util.Optional.of(diffNode);\n";

        return String.format(s, DiffState.class.getName(), DiffState.CHANGED.name());
    }

    private boolean isPrimitiveType(Class<?> type) {
        return type.isPrimitive() || type.getName().equals(String.class.getName());
    }

    private String getCheckOptional() {
        String s =  "   if (optional.isPresent()) {\n" +
                    "       childNodeList.add(optional.get());\n" +
                    "   }\n";
        return s;
    }

    private String getPrimitiveDiff(String propertyName, String getterMethodName) {
        String s = "    optional = primitiveDiff(diffNode, \"%s\", origin.%s(), target.%s());\n";
        return String.format(s, propertyName, getterMethodName, getterMethodName);
    }
    private String getListDiff(String propertyName, String getterMethodName) {
        String s = "    optional = listDiff(diffNode, \"%s\", origin.%s(), target.%s());\n";
        return String.format(s, propertyName, getterMethodName, getterMethodName);
    }
    private String getCollectionDiff(String propertyName, String getterMethodName) {
        String s = "    optional = collectionDiff(diffNode, \"%s\", origin.%s(), target.%s());\n";
        return String.format(s, propertyName, getterMethodName, getterMethodName);
    }
    private String getMapDiff(String valueTypeName, String propertyName, String getterMethodName) {
        String s = "    optional = mapDiff(\"%s\", diffNode, \"%s\", origin.%s(), target.%s());\n";
        return String.format(s, valueTypeName, propertyName, getterMethodName, getterMethodName);
    }
    private String getCustomTypeDiff(String customTypeName, String propertyName, String getterMethodName) {
        String s = "    optional = customObjectDiff(\"%s\", diffNode, \"%s\", origin.%s(), target.%s());\n";
        return String.format(s, customTypeName, propertyName, getterMethodName, getterMethodName);
    }
    private String getArrayDiff(String propertyName, String getterMethodName) {
        String s = "    optional = primitiveArrayDiff(diffNode, \"%s\", origin.%s(), target.%s());\n";
        return String.format(s, propertyName, getterMethodName, getterMethodName);
    }

    private boolean isMapType(Class<?> type) {
        return type.isAssignableFrom(Map.class);
    }

    private boolean isSetType(Class<?> type) {
        return type.isAssignableFrom(Set.class);
    }

    private boolean isListType(Class<?> type) {
        return type.isAssignableFrom(List.class);
    }

    private boolean isCollectionType(Class<?> type) {
        return type.isAssignableFrom(Collection.class);
    }


}
