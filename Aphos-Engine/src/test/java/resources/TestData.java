package resources;

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
                    "hp_max=5",
                    "moves_max=1",
                    "defense_action=brace",
            },
            {
                    "name=dampling",
                    "type=Unit",
                    "moves_max=1",
                    "hp_max=11",
                    "defense_action=brace",
            },
            {
                    "name=brace",
                    "type=Action",
                    "ap_cost=1",
                    "exec_data=brace",
            }
    };
    public static final String[] battleData = {
            "battle_data::battle_type=skirmish;base_flame=1;night=false",
            "allies::3=dampling;4=dampling;",
            "enemies::12=dummling"
    };
}
