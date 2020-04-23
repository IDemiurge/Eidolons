package main.system.auxiliary.data;

public interface Converter<T, T1> {
    T1 convert(T t);
}
