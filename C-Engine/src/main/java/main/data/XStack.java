package main.data;

import java.util.Stack;

public class XStack<E> extends Stack<E> {
    @Override
    public synchronized E peek() {
        if (isEmpty())
            return null;
        return super.peek();
    }
}
