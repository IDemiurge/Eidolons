package main.level_editor.backend.handlers.ai;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import main.content.DC_TYPE;
import main.data.xml.XML_Converter;
import main.data.xml.XmlNodeMaster;
import main.entity.obj.Obj;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static eidolons.game.module.dungeoncrawl.generator.init.RngMainSpawner.UNIT_GROUP_TYPE;

public class LE_AiHandler extends LE_Handler implements IAiHandler {

    private static final String CUSTOM_AI_GROUPS = "custom";
    private static final String ENCOUNTER_AI_GROUPS = "encounter";
    Map<BattleFieldObject, AiData> encounterAiMap;
    Map<BattleFieldObject, AiData> customAiMap;

    Stack<Map<BattleFieldObject, AiData>> encounterAiStack = new Stack<>();
    Stack<Map<BattleFieldObject, AiData>> customAiStack = new Stack<>();

    public LE_AiHandler(LE_Manager manager) {
        super(manager);
    }

    public void objectAdded(BattleFieldObject obj) {
//    wtf?
//    Integer id = getIdManager().getId(obj);
//        AiData ai = new AiData(id);
//        if (obj.getOBJ_TYPE_ENUM() == DC_TYPE.ENCOUNTERS) {
//            putEncounter(obj, ai);
//        } else {
//            putCustom(obj, ai);
//        }
    }

    private void putEncounter(BattleFieldObject obj, AiData ai) {
        encounterAiStack.push(encounterAiMap);
        encounterAiMap = new HashMap<>(encounterAiMap);
        encounterAiMap.put(obj, ai);
    }

    private void putCustom(BattleFieldObject obj, AiData ai) {
        customAiStack.push(customAiMap);
        customAiMap = new HashMap<>(customAiMap);
        customAiMap.put(obj, ai);
    }

    public void undone() {
        customAiMap = customAiStack.pop();
        encounterAiMap = encounterAiStack.pop();
    }

    public AiData getAiForEncounter(DC_Obj obj) {
        return encounterAiMap.get(obj);
    }

    public String toXml() {
        StringBuilder builder = new StringBuilder();
        for (BattleFieldObject object : encounterAiMap.keySet()) {
            builder.append(getIdManager().getId(object)).append("=");
            builder.append(encounterAiMap.get(object).toString()).append(";");
        }
        String xml = XML_Converter.wrap(ENCOUNTER_AI_GROUPS, builder.toString());

        builder = new StringBuilder();
        for (AiData data : customAiMap.values()) {
            for (BattleFieldObject object : customAiMap.keySet()) {
                if (customAiMap.get(object) == data) {
                    builder.append(getIdManager().getId(object)).append(";");
                }
            }
            builder.append("=");
            builder.append(data.toString()).append(";");
        }
        xml += XML_Converter.wrap(CUSTOM_AI_GROUPS, builder.toString());

        return xml;
    }


    public void initAiData(String nodeContents) {
        String node = XmlNodeMaster.findNodeText(nodeContents, ENCOUNTER_AI_GROUPS);
        for (String sub : ContainerUtils.openContainer(node)) {
            String[] split = sub.split("=");
            Obj obj = getIdManager().getObjectById(
                    NumberUtils.getInteger(split[0]));
            AiData data = new AiData(split[1]);
            encounterAiMap.put((BattleFieldObject) obj, data);
        }

        node = XmlNodeMaster.findNodeText(nodeContents, CUSTOM_AI_GROUPS);
        for (String sub : ContainerUtils.openContainer(node)) {
            String[] split = sub.split("=");
            for (String s : ContainerUtils.openContainer(split[0])) {
                Obj obj = getIdManager().getObjectById(
                        NumberUtils.getInteger(s));
                AiData data = new AiData(split[1]);
                customAiMap.put((BattleFieldObject) obj, data);
            }
        }

    }

    public void removed(BattleFieldObject obj) {
        Integer id = getIdManager().getId(obj);
        encounterAiMap.remove(obj);
        customAiMap.remove(obj);

        //UNDO?!


    }

    @Override
    public void setLeader() {
        BattleFieldObject from = getSelectionHandler().getObject();
        getSelectedGroup().setLeader(getIdManager().getId(from));
    }

    @Override
    public void createGroup() {
        BattleFieldObject from = getSelectionHandler().getObject();
        UNIT_GROUP_TYPE t = getDialogHandler().chooseEnum(
                UNIT_GROUP_TYPE.class);

        AiData aiData = new AiData(from.getOBJ_TYPE_ENUM() == DC_TYPE.ENCOUNTERS,
                t, getIdManager().getId(from));
        aiData.setIds(getSelectionHandler().getSelection().getIds());
    }

    @Override
    public void addToGroup() {
        AiData group = getSelectedGroup();
    }

    private AiData getSelectedGroup() {
        for (BattleFieldObject object : encounterAiMap.keySet()) {
            if (getSelectionHandler().isSelected(object)) {
                return encounterAiMap.get(encounterAiMap.get(object));
            }
        }
        for (BattleFieldObject object : customAiMap.keySet()) {
            if (getSelectionHandler().isSelected(object)) {
                return encounterAiMap.get(encounterAiMap.get(object));
            }
        }
        return null;
    }

    @Override
    public void editGroup() {
        AiData ai = getSelectedGroup();
        getEditHandler().editDataUnit(ai);
    }

    @Override
    public void toggleEncounter() {

    }

    @Override
    public void toggleShowInfo() {

    }

}
