package com.github.spsl.objectdiff.example.typecircular;

import com.github.spsl.objectdiff.core.DiffNode;
import com.github.spsl.objectdiff.core.Differ;
import com.github.spsl.objectdiff.core.DifferFactory;

import java.util.Optional;

public class Test {

    public static void main(String[] args) {

        Foo foo1 = new Foo();
        foo1.setName("foo1");

        Foo foo2 = new Foo();
        foo2.setName("foo2");

        Bar bar1 = new Bar();
        bar1.setName("bar1");

        Bar bar2 = new Bar();
        bar2.setName("bar2");

        foo1.setBar(bar1);

        foo2.setBar(bar2);


        Differ studentDiffer = DifferFactory.getInstance().getDiffer(Foo.class);


        Optional<DiffNode> diffNodeOptional = studentDiffer.diff(null, "", foo1, foo2);


        diffNodeOptional.ifPresent(System.out::println);


    }
}
