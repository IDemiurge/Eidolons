package eidolons.system.libgdx.api;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battlefield.vision.colormap.ColorMapDataSource;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.module.dungeoncrawl.explore.ExploreGameLoop;
import eidolons.game.module.dungeoncrawl.explore.RealTimeGameLoop;
import main.system.datatypes.DequeImpl;

import java.util.Set;

public interface ScreenApi {
    void setRealTimeGameLoop(RealTimeGameLoop exploreGameLoop);

    void setSpeed(Float f);

    void moduleEntered(Module module, DequeImpl<BattleFieldObject> objects);

    int getWidth();
    int getHeight();

    void illuminationUpdated(Set<ColorMapDataSource.LightDS> set);
}
