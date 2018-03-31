package main.system.datatypes;

import java.util.function.Supplier;

/**
 * Created by JustMe on 1/9/2017.
 */
public class Boxer<T> implements Supplier<T> {

    private Supplier<T> supplier;
    private T value;

    public Boxer(T t) {
        this.value = t;
    }

    public Boxer(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (supplier != null) {
            return supplier.get();
        }
        return value;
    }
}
