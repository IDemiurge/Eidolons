package main.content;

import java.io.Serializable;

public interface ValueMap extends Serializable {

    String get(VALUE valueName);

    String get(String valueName);

    String put(String valueName, String value);

    boolean containsKey(Object val);
}
