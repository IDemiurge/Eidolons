package main.system;

/**
 * Created with IntelliJ IDEA.
 * Date: 04.11.2016
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public interface EventCallback<T> {
    void call(T obj);
}
