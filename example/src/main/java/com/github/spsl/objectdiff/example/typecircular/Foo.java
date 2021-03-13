package com.github.spsl.objectdiff.example.typecircular;

public class Foo {

    private String name;

    private Bar bar;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bar getBar() {
        return bar;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }
}
