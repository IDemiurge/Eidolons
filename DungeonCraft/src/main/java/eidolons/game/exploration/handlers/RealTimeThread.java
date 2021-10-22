package eidolons.game.exploration.handlers;

import eidolons.game.core.Core;
import eidolons.game.core.game.DC_Game;
import main.system.auxiliary.log.FileLogManager;
import main.system.threading.WaitMaster;

import static main.system.auxiliary.log.LogMaster.important;

/**
 * Created by JustMe on 5/3/2018.
 */
public class RealTimeThread extends Thread {

    private static final float REAL_TIME_LOGIC_PERIOD = 0.1f;
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
            Core.getGame().getDungeonMaster().getExplorationMaster().getPartyMaster().reset();
            Core.getGame().getDungeonMaster().getExplorationMaster().getAiMaster().reset();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        float period = REAL_TIME_LOGIC_PERIOD;
        float checkPeriod = 0.5f;
        float timer = 0;
        while (true) {
            WaitMaster.WAIT((int) (period * 1000));


            if (FileLogManager.isOn()) {
                FileLogManager.act(period);
            }

            if (Core.getGame() == null)
                return;
            if (loop.isExited())
                return;
            if (loop.isStopped())
                return;
            if (Core.getGame() != game)
                return;
            if (loop.isVisualLock())
                continue;
            //TODO gdx sync
            // if (!ScreenMaster.getScreen().isLoaded()) {
            //     continue;
            // }
            if (Core.getGame().isPaused()) continue;
            if (!ExplorationMaster.isExplorationOn()) continue;
            if (ExplorationMaster.isRealTimePaused()) continue;
            loop.setVisualLock(true);
            //linked to gdx thread this way
            try {
                loop.act(period);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                important("Realtime loop failed!");
            }
            if (timer >= checkPeriod) {
                timer = 0;
            }
            timer += period;
            try {
                Core.getGame().getDungeonMaster().getExplorationMaster().
                        getTimeMaster().checkTimedEvents();

                //           do we really want time to pass while we're down in a dungeon?
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }
}
