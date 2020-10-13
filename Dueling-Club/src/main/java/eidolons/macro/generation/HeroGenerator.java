package eidolons.macro.generation;

import eidolons.ability.UnitLibrary;
import eidolons.ability.UnitShop;
import eidolons.ability.UnitTrainer;
import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;
import eidolons.macro.entity.town.Tavern;
import eidolons.system.text.NameMaster;
import main.content.enums.entity.HeroEnums;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import java.util.ArrayList;
import java.util.List;

public class HeroGenerator {

    public static Unit generateHero(ObjType type, int xp, Tavern tavern) {
        type = new ObjType(type);
        type.initType();
        // apply random changes to base
        applyBackgroundChanges(type, getMaxApplied(xp, true) + 1);
        Unit hero = new Unit(type, DC_Game.game);
        // set XP/gold percentages and plans!
        UnitTrainer.train(hero); // award classes!
        UnitLibrary.learnSpellsForUnit(hero);
        UnitShop.buyItemsForUnit(hero);

        // TODO next, similar to Unit Training?

        // generate name
        return hero;
    }

    private static void applyBackgroundChanges(ObjType type, int max) {
        List<BACKGROUND_CHANGE_TEMPLATES> applied = new ArrayList<>();
        while (true) {
            int i = RandomWizard.getRandomInt(BACKGROUND_CHANGE_TEMPLATES
             .values().length);
            BACKGROUND_CHANGE_TEMPLATES t = BACKGROUND_CHANGE_TEMPLATES
             .values()[i];
            if (applied.contains(t)) {
                continue;
            }
            applyTemplateChange(type, t);
            applied.add(t);
            if (applied.size() > RandomWizard.getRandomInt(max)) {
                break;
            }
        }
    }

    private static int getMaxApplied(int xp, boolean background) {
        return (int) Math.round((Math.sqrt(xp) / 25));
    }

    public static void alterHero(Unit hero) {
        // TODO sell and re-buy items
        // alter end-point skills
        // alter magic school

        applyPresetChanges(hero, hero.getIntParam(PARAMS.LEVEL));

        List<String> heroNamesInGame = new ArrayList<>();
        Loop.startLoop(heroNamesInGame.size());
        String name = hero.getName();
        while (Loop.loopEnded()) {
            name = NameMaster.generateName(hero);
            if (!heroNamesInGame.contains(name)) {
                break;
            }
        }
        hero.setName(name);
    }

    private static boolean applyTemplateChange(Unit hero,
                                               PRESET_CHANGE_TEMPLATES t) {
        switch (t) {
            case ALTER_ATTRIBUTE:
                // ATTRIBUTE attr = new RandomWizard<ATTRIBUTE>()
                // .getRandomListItem(Arrays.asList(ATTRIBUTE.values()));
                // PARAMETER attribute1 = ContentManager.getBaseAttribute(attr
                // .getParameter());
                // PARAMETER attribute2 = DC_ContentManager
                // .getPairedAttribute(attr);
                // int buffer = hero.getType().getIntParam(attribute1);
                // hero.setParam(attribute1, hero.getType()
                // .getIntParam(attribute2), true);
                // hero.setParam(attribute2, buffer, true);//return true;
                break;
            case DEITY:
                // Deity deity = new
                // RandomWizard<Deity>().getRandomListItem(hero
                // .getDeity().getAllyDeities());
                // hero.setDeity(deity);
                // return true;
            case ITEMS:
                break;
            case PORTRAIT:
                Loop.startLoop(125);
                while (!Loop.loopEnded()) {
                    String newPortrait = new RandomWizard<String>()
                     .getRandomListItem(ImageManager
                      .getPortraitsForBackground(hero
                       .getBackground().toString()));
                    if (ImageManager.isImage(newPortrait)) {
                        if (!ImageManager.getImage(newPortrait).equals(
                         hero.getIcon().getImage())) {
                            if (StringMaster.isFemalePortrait(newPortrait)) {
                                if (hero.getGender() != HeroEnums.GENDER.FEMALE) {
                                    continue;
                                }
                            }
                            hero.setImage(newPortrait);
                            return true;
                        }
                    }
                }
                break;

            default:
                break;

        }
        return false;

    }

    private static void applyPresetChanges(Unit type, int max) {
        List<PRESET_CHANGE_TEMPLATES> applied = new ArrayList<>();
        int n = 0;
        while (true) {
            if (n > RandomWizard.getRandomInt(max)) {
                break;
            }
            int i = RandomWizard
             .getRandomInt(PRESET_CHANGE_TEMPLATES.values().length);
            PRESET_CHANGE_TEMPLATES t = PRESET_CHANGE_TEMPLATES.values()[i];
            if (applied.contains(t)) {
                continue;
            }
            if (applyTemplateChange(type, t)) {
                n++;
            }
            applied.add(t);

        }
    }

    private static void applyTemplateChange(Entity hero,
                                            BACKGROUND_CHANGE_TEMPLATES t) {

        switch (t) {
            case DEITY:
                // getOrCreate ally deities ; preCheck principles
                break;
            default:
                break;

        }

    }

    public enum BACKGROUND_CHANGE_TEMPLATES {
        ALTER_WEAPON_MASTERY,
        ALTER_MISC_MASTERY,
        ALTER_MAGIC_MASTERY,
        ALTER_ATTRIBUTE, // swap two 'similar' attributes
        DEITY,
        PORTRAIT,
        // CLASS_PLAN, ITEM_PLAN,
    }

    public enum PRESET_CHANGE_TEMPLATES {
        ALTER_ATTRIBUTE, DEITY, PORTRAIT, ITEMS, CLASSES, SPELLS, SKILLS

    }
    /*
     * based on templates... changing base
	 * 
	 * Hero Level Up - "recommended" -
	 */

}
