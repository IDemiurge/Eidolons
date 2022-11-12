package eidolons.game.battlecraft.rules.combat.attack.block;

import eidolons.game.battlecraft.rules.combat.attack.Attack;
import eidolons.system.math.roll.Roll;
import eidolons.system.math.roll.RollMaster;
import main.content.enums.entity.NewRpgEnums;
import main.entity.Ref;

import static main.content.enums.GenericEnums.*;

/*
IDEA: don't rely on concrete item, create an abstraction - so we can use spells that will 'fake' such blocking fx!
 */
public abstract class BlockRule {
    protected BlockResult result;
    protected Attack attack;
    private boolean simulation;

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

    //at the end only!
    public void blockTriggered(Attack attack, BlockResult result, Blocker blocker) {
        playBlockSound(blocker, result.blockType);
        floatingText(blocker, result );
    }

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
        //TODO NF Rules armor rev
        int reduction=0;
        int bonus = blocker.getBlockChance(reduction);
        Roll roll = new Roll(RollType.reflex, DieType.d20, String.valueOf(bonus), "0");
        Ref ref= new Ref();
        boolean result = RollMaster.roll(roll, ref);
        return result;
    }


    protected abstract void floatingText(Blocker blocker, BlockResult result);

    protected abstract void playBlockSound(Blocker blocker, NewRpgEnums.BlockType blockType);
}
