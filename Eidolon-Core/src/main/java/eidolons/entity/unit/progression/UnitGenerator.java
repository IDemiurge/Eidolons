package eidolons.entity.unit.progression;

import eidolons.content.ATTRIBUTE;
import eidolons.content.PARAMS;
import eidolons.content.values.DC_ValueManager;
import main.content.enums.entity.SkillEnums;
import main.content.enums.entity.UnitEnums;
import main.system.yaml.YamlEntity;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Created by Alexander on 2/2/2022
 */
public class UnitGenerator extends AbstractConstruct {

    public static void main(String[] s) throws FileNotFoundException {
        Representer representer = new Representer();
        representer.addClassTag(YamlEntity.class, new Tag("!yah"));
        // Yaml yaml = new Yaml(new Constructor(UnitClass.class), new Representer(), new DumperOptions(),
        //         new LoaderOptions(){
        //             @Override
        //             public boolean isEnumCaseSensitive() {
        //                 return false;
        //             }
        //         }, new Resolver());
        Yaml yaml = new Yaml(new LoaderOptions(){
            @Override
            public boolean isEnumCaseSensitive() {
                return false;
            }
        });
        Object load = yaml.loadAs(new FileInputStream("C:\\code\\Eidolons\\" +
                        "DungeonCraft\\src\\main\\resources\\data\\unitclass.yaml")
   , UnitClass.class    );
// yaml.represent(load);
        load.getClass();
    }

    @Override
    public Object construct(Node node) {
        Class<?> type = node.getType();
        return null;
    }
    public static class UnitClass{
        //prioritized?
        String name;
        String group;
        String description;
        Map<SkillEnums.MSTR, Integer> mstrMap;
        Map<ATTRIBUTE, Integer> attrMap;
        SkillEnums.MSTR[] optional;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Map<SkillEnums.MSTR, Integer> getMstrMap() {
            return mstrMap;
        }

        public void setMstrMap(Map<SkillEnums.MSTR, Integer> mstrMap) {
            this.mstrMap = mstrMap;
        }

        public Map<ATTRIBUTE, Integer> getAttrMap() {
            return attrMap;
        }

        public void setAttrMap(Map<ATTRIBUTE, Integer> attrMap) {
            this.attrMap = attrMap;
        }

        public SkillEnums.MSTR[] getOptional() {
            return optional;
        }

        public void setOptional(SkillEnums.MSTR[] optional) {
            this.optional = optional;
        }
    }
    public enum UnitClassEnum {
        // Skirmisher("Striker", DC_ValueManager.VALUE_GROUP.WEAPON.getParams(), PARAMS.MOBILITY_MASTERY, )
/*
⛧
[Mobility, Athletics, Dual Wielding] {Weapon}
🕀 Champion
[Mobility, Athletics, Two-Handed]{Weapon}
✙ Tank ✙
Guardian
[Armorer, Defense, Athletics]{Shield/Discipline}
Vindicator
[Armorer, Defense, Athletics]{Weapon}
✙ Grappler ✙
Brawler
[Unarmed, Mobility, Athletics]{Defense/Armorer}
Enforcer
[Armorer, Mobility, Athletics]{Defense/Discipline}
✙ Archer ✙
Gunner
[Marksmanship, Athletics, Armorer]{Weapon}
Scout
[Marksmanship, Mobility, Mindfulness]{Defense/Discipline}
✙ Trickster ✙
Duelist
[Mobility, Defense, Dual Wielding]{Weapon}
Attrs: <STR-1, VIT-1, AGI-4, DEX-3, WIL-1>
Saboteur
[Mobility, Stealth, Items]{Weapon}
Attrs: <STR-1, VIT-1, AGI-4, DEX-3, WIL-1>
✙ Ambusher ✙
Slasher
[Dual Wielding, Mobility, Athletics]{Weapon}
Attrs: <STR-1, VIT-1, AGI-4, DEX-3, WIL-1>
Slayer
[Two-Handed, Mobility, Athletics]{Weapon}
Attrs: <STR-2, VIT-1, AGI-4, DEX-2, WIL-1>
✙ Support ✙
Scholar [Celest/Life/Conjuration/Elemental]
[Spellcraft, Wizardry, Mindfulness]2x{Celest/Life/Conjuration/Elemental}
Acolyte [Holy/Life/Necromancy/Demon]
[Spellcraft, Wizardry, Mindfulness]2x{Holy/Life/Necromancy/Demon/
✙ Destroyer ✙
Destroyer [Redem/Destr/Fire/Celestial/Shadow]
[Spellcraft, Discipline, Wizardry]2x{Redem/Destr/Fire/Celestial/Shadow}
Theurge [Afflict/Destr/Sorcery/Air/Trans]
[Wizardry, Spellcraft, Discipline]2x{Afflict/Destr/Sorcery/Air/Trans
✙ Suppressor ✙
Psionicist [Psy/Witch/Blood/Warp/Ench]
[Spellcraft, Wizardry, Discipline]2x{Psy/Witch/Blood/Warp/Ench}
Warlock [Shadow/Witch/Death/Warp]
[Spellcraft, Wizardry, Athletics]2x{Shadow/Witch/Death/Warp}
 */
    }
}
