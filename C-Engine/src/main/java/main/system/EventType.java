package main.system;

/**
 * Created by JustMe on 2/7/2018.
 */
public interface EventType {
    default boolean isMultiArgsInvocationSupported() {
        return false;
    }

}
