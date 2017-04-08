package main.libgdx.bf;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.values.properties.G_PROPS;
import main.entity.obj.BattleFieldObject;
import main.libgdx.texture.TextureCache;

import java.util.Map;

import static main.content.PARAMS.C_INITIATIVE;

public class UnitViewOptions {

    private Runnable runnable;

    private TextureRegion portrateTexture;

    private TextureRegion directionPointerTexture;

    private Texture iconTexture;

    private TextureRegion clockTexture;
    private int directionValue;

    private int clockValue;
    private boolean hideBorder;
    private boolean overlaying;

    private BattleFieldObject obj;

    private Map unitMap;


    public UnitViewOptions(BattleFieldObject obj, Map unitMap) {
        this.obj = obj;
        this.unitMap = unitMap;
        this.createFromGameObject(this.obj);
    }

    public final Runnable getRunnable() {
        return this.runnable;
    }

    public final void setRunnable(Runnable var1) {
        this.runnable = var1;
    }

    public final TextureRegion getPortrateTexture() {
        return this.portrateTexture;
    }


    public final TextureRegion getDirectionPointerTexture() {
        return this.directionPointerTexture;
    }


    public final Texture getIconTexture() {
        return this.iconTexture;
    }


    public final TextureRegion getClockTexture() {
        return this.clockTexture;
    }


    public final int getDirectionValue() {
        return this.directionValue;
    }


    public final int getClockValue() {
        return this.clockValue;
    }


    public final boolean getHideBorder() {
        return this.hideBorder;
    }

    public final void setHideBorder(boolean var1) {
        this.hideBorder = var1;
    }

    public final boolean getOverlaying() {
        return this.overlaying;
    }

    public final void setOverlaying(boolean var1) {
        this.overlaying = var1;
    }

    public final void createFromGameObject(BattleFieldObject obj) {
        this.portrateTexture = TextureCache.getOrCreateR(obj.getImagePath());
        if (C_OBJ_TYPE.UNITS_CHARS.equals(obj.getOBJ_TYPE_ENUM())) {
            this.directionValue = obj.getFacing().getDirection().getDegrees();
            this.directionPointerTexture = TextureCache.getOrCreateR("/UI/DIRECTION POINTER.png");

            this.clockTexture = TextureCache.getOrCreateR("/UI/value icons/actions.png");
            String emblem = obj.getProperty(G_PROPS.EMBLEM, true);
            this.clockValue = obj.getIntParam(C_INITIATIVE);
        }

        if (obj.isOverlaying()) {
            this.overlaying = true;
            this.portrateTexture = TextureCache.getOrCreateR(obj.getImagePath());
        }

        if (obj.getOBJ_TYPE_ENUM() == DC_TYPE.BF_OBJ) {
            this.hideBorder = true;
        }

    }

    public final BattleFieldObject getObj() {
        return this.obj;
    }

    public final void setObj(BattleFieldObject var1) {
        this.obj = var1;
    }

    public final Map getUnitMap() {
        return this.unitMap;
    }

    public final void setUnitMap(Map var1) {
        this.unitMap = var1;
    }
}
