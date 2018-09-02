package com.example.manisha.travelplan;

public class Trip_Object {
    String name, place, start_date, end_date, availablecount;

    public Trip_Object( String name, String place, String start_date, String end_date, String availablecount) {
        this.name = name;
        this.place = place;
        this.start_date = start_date;
        this.end_date = end_date;
        this.availablecount = availablecount;
    }

    public String getAvailablecount() {
        return availablecount;
    }

    public String getName() {
        return name;
    }

    public String getPlace() {
        return place;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getEnd_date() {
        return end_date;
    }
}
