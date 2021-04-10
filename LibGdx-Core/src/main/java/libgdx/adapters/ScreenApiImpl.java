package libgdx.adapters;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battlefield.vision.colormap.ColorMapDataSource;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.module.dungeoncrawl.explore.RealTimeGameLoop;
import eidolons.system.libgdx.api.ScreenApi;
import libgdx.screens.GameScreen;
import libgdx.screens.dungeon.DungeonScreen;
import main.system.datatypes.DequeImpl;

import java.util.Set;

public class ScreenApiImpl implements ScreenApi {
        GameScreen screen;

    @Override
    public void setRealTimeGameLoop(RealTimeGameLoop exploreGameLoop) {

    }

    @Override
    public void setSpeed(Float f) {

    }

    @Override
    public void moduleEntered(Module module, DequeImpl<BattleFieldObject> objects) {

    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void illuminationUpdated(Set<ColorMapDataSource.LightDS> set) {
        if (screen instanceof DungeonScreen) {
            DungeonScreen dungeonScreen = (DungeonScreen) screen;
            dungeonScreen.getGridPanel().getColorMap().reset(set);
        }
    }
}
