package gdx.general.anims;

import logic.content.AUnitEnums;
import logic.core.Aphos;
import logic.entity.Entity;
import logic.functions.combat.CombatLogic;
import main.content.enums.entity.ActionEnums;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.FileManager;

public class SpriteFinder {
    //TODO
    public static String getSpritePath(ActionAnims.DUMMY_ANIM_TYPE anim, Entity entity, CombatLogic.ATK_OUTCOME outcome, Object... args) {
        switch (anim) {
            case hit -> {
//                return "sprite\\hit\\blood\\shower.txt";
                String base = "sprite/hit/";
                Object type = null;
                if (outcome == CombatLogic.ATK_OUTCOME.Ineffective) {
                    type = "metal";
                } else {
                    type = entity.getValueMap().get(AUnitEnums.BODY);
                    if (type == null)
                        type = "blood";
                }
                base += type + "/";
                AUnitEnums.Body body = new EnumMaster<AUnitEnums.Body>().retrieveEnumConst(AUnitEnums.Body.class, type.toString());
                switch (body) {
                    case bone -> {
                        return base + "bone.txt";
                    }
                    case dust, stone -> {
                        return base + "shower.txt";
                    }
                    case blood, metal -> {
                        return RandomWizard.getRandomFrom(
                                base + "smash 3 3.txt",
                                base + "shower.txt",
                                base + "squirt.txt");
                    }
                }
            }
            case atk -> {
                Object weapon = Aphos.hero.getValue("Weapon");
                String attack = getAtkName(args[0], weapon);
                String base = "sprite/atk/"+weapon+"/"+attack+"/"+attack;
//                base += "blade\\scimitar\\slash.txt";
//                return  "sprite\\atk\\blade\\scimitar\\slash\\slash.txt";
                return base+".txt";
            }
            case death -> {
                return  "sprite\\death\\soul 5 6.png";
                // some SPELL FX?
            }
        }
        return null;
    }

    private static String getAtkName(Object arg, Object weapon) {
        if (arg instanceof CombatLogic.ATK_TYPE) {
            switch (((CombatLogic.ATK_TYPE) arg)) {
                case Standard -> {
                    switch (weapon.toString()){
                        case "Sword":
                            return "Thrust";
                        case "Scimitar":
                            return "Swing";
                    }
                }
                case Quick -> {
                    switch (weapon.toString()){
                        case "Sword":
                            return "Swing";
                        case "Scimitar":
                            return "Slash";
                    }
                }
                case Power -> {
                    switch (weapon.toString()){
                        case "Sword":
                            return "Swing";
                        case "Scimitar":
                            return "Swing";
                    }
                }
            }
        }
        return "Swing";
    }
}
