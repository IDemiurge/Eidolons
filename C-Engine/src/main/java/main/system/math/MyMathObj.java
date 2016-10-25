package main.system.math;

import main.entity.Ref;

import java.io.Serializable;

public interface MyMathObj extends Serializable {
    int getInt();

    Integer getInt(Ref ref);

}
