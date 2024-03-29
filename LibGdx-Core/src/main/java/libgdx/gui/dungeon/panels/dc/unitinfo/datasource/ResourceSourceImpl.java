package libgdx.gui.dungeon.panels.dc.unitinfo.datasource;

import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import main.content.values.parameters.PARAMETER;
import main.entity.obj.BfObj;

/**
 * Created by JustMe on 11/21/2017.
 */
public class ResourceSourceImpl implements ResourceSource {
    BfObj obj;

    public ResourceSourceImpl(BattleFieldObject obj) {
        this.obj = new UnitDataModelSnapshot(obj);
    }

    @Override
    public String getToughness() {
        return getParam(PARAMS.TOUGHNESS);
    }

    @Override
    public String getEndurance() {
        return getParam(PARAMS.ENDURANCE);
    }

    @Override
    public String getEssence() {
        return getParam(PARAMS.ESSENCE);
    }

    @Override
    public String getFocus() {
        return getParam(PARAMS.FOCUS);
    }

    @Override
    public String getParam(PARAMS param) {
        return obj.getParam(param);
    }

    public Integer getIntParam(PARAMS param) {
        return obj.getIntParam(param);
    }

    public boolean isFull(PARAMETER p) {
        return obj.isFull(p);
    }


    public boolean isDetectedByPlayer() {
        return obj.isPlayerDetected();
    }

    public boolean canHpBarBeVisible() {
        if (obj.isDead())
            return false;
        if (!obj.isMine())
            return isDetectedByPlayer();
        return true;
    }

    public boolean isHpBarVisible() {
        if (!isFull((PARAMS.TOUGHNESS)))
            return true;
        return !isFull((PARAMS.ENDURANCE));
    }

    public String getName() {
        return obj.getName();
    }

    @Override
    public String toString() {
        return obj.getName() + " data source";
    }
}
