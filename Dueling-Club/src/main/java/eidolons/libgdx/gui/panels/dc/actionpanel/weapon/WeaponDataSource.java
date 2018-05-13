package eidolons.libgdx.gui.panels.dc.actionpanel.weapon;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.gui.datasource.EntityDataSource;
import main.content.enums.entity.ItemEnums.WEAPON_CLASS;
import main.content.enums.entity.ItemEnums.WEAPON_GROUP;
import main.content.enums.entity.ItemEnums.WEAPON_SIZE;
import main.content.enums.entity.ItemEnums.WEAPON_TYPE;
import main.system.images.ImageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 3/29/2018.
 */
public class WeaponDataSource extends EntityDataSource<DC_WeaponObj> {

    public WeaponDataSource(DC_WeaponObj weapon) {
        super(weapon);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WeaponDataSource) {
            return ((WeaponDataSource) obj).getWeapon().equals(getWeapon());
        }
        return super.equals(obj);
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
        if (ImageManager.isImage(entity.getSpriteImagePath()))
             return entity.getSpriteImagePath();
        return getDefaultSpriteImage();
    }

    private String getDefaultSpriteImage() {
        return "main\\item\\weapon\\sprites\\battle axe.png";
    }

    public Unit getOwnerObj() {
        return entity.getOwnerObj();
    }

    public DC_WeaponObj getWeapon() {
        return entity;
    }

    public Image getNormalImage() {
        if (!isMainHand()){
            return new Image(GdxImageMaster.flip(
             GdxImageMaster.getSizedImagePath(getSpriteImagePath(), 96),
             true, false, true));
        }
        return new Image(GdxImageMaster.size(getSpriteImagePath(),
         96, true));
    }
    public Image getLargeImage() {
        if (!isMainHand()){
            return new Image(GdxImageMaster.flip(
             getSpriteImagePath(),
             true, false, true));
        }
        return new Image(GdxImageMaster.size(getSpriteImagePath(),
         128, true));
    }
}
