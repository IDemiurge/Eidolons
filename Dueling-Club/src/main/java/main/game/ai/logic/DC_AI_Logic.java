package main.game.ai.logic;

import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.Active;
import main.entity.obj.Obj;
import main.game.ai.AI;
import main.game.ai.AI_Logic;
import main.game.ai.logic.ActionTypeManager.ACTION_TYPES;
import main.game.core.game.MicroGame;
import main.game.logic.battle.player.Player;
import main.game.logic.generic.ActionManager;
import main.system.auxiliary.log.LogMaster;

import java.util.Set;

public abstract class DC_AI_Logic implements AI_Logic {
    protected Obj unit;
    protected DC_ActiveObj active;
    protected Set<Obj> units;
    protected Set<Obj> enemyUnits;
    protected AI ai;
    protected ACTION_TYPES action;
    protected ActionManager actionManager;
    protected MicroGame game;
    protected Player player;
    protected Player enemy;
    protected TargetingManager tManager;
    protected OldPriorityManager pManager;
    protected ActionTypeManager aManager;
    protected Analyzer analyzer;

    private Integer target;

    public DC_AI_Logic(AI ai) {
        this.ai = ai;
        this.game = ai.getGame();
        this.player = ai.getPlayer();
        this.enemy = game.getPlayer(true);
        this.actionManager = ai.getGame().getActionManager();

    }

    /**
     * USE: once
     *
     * @return
     */
    @Override
    public Object[] getArgsForExecution(Obj unit) {
        setUnit(unit);
        Object[] args = new Object[2];
        Obj active = getActive();
        int id1 = active.getId();

        args[0] = id1;
        Ref ref = Ref.getCopy(unit.getRef());
        if (getTarget() == null) {
            target = initTargetId();
            if (target == -1) {
                return null;
            }
        }
        ref.setTarget(target);
        args[1] = ref.getData();
        // main.system.auxiliary.LogMaster.log(LogMaster.AI_DEBUG, unit +
        // " has "
        // + args);
        return args;
    }

    @Override
    public int initTargetId() {
        return tManager.initTarget();
    }

    public int getPriorityForUnit(Obj unit, Set<Obj> units) {
        return pManager.getPriorityForUnit();
    }

    @Override
    public Obj getPriorityUnit() {
        Obj UNIT;
        int greatest_priority = 0;
        int index = -1;
        int i = 0;
        for (Obj unit : units) {
            int priority = getPriorityForUnit(unit, units);
            index++;
            if (priority > greatest_priority) {
                greatest_priority = priority;
                i = index;
            }
        }
        UNIT = (Obj) units.toArray()[i];
        return UNIT;
    }

    @Override
    public void newTurn() {
        setUnits(this.player.getControlledUnits());
        setEnemyUnits(this.getEnemy().getControlledUnits());

    }

    @Override
    public ACTION_TYPES initAction() {
        this.action = aManager.getAction();

        LogMaster
                .log(LogMaster.AI_DEBUG, "Action chosen: " + action + " for "
                        + unit);
        return getAction();
    }

    @Override
    public ACTION_TYPES getAction() {
        return action;
    }

    @Override
    public void setAction(ACTION_TYPES action) {
        this.action = action;
    }

    @Override
    public Obj getUnit() {
        return unit;
    }

    @Override
    public void setUnit(Obj unit) {
        this.unit = unit;
    }

    @Override
    public Set<Obj> getUnits() {
        return units;
    }

    @Override
    public void setUnits(Set<Obj> units) {
        this.units = units;
    }

    @Override
    public AI getAi() {
        return ai;
    }

    @Override
    public void setAi(AI ai) {
        this.ai = ai;
    }

    @Override
    public ActionManager getActionManager() {
        return actionManager;
    }

    @Override
    public void setActionManager(ActionManager actionManager) {
        this.actionManager = actionManager;
    }

    @Override
    public MicroGame getGame() {
        return game;
    }

    @Override
    public void setGame(MicroGame game) {
        this.game = game;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public TargetingManager gettManager() {
        return tManager;
    }

    @Override
    public void settManager(TargetingManager tManager) {
        this.tManager = tManager;
    }

    @Override
    public OldPriorityManager getpManager() {
        return pManager;
    }

    @Override
    public void setpManager(OldPriorityManager pManager) {
        this.pManager = pManager;
    }

    @Override
    public ActionTypeManager getaManager() {
        return aManager;
    }

    @Override
    public void setaManager(ActionTypeManager aManager) {
        this.aManager = aManager;
    }

    @Override
    public void setActive(Active active) {
        this.active = (DC_ActiveObj) active;
    }

    @Override
    public Analyzer getAnalyzer() {
        return this.analyzer;
    }

    @Override
    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    @Override
    public Player getEnemy() {
        return enemy;
    }

    public void setEnemy(Player enemy) {
        this.enemy = enemy;
    }

    @Override
    public Set<Obj> getEnemyUnits() {
        if (enemyUnits == null) {
            enemyUnits = enemy.getControlledUnits();
        }
        return enemyUnits;
    }

    public void setEnemyUnits(Set<Obj> enemyUnits) {
        this.enemyUnits = enemyUnits;
    }

    @Override
    public Integer getTarget() {
        return target;
    }

    @Override
    public void setTarget(Integer target) {
        this.target = target;
    }
}
