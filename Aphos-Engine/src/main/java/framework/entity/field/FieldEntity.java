package framework.entity.field;

import framework.entity.Entity;
import framework.field.FieldPos;
import framework.field.Visibility;

import java.util.Map;

/**
 * Created by Alexander on 8/21/2023
 */
public class FieldEntity extends Entity {
    protected FieldPos pos; //what about LARGE?
    protected Visibility visibility = Visibility.Visible;
    protected FieldPos prevPos;

    public FieldEntity(Map<String, Object> valueMap, FieldPos pos) {
        super(valueMap);
        this.pos = pos;
    }

    public FieldPos getPos() {
        return pos;
    }

    public Visibility getEnemyVisibility() {
        return visibility;
    }

    public void setPos(FieldPos pos) {
        prevPos = this.pos;
        this.pos = pos;
    }
}