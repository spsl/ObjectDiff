package com.github.spsl.objectdiff.example.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student {

    private int age;

    private String name;

    private School school;

    private List<String> aaa;

    Map<String, School> schoolMap = new HashMap<>();

    public Map<String, School> getSchoolMap() {
        return schoolMap;
    }

    public void setSchoolMap(Map<String, School> schoolMap) {
        this.schoolMap = schoolMap;
    }

    public List<String> getAaa() {
        return aaa;
    }

    public void setAaa(List<String> aaa) {
        this.aaa = aaa;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }
}
