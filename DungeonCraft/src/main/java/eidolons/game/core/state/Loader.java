package eidolons.game.core.state;

import eidolons.entity.DC_IdManager;
import eidolons.entity.active.UnitAction;
import eidolons.entity.active.Spell;
import eidolons.entity.item.ArmorItem;
import eidolons.entity.item.trinket.JewelryItem;
import eidolons.entity.item.QuickItem;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.obj.GridCell;
import eidolons.entity.obj.Structure;
import eidolons.entity.unit.attach.buff.DC_BuffObj;
import eidolons.entity.unit.attach.DC_PassiveObj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonData;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonData.DUNGEON_VALUE;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.GameFactory;
import eidolons.game.core.game.GameFactory.GAME_SUBCLASS;
import main.ability.effects.Effect;
import main.ability.effects.continuous.ContinuousEffect;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.values.parameters.G_PARAMS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.data.ability.construct.ConstructionManager;
import main.data.xml.XML_Converter;
import main.data.xml.XmlNodeMaster;
import main.elements.triggers.Trigger;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.entity.type.TypeBuilder;
import main.game.bf.Coordinates;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.data.FileManager;
import main.system.data.DataUnitFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 8/1/2017.
 * <p>
 * how to make sure all objects' state is correct?
 * some fields are mutable...
 * buffs, skills, spells, ...
 * reset() should fix them of course
 * <p>
 * what about REFS?!
 */
public class Loader {

    private static String pendingLoadPath;

    static DC_Game getGame() {
        return DC_Game.game;
    }

    public static DC_Game loadNewGame(String savePath) {
        String data = FileManager.readFile(savePath);
        DC_Game game = GameFactory.createGame(GAME_SUBCLASS.TEST);
        Node dungeonNode = XmlNodeMaster.findNode(data, Saver.DUNGEON_NODE);
        initDungeon(dungeonNode);
        game.init();
//        initFlags(); dungeon is not set!
        game.battleInit();
        loadGame(data);
        game.start(true);
        return game;
    }

    public static void loadGame(String saveData) {
//reinit the game - clean etc
        // master info
        // modes and switches?

        List<Node> triggerNodes = XmlNodeMaster.getNodeList(
         XmlNodeMaster.findNode(saveData, Saver.TRIGGERS_NODE));
        initTriggers(triggerNodes);

        List<Node> effectNodes = XmlNodeMaster.getNodeList(
         XmlNodeMaster.findNode(saveData, Saver.EFECTS_NODE));
        initEffects(effectNodes);

        List<String> objectNodes = XmlNodeMaster.getNodeList(
         XmlNodeMaster.findNode(saveData, Saver.OBJ_NODE)).stream().map
         (XML_Converter::getStringFromXML).collect(Collectors.toList());
        List<Obj> objects = createObjects(objectNodes);
        initializeObjects(objects);
    }

    private static void initDungeon(Node dungeonNode) {
        String xml = XML_Converter.toString(dungeonNode);
        String name = XmlNodeMaster.findNode(
         xml, "Name")
         .getTextContent();
        String path = XmlNodeMaster.findNode(
         xml, "LevelFilePath")
         .getTextContent();
        DataUnitFactory factory = new DataUnitFactory(DungeonData.FORMAT);
        factory.setValueNames(DUNGEON_VALUE.TYPE_NAME, DUNGEON_VALUE.PATH);
        factory.setValues(name, path);
        DungeonData data = new DungeonData(factory.constructDataString());
        getGame().getDataKeeper().setDungeonData(data);
//        ObjType type = DataManager.getType(XML_Converter.findNode(
//        xml, "Name")
//         .getTextContent(), DC_TYPE.DUNGEONS);
//        Map<PROPERTY, String> props = getPropsFromNode(xml);
//        Map<PARAMETER, String> params = getParamsFromNode(xml);
//        getGame().getDungeonMaster().s
    }
    //TODO AFTER INIT funcs

    private static void initEffects(List<Node> effectNodes) {
        for (Node sub : effectNodes) {
            ContinuousEffect e = new ContinuousEffect((Effect)
             ConstructionManager.construct(sub));
            getGame().getState().addEffect(e);
        }
    }

    private static void initTriggers(List<Node> triggerNodes) {
        for (Node sub : triggerNodes) {
            Trigger t = (Trigger) ConstructionManager.construct(sub);
            getGame().getState().addTrigger(t);
        }
    }

    private static void initializeObjects(List<Obj> objects) {
        DC_Game game = DC_Game.game;
        DC_GameState state = game.getState();
        for (Obj obj : objects) {
            state.addObject(obj);
        }
    }

