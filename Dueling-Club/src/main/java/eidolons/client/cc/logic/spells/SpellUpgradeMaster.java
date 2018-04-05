package eidolons.client.cc.logic.spells;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.content.ValuePages;
import eidolons.entity.active.DC_SpellObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.system.DC_Formulas;
import main.content.enums.entity.SpellEnums.SPELL_UPGRADE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.ability.construct.VariableManager;
import main.entity.Entity;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class SpellUpgradeMaster {

	/*
     * CUMULATIVE UPGRADES THEN!!!
	 * 
	 * For HC, I need to display relevant information about the spell, the costs and the like 
	 * Appending to description only the names of upgrades + "see details on upgrade page"
	 * 
	 * so for HC, I will getUPGS() then getType() for info! 
	 */

    private static final boolean TEST_MODE = true;

    public static ImageIcon generateSpellIcon(DC_SpellObj spell) {
        BufferedImage image = ImageManager.getBufferedImage(spell.getImagePath());
        boolean x_y_direction = false;
        boolean top_corner = true;
        int size = GuiManager.getSmallObjSize();
        int n = 0;
        for (SPELL_UPGRADE u : getActiveUpgradesFromSpell(spell)) {
            Image img = u.getGlyphSmallImage();

            int x;
            int y = 0;
            if (!top_corner) {
                x = size - img.getHeight(null);
                y = size - img.getHeight(null);
            }

            boolean left_corner = !x_y_direction;
            if (!left_corner) {
                x = size - img.getHeight(null);
            } else {
                x = 0;
            }
            int i = 2;
            while (n / 2 > i) {
                if (x_y_direction) {
                    if (top_corner) {
                        x += img.getWidth(null) + 2;
                    } else {
                        x -= img.getWidth(null) + 2;
                    }
                } else {
                    if (top_corner) {
                        y += img.getHeight(null) + 2;
                    } else {
                        y -= img.getHeight(null) + 2;
                    }
                }
                i++;
            } // 2 corners, 2 directions

            Point p = new Point(x, y);
            image.getGraphics().drawImage(img, p.x, p.y, null);

            top_corner = !top_corner;
            x_y_direction = !x_y_direction;
            n++;
        }

        // spell.setCustomIcon();
        return new ImageIcon(image);
    }

    public static void isUpgradeUnlocked(SPELL_UPGRADE upgrade) {

    }

    public static boolean isUpgraded(Entity spell) {
        return !getActiveUpgradesFromSpell(spell).isEmpty();
    }

    public static List<SPELL_UPGRADE> getAvailableUpgradesFromSpell(Entity spell) {
        return getUpgradesFromSpell(spell, false);
    }

    public static List<SPELL_UPGRADE> getActiveUpgradesFromSpell(Entity spell) {
        return getUpgradesFromSpell(spell, true);
    }

    public static List<SPELL_UPGRADE> getUpgradesFromSpell(Entity spell, boolean active) {
        List<SPELL_UPGRADE> list = new ArrayList<>();
        for (String s : StringMaster.open(spell
         .getProperty(((active ? PROPS.SPELL_UPGRADES : PROPS.SPELL_UPGRADE_GROUPS))))) {

            SPELL_UPGRADE su = new EnumMaster<SPELL_UPGRADE>().retrieveEnumConst(
             SPELL_UPGRADE.class, s);
            if (su != null) {
                list.add(su);
            }
        }
        return list;
    }

	/*
     * generateUpgradedSpellIcon()
	 * 
	 * isUpgradeUnlocked()
	 * 
	 * upgradeSpell()
	 * 
	 * 
	 */

    public static boolean checkUpgrade(boolean verbatim, Unit hero, Entity spell,
                                       SPELL_UPGRADE ug) {
        if (TEST_MODE) {
            return true;
        }
        if (spell.getIntParam(PARAMS.MAX_SPELL_UPGRADES) < StringMaster.openContainer(
         spell.getProperty(PROPS.SPELL_UPGRADES)).size()) {
            return false;
        }

        if (!hero.checkProperty(PROPS.SPELL_UPGRADE_GROUPS, ug.toString())) {
            return false;
        }
        if (verbatim) {
            return hero.checkParameter(PARAMS.MEMORY_REMAINING, getXpCost(spell, hero, ug));
        }
        int sd = getSdBonus(spell, ug);
        return hero.checkParameter(PARAMS.MEMORY_REMAINING, sd);

    }

    public static int getXpCost(Entity entity, Unit hero, SPELL_UPGRADE ug) {
        // hero.getIntParam(PARAMS.XP_COST_REDUCTION_SPELL_UPGRADES);
        int sd = getSdBonus(entity, ug);
        return sd * 10;
    }

    private static int getSdBonus(Entity entity, SPELL_UPGRADE ug) {
        return entity.getIntParam(PARAMS.SPELL_DIFFICULTY, true) * ug.getSpellDifficultyMod() / 100;
    }

    public static void initSpellUpgrades(Unit hero) {
        List<String> list = StringMaster.openContainer(hero.getProperty(PROPS.SPELL_UPGRADES));
        if (list.isEmpty()) {
            return;
        }
        loop:
        for (DC_SpellObj spell : hero.getSpells()) {
            for (String string : list) {
                if (string.contains(spell.getName() + "(")) {
                    spell.setProperty(PROPS.SPELL_UPGRADES, VariableManager.getVarPart(string)
                     .replace(",", ";"));
                    continue loop;
                }
            }

        }
    }

    public static void removeUpgrades(Unit hero, Entity spell) {
        DC_SpellObj spellObj = hero.getSpell(spell.getName());
        if (spellObj != null) {
            spellObj.removeProperty(PROPS.SPELL_UPGRADES);
            spellObj.getType().removeProperty(PROPS.SPELL_UPGRADES);
        }
    }

    public static boolean toggleUpgrade(Unit hero, Entity spell, SPELL_UPGRADE ug) {
        Boolean first_last = null;
        String upgrades = spell.getProperty(PROPS.SPELL_UPGRADES);
        boolean remove = upgrades.contains(ug.getName());
        if (remove) {
            spell.removeProperty(PROPS.SPELL_UPGRADES, ug.getName());
            if (!spell.checkProperty(PROPS.SPELL_UPGRADES)) {
                first_last = false;
            }
        } else {
            if (!spell.checkProperty(PROPS.SPELL_UPGRADES)) {
                first_last = true;
            }
            spell.addProperty(PROPS.SPELL_UPGRADES, ug.getName());
        }
        if (first_last != null) {
            if (!remove) {
                if (first_last) {
                    spell.appendProperty(G_PROPS.DESCRIPTION, "Upgrades: "
                     + StringMaster.getWellFormattedString(ug.toString()));
                }
            } else if (!first_last) {
                spell.removeFromProperty(G_PROPS.DESCRIPTION, "Upgrades: "
                 + StringMaster.getWellFormattedString(ug.toString()));
            }
        }
        // upgrades = spell.getProperty(PROPS.SPELL_UPGRADES);
        // String spellString = spell.getName()
        // + StringMaster.wrapInParenthesis((upgrades.replace(";", ",")));
        // String value = hero.getProperty(PROPS.SPELL_UPGRADES);
        // if (first_last == null) {
        // String substring = value.substring(value.indexOf(spell.getName()));
        // String prev = substring.substring(0, substring.indexOf(")"));
        // value = value.replace(prev, spellString);
        // hero.setProperty(PROPS.SPELL_UPGRADES, value, true);
        //
        // } else { // add spell string
        // if (remove) {
        // if (first_last)
        // hero.removeProperty(PROPS.SPELL_UPGRADES, spellString, true);
        // } else {
        // hero.addProperty(PROPS.SPELL_UPGRADES, spellString, true);
        // }
        //
        // }
        // value = hero.getProperty(PROPS.SPELL_UPGRADES) + spellString;
        // main.system.auxiliary.LogMaster.log(1,
        // hero.getType().getProperty(PROPS.SPELL_UPGRADES)
        // + " on " + hero.getName());
        return !remove;
    }

    public static boolean applyUpgrades(Entity spell) {
        // TODO Auto-generated method stub
        List<SPELL_UPGRADE> upgradesFromSpell = getActiveUpgradesFromSpell(spell);
        if (upgradesFromSpell.isEmpty()) {
            return false;
        }
        for (SPELL_UPGRADE ug : upgradesFromSpell) {
            applyUpgrade(spell.getGame().isSimulation() ? spell.getType() : spell, ug);
        }
        return true;
    }


    public static void applyUpgrade(Entity type, SPELL_UPGRADE... ug) {
        for (SPELL_UPGRADE sub : ug) {
            applyUpgrade(type, sub);
        }
    }

    public static void applyUpgrade(Entity type, SPELL_UPGRADE ug) {
        if (ug.getAddPropMap() != null)

        {
            for (String s : ug.getAddPropMap().keySet()) {
                type.addProperty(s, ug.getAddPropMap().get(s));
            }
        }
        if (ug.getSetPropMap() != null) {
            for (String s : ug.getSetPropMap().keySet()) {
                type.setProperty(s, ug.getSetPropMap().get(s));
            }
        }
        if (ug.getParamBonusMap() != null) {
            for (String s : ug.getParamBonusMap().keySet()) {
                type.setModifierKey(ug.getName());
                type.modifyParameter(s, ug.getParamBonusMap().get(s));
            }
        }
        if (ug.getParamModMap() != null) {
            for (String s : ug.getParamModMap().keySet()) {
                type.setModifierKey(ug.getName());
                type.modifyParamByPercent(s, ug.getParamModMap().get(s));
            }
        }

        type.setModifierKey(ug.getName());
        type.modifyParamByPercent(PARAMS.SPELL_DIFFICULTY, ug.getSpellDifficultyMod());
        type.setParam(PARAMS.XP_COST, // ++ XP COST MODIFIER PER GROUP
         type.getIntParam(PARAMS.SPELL_DIFFICULTY)
          * DC_Formulas.XP_COST_PER_SPELL_DIFFICULTY);

        for (PARAMETER costParam : ValuePages.COSTS) {
            type.setModifierKey(ug.getName());
            type.modifyParamByPercent(costParam, ug.getCostMod());
        }
        // TODO into penalties, and keep float format!

        // getImgSuffix();
        // switch per spell logic?
        // type.appendProperty(G_PROPS.DESCRIPTION, ug.getDescription());
        type.appendProperty(G_PROPS.DESCRIPTION, " "
         + StringMaster.getWellFormattedString(ug.toString()));
        type.setName(SpellGenerator.generateName(type));

        LogMaster.log(1, type.getModifierMaps() + " ");
    }

}
