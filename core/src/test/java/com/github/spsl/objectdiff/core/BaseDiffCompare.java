package com.github.spsl.objectdiff.core;


import org.junit.Before;
import org.junit.Test;

public class BaseDiffCompare {

    private Differ differ;

    @Before
    public void initDiffer() throws GeneratorDiffException {
        differ = DifferFactory.getInstance().getDiffer(Foo.class);
    }

    @Test
    public void test() {

        Foo foo = new Foo();
        foo.setName("from");

        Foo foo2 = new Foo();
        foo2.setName("to");

        differ.diff( foo, foo2);

    }
}
