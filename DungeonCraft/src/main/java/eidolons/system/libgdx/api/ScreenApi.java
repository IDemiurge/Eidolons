package eidolons.system.libgdx.api;

import eidolons.game.module.dungeoncrawl.explore.ExploreGameLoop;
import eidolons.game.module.dungeoncrawl.explore.RealTimeGameLoop;

public interface ScreenApi {
    void setRealTimeGameLoop(RealTimeGameLoop exploreGameLoop);

    void setSpeed(Float f);
}
