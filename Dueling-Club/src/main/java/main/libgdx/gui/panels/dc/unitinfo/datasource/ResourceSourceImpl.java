package main.libgdx.gui.panels.dc.unitinfo.datasource;

import main.content.PARAMS;
import main.content.values.parameters.PARAMETER;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.BfObj;

/**
 * Created by JustMe on 11/21/2017.
 */
public class ResourceSourceImpl implements ResourceSource {
    BfObj obj;

    public ResourceSourceImpl(BattleFieldObject obj) {
        this.obj =  new UnitDataModelSnapshot(obj);
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
    public String getStamina() {
        return getParam(PARAMS.STAMINA);
    }

    @Override
    public String getMorale() {
        return getParam(PARAMS.MORALE);
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
            if (!isDetectedByPlayer())
                return false;
        return true;
    }
        public boolean isHpBarVisible() {
        if (!isFull((PARAMS.TOUGHNESS)))
            return true;
            return !isFull((PARAMS.ENDURANCE));
        }

    public String getName() {
        return  obj.getName();
    }
    @Override
    public String toString() {
        return obj.getName()+" data source";
    }
}
