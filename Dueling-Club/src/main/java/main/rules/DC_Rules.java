package main.rules;

import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.game.core.game.GameRules;
import main.game.logic.event.Rule;
import main.rules.action.*;
import main.rules.buff.*;
import main.rules.combat.*;
import main.rules.counter.*;
import main.rules.mechanics.DurabilityRule;
import main.rules.mechanics.WaitRule;
import main.rules.round.*;
import main.system.datatypes.DequeImpl;

public class DC_Rules implements GameRules {

    protected DequeImpl<Rule> buffRules = new DequeImpl<>();
    protected DequeImpl<DamageCounterRule> damageRules = new DequeImpl<>();


    WatchRule watchRule;
    private DC_Game game;
    private DequeImpl<DC_CounterRule> counterRules = new DequeImpl<>();
    private DequeImpl<RoundRule> roundRules = new DequeImpl<>();
    private MoraleBuffRule moraleBuffRule;
    private StaminaBuffRule staminaRule;
    private WeightBuffRule weightRule;
    private FocusBuffRule focusBuffRule;
    private DurabilityRule durabilityRule;
    private BleedingRule bleedingTriggerRule;
    private TimeRule lateActionsRule;
    private BleedingCounterRule bleedingRule;
    private WoundsBuffRule woundsRule;
    private PoisonRule poisonRule;
    private FreezeRule freezeRule;
    private MoraleKillingRule moraleKillingRule;
    private FocusRule focusRule;
    private MoraleRule moraleRule;
    private UpkeepRule upkeepRule;
    private CleaveRule cleaveRule;
    private RageRule rageRule;
    private StealthRule stealthRule;
    private DequeImpl<ActionRule> actionRules = new DequeImpl<>();
    private DiseaseRule diseaseRule;
    private MoistRule moistRule;
    private EnsnaredRule ensnareRule;
    private ScoutingRule scoutingRule;
    private TrampleRule trampleRule;
    private CorrosionRule corrosionRule;
    private BlightRule blightRule;
    private BlazeRule blazeRule;
    private StackingRule stackingRule;
    private EngagedRule engagedRule;
    private WaterRule waterRule;
    private UnconsciousRule unconsciousRule;

    public DC_Rules(DC_Game game) {
        this.setGame(game);
        init();
    }

    public void applyContinuousRules(Unit unit) {
        KnockdownRule.checkApplyProneEffect(unit);
    }

    private void init() {
        RuleMaster.init();
        WaitRule.reset();

        unconsciousRule = new UnconsciousRule(game);
        watchRule = new WatchRule();
        engagedRule = new EngagedRule(getGame());
        stackingRule = new StackingRule(getGame());
        ensnareRule = new EnsnaredRule(getGame());
        stealthRule = new StealthRule(getGame());
        waterRule = new WaterRule(getGame());
        stackingRule = new StackingRule(getGame());
        actionRules.add(unconsciousRule);
        actionRules.add(watchRule);
        actionRules.add(waterRule);
        actionRules.add(stealthRule);
        actionRules.add(ensnareRule);
        actionRules.add(stackingRule);
        actionRules.add(engagedRule);
        getGame().setActionRules(actionRules);

        cleaveRule = new CleaveRule(getGame());

        focusRule = new FocusRule(getGame());
        moraleRule = new MoraleRule(getGame());
        upkeepRule = new UpkeepRule(getGame());
        scoutingRule = new ScoutingRule(getGame());
        roundRules.add(focusRule);
        roundRules.add(moraleRule);
        roundRules.add(upkeepRule);
        roundRules.add(scoutingRule);
        roundRules.add(waterRule);
        roundRules.add(unconsciousRule);

        moraleKillingRule = new MoraleKillingRule(getGame());

        bleedingRule = new BleedingCounterRule(game);
        damageRules.add(bleedingRule);
        poisonRule = new PoisonRule(game);
        damageRules.add(poisonRule);
        diseaseRule = new DiseaseRule(game);

        damageRules.add(diseaseRule);

        blazeRule = new BlazeRule(game);
        freezeRule = new FreezeRule(game);
        moistRule = new MoistRule(game);
        rageRule = new RageRule(game);
        corrosionRule = new CorrosionRule(game);
        blightRule = new BlightRule(game);

        // despairRule = new DespairRule(game);
        // lustRule = new LustRule(game);
        // hatredRule = new HatredRule(game);

        counterRules.add(blightRule);
        counterRules.add(corrosionRule);
        counterRules.add(rageRule);
        counterRules.add(moistRule);
        counterRules.add(freezeRule);
        counterRules.add(blazeRule);
        counterRules.add(ensnareRule);
        counterRules.addAll(damageRules);

        lateActionsRule = new TimeRule(getGame());
        moraleBuffRule = new MoraleBuffRule(getGame());
        this.buffRules.add(moraleBuffRule);
        staminaRule = new StaminaBuffRule(getGame());
        this.buffRules.add(staminaRule);
        weightRule = new WeightBuffRule(getGame());
        this.buffRules.add(weightRule);
        focusBuffRule = new FocusBuffRule(getGame());
        this.buffRules.add(focusBuffRule);
        woundsRule = new WoundsBuffRule(getGame());
        this.buffRules.add(woundsRule);

        trampleRule = new TrampleRule(getGame());
        durabilityRule = new DurabilityRule(getGame());
        bleedingTriggerRule = new BleedingRule(game);
        game.getState().getTriggerRules().add(trampleRule);
        game.getState().getTriggerRules().add(durabilityRule);
        game.getState().getTriggerRules().add(bleedingTriggerRule);

        // this.rules.add(rule);
        // rule = new TreasonRule(getGame());
        // this.rules.add(rule);
        // rule = new PanicRule(getGame());
        // this.rules.add(rule);
        // rule = new ClaimRule(getGame());
        // this.rules.add(rule);
    }

