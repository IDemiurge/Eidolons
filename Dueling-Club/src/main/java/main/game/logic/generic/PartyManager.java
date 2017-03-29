package main.game.logic.generic;

import main.ability.InventoryTransactionManager;
import main.client.cc.CharacterCreator;
import main.client.cc.logic.HeroCreator;
import main.client.cc.logic.HeroLevelManager;
import main.client.cc.logic.party.PartyObj;
import main.client.dc.MetaManager;
import main.client.dc.Simulation;
import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Reader;
import main.data.xml.XML_Writer;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.PrinciplesCondition;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.core.game.DC_Game;
import main.game.core.game.DC_Game.GAME_MODES;
import main.game.logic.arena.Wave;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.LogMaster;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class PartyManager {

    private static final String DEFAULT_TYPE_NAME = "Inglorious Bastards";
    private static final String PARTY_FOLDER = "\\parties";
    private static final String XML_ROOT = "Party";
    private static final String PARTY_NAME_TIP = "This fellowship was called...";
    private static final int MAX_PARTY_MEMBERS_DEFAULT = 4;
    private static PartyObj party;
    private static PartyObj enemyParty;
    private static List<PartyObj> parties = new LinkedList<>();

    private static File getPartyFile(String typeName) {
        return FileManager.getFile(getPartyFolderPath() + "\\" + typeName + ".xml");
    }

    public static void addMember(Unit hero) {
        getParty().addMember(hero);
        if (hero.getGame().isSimulation()) {
            CharacterCreator.partyMemberAdded(hero);
        }
        if (hero.getIntParam(PARAMS.LEVEL) < getParty().getIntParam(PARAMS.LEVEL)) {
            // SoundMaster.playStandardSound(STD_SOUNDS.LEVEL_UP); why not have
            // some fun with this loop...
            Loop.startLoop(10);
            while (!Loop.loopEnded()
                    && hero.getIntParam(PARAMS.LEVEL) < getParty().getIntParam(PARAMS.LEVEL)) {
                if (hero.getGame().isSimulation()) {
                    SoundMaster.playStandardSound(STD_SOUNDS.LEVEL_UP);
                }
                HeroLevelManager.levelUp(hero);
            }
        }
    }

    public static PartyObj newParty(Unit hero) {
        String newName = "";

        if (CharacterCreator.isArcadeMode()) {
            while (true) {
                if ((newName) == null) {
                    return null;
                }
                if (checkPartyName(newName)) {
                    newName = StringMaster.getPossessive(hero.getName()) + " Party";
                    break;
                }
                newName = DialogMaster.inputText(PARTY_NAME_TIP, DEFAULT_TYPE_NAME);
            }
        }

        PartyObj party = createParty(hero);
        // party.getLeader().getProperty(G_PROPS.NAME) +
        // StringMaster.PARTY_SUFFIX;
        party.setProperty(G_PROPS.NAME, newName, true);

        // if (CharacterCreator.isArcadeMode()) {
        // savePartyAs(true);
        // } Now after 1st battle!

        setParty(party);
        return party;
    }

    public static PartyObj createParty(Unit hero) {
        return createParty(getType(), hero);
    }

    public static PartyObj createParty(ObjType type, Unit hero) {
        party = newParty(type);
        party.setLeader(hero);
        party.addMember(hero);
        hero.getGame().getState().addObject(party);
        party.setImage(hero.getImagePath());
        hero.getGame().initType(party.getType());
        party.toBase();
        party.setProperty(G_PROPS.GROUP, StringMaster.CUSTOM, true);
        return party;
    }

    public static void saveParty(PartyObj party) {
        saveParty(party, false);
    }

    private static void prepareType(ObjType type) {
        for (PROPERTY prop : InventoryTransactionManager.INV_PROPS) {
            String propValue = type.getProperty(prop);
            List<String> items = StringMaster.openContainer(propValue);
            for (String item : items) {
                if (StringMaster.isInteger(item)) {
                    try {
                        propValue = StringMaster.replaceFirst(propValue, item, type.getGame()
                                .getObjectById(StringMaster.getInteger(item)).getType().getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            type.setProperty(prop, propValue);
        }

    }

    public static void saveParty(PartyObj party, boolean newType) {

        if (party.getName().equals(DEFAULT_TYPE_NAME)) {
            party.setProperty(G_PROPS.NAME, party.getLeader().getProperty(G_PROPS.NAME)
                    + StringMaster.PARTY_SUFFIX, true);
        }
        String xml = XML_Writer.openXML(XML_ROOT);
        xml += XML_Writer.getTypeXML(party.getType(), new StringBuilder(XML_Writer.STR_CAPACITY));
        String names = "";
        for (Unit hero : party.getMembers()) {
            // durability persistence?

            ObjType type = hero.getType();

            prepareType(type);

            xml += XML_Writer.getTypeXML(type, new StringBuilder(XML_Writer.STR_CAPACITY));
            names += hero.getName() + StringMaster.CONTAINER_SEPARATOR;
        }

        xml += XML_Writer.closeXML(XML_ROOT);
        party.setProperty(PROPS.MEMBERS, names, true);

        ObjType type = party.getType(); // What is this for?
        if (newType) {
            type = new ObjType(type, true);
            Simulation.getGame().initType(type);
        }
        DataManager.addType(type.getName(), DC_TYPE.PARTY, type);

        XML_Writer.write(xml, getPartyFolderPath(), getFileName(party));
        try {
            XML_Writer.writeXML_ForType(type, DC_TYPE.PARTY);
        } catch (Exception e) {
            LogMaster.log(1, " failed to save party type " + party);
        }
    }

    public static void initArcade() {
        if (!party.isArcade()) {
            savePartyAs(true, true);
        }

    }

    public static void savePartyAs(boolean arcade) {
        savePartyAs(arcade, false);
    }

    /**
     * @param arcade true for New Branch in arcade mode, false for Export Party
     *               from anywhere
     */
    public static void savePartyAs(boolean arcade, boolean auto) {

        String newName = party.getName();
        if (!arcade) {
            newName = DialogMaster.inputText(PARTY_NAME_TIP, party.getName());
        }
        if (newName == null) {
            return;
        }

        if (auto) {
            if (!newName.contains(StringMaster.ARCADE)) {
                newName = newName + " " + StringMaster.ARCADE;
            }
        }

        if (!auto) {
            if (DataManager.isTypeName(newName) && !party.getName().equals(newName)) {
                if (!newName.contains(StringMaster.ARCADE)) {
                    newName = newName + " " + StringMaster.ARCADE;
                }
                Loop.startLoop(97);
                int i = 2;
                while (!Loop.loopEnded()) {
                    if (DataManager.isTypeName(newName + "-" + i)) {
                        continue;
                    }
                    newName = newName + "-" + i;
                    break;
                }
                if (!checkPartyName(newName)) {
                    DialogMaster.error("Invalid name!");
                    return;
                }
            }
        }

        saveParty(); // save old party
        ObjType newType = new ObjType(party.getType());
        Simulation.getGame().initType(newType);

        if (arcade) {
            party.setType(newType);
            party.setProperty(G_PROPS.GROUP, StringMaster.ARCADE, true);
            getParty().setName(newName);
            saveParty();
        } else {
            PartyObj exportedParty = newParty(newType);
            exportedParty.setProperty(G_PROPS.GROUP, StringMaster.PRESET, true);
            exportedParty.setName(newName);
            saveParty(exportedParty, true);
        }

    }

    private static PartyObj newParty(ObjType newType) {
        PartyObj partyObj = new PartyObj(newType);
        DC_Game.game.getState().addObject(partyObj);
        if (DC_Game.game.getGameMode() == GAME_MODES.ARENA_ARCADE) {
            DC_Game.game.getArenaArcadeMaster().init(partyObj);
        }
        return partyObj;
    }

    private static boolean checkPartyName(String newName) {
        if (party != null) {
            if (newName.equals(party.getName())) {
                return false;
            }
        }
        if (newName == null) {
            return false;
        }

        if (DataManager.isTypeName(newName)) {
            DialogMaster.error("Name already exists!");
            return false;
        }
        if (!DataManager.checkTypeName(newName)) {
            DialogMaster.error("Invalid name!");
            return false;
        }
        return true;
    }

    private static String getFileName(PartyObj party) {
        return party.getName() + ".xml";
    }

    private static String getPartyFolderPath() {
        return PathFinder.getTYPES_PATH() + PARTY_FOLDER;
    }

    public static List<Unit> loadParty(String typeName) {
        return loadParty(typeName, Simulation.getGame());
    }

    public static List<Unit> loadParty(String typeName, DC_Game game) {
        // invoke before obj init, to getOrCreate full obj string
        File file = getPartyFile(typeName);
        String xml = FileManager.readFile(file);
        if (xml.contains(XML_Converter.openXmlFormatted(typeName))) {
            String partyTypeData = StringMaster.getXmlNode(xml, typeName);
            xml = xml.replace(partyTypeData, "");
            XML_Reader.createCustomTypeList(partyTypeData, DC_TYPE.PARTY, game, true);
        }
        XML_Reader.readCustomTypeFile(file, DC_TYPE.CHARS, game);
        ObjType partyType = DataManager.getType(typeName, DC_TYPE.PARTY);
        setParty(newParty(partyType));
        party.toBase();
        return party.getMembers();

    }

    public static void saveParty() {
        if (getParty() != null) {
            saveParty(getParty());
        }
    }

    public static void remove(Unit hero) {
        if (hero == getParty().getLeader()) {
            return;
        }
        getParty().removeMember(hero);
        CharacterCreator.partyMemberRemoved(hero);
    }

    public static PartyObj getParty() {
        return party;
    }

    public static void setParty(PartyObj party) {
        PartyManager.party = party;

        DC_Game.game.getState().addObject(party);

        if (party != null) {
            if (CharacterCreator.isArcadeMode()) {
                writeLatestPartyType();
            }
        }
    }

    public static void addMember(String heroName) {
        addMember(HeroCreator.initHero(heroName));
    }

    // only from DC
    public static void levelUp() {
        getParty().getGame().setSimulation(true);
        getParty().modifyParameter(PARAMS.LEVEL, 1, true);
        try {
            for (Unit hero : getParty().getMembers()) {
                HeroLevelManager.levelUp(hero, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getParty().getGame().setSimulation(false);
        }
    }

    public static PartyObj initParty(String last) {
        ObjType type = DataManager.getType(last, DC_TYPE.PARTY);

        return newParty(type);
    }

    public static void writeLatestPartyType() {
        MetaManager.setProperty(PROPS.LAST_ARCADE, getParty().getName());
        MetaManager.saveMetaData();
    }

    public static String readLastPartyType() {
        return MetaManager.getProperty(PROPS.LAST_ARCADE);
    }

    public static boolean checkPartySize() {

        return checkPartySize(party);
    }

    private static boolean checkPartySize(PartyObj party2) {
        return party2.getMembers().size() <= getMaxPartyMembers(party2);
    }

    private static int getMaxPartyMembers(PartyObj party) {
        Integer max = party.getIntParam(PARAMS.MAX_HEROES);
        if (max == 0) {
            return MAX_PARTY_MEMBERS_DEFAULT;
        }
        return max;
    }

    public static Conditions getPrincipleConditions(PartyObj party) {
        Conditions principlesConditions = new Conditions();
        for (Unit m : party.getMembers()) {
            String principles = m.getProperty(G_PROPS.PRINCIPLES);
            Condition principlesCondition = new PrinciplesCondition(principles, "{MATCH_"
                    + G_PROPS.PRINCIPLES + "}", true);
            principlesConditions.add(principlesCondition);

        }
        return principlesConditions;
    }

    public static List<PartyObj> getParties() {
        return parties;
    }

    public static void addCreepParty(Wave wave) {
        DC_Game game = wave.getGame();
        Unit leader = wave.getLeader();
        String name = wave.getName();
        List<Unit> units = wave.getUnits();
        ObjType newType = new ObjType(game);
        wave.setParty(addCreepParty(leader, name, units, newType));

    }

    public static PartyObj addCreepParty(Unit leader, String name, List<Unit> units,
                                         ObjType newType) {
        newType.setName(name);
        PartyObj p = createParty(newType, leader);
        for (Unit unit : units) {
            p.addMember(unit);
        }
        parties.add(p);
        return p;
    }

    public static boolean checkMergeParty(Wave wave) {
        // TODO Auto-generated method stub
        return false;
    }

    public static ObjType getType() {
        // if (type == null) {
        ObjType type = (new ObjType(DEFAULT_TYPE_NAME));
        type.setOBJ_TYPE_ENUM(DC_TYPE.PARTY);
        // DataManager.getType(DEFAULT_TYPE_NAME, OBJ_TYPES.PARTY);
        type.setGame(DC_Game.game);
        // }
        return type;
    }

}
