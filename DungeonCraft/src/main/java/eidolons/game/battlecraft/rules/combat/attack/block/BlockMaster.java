package eidolons.game.battlecraft.rules.combat.attack.block;

import eidolons.content.PARAMS;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.action.StackingRule;
import eidolons.game.battlecraft.rules.combat.CombatFunctions;
import eidolons.game.battlecraft.rules.combat.attack.Attack;
import eidolons.game.battlecraft.rules.combat.attack.accuracy.AccuracyMaster;
import eidolons.game.battlecraft.rules.mechanics.DurabilityRule;
import eidolons.game.core.game.DC_Game;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.math.roll.DiceMaster;
import main.ability.effects.Effect;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.NewRpgEnums;
import main.system.auxiliary.log.LogMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BlockMaster {
    private DC_Game game;
    private final ParryRule parryRule;
    private final ShieldRule shieldRule;
    Map<Unit, List<Blocker>> cache;

    public BlockMaster(DC_Game game) {
        this.game = game;
        parryRule = new ParryRule();
        shieldRule = new ShieldRule();
    }

    public BlockResult attacked(Attack attack) {
        List<Blocker> blockers = getBlockers((Unit) attack.getAttacked());
        List<BlockResult> results = new ArrayList<>();
        for (Blocker blocker : blockers) {
            BlockResult result = null;
            if (blocker.type == NewRpgEnums.BlockerType.shield) {
                result = shieldRule.processAttack(attack, blocker);
            } else if (blocker.type == NewRpgEnums.BlockerType.weapon) {
                result = parryRule.processAttack(attack, blocker);
            } else {
                //TODO 'magic'?..
                // result =  parryRule.processAttack(attack, blocker);
            }

            results.add(result);
        }
        //is there just one?

        return new BlockResult(results.toArray(new BlockResult[0]));
    }

    private List<Blocker> getBlockers(Unit attacked) {
        List<Blocker> blockers = cache.get(attacked);
        if (blockers == null) {
            blockers = createBlockers(attacked);
            cache.put(attacked, blockers);
        }
        return blockers;
    }

    //at the end only!
    public boolean applyBlock(Attack attack, BlockResult result) {
        boolean negated = false;
        if (result.accuracyReduction > 0) {
            int accuracy = attack.getAccuracyRate();
            NewRpgEnums.HitType hitType = AccuracyMaster.getHitType(accuracy - result.accuracyReduction);

            if (hitType == NewRpgEnums.HitType.miss || hitType == NewRpgEnums.HitType.critical_miss)
                negated = true;
            attack.setHitType(hitType);
        }
        if (negated) {
            attack.setDodged(true);
        } else {
            //TODO logging?
        }
        // BattleFieldObject attacked = attack.getAttacked();
        // BattleFieldObject attacker = attack.getAttacker();
        // attacked.getGame().getLogManager().log(LogMaster.LOG.GAME_INFO,
        //         result.logMsg.toString());

        return negated;
    }

    // In a form displayable as Tooltip?
    public List<String> getBlockDescriptions(Attack attack) {
        List<String> list = new ArrayList<>();
        if (!(attack.getAttacked() instanceof Unit)) {
            return list;
        }
        List<Blocker> blockers = createBlockers((Unit) attack.getAttacked());
        int reduction = CombatFunctions.getBlockChanceReduction(attack.getAttacker());
        for (Blocker blocker : blockers) {
            list.add(blocker.getDescription(reduction));
        }
        return list;
    }

    public int getBlockRating(Attack attack) {
        //TODO
        //for AI/tooltips only
        return 0;
    }


    public void dodged(Attack attack) {
        if (attack.getHitType() == NewRpgEnums.HitType.critical_miss) {
            StackingRule.actionMissed(attack.getAction());
            attack.getAction().setFailedLast(true);
            game.getLogManager().log(LogMaster.LOG.GAME_INFO, attack + " is a Critical Miss!");
        } else
            game.getLogManager().log(LogMaster.LOG.GAME_INFO, attack + " misses!");
        attack.setDodged(true);
        DC_SoundMaster.playMissedSound(attack.getAttacker(), attack.getWeapon());
        // if (attack.getHitType()== NewRpgEnums.HitType.critical_miss) {
        // }
        // // ++ animation? *MISS* //TODO ++ true strike

    }

    public List<Blocker> createBlockers(Unit unit) {
        //compare weapon sizes?
        List<Blocker> list = new ArrayList<>();

        if (unit.getWeapon(true).isShield()) {
            list.add(createShieldBlocker(unit.getWeapon(true)));
        } else if (isParryWeapon(unit.getWeapon(true))) {
            list.add(createWeaponBlocker(unit.getWeapon(true), false));
        }
        if (isParryWeapon(unit.getWeapon(false)))
            list.add(createWeaponBlocker(unit.getWeapon(false), false));
        //TODO check magic
        return list;
    }

    private boolean isParryWeapon(DC_WeaponObj weapon) {
        if (weapon.isRanged()) {
            return false;
        }
        if (weapon.getWeaponSize() == ItemEnums.WEAPON_SIZE.TINY) {
            return false;
        }
        if (weapon.getWeaponType() == ItemEnums.WEAPON_TYPE.MAGICAL) {
            return false;
        }
        if (weapon.getWeaponType() == ItemEnums.WEAPON_TYPE.NATURAL) {
            return false;
        }
        return true;
    }

    private Blocker createShieldBlocker(DC_WeaponObj weapon) {
        return createWeaponBlocker(weapon, true);
    }

    private Blocker createWeaponBlocker(DC_WeaponObj weapon, boolean shield) {
        Supplier<Integer> base = () -> weapon.getIntParam(PARAMS.BLOCK_CHANCE);
        Supplier<Integer> bonus = () -> weapon.getOwnerObj().getIntParam(PARAMS.BLOCK_CHANCE_BONUS)
                + weapon.getOwnerObj().getIntParam(
                shield ? PARAMS.BLOCK_CHANCE_BONUS_SHIELD : PARAMS.BLOCK_CHANCE_BONUS_PARRY);
        Supplier<Integer> dice = () -> DiceMaster.getDefaultDieNumber(weapon.getOwnerObj());
        Supplier<Integer> value = () -> weapon.getIntParam(PARAMS.DAMAGE_BONUS);
        Consumer<Integer> durFunc = (dmgBlocked) -> DurabilityRule.itemBlocked(dmgBlocked, weapon);
        Effect fx = null;

        return new Blocker(NewRpgEnums.BlockerType.shield, base, bonus, dice, value, durFunc, fx, weapon.getName(), weapon.getOwnerObj());
    }


}
