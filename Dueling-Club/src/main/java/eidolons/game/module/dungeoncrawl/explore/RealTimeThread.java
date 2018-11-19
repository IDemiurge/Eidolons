package eidolons.game.module.dungeoncrawl.explore;

import com.badlogic.gdx.Gdx;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.anims.construct.AnimConstructor;
import eidolons.macro.global.time.MacroTimeMaster;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;

/**
 * Created by JustMe on 5/3/2018.
 */
public class RealTimeThread extends Thread {

    private static final int REAL_TIME_LOGIC_PERIOD = 500;
    private final ExploreGameLoop loop;
    private final DC_Game game;
    private boolean done;

    public RealTimeThread(ExploreGameLoop exploreGameLoop) {
        super("RT thread - " + exploreGameLoop.getThreadName());
        this.loop = exploreGameLoop;
        game = loop.getGame();
    }

    @Override
    public void run() {
        realTimeLogic();
        done = true;
    }

    public boolean isDone() {
        return done;
    }

    protected void realTimeLogic() {
        try {
            Eidolons.getGame().getDungeonMaster().getExplorationMaster().getPartyMaster().reset();
            Eidolons.getGame().getDungeonMaster().getExplorationMaster().getAiMaster().reset();
            if (!CoreEngine.isGraphicsOff())
                Eidolons.getGame().getDungeonMaster().getExplorationMaster().getAiMaster().getAllies().forEach(unit -> {
                    Gdx.app.postRunnable(() ->
                     {
                         try {
                             AnimConstructor.preconstructAll(unit);
                         } catch (Exception e) {
                             main.system.ExceptionMaster.printStackTrace(e);
                         }
                     }
                    );
                });
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

        while (true) {

            WaitMaster.WAIT(REAL_TIME_LOGIC_PERIOD);
            if (Eidolons.getGame() == null)
                return;
            if (loop.isExited())
                return;
            if (loop.isStopped()) {
                return;
            }
            if (Eidolons.getGame() != game)
                return;

            if (Eidolons.getGame().isPaused()) continue;
            if (!ExplorationMaster.isExplorationOn()) continue;
            if (ExplorationMaster.isRealTimePaused()) continue;
            try {
                Eidolons.getGame().getDungeonMaster().getExplorationMaster().
                 getTimeMaster().checkTimedEvents();
                //           do we really want time to pass while we're down in a dungeon?
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            try {
                MacroTimeMaster.getInstance().timedCheck();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }
}
