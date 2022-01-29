package libgdx.gui.dungeon.panels.dc.actionpanel.weapon;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.unit.Unit;
import libgdx.gui.dungeon.datasource.EntityDataSource;
import libgdx.gui.generic.btn.FlipDrawable;
import libgdx.assets.texture.TextureCache;
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
public class WeaponDataSource extends EntityDataSource<WeaponItem> {

    public WeaponDataSource(WeaponItem weapon) {
        super(weapon);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WeaponDataSource) {
            return (getWeapon().equals(((WeaponDataSource) obj).getWeapon()));
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
        return "main/item/weapon/sprites/reaper scythe.png";
    }

    public Unit getOwnerObj() {
        return entity.getOwnerObj();
    }

    public WeaponItem getWeapon() {
        return entity;
    }

    public Image getNormalImage() {
        TextureRegion region = TextureCache.getSizedRegion(getDefaultSize(), getSpriteImagePath());
        if (!isMainHand()) {
            Drawable drawable = new FlipDrawable(new TextureRegionDrawable(region), () -> true, () -> false);
            return new Image(drawable);
        }
        return new Image(region);
    }

    protected int getDefaultSize() {
        //        if (getWeapon().isMagical())
        if (getWeapon().getName().contains("Force")) {
            return 128;
        }
        return 96;
    }

    public Image getLargeImage() {
        TextureRegion region = TextureCache.getRegionUI_DC(getSpriteImagePath());
        if (!isMainHand()) {
            Drawable drawable = new FlipDrawable(new TextureRegionDrawable(region), () -> true, () -> false);
            return new Image(drawable);
        }
        return new Image(region);
    }
}
