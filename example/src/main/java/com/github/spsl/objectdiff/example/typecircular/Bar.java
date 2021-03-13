package com.github.spsl.objectdiff.example.typecircular;

public class Bar {

    private String name;

    private Foo foo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Foo getFoo() {
        return foo;
    }

    public void setFoo(Foo foo) {
        this.foo = foo;
    }
}
