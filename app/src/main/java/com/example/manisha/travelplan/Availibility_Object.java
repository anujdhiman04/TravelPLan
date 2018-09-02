package com.example.manisha.travelplan;

public class Availibility_Object {
    String name;
    boolean available;

    public Availibility_Object(String name, boolean available) {
        this.available = available;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isAvailable() {
        return available;
    }

}
