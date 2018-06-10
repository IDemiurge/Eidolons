package eidolons.game.module.dungeoncrawl.explore;

import com.badlogic.gdx.Gdx;
import eidolons.game.core.Eidolons;
import eidolons.macro.global.time.MacroTimeMaster;
import eidolons.libgdx.anims.AnimMaster;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;

/**
 * Created by JustMe on 5/3/2018.
 */
public class RealTimeThread extends Thread {

    private static final int REAL_TIME_LOGIC_PERIOD = 350;
    private final ExploreGameLoop loop;

    public RealTimeThread(ExploreGameLoop exploreGameLoop) {
        super("");
        this.loop = exploreGameLoop;

    }
    @Override
    public void run() {
        realTimeLogic();
    }

    protected   void realTimeLogic() {
        Eidolons.getGame().getDungeonMaster().getExplorationMaster().getPartyMaster().reset();
        Eidolons.getGame().getDungeonMaster().getExplorationMaster().getAiMaster().reset();
        if (!CoreEngine.isGraphicsOff())
            Eidolons.getGame().getDungeonMaster().getExplorationMaster().getAiMaster().getAllies().forEach(unit -> {
                Gdx.app.postRunnable(() ->
                 {
                     try {
                         AnimMaster.getInstance().getConstructor().preconstructAll(unit);
                     } catch (Exception e) {
                         main.system.ExceptionMaster.printStackTrace(e);
                     }
                 }
                );
            });

        while (true) {

            WaitMaster.WAIT(REAL_TIME_LOGIC_PERIOD);
            if (Eidolons.getGame() == null)
                return;
            if (loop.isExited())
                return;
            if (loop.isStopped()){
                return;
            }
            if (Eidolons.getGame().isPaused()) continue;
            if (!ExplorationMaster.isExplorationOn()) continue;
            if (ExplorationMaster.isRealTimePaused()) continue;
            try {
                Eidolons.getGame().getDungeonMaster().getExplorationMaster().
                 getTimeMaster().checkTimedEvents();

                MacroTimeMaster.getInstance().timedCheck();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }
}
