package framework.data;

import content.LinkedStringMap;

/**
 * Created by Alexander on 8/25/2023
 */
public abstract class Datum {
    protected final TypeData data;

    public Datum() {
        data = new TypeData(new LinkedStringMap<>());
    }
    //wrapper methods?
}
