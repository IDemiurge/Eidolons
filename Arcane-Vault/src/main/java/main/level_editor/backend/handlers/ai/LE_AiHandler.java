package main.level_editor.backend.handlers.ai;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.ai.elements.generic.AiData;
import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorLoader;
import main.content.DC_TYPE;
import main.data.xml.XML_Converter;
import main.game.bf.Coordinates;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.Strings;
import main.system.auxiliary.data.MapBuilder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;

import static main.content.enums.EncounterEnums.UNIT_GROUP_TYPE;

public class LE_AiHandler extends LE_Handler implements IAiHandler {

    Map<Integer, AiData> encounterAiMap = new LinkedHashMap<>();
    Map<BattleFieldObject, AiData> customAiMap = new LinkedHashMap<>();

    Stack<Map<Integer, AiData>> encounterAiStack = new Stack<>();
    Stack<Map<BattleFieldObject, AiData>> customAiStack = new Stack<>();

    public LE_AiHandler(LE_Manager manager) {
        super(manager);
    }


    private AiData getAiForObject(BattleFieldObject obj) {
        if (obj.getOBJ_TYPE_ENUM() == DC_TYPE.ENCOUNTERS) {
            return getAiForEncounter(obj);
        }
        return customAiMap.get(obj);
    }

    public AiData getAiForEncounter(DC_Obj obj) {
        return encounterAiMap.get(obj);
    }


    public void objectAdded(BattleFieldObject obj) {
//    TODO wtf?
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
        Integer id = getIdManager().getId(obj);
        encounterAiMap.put(id, ai);
        updateText(obj);
    }

    private void putCustom(BattleFieldObject obj, AiData ai) {
        customAiStack.push(customAiMap);
        customAiMap = new HashMap<>(customAiMap);
        customAiMap.put(obj, ai);
        updateText(obj);
    }

    public void undone() {
        if (!customAiStack.isEmpty()) {
            customAiMap = customAiStack.pop();
        }
        if (!encounterAiStack.isEmpty()) {
            encounterAiMap = encounterAiStack.pop();
        }
    }

    public void removed(BattleFieldObject obj) {
        removeEncounterAiMap(obj);
        removeCustomAiMap(obj);
    }

    private void removeCustomAiMap(BattleFieldObject obj) {
        if (!customAiMap.containsKey(obj)) {
            return;
        }
        customAiStack.push(customAiMap);
        customAiMap = new HashMap<>(customAiMap);
        if (customAiMap.remove(obj) != null)
            updateText(obj);
    }

    private void updateText(BattleFieldObject obj) {
        String text = "";
        AiData ai = getAiForObject(obj);
        if (ai == null) {
            return;
        }
        Integer id = getIdManager().getId(obj);
        if (id == ai.getLeader()) {
            text += "+++";
        }
        text += ai.getType();
        text += "[" + id + "]";
        GuiEventManager.triggerWithParams(GuiEventType.LE_AI_DATA_UPDATE, obj, text);
    }


    private void removeEncounterAiMap(BattleFieldObject obj) {
        Integer id = getIdManager().getId(obj);
        if (!encounterAiMap.containsKey(id)) {
            return;
        }
        encounterAiStack.push(encounterAiMap);
        encounterAiMap = new HashMap<>(encounterAiMap);
        if (encounterAiMap.remove(id) != null)
            updateText(obj);
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
        if (aiData.getBooleanValue(AiData.AI_VALUE.encounter)) {
            putEncounter(from, aiData);
        } else {
            putCustom(from, aiData);
        }
    }

    @Override
    public void addToGroup() {
        AiData group = getSelectedGroup();
        for (Integer id : getSelectionHandler().getSelection().getIds()) {
            putCustom(getIdManager().getObjectById(id), group);
        }
    }

    private AiData getSelectedGroup() {
        for (Integer id : encounterAiMap.keySet()) {
            if (getSelectionHandler().isSelected(id)) {
                return encounterAiMap.get(encounterAiMap.get(id));
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
//show units?
    }

    @Override
    public void toggleShowInfo() {
        getModel().getDisplayMode().setShowMetaAi(!getModel().getDisplayMode().isShowMetaAi());
    }

    public String getXml(Function<Integer, Boolean> idFilter, Function<Coordinates, Boolean> coordinateFilter) {
        StringBuilder builder = new StringBuilder();
        for (Integer id : encounterAiMap.keySet()) {
            builder.append(id).append("=");
            builder.append(encounterAiMap.get(id).toString()).append(Strings.VERTICAL_BAR);
        }
        String xml = XML_Converter.wrap(FloorLoader.ENCOUNTER_AI_GROUPS, builder.toString());

        builder = new StringBuilder();
        for (AiData data : customAiMap.values()) {
            for (BattleFieldObject object : customAiMap.keySet()) {
                if (customAiMap.get(object) == data) {
                    builder.append(getIdManager().getId(object)).append(";");
                }
            }
            builder.append(":");
            builder.append(data.toString()).append(Strings.VERTICAL_BAR);
        }
        xml += XML_Converter.wrap(FloorLoader.CUSTOM_AI_GROUPS, builder.toString());

        return xml;
    }

    public void initEncounterGroups(String textContent) {
        encounterAiMap =
                new MapBuilder<>(":", Strings.VERTICAL_BAR,
                        s -> NumberUtils.getIntParse(s),
                        s -> new AiData(s))
                        .build(textContent);
    }
}
