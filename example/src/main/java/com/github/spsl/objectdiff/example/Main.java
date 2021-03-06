package com.github.spsl.objectdiff.example;


import com.github.spsl.objectdiff.core.*;
import com.github.spsl.objectdiff.example.model.School;
import com.github.spsl.objectdiff.example.model.Student;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) throws GeneratorDiffException {
        Student a = new Student();
        a.setAge(1);
        a.setName("hello world");

        School school = new School();
        school.setName("school name");
        a.setSchool(school);

        a.getSchoolMap().put("1", new School());
        a.getSchoolMap().put("2", school);

//        a.setParent(parentStudent2);

        Student b = new Student();
        b.setAge(2);
        b.setName("hello");
        School school1 = new School();
        school1.setName("he");
        b.setSchool(school1);

        Student parentStudent = new Student();
        parentStudent.setName("parent");
        parentStudent.setAge(11);
//        b.setParent(parentStudent);

        a.setList(Arrays.asList("hello", "world"));
        b.setList(Arrays.asList("hello", "wow", "hi"));

        b.getSchoolMap().put("1", school1);
        b.getSchoolMap().put("3", new School());



        Differ studentDiffer = DifferFactory.getInstance().getDiffer(Student.class);


        Optional<DiffNode> diffNodeOptional = studentDiffer.diff(a, b, new Filter() {
            @Override
            public List<String> excludeProperties() {
                return Arrays.asList("age");
            }
        });


        diffNodeOptional.ifPresent(System.out::println);

    }
}
