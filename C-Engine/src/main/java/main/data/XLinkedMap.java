package main.data;

import main.system.auxiliary.StringMaster;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class XLinkedMap<E, T> extends LinkedHashMap<E, T> {
    public XLinkedMap(int i) {
        super(i);
    }

    public XLinkedMap() {
        super();
    }

    @Override
    public String toString() {
        String string = size() + " size map: ";
        for (java.util.Map.Entry<E, T> entry : entrySet()) {
            string += entry.getKey() + " = " + entry.getValue() + ";";
        }
        return string;
    }

    // sort method

    public T getByIndex(int index) {
        return get(new LinkedList<>(keySet()).get(index));
    }

    @Override
    public T get(Object key) {
        if (key == null)
            return null;
        T t = super.get(key);
        if (t == null) {
            for (E e : keySet()) {
                if (e != null)
                    if (e.toString().equalsIgnoreCase(key.toString()))
                        return super.get(e);

            }
            if (key instanceof String) {
                String string = (String) key;
                if (t == null)
                    t = super.get(string.toUpperCase());
                if (t == null)
                    t = super.get(string.toLowerCase());
                if (t == null) {
                    String wellFormattedString = StringMaster.getWellFormattedString(string);
                    t = super.get(wellFormattedString);

                    // s.replace(" ", "")

                    if (t == null) {
                        if (wellFormattedString.endsWith("s"))
                            t = super.get(wellFormattedString.substring(0, string.length() - 1));
                        else if (t == null)
                            t = super.get(wellFormattedString + "s");
                    }
                }
            }
        }
        return t;
    }

    @Override
    public T remove(Object key) {
        return super.remove(key);
    }

}
