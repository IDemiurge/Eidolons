package eidolons.game.battlecraft.rules.combat.attack.block;

import eidolons.entity.obj.unit.Unit;
import main.ability.effects.Effect;
import main.content.enums.entity.NewRpgEnums;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Blocker {
    NewRpgEnums.BlockerType type;
    Supplier<Integer> blockChanceBase;
    Supplier<Integer> blockChanceBonus;
    Supplier<Integer> blockDice;
    Supplier<Integer> blockValue;
    Consumer<Integer> durabilityFunction;
    Effect onBlock;
    private String name;
    private Unit owner;

    public Blocker(NewRpgEnums.BlockerType type, Supplier<Integer> blockChanceBase, Supplier<Integer> blockChanceBonus,
                   Supplier<Integer> blockDice, Supplier<Integer> blockValue,
                   Consumer<Integer> durabilityFunction, Effect onBlock, String name, Unit owner) {
        this.type = type;
        this.blockChanceBase = blockChanceBase;
        this.blockChanceBonus = blockChanceBonus;
        this.blockDice = blockDice;
        this.blockValue = blockValue;
        this.durabilityFunction = durabilityFunction;
        this.onBlock = onBlock;
        this.name = name;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public Unit getOwner() {
        return owner;
    }

    public String getDescription(int chanceReduction) {
        int chance = getBlockChance(chanceReduction);
        return name + ": " +
                chance +
                " base chance to block or deflect (" +
                blockValue.get() +
                ")";
    }

    public int getBlockChance(int chanceReduction) {
        return blockChanceBase.get() + blockChanceBonus.get() - chanceReduction;
    }
}
