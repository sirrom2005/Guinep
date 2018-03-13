package com.trafalgartmc.guinep.Classes;

/**
 * Created by rohan on 6/28/2017.
 */

public class SelectableItem {
    private final int key;
    private final String value;

    public SelectableItem(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
