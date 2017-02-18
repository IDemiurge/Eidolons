package main.system.math;

import main.entity.Entity;
import main.entity.Ref;
import main.entity.Referred;
import main.game.core.game.Game;

public abstract class DynamicValue implements Referred {

    protected Ref ref;
    protected String value_ref;
    protected String str;
    protected String obj_ref;
    protected Entity entity;
    protected Game game;
    protected Integer source;
    protected Integer target;
    protected boolean base;

    public DynamicValue(String valueString) {
        if (valueString.contains("@")) {
            valueString = valueString.replace("@", "");
            base = true;
        }

        if (valueString.contains("_") && (base || valueString.contains("{"))) {
            valueString = valueString.replace("{", "").replace("}", "");
            if (Ref.isKey(valueString)) {
                this.value_ref = valueString;
            } else {
                String s[] = valueString.split("_");

                obj_ref = s[0];
                this.value_ref = valueString.substring(valueString.indexOf("_") + 1);
            }
        } else {
            str = valueString;
        }
    }

    public DynamicValue() {
    }

    public DynamicValue(String value_ref, String obj_ref, boolean base) {
        this.value_ref = value_ref;
        this.obj_ref = obj_ref;
        this.base = base;
    }

    @Override
    public Ref getRef() {
        return ref;
    }

    @Override
    public void setRef(Ref ref) {
        this.ref = ref;
        this.game = ref.getGame();
        this.source = ref.getSource();
        this.target = ref.getTarget();
        this.base = ref.isBase();

    }

    public void checkRefReplacement() {

    }

    public String getValue_ref() {
        return value_ref;
    }

    public String getStr() {
        return str;
    }

    public String getObj_ref() {
        return obj_ref;
    }

    public Entity getEntity() {
        return entity;
    }

    public Game getGame() {
        return game;
    }

    public Integer getSource() {
        return source;
    }

    public Integer getTarget() {
        return target;
    }

    public boolean isBase() {
        return base;
    }
}
