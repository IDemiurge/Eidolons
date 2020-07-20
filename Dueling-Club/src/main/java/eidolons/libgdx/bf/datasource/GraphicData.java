package eidolons.libgdx.bf.datasource;

import com.badlogic.gdx.graphics.Color;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.assets.Atlases;
import eidolons.libgdx.particles.util.EmitterPresetMaster;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.Sprites;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.GenericEnums;
import main.data.filesys.PathFinder;
import main.system.PathUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.data.DataUnit;
import main.system.images.ImageManager;

import java.util.regex.Pattern;

import static main.content.enums.GenericEnums.ALPHA_TEMPLATE;

public class GraphicData extends DataUnit<GraphicData.GRAPHIC_VALUE> {
    private boolean alt;

    public GraphicData(String text, boolean alt) {
        super(text);
        this.alt = alt;
    }

    public GraphicData(String text) {
        super(text);
    }

    @Override
    protected String getSeparator() {
        if (alt)
            return Pattern.quote("|");
        return ",";
    }

    @Override
    protected String getPairSeparator() {
        if (alt)
            return Pattern.quote("::");
        return "=";
    }

    @Override
    public GraphicData setValue(String name, String value) {
        return (GraphicData) super.setValue(name, value);
    }

    @Override
    public GraphicData clone() {
        return new GraphicData(getData());
    }

    @Override
    public GraphicData setValue(GRAPHIC_VALUE name, String value) {
        return (GraphicData) super.setValue(name, value);
    }

    @Override
    public Class<? extends GRAPHIC_VALUE> getEnumClazz() {
        return GRAPHIC_VALUE.class;
    }

    public Color getColor() {
        if (getValue(GRAPHIC_VALUE.color).isEmpty()) {
            return new Color(1, 1, 1, 1);
        }
        return GdxColorMaster.getColorByName(getValue(GRAPHIC_VALUE.color));
    }

    public ALPHA_TEMPLATE getAlphaTemplate() {
        if (getValue(GRAPHIC_VALUE.alpha_template).isEmpty()) {
            return null;
        }
        return new EnumMaster<ALPHA_TEMPLATE>().retrieveEnumConst(ALPHA_TEMPLATE.class,
                getValue(GRAPHIC_VALUE.alpha_template));
    }

    public String getName() {
        return PathUtils.getLastPathSegment(StringMaster.cropFormat(getMainPath()));
    }

    public String getMainPath() {
        if (getValue(GRAPHIC_VALUE.texture).isEmpty()) {
            return getValue(GRAPHIC_VALUE.sprite);
        }
        return getValue(GRAPHIC_VALUE.texture);
    }

    public String getImage() {
        String path = null;
        if (!getValue(GRAPHIC_VALUE.texture).isEmpty())
            path = getTexturePath();
        if (path == null) {
            path = getSpritePath();
            if (!StringMaster.isEmpty(path)) {
                //atlas Review - what's this?
            return GdxImageMaster.cropImagePath(Atlases.getOneFrameImagePath(path));
            }
        }
        if (!getVfxPath().isEmpty()) {
            String imagePath = EmitterPresetMaster.getInstance().getImagePath(getVfxPath());
            return imagePath;

        }
        return path;
    }

    public String getTexturePath() {
        String path = getValue(GRAPHIC_VALUE.texture);
        if (!ImageManager.isImageFile(path)) {
            if (TextureCache.isImage(PathFinder.getTexturesPath() + path + ".png")) {
                path = PathFinder.getTexturesPath() + path + ".png";
            } else
                path = Images.getByName(path);
        }
        return path;
    }

    public String getSpritePath() {
        String path = getValue(GraphicData.GRAPHIC_VALUE.sprite);
        if (!FileManager.isFile(PathFinder.getImagePath() + path)) {
            // if (TextureCache.isImage(PathFinder.getSpritesPathFull() + path + ".png")) {
            //     path = PathFinder.getTexturesPath() + path + ".png";
            // } else
            // TODO sprite list via recursion on /..cells!
            path = Sprites.getByName(path);
        }
        return path;
    }

    public String getVfxPath() {
        String path = getValue(GRAPHIC_VALUE.vfx);
        if (!path.isEmpty())
            if (!FileManager.isFile(PathFinder.getImagePath() + path)) {
                path = GenericEnums.VFX.valueOf(path).getPath();
            }
        return path;
    }


    public enum GRAPHIC_VALUE {
        x, y, dur, scale, rotation, flipX, flipY, color, alpha,
        texture, alpha_template,editor,
        blending, sprite, fps, vfx, origin, interpolation
    }
}
