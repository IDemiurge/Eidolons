package eidolons.game.battlecraft.rules.combat.attack.block;

import eidolons.content.consts.VisualEnums;
import eidolons.system.libgdx.GdxStatic;
import main.content.enums.entity.NewRpgEnums;

/**
 *
 */
public class ShieldRule extends BlockRule{
    @Override
    protected void floatingText(Blocker blocker, BlockResult result) {
        GdxStatic.floatingText(VisualEnums.TEXT_CASES.COUNTER_ATTACK,
                "Shield block!", blocker.getOwner());
    }

    @Override
    protected void playBlockSound(Blocker blocker, NewRpgEnums.BlockType blockType) {
        // DC_SoundMaster.playBlockedSound(attacker, attacked, shield, weapon, blockValue, damage);
    }
}