    private static List<Obj> createObjects(List<String> objectNodes) {
        DC_Game game = DC_Game.game;
        List<Obj> objects = new ArrayList<>();
        //TODO ID ORDER MUST BE PRESERVED! put in parameter?
        for (String typesNode : objectNodes) {
            Document node = XML_Converter.getDoc(typesNode);
            DC_TYPE TYPE = DC_TYPE.getType(node.getNodeName());
            for (Node subNode : XmlNodeMaster.getNodeList(node)) {
                String sub = XML_Converter.getStringFromXML(subNode);
                game.setIdManager(new DC_IdManager(game));
                Map<PROPERTY, String> props = getPropsFromNode(sub);
                Map<PARAMETER, String> params = getParamsFromNode(sub);
                ObjType type = DataManager.getType(subNode.getNodeName(), TYPE);
                //preset ID?! init containers by id... including buffs; but first create them
                Ref ref = new Ref(game);
                Node refNode = XmlNodeMaster.findNode(sub, Saver.OBJ_NODE);
                if (refNode != null)
                    for (String substring : ContainerUtils.open(
                     refNode.getTextContent())) {
                        ref.setValue(KEYS.valueOf(
                         substring.split("=")[0].toUpperCase()),
                         substring.split("=")[1]);
                    }
                String ownerName = null;
                DC_Player owner = game.getMissionMaster().getPlayerManager().
                 getPlayer(ownerName); //property?
                if (owner == null) {
                    owner = DC_Player.NEUTRAL;
                }
                Coordinates c = Coordinates.get(params.get(G_PARAMS.POS_X) + "-" + params.get(G_PARAMS.POS_Y));
                Obj object = createObj(type, c.x, c.y, owner, game, ref);

                object.getPropMap().putAll(props);
                object.getParamMap().putAll(params);
                object.setId(NumberUtils.getIntParse(props.get(G_PROPS.ID)));
                objects.add(object);
                init(object);

            }
        }
        return objects;
    }

    private static void init(Obj object) {
        if (object instanceof DC_BuffObj) {
            ((DC_BuffObj) object).setDuration(object.getIntParam(G_PARAMS.C_DURATION));
//retain conditions?
        }
    }

    private static Obj createObj(ObjType type, int x, int y, DC_Player owner, DC_Game game, Ref ref) {
        DC_TYPE TYPE = (DC_TYPE) type.getOBJ_TYPE_ENUM();
        switch (TYPE) {
//            case ABILS: ?
//                return new AbilityObj();
            case BF_OBJ:
                return new Structure(type, x, y, owner, game, ref);
            case UNITS:
            case CHARS:
                return new Unit(type, x, y, owner, game, ref);

            case BUFFS:
                return new DC_BuffObj(type, owner, game, ref);
            case ACTIONS:
                return new UnitAction(type, owner, game, ref);
            case SPELLS:
                return new Spell(type, owner, game, ref);
            case SKILLS:
            case CLASSES:
                return new DC_PassiveObj(type, owner, game, ref);
            case WEAPONS:
                return new WeaponItem(type, owner, game, ref);
            case ARMOR:
                return new ArmorItem(type, owner, game, ref);
            case ITEMS:
                return new QuickItem(type, owner, game, ref);
            case JEWELRY:
                return new JewelryItem(type, owner, game, ref);

            case TERRAIN:
                return new GridCell(type, x, y, game, ref
                 , game.getDungeon());

        }
        return null;
    }

    private static Map<PARAMETER, String> getParamsFromNode(String sub) {
        Node node = XmlNodeMaster.findNode(sub, TypeBuilder.PROPS_NODE);
        Map<PARAMETER, String> map = new XLinkedMap<>();
        XmlNodeMaster.getNodeList(node).forEach(subNode -> {
            PARAMETER parameter = ContentValsManager.getPARAM(subNode.getNodeName());
            String value = subNode.getTextContent();
            map.put(parameter, value);
        });
        return map;
    }

    private static Map<PROPERTY, String> getPropsFromNode(String sub) {
        Node node = XmlNodeMaster.findNode(sub, TypeBuilder.PROPS_NODE);
        Map<PROPERTY, String> map = new XLinkedMap<>();
        XmlNodeMaster.getNodeList(node).forEach(subNode -> {
            PROPERTY prop = ContentValsManager.getPROP(subNode.getNodeName());
            String value = subNode.getTextContent();
            map.put(prop, value);
        });
        return map;
    }

    private static ObjType getTypeFromNode(String sub) {
        return null;

    }

    public static String getPendingLoadPath() {
        return pendingLoadPath;
    }

    public static void setPendingLoadPath(String pendingLoadPath) {
        Loader.pendingLoadPath = pendingLoadPath;
    }

    public static DC_Game loadPendingSave() {
        String path = pendingLoadPath;
        pendingLoadPath = null;
        return loadNewGame(path);
    }
}
