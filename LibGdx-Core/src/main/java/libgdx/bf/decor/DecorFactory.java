package libgdx.bf.decor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import eidolons.game.core.game.DC_Game;
import libgdx.GdxMaster;
import libgdx.anims.sprite.SpriteX;
import libgdx.assets.AssetEnums;
import libgdx.bf.datasource.GraphicData;
import libgdx.bf.datasource.SpriteData;
import libgdx.bf.generic.Flippable;
import libgdx.bf.generic.ImageContainer;
import libgdx.gui.generic.NoHitImageX;
import libgdx.particles.EmitterActor;
import libgdx.texture.Images;
import main.content.enums.GenericEnums;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.StringMaster;

public class DecorFactory {

    private static final int DEFAULT_SIGHT_RANGE = 12;

    public static CellDecor createDecor(Coordinates c, GraphicData data) {
        Actor actor;
        // if (!CoreEngine.isLevelEditor())
        // if (data.getBooleanValue(GraphicData.GRAPHIC_VALUE.editor)) {
        //     return new NoHitImageX(Images.REALLY_EMPTY_32);
        // }
        String path = data.getTexturePath();
        if (path == null) {
            path = data.getSpritePath();
            if (StringMaster.isEmpty(path)) {
                actor = new NoHitImageX(Images.COLOR_EMBLEM);
            } else {
                actor = new SpriteX(path);
            }
        } else {
            actor = new NoHitImageX(path);
            ((NoHitImageX) actor).setAtlas(AssetEnums.ATLAS.TEXTURES);
        }
        boolean x = data.getBooleanValue(GraphicData.GRAPHIC_VALUE.flipX);
        boolean y = data.getBooleanValue(GraphicData.GRAPHIC_VALUE.flipY);
        float scale = data.getFloatValue(GraphicData.GRAPHIC_VALUE.scale);
        if (scale != 0) {
            actor.setScale(scale);
        }
        if (actor instanceof Flippable) {
            ((Flippable) actor).setFlipX(x);
            ((Flippable) actor).setFlipY(y);
        }

        float alpha = data.getFloatValue(GraphicData.GRAPHIC_VALUE.alpha);
        if (alpha != 0)
            actor.getColor().a = alpha;
        boolean sprite = true;
        if (actor instanceof ImageContainer) {
            if (data.getAlphaTemplate() != null) {
                ((ImageContainer) actor).setAlphaTemplate(data.getAlphaTemplate());
            }
            ((ImageContainer) actor).pack();
        } else {
            if (actor instanceof SpriteX) {
                sprite = true;
                initSprite((SpriteX) actor, data);
                //TODO
            }
        }
        String val = data.getValue(GraphicData.GRAPHIC_VALUE.origin);
        if (!val .isEmpty()) {
            actor.setOrigin(GdxMaster.getAlignForDirection(DIRECTION.get(val)));
        } else
            actor.setOrigin(Align.center);
        float rotation = data.getFloatValue(GraphicData.GRAPHIC_VALUE.rotation);
        if (rotation != 0) {
            actor.setRotation(rotation);
        }
        String value = data.getVfxPath();
        if (!value.isEmpty()) {
            //what about CustomObject and such?
            return new CellDecor(new EmitterActor(value),
                    actor, DEFAULT_SIGHT_RANGE, DC_Game.game.getCellByCoordinate(c), sprite);
        }
        CellDecor decor = new CellDecor(actor, DEFAULT_SIGHT_RANGE, DC_Game.game.getCellByCoordinate(c), sprite);
        decor.setBaseColor(data.getColor());
        return decor;
    }


    private static void initSprite(SpriteX actor, GraphicData data) {
        actor.getSprite().setData(new SpriteData(data.getData()));
        float fps = data.getFloatValue(GraphicData.GRAPHIC_VALUE.fps);
        if (fps != 0) {
            actor.setFps((int) fps);
        } else
            actor.setFps(15); //?

        String blending = data.getValue(GraphicData.GRAPHIC_VALUE.blending);
        if (blending.isEmpty()) {
            actor.setBlending(GenericEnums.BLENDING.SCREEN);
        } else {
            actor.setBlending(GenericEnums.BLENDING.valueOf(blending.toUpperCase()));
        }
        if (data.getIntValue(GraphicData.GRAPHIC_VALUE.x) == 0) {
            if (data.getIntValue(GraphicData.GRAPHIC_VALUE.y) == 0) {
                actor.setWidth(128);
                actor.setHeight(128);
                actor.getSprite().centerOnParent(actor);
            }
        }
    }

}
