package eidolons.game.module.herocreator.logic;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.mission.universal.PlayerManager;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.macro.AdventureInitializer;
import eidolons.macro.global.persist.Loader;
import eidolons.system.test.TestMasterContent;
import main.content.CONTENT_CONSTS.RANK;
import main.content.DC_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.swing.generic.components.editors.lists.ListChooser;

import java.util.List;

public class HeroCreator {

    public static final String BASE_HERO = "Base Hero Type";
    public static final int NEW_HERO_LEVELS = 3;
    public static ObjType ROOT_TYPE;
    static HeroCreator instance;

    public static HeroCreator getInstance() {
        if (instance == null) {
            instance = new HeroCreator();
        }
        return instance;
    }

    private HeroCreator() {
        ROOT_TYPE = DataManager.getType(BASE_HERO, DC_TYPE.CHARS);
    }

    public static Unit initHero(String typeName) {
        if (AdventureInitializer.isLoad()) {
            return Loader.getLoadedHero(typeName);
        }
        ObjType type = new ObjType(DataManager.getType(typeName, DC_TYPE.CHARS));
        Eidolons.getGame().initType(type);
        try {
            TestMasterContent.addTestItems(type, false);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return createHeroObj(type);
    }

    public static Unit createHeroObj(ObjType type) {
        Unit hero = new Unit(type, 0, 0, PlayerManager.getDefaultPlayer(), Eidolons.getGame(),
                new Ref(Eidolons.getGame()));
        newId(type);
        Eidolons.getGame().getState().addObject(hero);
        return hero;
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

    public Unit newHero() {
        return newHero(false);
    }

    public Unit newHero(boolean levelUp) {
        return createHeroObj(new ObjType(ROOT_TYPE));
    }


    public ObjType chooseBaseType() {
        List<String> listData = DataManager.getHeroList(RANK.NEW_HERO.toString());
        ListChooser lc = new ListChooser(listData, false, DC_TYPE.CHARS);
        lc.setColumns(3);
        String name = lc.getString();
        return DataManager.getType(name, DC_TYPE.CHARS);
    }


    public DC_Game getGame() {
        return DC_Game.game;
    }

}
