package main.client.cc.gui.neo.tooltip;

import main.client.cc.CharacterCreator;
import main.client.cc.logic.spells.LibraryManager;
import main.content.ContentManager;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.VALUE;
import main.content.values.properties.G_PROPS;
import main.elements.conditions.RequirementsManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.attach.DC_FeatObj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.swing.components.panels.page.log.WrappedTextComp;
import main.system.DC_Formulas;
import main.system.graphics.ColorManager;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.text.TextParser;
import main.system.text.TextWrapper;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ToolTipTextComp extends WrappedTextComp {
    private Boolean req;
    private Entity item;
    private Unit hero;

    /*
     * to be used in trees and dc?
     * boost rank? rank info?
     */
    public ToolTipTextComp() {
        super(null, false);
    }

    @Override
    public void paint(Graphics g) {
        int size = 16;
        if (textLines != null) {
            size -= Math.max(0, textLines.size() - 3);
        }
        setFont(g.getFont().deriveFont(new Float(size)));

        super.paint(g);
    }

    @Override
    protected int getDefaultFontSize() {
        return super.getDefaultFontSize() - 2;
    }

    @Override
    protected int paintLine(Graphics g, String str, int x2, int y2, int i) {
        // fontMap.getOrCreate(str);
        if (BooleanMaster.isTrue(req)) {
            g.setColor(ColorManager.CRIMSON);
        } else {
            g.setColor(getColor());
        }
        if (req == null) {
            g.setFont(g.getFont().deriveFont(Font.ITALIC));
        }
        return super.paintLine(g, str, x2, y2, i);
    }

    private String[] getControlTooltipLinesClass() {
        String learn = "Double click to increase rank";
        return new String[]{learn};
    }

    private String[] getControlTooltipLinesSkill() {
        String learn = "Double click to increase rank";
        return new String[]{learn};
    }

    private String[] getIntegrityTooltipLines() {
        return new String[]{};
    }

    private String[] getValueTooltipLines(VALUE V) {
        return new String[]{};
    }

    private String[] getControlTooltipLinesSpell() {
        String learn = "Double click in Library to Learn (add to Spellbook)";
        String verbatim = "Double click in Spellbook to make Verbatim permanently";
        String memorize = "Double right-click in Spellbook to Memorize";
        String dememorize = "Right-click in Memorized List to De-Memorize";
        // TODO runes
        // increase KN MSTR to ...
        return new String[]{learn, verbatim, memorize, dememorize};
    }

    private String[] getSpellLines(Boolean req, Entity item, Unit hero) {
        int sd = item.getIntParam(PARAMS.SPELL_DIFFICULTY);
        int amount = (int) (sd * DC_Formulas.KNOWLEDGE_ANY_SPELL_FACTOR);
        String mastery = ContentManager.findMastery(item.getProperty(G_PROPS.SPELL_GROUP))
                .getName().replace(" Mastery", "");
        // CharacterCreator.getHeroManager().
        if (req) {

        }
        boolean verbatim = false;
        if (LibraryManager.isKnown(hero, item)) {

            String s = "Learn Verbatim";
            String s1 = "Memorize";

        } else {

        }
        String selfLearn = "> Spell Difficulty " + sd + ", Learn with ";
        String selfLearn2 = "total Intelligence and " + mastery + " of " + amount;
        // discounts!?
        // if (LibraryManager.checkStandardSpell(item.getType())) {
        // }

        String autoLearn = "> Known (or Verbatim if Learned) with ";
        String autoLearn2 = amount + " " + mastery + " and Knowledge ";
        String autoLearn3 = "* Or 2x Knowledge and Mastery unlocked";

        String[] strings = new String[]{selfLearn, selfLearn2, autoLearn, autoLearn2, autoLearn3};

        return strings;
    }

    private String[] getSkillLines(Boolean req, ObjType item, Unit hero) {
        return getFeatLines(req, item, hero, true);
    }

    private String[] getFeatLines(Boolean req, ObjType item, Unit hero, boolean skill) {

        List<String> list = new LinkedList<>();
        PROPS p = skill ? PROPS.SKILLS : PROPS.CLASSES;
        if (!hero.checkProperty(p, item.getName())) {
            addReqLines(item, hero, list, RequirementsManager.NORMAL_MODE);
        } else {
            DC_FeatObj feat = hero.getFeat(skill, item);
            int sd = feat.getIntParam(PARAMS.SKILL_DIFFICULTY);

            if (skill) {
                String points = "Skill Difficulty (skill points spent): " + sd;
                list.add(points);
            } else {

            }
            int rank = feat.getIntParam(PARAMS.RANK);
            if (rank > 0) {
                int xp = feat.getIntParam(PARAMS.XP_COST) * feat.getIntParam(PARAMS.RANK_XP_MOD)
                        / 100;
                int skillPoints = sd * feat.getIntParam(PARAMS.RANK_SD_MOD) / 100;
                String rankInfo = "Current rank: " + rank + ", max: "
                        + feat.getIntParam(PARAMS.RANK_MAX);
                String rankBonus = "Rank Bonus: " + rank
                        * feat.getIntParam(PARAMS.RANK_FORMULA_MOD) + "%";
                String skillRanksInfo = "Increase rank for " + xp
                        + (skill ? " Xp and " + skillPoints + " Skill Points" : "");
                if (!skill) {// skills also have those!
                    addReqLines(item, hero, list, RequirementsManager.RANK_MODE);
                }
                if (list.size() <= getMaxListSize()) {
                    list.add(rankInfo);
                }
                if (list.size() <= getMaxListSize()) {
                    list.add(rankBonus);
                }
                if (list.size() <= getMaxListSize()) {
                    list.add(skillRanksInfo);
                }
            }
        }
        return list.toArray(new String[list.size()]);
    }

    private int getMaxListSize() {
        return 7;
    }

    private void addReqLines(ObjType item, Unit hero, List<String> list, int mode) {
        String reqs = hero.getGame().getRequirementsManager().getRequirements(item, mode)
                .getInfoStrings();
        reqs = TextParser.parse(reqs, new Ref(hero), TextParser.VARIABLE_PARSING_CODE);
        List<String> rankReqs = TextWrapper.wrap(reqs, getWrapLength(), getRef());
        list.addAll(rankReqs);
    }

    @Override
    public synchronized List<String> getTextLines() {
        return textLines;
    }

    @Override
    public void refresh() {

        textLines = Arrays.asList(getLines(req, item, hero));
    }

    public String[] getLines(Boolean req, Entity item, Unit hero) {

        switch (CharacterCreator.getHeroPanel().getView()) {
            case CLASSES:
                if (req == null) {
                    return getControlTooltipLinesClass();
                }
                return getFeatLines(req, (ObjType) item, hero, false);
            case LIBRARY:
                if (req == null) {
                    return getControlTooltipLinesSpell();
                }
                return getSpellLines(req, item, hero);
            case SKILLS:

                if (req == null) {
                    return getControlTooltipLinesSkill();
                }
                return getSkillLines(req, (ObjType) item, hero);

        }
        return new String[0];

    }

    public Boolean getReq() {
        return req;
    }

    public void setReq(Boolean req) {
        this.req = req;
    }

    public Entity getItem() {
        return item;
    }

    public void setItem(Entity entity) {
        this.item = entity;
    }

    public Unit getHero() {
        return hero;
    }

    public void setHero(Unit hero) {
        this.hero = hero;
    }

}
