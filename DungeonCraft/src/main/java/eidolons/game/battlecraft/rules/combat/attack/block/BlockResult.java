package eidolons.game.battlecraft.rules.combat.attack.block;

import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.content.enums.entity.NewRpgEnums;

import java.util.Arrays;
import java.util.stream.Collectors;

public class BlockResult {
    public NewRpgEnums.BlockType blockType;
    public int accuracyReduction;
    public int durabilityDamage;
    // public int durabilitySelfDamage;
    public Effect onBlockEffects;
    public StringBuilder logMsg = new StringBuilder();

    public BlockResult(BlockResult... results) {
        if (results.length > 0) {
            onBlockEffects = new Effects(Arrays.stream(results).map(r -> r.onBlockEffects).collect(Collectors.toList()));
            for (BlockResult result : results) {
                durabilityDamage += result.durabilityDamage;
                accuracyReduction += result.accuracyReduction;
                logMsg.append(result.logMsg.toString()).append("\n");
            }
        }
    }
}