    public DC_Game getGame() {
        return game;
    }

    public void setGame(DC_Game game) {
        this.game = game;
    }

    public synchronized DequeImpl<Rule> getBuffRules() {
        return buffRules;
    }

    public synchronized void setBuffRules(DequeImpl<Rule> buffRules) {
        this.buffRules = buffRules;
    }

    public synchronized MoraleBuffRule getMoraleRule() {
        return moraleBuffRule;
    }

    public synchronized void setMoraleRule(MoraleBuffRule moraleRule) {
        this.moraleBuffRule = moraleRule;
    }

    public synchronized StaminaBuffRule getStaminaRule() {
        return staminaRule;
    }

    public synchronized void setStaminaRule(StaminaBuffRule staminaRule) {
        this.staminaRule = staminaRule;
    }

    public synchronized WeightBuffRule getWeightRule() {
        return weightRule;
    }

    public synchronized void setWeightRule(WeightBuffRule weightRule) {
        this.weightRule = weightRule;
    }

    public synchronized FocusBuffRule getFocusRule() {
        return focusBuffRule;
    }

    public synchronized void setFocusRule(FocusBuffRule focusRule) {
        this.focusBuffRule = focusRule;
    }

    public synchronized DurabilityRule getDurabilityRule() {
        return durabilityRule;
    }

    public synchronized void setDurabilityRule(DurabilityRule durabilityRule) {
        this.durabilityRule = durabilityRule;
    }

    public synchronized TimeRule getTimeRule() {
        return lateActionsRule;
    }

    public synchronized void setLateActionsRule(TimeRule lateActionsRule) {
        this.lateActionsRule = lateActionsRule;
    }

    public DequeImpl<RoundRule> getRoundRules() {
        return roundRules;
    }

    public synchronized DequeImpl<DamageCounterRule> getDamageRules() {
        return damageRules;
    }

    public synchronized void setDamageRules(DequeImpl<DamageCounterRule> damageRules) {
        this.damageRules = damageRules;
    }

    public synchronized BleedingCounterRule getBleedingRule() {
        return bleedingRule;
    }

    public synchronized void setBleedingRule(BleedingCounterRule bleedingRule) {
        this.bleedingRule = bleedingRule;
    }

    public synchronized WoundsBuffRule getWoundsRule() {
        return woundsRule;
    }

    public synchronized void setWoundsRule(WoundsBuffRule woundsRule) {
        this.woundsRule = woundsRule;
    }

    public MoraleKillingRule getMoraleKillingRule() {
        return moraleKillingRule;
    }

    public CleaveRule getCleaveRule() {
        return cleaveRule;
    }

    public DequeImpl<DC_CounterRule> getCounterRules() {
        return counterRules;
    }

    public MoraleBuffRule getMoraleBuffRule() {
        return moraleBuffRule;
    }

    public FocusBuffRule getFocusBuffRule() {
        return focusBuffRule;
    }

    public BleedingRule getBleedingTriggerRule() {
        return bleedingTriggerRule;
    }

    public PoisonRule getPoisonRule() {
        return poisonRule;
    }

    public FreezeRule getFreezeRule() {
        return freezeRule;
    }

    public UpkeepRule getUpkeepRule() {
        return upkeepRule;
    }

    public RageRule getRageRule() {
        return rageRule;
    }

    public StealthRule getStealthRule() {
        return stealthRule;
    }

    public DequeImpl<ActionRule> getActionRules() {
        return actionRules;
    }

    public DiseaseRule getDiseaseRule() {
        return diseaseRule;
    }

    public MoistRule getMoistRule() {
        return moistRule;
    }

    public EnsnaredRule getEnsnareRule() {
        return ensnareRule;
    }

    public ScoutingRule getScoutingRule() {
        return scoutingRule;
    }

    public TrampleRule getTrampleRule() {
        return trampleRule;
    }

    public CorrosionRule getCorrosionRule() {
        return corrosionRule;
    }

    public BlightRule getBlightRule() {
        return blightRule;
    }

    public EngagedRule getEngagedRule() {
        return engagedRule;
    }

    public void setEngagedRule(EngagedRule engagedRule) {
        this.engagedRule = engagedRule;
    }

    public BlazeRule getBlazeRule() {
        return blazeRule;
    }

    public StackingRule getStackingRule() {
        return stackingRule;

    }

    public WatchRule getWatchRule() {
        return watchRule;
    }

    public void setWatchRule(WatchRule watchRule) {
        this.watchRule = watchRule;
    }

}
