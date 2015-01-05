package com.mak001.api;

import java.util.ArrayList;

public class OrganizedMap<K extends Object, V extends Object> {

    private ArrayList<K> keys;
    private ArrayList<V> values;

    public OrganizedMap() {
        keys = new ArrayList<K>();
        values = new ArrayList<V>();
    }

    public ArrayList<K> keys() {
        return keys;
    }

    public ArrayList<V> values() {
        return values;
    }

    public void put(K key, V value) {
        keys.add(key);
        values.add(value);
    }

    public V get(K key) {
        if (keys.contains(key))
            return values.get(keys.indexOf(key));
        return null;
    }

    public V getAt(int index) {
        return values.get(index);
    }
}
