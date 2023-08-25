package tests.action;

import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.MapMaster;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import tests.basic.BattleInitTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexander on 8/24/2023
 */
public class TriggerTest extends BattleInitTest {

    @Override
    public void test() {
        super.test();

        //regen? some passive that works reliably
        //what level of rarity warrants Trigger over code-rule?
        //gain rage when being Hit
        //Living Armor - remove broken armor or restore to full armor
        //
        //modify 100% syntax
    }


        @Test
    public void yamlTest(){
        FileInputStream input = null;
        try {
            input = new FileInputStream(
                    "C:\\code\\eidolons\\resources\\common\\img\\abil.yml");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
            String s = FileManager.readFile(new File("C:\\code\\eidolons\\resources\\common\\img\\abil.yml"));
        s.trim();
            Yaml yaml = new Yaml();
        // yaml.addImplicitResolver();
        // yaml.addTypeDescription();
        // Parse the YAML file into a List of maps
            Object abilityList = yaml.load(input);
abilityList.toString();
        // Process each ability
        // for (Map<String, Object> abilityMap : abilityList) {
        //     System.out.println("Ability Name: " + MapMaster.getNetStringForMap(abilityMap));
        //     System.out.println();
        // }
    }
}
