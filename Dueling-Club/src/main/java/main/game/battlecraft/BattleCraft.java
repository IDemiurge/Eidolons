package main.game.battlecraft;

import main.content.DC_ValueManager.VALUE_GROUP;
import main.content.PROPS;
import main.content.values.parameters.PARAMETER;
import main.data.ability.construct.VariableManager;
import main.entity.obj.unit.Unit;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.List;

public class BattleCraft {
    /**
     * BattleCraft ReCapture To Fix Spawn wave failed? AI thinking is visible
     * on refresh  preCheck this! Check dungeon-spawning Jewelry selection, armor
     * filtering Allied but not controlled units (mercs, fodder, summons)
     * <portrait>
     * Unknown unit priority DC_INV slot items and refresh Stacking  girth for
     * miss-hits, update trample Upkeep should be displayed Warcries dont work
     * Actions  knockdown, disarm, knockback - ++ AI Update  maneuvers, Add
     * Resurrect, gift Conjurate, Spell Upgrades Weight Rule review/transparency
     * <portrait>
     * A New Artist for real icons? Make a capture Some icons are *missing*!
     * Skills especially Review Alchemy/Enchantments plan
     * <portrait>
     * <portrait>
     * Meta Dev Saving Log into txt would be great! Waiter time limit  at least
     * optional
     */
    public static void chooseMasteryGroups(Unit hero) {
        chooseMastery(hero, true);
        chooseMastery(hero, false);
        chooseMastery(hero, null);
    }

    public static void chooseMastery(Unit hero, Boolean weapon_magic_misc) {
        PROPS prop = PROPS.MASTERY_GROUPS_WEAPONS;
        if (weapon_magic_misc == null) {
            prop = PROPS.MASTERY_GROUPS_MISC;
        } else if (!weapon_magic_misc) {
            prop = PROPS.MASTERY_GROUPS_MAGIC;
        }
        String tip = " had trained with...";
        if (weapon_magic_misc == null) {
            tip = " had learned... ";
        } else if (!weapon_magic_misc) {
            tip = " had studied... ";
        }
        for (String item : StringMaster.open(hero.getProperty(prop))) {
            if (item.contains(StringMaster.OR)) {
                String[] options = item.split(StringMaster.OR);
                int index = DialogMaster.askOptionsAndWait(hero.getName() + tip, false, options);
                item = options[index];
            }
            int amount = StringMaster.getInteger(VariableManager.getVarPart(item));
            PARAMETER[] masteries = VALUE_GROUP.valueOf(
                    StringMaster.toEnumFormat(VariableManager.removeVarPart(item))).getParams();
            List<String> options = new ArrayList<>();
            for (PARAMETER m : masteries) {
                options.add(m.toString().replace(" Mastery", ""));
            }
            // multi-disc groups?
            int index = DialogMaster.askOptionsAndWait(hero.getName() + tip
                    + StringMaster.wrapInParenthesis("" + amount), false, options.toArray());
            if (index == -1) {
                index = RandomWizard.getRandomIntBetween(0, masteries.length - 1);
                DialogMaster.inform("Random choice - " + masteries[index]);
                // bonus 1? 50%!
            }
            hero.modifyParameter(masteries[index], amount, true);

        }

    }

}
