package eidolons.game.netherflame.boss;

import eidolons.game.core.game.DC_Game;
import eidolons.game.netherflame.boss.ai.BossAi;
import eidolons.game.netherflame.boss.anims.BossAnim3dHandler;
import eidolons.game.netherflame.boss.anims.BossAnimHandler;
import eidolons.game.netherflame.boss.anims.view.BossViewFactory;
import eidolons.game.netherflame.boss.logic.BossCycle;
import eidolons.game.netherflame.boss.logic.action.BossActionMaster;
import eidolons.game.netherflame.boss.logic.entity.BossUnit;
import eidolons.game.netherflame.boss.logic.rules.*;
import main.content.CONTENT_CONSTS;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public abstract class BossManager<T extends BossModel> {
    protected final BossTargeter targeter;
    protected final RoundRules roundRules;
    protected final BossVision visionRules;
    protected final BossCycle cycle;
    protected final BossViewFactory factory;
    protected final BossActionMaster actionMaster;
    protected final T model; //full status?
    protected final BossAi ai;
    protected final BossAnim3dHandler animHandler3d;
    protected final BossRulesImpl rules; //should handle most of the 'special' cases
    protected final Set<BossHandler> handlers = new LinkedHashSet<>();
    protected Coordinates origin;
    private final DC_Game game;
    // protected final  BossAssembly assembly;
    //2d impl is important especially for modders - if it works, they can make 100 bosses...

    public BossManager(DC_Game game) {
        this.game = game;
        model = createModel();
        handlers.add(cycle = createCycle());
        handlers.add(ai = createAi());
        handlers.add(actionMaster = createActionMaster());
        handlers.add(rules = createRules());
        handlers.add(animHandler3d = createAnimHandler());
        handlers.add(factory = createFactory());
        handlers.add(targeter = createTargeter());
        handlers.add(roundRules = createRoundRules());
        handlers.add(visionRules = createVisionRules());
    }

    protected abstract BossActionMaster createActionMaster();

    public void init() {
        for (Coordinates c : game.getModule().getCoordinatesSet()) {
            if (game.getCellByCoordinate(c).getMarks().contains(CONTENT_CONSTS.MARK.boss)) {
                origin = c;
                break;
            }
        }
        Map<BossCycle.BOSS_TYPE, BossUnit> entities = createEntities();
        model.init(entities);
        for (BossHandler handler : getHandlers()) {
            handler.init();
        }
        for (BossHandler handler : getHandlers()) {
            handler.afterInit();
        }
        // BOSS_PART[] parts = createParts();
        // assembly = new BossAssembly(parts);
    }

    public void battleStarted() {
        for (BossHandler handler : getHandlers()) {
            handler.battleStarted();
        }
    }

    private Map<BossCycle.BOSS_TYPE, BossUnit> createEntities() {
        Map<BossCycle.BOSS_TYPE, BossUnit> entities = new LinkedHashMap<>();
        for (BossCycle.BOSS_TYPE boss_type : getModel().getCycle()) {
            ObjType type = DataManager.getType(getModel().getName(boss_type), DC_TYPE.BOSS);
            BossUnit unit = (BossUnit) game.createObject(type,
                    origin.getOffset(model.getOffset(boss_type)),
                    game.getPlayer(false));
            entities.put(boss_type, unit);
            unit.init(this);
        }
        return entities;
    }


    protected abstract BossViewFactory createFactory();

    protected abstract BossCycle createCycle();

    protected abstract BossVision createVisionRules();

    protected abstract RoundRules createRoundRules();

    protected abstract BossTargeter createTargeter();

    protected abstract T createModel();

    protected abstract BossAnim3dHandler createAnimHandler();

    protected abstract BossRulesImpl createRules();

    protected abstract BossAi createAi();

    public BossActionMaster getActionMaster() {
        return actionMaster;
    }

    public T getModel() {
        return model;
    }

    public BossAi getAi() {
        return ai;
    }

    public BossAnimHandler getAnimHandler3d() {
        return animHandler3d;
    }

    public BossRules getRules() {
        return rules;
    }

    public BossTargeter getTargeter() {
        return targeter;
    }

    public RoundRules getRoundRules() {
        return roundRules;
    }

    public BossVision getVisionRules() {
        return visionRules;
    }

    public BossCycle getCycle() {
        return cycle;
    }

    public Set<BossHandler> getHandlers() {
        return handlers;
    }

    public BossViewFactory getFactory() {
        return factory;
    }

    public DC_Game getGame() {
        return game;
    }
}
