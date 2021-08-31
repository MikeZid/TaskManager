package com.hfad.organaizer;

public class ScheduleNameForRecyclerView {
    private final String name;
    private int activated;

    ScheduleNameForRecyclerView(String name, int activated) {
        this.name = name;
        this.activated = activated;
    }

    public String getName() {
        return this.name;
    }

    public boolean isActivated() {
        return this.activated == 1;
    }

    @Override
    public String toString() {
        return "name=" + name + ", activated=" + activated;
    }
}
