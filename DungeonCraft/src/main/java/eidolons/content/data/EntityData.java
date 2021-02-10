package eidolons.content.data;

import main.entity.Entity;
import main.system.data.DataUnit;

import java.util.Arrays;

public class EntityData<S extends Enum<S>> extends DataUnit<S> {
    public EntityData(Entity entity) {
        initDataFromEntity(entity);
    }

    public EntityData(String text) {
        super(text);
    }

    protected void initDataFromEntity(Entity entity) {
        for (String relevantValue : getRelevantValues()) {
            setValue(relevantValue, entity.getValue(relevantValue));
        }
    }
    @Override
    public String[] getRelevantValues() {
        return Arrays.stream(getEnumClazz().getEnumConstants()).map(Enum::toString).toArray(String[]::new);
    }
}
