package main.system;

/**
 * Created by JustMe on 1/25/2017.
 */
@FunctionalInterface
public interface Producer<T, E> {

    public E produce(T t);
}
