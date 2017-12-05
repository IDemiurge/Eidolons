package main.client.cc.logic;

import main.client.cc.gui.misc.HeroItemChooser;
import main.content.CONTENT_CONSTS.RANK;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.entity.HeroEnums.PRINCIPLES;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battle.universal.DC_Player;
import main.game.core.Eidolons;
import main.game.core.game.DC_Game;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import java.awt.*;
import java.util.List;
import java.util.Stack;

public class HeroCreator {

    public static final String BASE_HERO = "Base Hero Type";
    public static final int NEW_HERO_LEVELS = 3;
    private Stack<HeroItemChooser> stack;
    private ObjType heroType;
    private DC_Game game;
    private ObjType ROOT_TYPE;

    public HeroCreator(DC_Game game) {
        this.setGame(game);
        try {
            ROOT_TYPE = DataManager.getType(BASE_HERO, DC_TYPE.CHARS);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            ROOT_TYPE = DataManager.getTypes().get(0);
        }
    }

    public static Unit initHero(String typeName) {

        ObjType type = new ObjType(DataManager.getType(typeName, DC_TYPE.CHARS));
        Eidolons.getGame().initType(type);

        return createHeroObj(type);
    }

    public static Unit createHeroObj(ObjType type) {
        Unit hero = new Unit(type, 0, 0, getDefaultPlayer(),  Eidolons.getGame(),
                new Ref( Eidolons.getGame()));
        newId(type);
         Eidolons.getGame().getState().addObject(hero);
//        hero.toBase();
//        hero.afterEffects();
        return hero;
    }

    private static DC_Player getDefaultPlayer() {
        if (DC_Player.NEUTRAL==null )
            DC_Player.NEUTRAL = new DC_Player("Simulation", Color.red, true);
        return DC_Player.NEUTRAL ;
    }

    private static void newId(ObjType type) {
        int id = type.getTypeId();
        for (ObjType t : DataManager.getTypes(DC_TYPE.CHARS)) {
            if (id <= t.getTypeId()) {
                id = t.getTypeId() + 1;
            }
        }
        type.setProperty(G_PROPS.ID, id + "");

    }

    public static Entity getObjForType(Unit hero, ObjType type) {
        switch ((DC_TYPE) type.getOBJ_TYPE_ENUM()) {
            case SPELLS:
                return hero.getSpell(type.getName());
            case ACTIONS:
                return hero.getAction(type.getName());
            case WEAPONS:
            case ARMOR:
            case ITEMS:
                return hero.getItem(type.getName());
            case CLASSES:
            case SKILLS:
                return hero.getFeat(type);
        }
        return null;
    }

    public Unit newHero() {
        return newHero(false);
    }

    public Unit newHero(boolean levelUp) {
        return createHeroObj(new ObjType(ROOT_TYPE));
    }

    public ObjType getHeroType() {
        heroType = chooseBaseType();
        if (heroType == null) {
            return null;
        }
        heroType = new ObjType(heroType);
        game.initType(heroType);
        if (doChoiceSequence()) {
            if (doPointDistribution()) {
                if (doXpDistribution()) {
                    return heroType;
                }
            }
        }
        // return back?
        return heroType;
    }

    private boolean doXpDistribution() {
        // TODO skills -> items -> spells
        return true;
    }

    private boolean doPointDistribution() {
        // TODO attrs -> masteries
        return true;
    }

    public ObjType chooseBaseType() {
        List<String> listData = DataManager.getHeroList(RANK.NEW_HERO.toString());
        ListChooser lc = new ListChooser(listData, false, DC_TYPE.CHARS);
        lc.setColumns(3);
        String name = lc.getString();
        return DataManager.getType(name, DC_TYPE.CHARS);
    }

    private boolean doChoiceSequence() {
        if (!doChoice(DC_TYPE.DEITIES, G_PROPS.DEITY)) {
            return false;
        }

        // if stranger?
        if (!doChoice(PRINCIPLES.class, G_PROPS.PRINCIPLES)) {
            return false;
        }

        return true;

    }

    private boolean doChoice(DC_TYPE TYPE, PROPERTY prop) {
        List<String> options = DataManager.getTypeNames(TYPE);
        return doChoice(options, prop, false, TYPE);

    }

    private boolean doChoice(Class<? extends Enum<?>> CLASS, PROPERTY prop) {
        List<String> options = EnumMaster.getEnumConstantNames(CLASS);
        return doChoice(options, prop, true, null);

    }

    private boolean doChoice(List<String> options, PROPERTY prop, boolean ENUM, OBJ_TYPE TYPE) {
        if (!StringMaster.isEmpty(heroType.getProperty(prop))) {
            return true;
        }

        HeroItemChooser chooser = new HeroItemChooser(heroType, options, prop, ENUM, TYPE);
        // stack.push(chooser); // to be able to return back
        String result = chooser.getString();
        if (result == null) {
            return false;
        }

        // modify hero
        return true;
    }

    public DC_Game getGame() {
        return game;
    }

    public void setGame(DC_Game game) {
        this.game = game;
    }

    public enum HERO_CHOICES {
        RACE, BACKGROUND, DEITY, PRINCIPLES,

    }
}
