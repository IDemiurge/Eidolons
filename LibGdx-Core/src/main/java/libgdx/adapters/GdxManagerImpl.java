package libgdx.adapters;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import eidolons.content.consts.Sprites;
import eidolons.content.consts.VisualEnums;
import eidolons.system.libgdx.api.GdxManagerApi;
import libgdx.anims.main.AnimMaster;
import libgdx.anims.main.EventAnimCreator;
import libgdx.anims.text.FloatingTextMaster;
import libgdx.assets.AssetEnums;
import libgdx.assets.Atlases;
import libgdx.particles.util.EmitterPresetMaster;
import libgdx.screens.ScreenMaster;
import libgdx.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.entity.obj.Obj;
import main.game.logic.event.Event;
import main.system.auxiliary.data.FileManager;

public class GdxManagerImpl implements GdxManagerApi {
    @Override
    public void onInputGdx(Runnable r) {

    }

    @Override
    public void switchBackScreen() {
        VisualEnums.SCREEN_TYPE type = ScreenMaster.getPreviousScreenType();
        // ScreenMaster.switchScreen(new ScreenData(type));
    }

    @Override
    public String getSpritePath(String path) {
        Array<TextureAtlas.AtlasRegion> atlasRegions = new Array();
        if (TextureCache.atlasesOn)
            atlasRegions = Atlases.getAtlasRegions(path,
                    Atlases.isUseOneFrameVersion(path)
                            ? AssetEnums.ATLAS.SPRITES_ONEFRAME
                            : AssetEnums.ATLAS.SPRITES_GRID);

        if (atlasRegions.size < 1)
            if (TextureCache.atlasesOn  || !FileManager.isFile(PathFinder.getImagePath() + path))
            {
                // if (TextureCache.isImage(PathFinder.getSpritesPathFull() + path + ".png")) {
                //     path = PathFinder.getTexturesPath() + path + ".png";
                // } else
                // TODO sprite list via recursion on /..cells!
                path = Sprites.getByName(path);
            }
        return path;
    }

    @Override
    public boolean isImage(String s) {
        return false;
    }

    @Override
    public String getVfxImgPath(String vfxPath) {
        return                EmitterPresetMaster.getInstance().getImagePath(vfxPath);
    }

    @Override
    public void checkHpBarReset(Obj sourceObj) {
        ScreenMaster.getGrid().getGridManager().getEventHandler().checkHpBarReset(sourceObj);
    }

    @Override
    public boolean isEventDisplayable(Event event) {
        return FloatingTextMaster.isEventDisplayable(event);
    }

    @Override
    public boolean isEventAnimated(Event event) {
        return EventAnimCreator.isEventAnimated(event);
    }
}
