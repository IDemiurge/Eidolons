package main.libgdx.bf;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.content.C_OBJ_TYPE;
import main.content.values.properties.G_PROPS;
import main.entity.obj.BattleFieldObject;
import main.libgdx.GdxColorMaster;
import main.system.images.ImageManager;

import static main.content.PARAMS.C_INITIATIVE;
import static main.libgdx.texture.TextureCache.getOrCreateR;

public class UnitViewOptions {

    private Runnable runnable;

    private TextureRegion portrateTexture;

    private TextureRegion directionPointerTexture;

    private Texture iconTexture;

    private TextureRegion clockTexture;
    private TextureRegion emblem;
    private int directionValue;

    private int clockValue;
    private Color teamColor;


    public UnitViewOptions(BattleFieldObject obj) {
        createFromGameObject(obj);
    }

    public Color getTeamColor() {
        return teamColor;
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

    public TextureRegion getEmblem() {
        return emblem;
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

    public final void createFromGameObject(BattleFieldObject obj) {
        this.portrateTexture = getOrCreateR(obj.getImagePath());
        if (C_OBJ_TYPE.UNITS_CHARS.equals(obj.getOBJ_TYPE_ENUM())) {
            this.directionValue = obj.getFacing().getDirection().getDegrees();
            this.directionPointerTexture = getOrCreateR("/UI/DIRECTION POINTER.png");

            this.clockTexture = getOrCreateR("/UI/value icons/actions.png");
            String emblem = obj.getProperty(G_PROPS.EMBLEM, true);
            if (ImageManager.isImage(emblem))
            this.emblem = getOrCreateR(emblem);
            this.clockValue = obj.getIntParam(C_INITIATIVE);
            this.teamColor =
             GdxColorMaster.getColor(obj.getOwner().getFlagColor());
            if (teamColor==null ){
                teamColor= GdxColorMaster.NEUTRAL;
            }
        }
    }
}
