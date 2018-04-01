package main.game.logic.dungeon.editor.logic;

import main.entity.obj.Obj;
import main.entity.type.ObjAtCoordinate;
import eidolons.game.battlecraft.ai.UnitAI.AI_BEHAVIOR_MODE;
import main.game.logic.dungeon.editor.LE_AiMaster.AI_GROUP_PARAM;
import main.system.auxiliary.StringMaster;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AiGroupData {
    ObjAtCoordinate leader;
    List<ObjAtCoordinate> members = new ArrayList<>();
    Map<AI_BEHAVIOR_MODE, String> behaviorsData;
    // patrol/wander coordinates, guard/idle prefs,
    Map<AI_GROUP_PARAM, String> paramData;

    public AiGroupData(Obj obj) {
        leader = new ObjAtCoordinate(obj.getType(), obj.getCoordinates());
        getMembers().add(leader);
    }

    @Override
    public String toString() {
        String string = leader.toString() + ";";
        string += StringMaster.constructStringContainer(getMembers());
        // MapMaster.toStringForm(behaviorsData);
        return string;
    }

    public void add(ObjAtCoordinate e) {
        if (getMembers().contains(e)) {
            return;
        }
        getMembers().add(e);
    }

    public void remove(ObjAtCoordinate e) {
        getMembers().remove(e);
    }

    public ObjAtCoordinate getLeader() {
        return leader;
    }

    public void setLeader(ObjAtCoordinate leader) {
        this.leader = leader;
    }

    public List<ObjAtCoordinate> getMembers() {
        return members;
    }

    public void setMembers(List<ObjAtCoordinate> members) {
        this.members = members;
    }

    public Map<AI_BEHAVIOR_MODE, String> getBehaviorsData() {
        if (behaviorsData == null) {
            behaviorsData = new HashMap<>();
        }
        return behaviorsData;
    }

    public void setBehaviorsData(Map<AI_BEHAVIOR_MODE, String> behaviorsData) {
        this.behaviorsData = behaviorsData;
    }

    public Map<AI_GROUP_PARAM, String> getParamData() {
        if (paramData == null) {
            paramData = new HashMap<>();
        }
        return paramData;
    }

    public void setParamData(Map<AI_GROUP_PARAM, String> paramData) {
        this.paramData = paramData;
    }

}
