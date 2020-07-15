package eidolons.game.battlecraft.logic.meta.universal;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.Simulation;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.herocreator.CharacterCreator;
import eidolons.game.module.herocreator.logic.HeroCreator;
import eidolons.game.module.herocreator.logic.HeroLevelManager;
import eidolons.game.module.herocreator.logic.party.Party;
import eidolons.swing.generic.services.dialog.DialogMaster;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.data.MetaManager;
import main.content.DC_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Reader;
import main.data.xml.XML_Writer;
import main.entity.type.ObjType;
import main.system.auxiliary.Loop;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.LogMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 5/14/2017.
 */
public class PartyHelper {
    private static final String DEFAULT_TYPE_NAME = "Inglorious Bastards";
    private static final String PARTY_FOLDER = "/parties";
    private static final String XML_ROOT = "Party";
    private static final String PARTY_NAME_TIP = "This fellowship was called...";
    private static final int MAX_PARTY_MEMBERS_DEFAULT = 4;
    static Party party;
    private static Party enemyParty;
    private static final List<Party> parties = new ArrayList<>();

    private static File getPartyFile(String typeName) {
        return FileManager.getFile(getPartyFolderPath() + "/" + typeName + ".xml");
    }

    public static void addMember(Unit hero) {
        getParty().addMember(hero);
        if (hero.getGame().isSimulation()) {
            CharacterCreator.partyMemberAdded(hero);
        }
        if (hero.getIntParam(PARAMS.LEVEL) < getParty().getIntParam(PARAMS.LEVEL)) {
            // DC_SoundMaster.playStandardSound(STD_SOUNDS.LEVEL_UP); why not have
            // some fun with this loop...
            Loop.startLoop(10);
            while (!Loop.loopEnded()
             && hero.getIntParam(PARAMS.LEVEL) < getParty().getIntParam(PARAMS.LEVEL)) {
                if (hero.getGame().isSimulation()) {
                    DC_SoundMaster.playStandardSound(STD_SOUNDS.LEVEL_UP);
                }
                HeroLevelManager.levelUp(hero);
            }
        }
    }

    public static Party newParty(Unit hero) {
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

        Party party = createParty(hero);
        // party.getLeader().getProperty(G_PROPS.NAME) +
        // StringMaster.PARTY_SUFFIX;
        party.setProperty(G_PROPS.NAME, newName, true);

        // if (CharacterCreator.isArcadeMode()) {
        // savePartyAs(true);
        // } Now after 1st battle!

        setParty(party);
        return party;
    }

    public static Party createParty(Unit hero) {
        return createParty(getType(), hero);
    }

    public static Party createParty(ObjType type, Unit hero) {
        party = newParty(type);
        party.setLeader(hero);
        party.addMember(hero);
        hero.getGame().getState().addObject(party);
        party.setImage(hero.getImagePath());
        hero.getGame().initType(party.getType());
        party.toBase();
        party.setProperty(G_PROPS.GROUP, Strings.CUSTOM, true);
        return party;
    }

    public static void saveParty(Party party) {
        saveParty(party, false);
    }

    public static void saveParty(Party party, boolean newType) {

        if (party.getName().equals(DEFAULT_TYPE_NAME)) {
            party.setProperty(G_PROPS.NAME, party.getLeader().getProperty(G_PROPS.NAME)
             + Strings.PARTY_SUFFIX, true);
        }
        String xml = XML_Writer.openXML(XML_ROOT);
        xml += XML_Writer.getTypeXML(party.getType(), new StringBuilder(XML_Writer.STR_CAPACITY));
        String names = "";
        StringBuilder xmlBuilder = new StringBuilder(xml);
        for (Unit hero : party.getMembers()) {
            // durability persistence?

            ObjType type = hero.getType();

            eidolons.macro.global.persist.Saver.prepareType(type);

            xmlBuilder.append(XML_Writer.getTypeXML(type, new StringBuilder(XML_Writer.STR_CAPACITY)));
            names += hero.getName() + Strings.CONTAINER_SEPARATOR;
        }
        xml = xmlBuilder.toString();

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
            if (!newName.contains(Strings.ARCADE)) {
                newName = newName + " " + Strings.ARCADE;
            }
        }

        if (!auto) {
            if (DataManager.isTypeName(newName) && !party.getName().equals(newName)) {
                if (!newName.contains(Strings.ARCADE)) {
                    newName = newName + " " + Strings.ARCADE;
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
            party.setProperty(G_PROPS.GROUP, Strings.ARCADE, true);
            getParty().setName(newName);
            saveParty();
        } else {
            Party exportedParty = newParty(newType);
            exportedParty.setProperty(G_PROPS.GROUP, Strings.PRESET, true);
            exportedParty.setName(newName);
            saveParty(exportedParty, true);
        }

    }

    private static Party newParty(ObjType newType) {
        Party party = new Party(newType);
        DC_Game.game.getState().addObject(party);
        return party;
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

    private static String getFileName(Party party) {
        return party.getName() + ".xml";
    }

    private static String getPartyFolderPath() {
        return PathFinder.getTYPES_PATH() + PARTY_FOLDER;
    }

    private static List<Unit> loadParty(String typeName, DC_Game game, boolean readTypes) {
        // invoke before obj init, to getOrCreate full obj string
        if (readTypes) {
            File file = getPartyFile(typeName);
            String xml = FileManager.readFile(file);
            if (xml.contains(XML_Converter.openXmlFormatted(typeName))) {
                String partyTypeData = NumberUtils.getXmlNode(xml, typeName);
                XML_Reader.createCustomTypeList(partyTypeData, DC_TYPE.PARTY, game, true);
            }
            XML_Reader.readCustomTypeFile(file, DC_TYPE.CHARS, game);
        }
        ObjType partyType = DataManager.getType(typeName, DC_TYPE.PARTY);
        setParty(newParty(partyType));
        party.toBase();
        return party.getMembers();

    }

    private static void saveParty() {
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

    public static Party getParty() {
        return party;
    }

    public static void setParty(Party party) {
        PartyHelper.party = party;

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
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            getParty().getGame().setSimulation(false);
        }
    }

    public static Party initParty(String last) {
        ObjType type = DataManager.getType(last, DC_TYPE.PARTY);

        return newParty(type);
    }

    public static void writeLatestPartyType() {
//        MetaManager.setProperty(PROPS.LAST_ARCADE, getParty().getName());
        MetaManager.saveMetaData();
    }


    private static boolean checkPartySize(Party party2) {
        return party2.getMembers().size() <= getMaxPartyMembers(party2);
    }

    private static int getMaxPartyMembers(Party party) {
        Integer max = party.getIntParam(PARAMS.MAX_HEROES);
        if (max == 0) {
            return MAX_PARTY_MEMBERS_DEFAULT;
        }
        return max;
    }


    public static List<Party> getParties() {
        return parties;
    }




    public static ObjType getType() {
        ObjType type = (new ObjType(DEFAULT_TYPE_NAME));
        type.setOBJ_TYPE_ENUM(DC_TYPE.PARTY);
        type.setGame(DC_Game.game);
        return type;
    }
}
