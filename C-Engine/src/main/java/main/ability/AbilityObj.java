package main.ability;

import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.content.values.properties.G_PROPS;
import main.data.ability.construct.AbilityConstructor;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.group.GroupImpl;
import main.entity.obj.ActiveObj;
import main.entity.obj.Obj;
import main.game.core.game.Game;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.StringMaster;
import main.system.graphics.ANIM;
import main.system.text.TextParser;

//obsolete version - too specific 

public class AbilityObj extends Obj implements Ability, ActiveObj, Interruptable {

    protected Abilities abilities;
    protected boolean interrupted;
    protected Boolean cancelled;
    private boolean forcePresetTarget;
    private Obj targetObj;
    private GroupImpl targetGroup;

    public AbilityObj(AbilityType type, Ref ref, Player player, Game game) {
        super(type, player, game, ref); // entity/obj?
        this.setAbilities(AbilityConstructor.constructAbilities(type.getDoc()));
        if (abilities != null) {
            abilities.setRef(ref);
            game.getEffectManager().setEffectRefs(abilities);
        }
    }

    public AbilityObj(AbilityType type, Ref ref) {
        this(type, ref, Player.NEUTRAL, ref.game);
    }

    @Override
    public boolean isSetThis() {
        return false;
    }

    @Override
    public String getToolTip() {
        try {
            String property = type.getProperty(G_PROPS.DESCRIPTION);
            if (!StringMaster.isEmpty(property)) {
                return TextParser.parse(property, ref);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.getToolTip();
    }

    @Override
    public boolean isInterrupted() {
        if (interrupted) {
            interrupted = false;
            return true;
        }
        return getAbilities().isInterrupted();
    }

    @Override
    public boolean isForcePresetTargeting() {
        return abilities.isForcePresetTargeting();
    }

    @Override
    public void setInterrupted(boolean b) {
        this.interrupted = b;

    }

    @Override
    public void setForcePresetTargeting(boolean forcePresetTargeting) {
        abilities.setForcePresetTargeting(forcePresetTargeting);
    }

    @Override
    public void setRef(Ref ref) {
        Ref REF = ref.getCopy();
        super.setRef(REF);
        this.ref.setID(KEYS.ABILITY, id);
    }

    public boolean activate(boolean transmit) {
        return false;
    }

    @Override
    public boolean activatedOn(Ref ref) {
        setRef(ref);
        return activate();

    }

    @Override
    public Obj getTargetObj() {
        return targetObj;
    }

    public void setTargetObj(Obj targetObj) {
        this.targetObj = targetObj;
    }

    @Override
    public GroupImpl getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(GroupImpl targetGroup) {
        this.targetGroup = targetGroup;
    }

    @Override
    public boolean canBeActivated(Ref ref) {
        return false;
    }

    @Override
    public void newRound() {
        // TODO Auto-generated method stub

    }

    @Override
    public void toBase() {
        // TODO Auto-generated method stub

    }

    @Override
    public void addEffect(Effect effect) {
        abilities.addEffect(effect);
    }

    @Override
    public boolean activate() {

        return getAbilities().activatedOn(ref);
    }

    public Abilities getAbilities() {
        return abilities;
    }

    public void setAbilities(Abilities abilities) {
        this.abilities = abilities;
    }

    @Override
    public void init() {
        super.init();

    }

    @Override
    public void clicked() {
        // TODO Auto-generated method stub

    }

    @Override
    public void playCancelSound() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void addDynamicValues() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resetPercentages() {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterEffects() {
        // TODO Auto-generated method stub

    }

    @Override
    public Obj getOwnerObj() {
        return ref.getSourceObj();
    }

    public Boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isFree() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isEffectSoundPlayed() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setEffectSoundPlayed(boolean effectSoundPlayed) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean resolve() {
        return abilities.resolve();
    }

    @Override
    public Effects getEffects() {
        return abilities.getEffects();
    }

    @Override
    public void setForceTargeting(boolean forceTargeting) {
        abilities.setForceTargeting(forceTargeting);
    }

    @Override
    public void setEffects(Effects effects) {
        abilities.setEffects(effects);
    }

    @Override
    public void setTargeting(Targeting targeting) {
        abilities.setTargeting(targeting);
    }

    @Override
    public Targeting getTargeting() {
        return null;
    }

    @Override
    public Ref getRef() {
        return abilities.getRef();
    }

    @Override
    public boolean isForcePresetTarget() {
        return forcePresetTarget;
    }

    @Override
    public void setForcePresetTarget(boolean b) {
        this.forcePresetTarget = b;
        abilities.setForcePresetTargeting(b);
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public boolean isZone() {
        return false;
    }

    @Override
    public boolean isMissile() {
        return false;
    }

    @Override
    public boolean isOffhand() {
        return false;
    }

    @Override
    public ANIM getAnimation() {
        return null;
    }

    @Override
    public void initAnimation() {

    }

    @Override
    public boolean isAttackGeneric() {
        return false;
    }

    @Override
    public boolean isBlocked() {
        return false;
    }

    @Override
    public boolean isRanged() {
        return false;
    }

    @Override
    public boolean isMelee() {
        return false;
    }

    @Override
    public boolean isMove() {
        return false;
    }

    @Override
    public boolean isTurn() {
        return false;
    }

}
