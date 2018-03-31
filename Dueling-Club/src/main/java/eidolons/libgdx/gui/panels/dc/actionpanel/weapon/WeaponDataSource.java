package eidolons.libgdx.gui.panels.dc.actionpanel.weapon;

import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.gui.datasource.EntityDataSource;
import main.content.enums.entity.ItemEnums.WEAPON_CLASS;
import main.content.enums.entity.ItemEnums.WEAPON_GROUP;
import main.content.enums.entity.ItemEnums.WEAPON_SIZE;
import main.content.enums.entity.ItemEnums.WEAPON_TYPE;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 3/29/2018.
 */
public class WeaponDataSource extends EntityDataSource<DC_WeaponObj> {

    public WeaponDataSource(DC_WeaponObj weapon) {
        super(weapon);
    }

    public List<DC_UnitAction> getActions() {
        return new ArrayList<>(entity.getAttackActions());
    }

    public boolean isTwoHanded() {
        return entity.isTwoHanded();
    }

    public boolean isMainHand() {
        return entity.isMainHand();
    }

    public boolean isNatural() {
        return entity.isNatural();
    }

    public boolean isShield() {
        return entity.isShield();
    }

    public boolean isAmmo() {
        return entity.isAmmo();
    }

    public boolean isRanged() {
        return entity.isRanged();
    }

    public boolean isMelee() {
        return entity.isMelee();
    }

    public WEAPON_TYPE getWeaponType() {
        return entity.getWeaponType();
    }

    public WEAPON_GROUP getWeaponGroup() {
        return entity.getWeaponGroup();
    }

    public WEAPON_SIZE getWeaponSize() {
        return entity.getWeaponSize();
    }

    public WEAPON_CLASS getWeaponClass() {
        return entity.getWeaponClass();
    }

    public String getSpriteImagePath() {
        return entity.getSpriteImagePath();
    }

    public Unit getOwnerObj() {
        return entity.getOwnerObj();
    }

    public DC_WeaponObj getWeapon() {
        return entity;
    }
}
