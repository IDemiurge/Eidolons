package main.game.battlecraft.rules;

import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.battlefield.vision.StealthRule;
import main.game.battlecraft.rules.action.*;
import main.game.battlecraft.rules.buff.*;
import main.game.battlecraft.rules.combat.mechanics.BleedingRule;
import main.game.battlecraft.rules.combat.mechanics.MoraleKillingRule;
import main.game.battlecraft.rules.combat.misc.CleaveRule;
import main.game.battlecraft.rules.combat.misc.KnockdownRule;
import main.game.battlecraft.rules.combat.misc.TrampleRule;
import main.game.battlecraft.rules.counter.*;
import main.game.battlecraft.rules.mechanics.AshAnnihilationRule;
import main.game.battlecraft.rules.mechanics.DurabilityRule;
import main.game.battlecraft.rules.mechanics.WaitRule;
import main.game.battlecraft.rules.round.*;
import main.game.core.game.DC_Game;
import main.game.core.game.GameRules;
import main.system.datatypes.DequeImpl;

public class DC_Rules implements GameRules {

    protected DequeImpl<DC_BuffRule> buffRules = new DequeImpl<>();
    protected DequeImpl<DamageCounterRule> damageRules = new DequeImpl<>();
    private DC_Game game;
    private DequeImpl<DC_CounterRule> counterRules = new DequeImpl<>();
    private DequeImpl<RoundRule> roundRules = new DequeImpl<>();
    private DequeImpl<ActionRule> actionRules = new DequeImpl<>();
    private DequeImpl<DC_RuleImpl> triggerRules= new DequeImpl<>();

    private TimeRule timeRule;
    private WatchRule watchRule;
    private FocusRule focusRule;
    private MoraleRule moraleRule;
    private UpkeepRule upkeepRule;
    private MoraleBuffRule moraleBuffRule;
    private StaminaBuffRule staminaRule;
    private WeightBuffRule weightRule;
    private FocusBuffRule focusBuffRule;
    private DurabilityRule durabilityRule;
    private ScoutingRule scoutingRule;

    private BleedingRule bleedingTriggerRule;
    private BleedingDamageRule bleedingRule;
    private WoundsBuffRule woundsRule;
    private PoisonRule poisonRule;
    private FreezeRule freezeRule;
    private MoraleKillingRule moraleKillingRule;
    private CleaveRule cleaveRule;
    private RageRule rageRule;
    private StealthRule stealthRule;
    private DiseaseRule diseaseRule;
    private MoistRule moistRule;
    private EnsnaredRule ensnareRule;
    private TrampleRule trampleRule;
    private CorrosionRule corrosionRule;
    private BlightRule blightRule;
    private BlazeRule blazeRule;
    private StackingRule stackingRule;
    private EngagedRule engagedRule;
    private WaterRule waterRule;
    private UnconsciousRule unconsciousRule;
    private GreaseRule greaseRule;
    private ClayRule clayRule;
    private EncaseRule encaseRule;
    private LavaRule lavaRule;
    private SuffocationRule suffocationRule;
    private AshAnnihilationRule ashAnnihilationRule;


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

        bleedingRule = new BleedingDamageRule(game);
        poisonRule = new PoisonRule(game);
        diseaseRule = new DiseaseRule(game);
        blazeRule = new BlazeRule(game);
        lavaRule = new LavaRule(game);
        suffocationRule = new SuffocationRule(game);

        damageRules.add(bleedingRule);
        damageRules.add(suffocationRule);
        damageRules.add(poisonRule);
        damageRules.add(diseaseRule);
        damageRules.add(blazeRule);
        damageRules.add(lavaRule);
        counterRules.addAll(damageRules);

        freezeRule = new FreezeRule(game);
        moistRule = new MoistRule(game);
        rageRule = new RageRule(game);
        corrosionRule = new CorrosionRule(game);
        blightRule = new BlightRule(game);

        greaseRule = new GreaseRule(game);
        clayRule = new ClayRule(game);
        encaseRule = new EncaseRule(game);

