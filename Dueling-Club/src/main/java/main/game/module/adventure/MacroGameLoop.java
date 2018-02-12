package main.game.module.adventure;

import com.badlogic.gdx.Gdx;
import main.game.core.Eidolons;
import main.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.game.module.dungeoncrawl.explore.ExploreGameLoop;
import main.game.module.dungeoncrawl.explore.RealTimeGameLoop;
import main.libgdx.anims.AnimMaster;
import main.libgdx.screens.map.MapScreen;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by JustMe on 2/9/2018.
 */
public class MacroGameLoop extends ExploreGameLoop implements RealTimeGameLoop{

    MacroTimeMaster timeMaster;

    private static final int REAL_TIME_LOGIC_PERIOD = 350;
    private static Thread realTimeThread;
    Lock lock = new ReentrantLock();
    Condition waiting = lock.newCondition();
    MacroGame game;
    public MacroGameLoop(MacroGame game) {
        super();
        this.game = game;
    }

    public void combatFinished(){
        setPaused(false);
    }
        public void enterCombat(){
        //some time will pass in combat

    }

    @Override
    public void setPaused(boolean paused) {
    }

    protected static void startRealTimeLogic() {
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
            if (Eidolons.getGame()==null  )
                return;
            if (Eidolons.getGame().isPaused()) continue;
            if (!ExplorationMaster.isExplorationOn()) continue;
            if (ExplorationMaster.isRealTimePaused()) continue;
            try {
                Eidolons.getGame().getDungeonMaster().getExplorationMaster().
                 getTimeMaster().checkTimedEvents();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }


    protected MapScreen getGdxScreen() {
            return MapScreen.getInstance();
    }

    @Override
    public void end() {

    }

    @Override
    public void act(float delta) {
        timeMaster.act(delta);
    }
}
