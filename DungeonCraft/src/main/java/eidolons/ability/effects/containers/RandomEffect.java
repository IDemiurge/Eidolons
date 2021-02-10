package eidolons.ability.effects.containers;

import main.ability.Ability;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.ability.effects.MicroEffect;
import main.data.ability.AE_ConstrArgs;
import main.data.ability.OmittedConstructor;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.Strings;
import main.system.auxiliary.data.ListMaster;

import java.util.ArrayList;
import java.util.List;

public class RandomEffect extends MicroEffect {

    private Ability[] abilities;
    private List<String> abilityNames;
    private ArrayList<Ability> abilityList;
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
        this(ContainerUtils.split(abilityNames, Strings.OR));
    }

    @OmittedConstructor
    public RandomEffect(List<String> abilNames) {
        this.abilityNames = abilNames;
    }

    @Override
    public boolean applyThis() {
        if (effects != null) {
            return (effects.getEffects().get(RandomWizard
             .getRandomIndex(effects.getEffects()))).apply(ref);
        }
        if (abilityList != null) {
            return new AbilityEffect(abilityList.get(RandomWizard
             .getRandomIndex(abilityList))).apply(ref);
        }
        String abilName = abilityNames.get(RandomWizard
         .getRandomIndex(abilityNames));
        return new AbilityEffect(abilName).apply(ref);

        // if (abilityList == null) {
        // abilityList = new ArrayList<>(Arrays.asList(abilities));
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
        // .getRandomIndex(abilityList));
        // return ability.activate(ref);
    }

}
