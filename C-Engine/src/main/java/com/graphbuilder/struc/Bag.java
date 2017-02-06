package com.graphbuilder.struc;

public class Bag {

    protected Object[] data = null;
    protected int size = 0;

    public Bag() {
        data = new Object[2];
    }

    public Bag(int initialCapacity) {
        data = new Object[initialCapacity];
    }

    public void add(Object o) {
        insert(o, size);
    }

    public int size() {
        return size;
    }

    public void insert(Object o, int i) {
        if (i < 0 || i > size) {
            throw new IllegalArgumentException("required: (i >= 0 && i <= size) but: (i = " + i + ", size = " + size + ")");
        }

        ensureCapacity(size + 1);

        for (int j = size; j > i; j--) {
            data[j] = data[j - 1];
        }

        data[i] = o;
        size++;
    }

    public void ensureCapacity(int minCapacity) {
        if (minCapacity > data.length) {
            int x = 2 * data.length;

            if (x < minCapacity) {
                x = minCapacity;
            }

            Object[] arr = new Object[x];

            for (int i = 0; i < size; i++) {
                arr[i] = data[i];
            }

            data = arr;
        }
    }

    public int getCapacity() {
        return data.length;
    }

    private int indexOf(Object o, int i, boolean forward) {
        if (size == 0) {
            return -1;
        }

        if (i < 0 || i >= size) {
            throw new IllegalArgumentException("required: (i >= 0 && i < size) when: (size > 0) but: (i = " + i + ", size = " + size + ")");
        }

        if (forward) {
            if (o == null) {
                for (; i < size; i++) {
                    if (data[i] == null) {
                        return i;
                    }
                }
            } else {
                for (; i < size; i++) {
                    if (o.equals(data[i])) {
                        return i;
                    }
                }
            }
        } else {
            if (o == null) {
                for (; i >= 0; i--) {
                    if (data[i] == null) {
                        return i;
                    }
                }
            } else {
                for (; i >= 0; i--) {
                    if (o.equals(data[i])) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public int remove(Object o) {
        int i = indexOf(o, 0, true);
        if (i >= 0) {
            remove(i);
        }
        return i;
    }

    public Object remove(int i) {
        if (i < 0 || i >= size) {
            throw new IllegalArgumentException("required: (i >= 0 && i < size) but: (i = " + i + ", size = " + size + ")");
        }

        Object o = data[i];

        for (int j = i + 1; j < size; j++) {
            data[j - 1] = data[j];
        }

        data[--size] = null;
        return o;
    }

    public Object get(int i) {
        if (i < 0 || i >= size) {
            throw new IllegalArgumentException("required: (i >= 0 && i < size) but: (i = " + i + ", size = " + size + ")");
        }

        return data[i];
    }

    public boolean contains(Object o) {
        return indexOf(o, 0, true) >= 0;
    }
}