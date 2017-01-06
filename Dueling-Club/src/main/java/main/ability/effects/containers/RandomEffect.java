package main.ability.effects.containers;

import main.ability.Ability;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.ability.effects.oneshot.MicroEffect;
import main.data.ability.AE_ConstrArgs;
import main.data.ability.OmittedConstructor;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;

import java.util.LinkedList;
import java.util.List;

public class RandomEffect extends MicroEffect {

    private Ability[] abilities;
    private List<String> abilityNames;
    private LinkedList<Ability> abilityList;
    private Effects effects;

    @OmittedConstructor
    public RandomEffect(Ability... abilities) {

    }

    @OmittedConstructor
    public RandomEffect(Effect... effects) {

    }

    @OmittedConstructor
    public RandomEffect(String... abilityNames) {
        this.abilityNames = new ListMaster<String>().getList(abilityNames);
    }

    public RandomEffect(Effects effects) {
        this.effects = effects;
    }

    @AE_ConstrArgs(argNames = {"abilityNames, separated by ' OR '"})
    public RandomEffect(String abilityNames) { // TODO support ^VAR;... by
        // excluding ";"'s from split!
        this(StringMaster.split(abilityNames, StringMaster.OR));
    }

    @OmittedConstructor
    public RandomEffect(List<String> abilNames) {
        this.abilityNames = abilNames;
    }

    @Override
    public boolean applyThis() {
        if (effects != null) {
            return (effects.getEffects().get(RandomWizard
                    .getRandomListIndex(effects.getEffects()))).apply(ref);
        }
        if (abilityList != null) {
            return new AbilityEffect(abilityList.get(RandomWizard
                    .getRandomListIndex(abilityList))).apply(ref);
        }
        String abilName = abilityNames.get(RandomWizard
                .getRandomListIndex(abilityNames));
        return new AbilityEffect(abilName).apply(ref);

        // if (abilityList == null) {
        // abilityList = new LinkedList<>(Arrays.asList(abilities));
        // if (abilities == null) {
        // if (abilityNames == null)
        // return false;
        // for (String name : abilityNames) {
        // AbilityType type = (AbilityType) DataManager.getType(name);
        // if (type == null)
        // type = VariableManager.getVarType(name);
        // Ability a = new AbilityObj(type, ref, ref.getPlayer(), game)
        // .getAbilities();
        // abilityList.add(a);
        // }
        // } else
        // for (Ability a : abilities)
        // abilityList.add(a);
        // }
        // // TODO targeting???
        // Ability ability = abilityList.getOrCreate(RandomWizard
        // .getRandomListIndex(abilityList));
        // return ability.activate(ref);
    }

}