        // despairRule = new DespairRule(game);
        // lustRule = new LustRule(game);
        // hatredRule = new HatredRule(game);
        counterRules.add(rageRule);

        counterRules.add(blightRule);
        counterRules.add(corrosionRule);
        counterRules.add(clayRule);
        counterRules.add(moistRule);
        counterRules.add(freezeRule);
        counterRules.add(ensnareRule);
        counterRules.add(encaseRule);
        counterRules.add(greaseRule);



        timeRule = new TimeRule(getGame());
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
        ashAnnihilationRule = new AshAnnihilationRule(game);
         getTriggerRules().add(trampleRule);
        getTriggerRules().add(durabilityRule);
        getTriggerRules().add(bleedingTriggerRule);
        getTriggerRules().add(ashAnnihilationRule);

        CounterMasterAdvanced.defineInteractions();

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

    public DequeImpl<DC_BuffRule> getBuffRules() {
        return buffRules;
    }

    public DequeImpl<DamageCounterRule> getDamageRules() {
        return damageRules;
    }

    public DequeImpl<DC_CounterRule> getCounterRules() {
        return counterRules;
    }

    public DequeImpl<RoundRule> getRoundRules() {
        return roundRules;
    }

    public DequeImpl<ActionRule> getActionRules() {
        return actionRules;
    }

    public TimeRule getTimeRule() {
        return timeRule;
    }

    public WatchRule getWatchRule() {
        return watchRule;
    }

    public FocusRule getFocusRule() {
        return focusRule;
    }

    public MoraleRule getMoraleRule() {
        return moraleRule;
    }

    public SuffocationRule getSuffocationRule() {
        return suffocationRule;
    }

    public AshAnnihilationRule getAshAnnihilationRule() {
        return ashAnnihilationRule;
    }

    public UpkeepRule getUpkeepRule() {
        return upkeepRule;
    }

    public MoraleBuffRule getMoraleBuffRule() {
        return moraleBuffRule;
    }

    public StaminaBuffRule getStaminaRule() {
        return staminaRule;
    }

    public WeightBuffRule getWeightRule() {
        return weightRule;
    }

    public FocusBuffRule getFocusBuffRule() {
        return focusBuffRule;
    }

    public DurabilityRule getDurabilityRule() {
        return durabilityRule;
    }

    public ScoutingRule getScoutingRule() {
        return scoutingRule;
    }

    public BleedingRule getBleedingTriggerRule() {
        return bleedingTriggerRule;
    }

    public BleedingDamageRule getBleedingRule() {
        return bleedingRule;
    }

    public WoundsBuffRule getWoundsRule() {
        return woundsRule;
    }

    public PoisonRule getPoisonRule() {
        return poisonRule;
    }

    public FreezeRule getFreezeRule() {
        return freezeRule;
    }

    public MoraleKillingRule getMoraleKillingRule() {
        return moraleKillingRule;
    }

    public CleaveRule getCleaveRule() {
        return cleaveRule;
    }

    public RageRule getRageRule() {
        return rageRule;
    }

    public StealthRule getStealthRule() {
        return stealthRule;
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

    public TrampleRule getTrampleRule() {
        return trampleRule;
    }

    public CorrosionRule getCorrosionRule() {
        return corrosionRule;
    }

    public BlightRule getBlightRule() {
        return blightRule;
    }

    public BlazeRule getBlazeRule() {
        return blazeRule;
    }

    public StackingRule getStackingRule() {
        return stackingRule;
    }

    public EngagedRule getEngagedRule() {
        return engagedRule;
    }

    public WaterRule getWaterRule() {
        return waterRule;
    }

    public UnconsciousRule getUnconsciousRule() {
        return unconsciousRule;
    }

    public GreaseRule getGreaseRule() {
        return greaseRule;
    }

    public ClayRule getClayRule() {
        return clayRule;
    }

    public EncaseRule getEncaseRule() {
        return encaseRule;
    }

    public LavaRule getLavaRule() {
        return lavaRule;
    }

    public DequeImpl<DC_RuleImpl> getTriggerRules() {
        return triggerRules;
    }
}
