package eidolons.game.battlecraft.rules.combat.attack.block;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.rules.combat.attack.Attack;
import eidolons.game.battlecraft.rules.combat.attack.accuracy.AccuracyMaster;
import main.ability.effects.Effect;
import main.content.enums.entity.NewRpgEnums;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;

/*
IDEA: don't rely on concrete item, create an abstraction - so we can use spells that will 'fake' such blocking fx!
 */
public abstract class BlockRule {
    protected BlockResult result;
    protected Attack attack;
    private boolean simulation;

    public static class BlockResult {
        NewRpgEnums.BlockType blockType;
        int accuracyReduction;
        int durabilityDamage;
        int durabilitySelfDamage;
        Effect onBlockEffects;
        StringBuilder logMsg=new StringBuilder();
    }

    //could override!
    public NewRpgEnums.BlockType getBlockType(NewRpgEnums.HitType hitType) {
        switch (hitType) {
            case graze:
            case hit:
                return NewRpgEnums.BlockType.deflect;
            case critical_hit:
            case deadeye:
                return NewRpgEnums.BlockType.block;
        }
        return null;
    }

    public void failedBlock(Attack attack, BlockResult result ) {
        // if (!simulation)// will be average instead
        //     result.logMsg = new StringBuilder(StringMaster.getMessagePrefix(true, attacked.getOwner().isMe())
        //             + attacked.getName() + " fails to use " + shield.getName()
        //             + " to block " + action.getName()
        //             + StringMaster.wrapInParenthesis(chance + "%"));

            }
    public void applyBlock(Attack attack, BlockResult result, Blocker blocker) {
        if (result.accuracyReduction>0) {
            int accuracy = attack.getAccuracyRate();
            NewRpgEnums.HitType hitType = AccuracyMaster.getHitType(accuracy - result.accuracyReduction);
            attack.setHitType(hitType);
        }

        attack.setDodged(true);
        // result.onBlockEffects
        BattleFieldObject attacked = attack.getAttacked();
        BattleFieldObject attacker = attack.getAttacker();

       result.logMsg.append(attacked.getName()).
               append(" uses ").
               append(blocker.getName()).
               append(" to block").
               append(attack.getRawDamage()).toString();

        playBlockSound(blocker, result.blockType);
        floatingText(blocker, result );

        attacked.getGame().getLogManager().log(LogMaster.LOG.GAME_INFO,
                result.logMsg.toString());

        // return hitType;
    }

    protected abstract void floatingText(Blocker blocker, BlockResult result);

    protected abstract void playBlockSound(Blocker blocker, NewRpgEnums.BlockType blockType);

    public BlockResult processAttack(Attack attack, Blocker blocker) {
        this.attack = attack;
        this.result = new BlockResult();
        if (checkBlock(blocker)) {
            NewRpgEnums.BlockType blockType = getBlockType(attack.getHitType());
            if (blockType != null)
            switch (blockType) {
                case deflect:
                    result.accuracyReduction = blocker.blockValue.get();
                    break;
                case block:
                    result.durabilityDamage = blocker.blockValue.get();
                    blocker.durabilityFunction.accept(attack.getDamage());

                    break;
            }

        }
        return result;
    }

    private boolean checkBlock(Blocker blocker) {

        return false;
    }
}
