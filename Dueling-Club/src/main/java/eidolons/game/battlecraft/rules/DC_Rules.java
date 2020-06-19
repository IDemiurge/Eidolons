package eidolons.game.battlecraft.rules;

import eidolons.entity.obj.attach.DynamicBuffRules;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.ai.tools.ParamAnalyzer.BUFF_RULE;
import eidolons.game.battlecraft.logic.battlefield.vision.advanced.StealthRule;
import eidolons.game.battlecraft.rules.action.*;
import eidolons.game.battlecraft.rules.buff.*;
import eidolons.game.battlecraft.rules.combat.mechanics.BleedingRule;
import eidolons.game.battlecraft.rules.combat.mechanics.MoraleKillingRule;
import eidolons.game.battlecraft.rules.combat.misc.CleaveRule;
import eidolons.game.battlecraft.rules.combat.misc.KnockdownRule;
import eidolons.game.battlecraft.rules.combat.misc.TrampleRule;
import eidolons.game.battlecraft.rules.counter.*;
import eidolons.game.battlecraft.rules.counter.generic.CounterMasterAdvanced;
import eidolons.game.battlecraft.rules.counter.generic.DC_CounterRule;
import eidolons.game.battlecraft.rules.counter.generic.DamageCounterRule;
import eidolons.game.battlecraft.rules.counter.timed.TimedRule;
import eidolons.game.battlecraft.rules.mechanics.AshAnnihilationRule;
import eidolons.game.battlecraft.rules.mechanics.DurabilityRule;
import eidolons.game.battlecraft.rules.mechanics.IlluminationRule;
import eidolons.game.battlecraft.rules.mechanics.WaitRule;
import eidolons.game.battlecraft.rules.round.*;
import eidolons.game.core.game.DC_Game;
import main.game.core.game.GameRules;
import main.system.datatypes.DequeImpl;

import java.util.LinkedHashMap;
import java.util.Map;

public class DC_Rules implements GameRules {

    protected DequeImpl<DC_BuffRule> buffRules = new DequeImpl<>();
    protected DequeImpl<DamageCounterRule> damageRules = new DequeImpl<>();
    private DC_Game game;
    private final DequeImpl<DC_CounterRule> counterRules = new DequeImpl<>();
    private final DequeImpl<RoundRule> roundRules = new DequeImpl<>();
    private final DequeImpl<ActionRule> actionRules = new DequeImpl<>();
    private final DequeImpl<DC_RuleImpl> triggerRules = new DequeImpl<>();

    private WatchRule watchRule;
    private FocusRule focusRule;
    private EssenceRule essenceRule;
    private UpkeepRule upkeepRule;
    private EssenceBuffRule essenceBuffRule;
    private ToughnessBuffRule staminaRule;
    private WeightBuffRule weightRule;
    private FocusBuffRule focusBuffRule;
    private DurabilityRule durabilityRule;

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
    private HearingRule hearingRule;
    private WaterRule waterRule;
    private UnconsciousRule unconsciousRule;
    private GreaseRule greaseRule;
    private ClayRule clayRule;
    private EncaseRule encaseRule;
    private LavaRule lavaRule;
    private SuffocationRule suffocationRule;
    private AshAnnihilationRule ashAnnihilationRule;
    private IlluminationRule illuminationRule;
    private final DC_RuleMaster master;
    private Map<DamageCounterRule, TimedRule> timedRules;
    private DynamicBuffRules dynamicBuffRules;


    public DC_Rules(DC_Game game) {
        this.setGame(game);
        master = new DC_RuleMaster(game, this);
        init();
    }

    public void applyContinuousRules(Unit unit) {
        KnockdownRule.checkApplyProneEffect(unit);
    }

    private void init() {
        RuleKeeper.init();
        WaitRule.reset();
        dynamicBuffRules = new DynamicBuffRules(game);
        illuminationRule = new IlluminationRule();
        unconsciousRule = new UnconsciousRule(game);
        watchRule = new WatchRule();
        engagedRule = new EngagedRule(getGame());
        stackingRule = new StackingRule(getGame());
        ensnareRule = new EnsnaredRule(getGame());
        stealthRule = new StealthRule(getGame());
        stackingRule = new StackingRule(getGame());
        hearingRule = new HearingRule(getGame());
        actionRules.add(unconsciousRule);
        actionRules.add(watchRule);
        actionRules.add(stealthRule);
        actionRules.add(ensnareRule);
        actionRules.add(stackingRule);
        actionRules.add(engagedRule);
        actionRules.add(hearingRule);
//        actionRules.add(waterRule= new WaterRule(getGame()));

        cleaveRule = new CleaveRule(getGame());

        focusRule = new FocusRule(getGame());
        essenceRule = new EssenceRule(getGame());
        roundRules.add(focusRule);
        roundRules.add(essenceRule);
//        roundRules.add( upkeepRule = new UpkeepRule(getGame()) );
//        roundRules.add( scoutingRule = new ScoutingRule(getGame()));
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

        essenceBuffRule = new EssenceBuffRule(getGame());
        this.buffRules.add(essenceBuffRule);
        staminaRule = new ToughnessBuffRule(getGame());
        this.buffRules.add(staminaRule);
        weightRule = new WeightBuffRule(getGame());
        this.buffRules.add(weightRule);
        focusBuffRule = new FocusBuffRule(getGame());
        this.buffRules.add(focusBuffRule);
        woundsRule = new WoundsBuffRule(getGame());
        this.buffRules.add(woundsRule);

//        getTriggerRules().add(trampleRule = new TrampleRule(getGame()));
        if (!EidolonsGame.FOOTAGE) {
        getTriggerRules().add(durabilityRule = new DurabilityRule(getGame()));
        getTriggerRules().add(bleedingTriggerRule = new BleedingRule(game));
        getTriggerRules().add(ashAnnihilationRule = new AshAnnihilationRule(game));
        }

        CounterMasterAdvanced.defineInteractions();

        timedRules =     new LinkedHashMap<>();
//        for (DamageCounterRule sub : damageRules) {
//        }
        timedRules.put(poisonRule,poisonRule);
        timedRules.put(diseaseRule,diseaseRule);
        timedRules.put(blazeRule,blazeRule);
        timedRules.put(bleedingRule,bleedingRule);
        timedRules.put(suffocationRule,suffocationRule);
        timedRules.put(lavaRule,lavaRule);


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

    public WatchRule getWatchRule() {
        return watchRule;
    }

    public FocusRule getFocusRule() {
        return focusRule;
    }

    public EssenceRule getEssenceRule() {
        return essenceRule;
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

    public EssenceBuffRule getEssenceBuffRule() {
        return essenceBuffRule;
    }

    public ToughnessBuffRule getStaminaRule() {
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

    public IlluminationRule getIlluminationRule() {
        return illuminationRule;
    }

    public void setIlluminationRule(IlluminationRule illuminationRule) {
        this.illuminationRule = illuminationRule;
    }

    public void timePassed(Float time) {
        master.timePassed(time);
    }

    public Map<DamageCounterRule, TimedRule> getTimedRules() {
        return timedRules;
    }

    public DC_BuffRule getBuffRule(BUFF_RULE rule) {
        switch (rule) {
            case MORALE:
                return essenceBuffRule;
            case FOCUS:
                return focusBuffRule;
            case STAMINA:
                return staminaRule;
            case WOUNDS:
                return woundsRule;
        }
        return null ;
    }

    public DynamicBuffRules getDynamicBuffRules() {
        return dynamicBuffRules;
    }

}
