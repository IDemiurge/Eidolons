package tests.basic;

import campaign.run.battle.BattleBuilder;
import combat.Battle;
import combat.init.BattleSetup;
import framework.AphosTest;
import framework.data.DataManager;

import static combat.sub.BattleManager.combat;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 8/22/2023
 */
public class BattleInitTest extends AphosTest  {

    private String name1= "dampling";
    private String name2= "dummling";

    static String[][] entityData = {
            {
                    "name=dummling",
                    "type=Unit",
                    "hp=5"
            },

            {
                    "name=dampling",
                    "type=Unit",
                    "hp=11"
            }
    };
    String[] battleData = {
            "battle_data::battle_type=skirmish;base_flame=1;night=false",
            "allies::3=dampling;4=dampling;",
            "enemies::12=dummling"
    };
    @Override
    public void test() {
        // new MockRun();
        DataManager.init(entityData);
        BattleSetup setup = new BattleBuilder().build(battleData);
        Battle battle = new Battle(setup);
        // battle.init();
        battle.start();

        assertTrue(combat().getData().getUnits().size()== 3);
        assertTrue(combat().getUnitById(0).getName().equals(name1));
        assertTrue(combat().getUnitById(1).getName().equals(name1));
        assertTrue(combat().getUnitById(2).getName().equals(name2));
        assertTrue(combat().getBattleState().getRound() == 1);
    }
}
