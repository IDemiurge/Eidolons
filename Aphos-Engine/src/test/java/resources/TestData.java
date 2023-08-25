package resources;

import content.LinkedStringMap;
import elements.exec.Executable;
import elements.exec.effect.framework.EffectTemplate;
import elements.exec.preset.ExecPresets;
import elements.exec.targeting.TargetingTemplates;

import java.util.Map;

import static elements.exec.effect.framework.EffectTemplate.ATTACK;
import static elements.exec.effect.framework.EffectTemplate.MODIFY;
import static elements.exec.preset.ExecPresetConstructor.construct;
import static elements.exec.preset.ExecPresets.initPreset;
import static elements.exec.targeting.TargetingTemplates.ConditionTemplate.UNTIL_ATTACK_OR_FALL;
import static elements.exec.targeting.TargetingTemplates.TargetingTemplate.CLOSE_QUARTERS;
import static elements.exec.targeting.TargetingTemplates.TargetingTemplate.SELF;

/**
 * Created by Alexander on 8/23/2023
 */
public class TestData {

    public static final String unit_name_ally = "dampling";
    public static final String unit_name_enemy = "dummling";
    public static final String action_defense = "brace";

    private static final String EXEC_DATA_BRACE = "";
    //easier to serialize here? but to test construction... up to some point, we can well do with just in-code objs!
    public static final String[][] entityData = {
            {
                    "name=dummling",
                    "type=Unit",
                    "hp_max=15",
                    "armor_max=5",
                    "dr=0",
                    "Melee_block=1",
                    "moves_max=1",
                    "defense_action=brace",
                    "standard_attack=sword swing",
                    "attack_base=3",
                    "defense_base=3",
            },
            {
                    "name=dampling",
                    "type=Unit",
                    "moves_max=1",
                    "dr=0",
                    "hp_max=11",
                    "standard_attack=sword swing",
                    "defense_action=brace",
                    "attack_base=3",
                    "defense_base=3",
            },
            {
                    "name=brace",
                    "type=Action",
                    "ap_cost=1",
                    "exec_data=brace",
            },
            {
                    "name=sword swing",
                    "type=Action",
                    "ap_cost=1",
                    "value=1__2__3",
                    "exec_data=sword swing",
                    "die=10",
            }
    };
    public static final String[] battleData = {
            "battle_data::battle_type=skirmish;base_flame=1;night=false",
            "allies::3=dampling;4=dampling;",
            "enemies::12=dummling"
    };

    public static void initAllPresets() {
        //it seems that with this syntax, we can easily create EXEC's from plain strings
        //but what will happen with nested stuff?
        initPreset("Brace", SELF, null, MODIFY, UNTIL_ATTACK_OR_FALL, true, null, "defense_min;2", null,  "");
        //restore hp per formation member - could be a special dynamic counter!
        //already not enough for "Restores Armor or if it is full, grants +3 DEF" ...

        initPreset("Sword Swing", CLOSE_QUARTERS, null, ATTACK, null, false, null, "Strike", null,  "");
        // initPreset("Quick Shot", RANGED, null, ATTACK, SELF_CHECK, false, null,  "values;2_3_4", null,"loaded_weapon;true");
        //
        // //PASSIVE
        // initPreset("Instant Fire", SELF, null, MODIFY, SELF_CHECK, true, null,  "initiative;x2", null,"loaded_weapon;true");


    }
}
