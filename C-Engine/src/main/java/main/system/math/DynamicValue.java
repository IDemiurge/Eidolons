package main.system.math;

import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.Referred;
import main.game.core.game.Game;
import main.system.util.Optimize;

public abstract class DynamicValue implements Referred {

    protected Ref ref;
    protected String value_string;
    protected String fullString;
    protected String obj_string;
    protected Entity entity;
    protected Game game;
    protected boolean base;
@Optimize
    public DynamicValue(String fullString) {
        if (fullString.contains("@")) {
            fullString = fullString.replace("@", "");
            base = true;
        }

        if (base || fullString.contains("{"))  {
            fullString = fullString.replace("{", "").replace("}", "");
            if (Ref.isKey(fullString)) {
                this.value_string = fullString;
            } else {

                if ( fullString.contains("_") ){
                    String s[] = fullString.split("_");
                    obj_string = s[0];
                    this.value_string = fullString.substring(fullString.indexOf("_") + 1);
                }
                else {
                    obj_string = KEYS.SOURCE.toString();
                    obj_string = fullString;
                }

                 }
        } else {
            this.fullString = fullString;
        }
    }

    public DynamicValue() {
    }

    public DynamicValue(String value_string, String obj_string, boolean base) {
        this.value_string = value_string;
        this.obj_string = obj_string;
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
        this.base = ref.isBase();

    }

    public void checkRefReplacement() {

    }

    public String getValue_string() {
        return value_string;
    }

    public String getFullString() {
        return fullString;
    }

    public Entity getEntity() {
        return entity;
    }

    public Game getGame() {
        return game;
    }

    public boolean isBase() {
        return base;
    }
}
