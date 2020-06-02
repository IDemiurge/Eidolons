package eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator;

import eidolons.content.PROPS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.GroupAI;
import eidolons.game.battlecraft.ai.advanced.engagement.EngageEvent;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import eidolons.libgdx.bf.datasource.GraphicData;
import eidolons.system.audio.DC_SoundMaster;
import main.content.DC_TYPE;
import main.content.enums.EncounterEnums;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.sound.SoundMaster;
import main.system.threading.WaitMaster;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Awakener {
    /*
    gargs, knights, maybe some undead from coffins/remains, sleepers (!)
-----------
    script - awaken in block,
    parameters? reinforcements?

    syntax:
    awaken(structID, type,
--------------
    usage:
    set script data on button cell
    use in puzzles
    plain script - on enter (block!)
    +triggers- on hit?

     */
    DC_Game game;
    private Map<BattleFieldObject, BattleFieldObject> map = new HashMap<>();

    public Awakener(DC_Game game) {
        this.game = game;
    }

    public void awaken(Ref ref, Object[] args) {

    }
    public void awaken(LevelStruct struct, EncounterEnums.UNIT_GROUP_TYPE aiType, awaken_type type) {
        Set<BattleFieldObject> objects = findObjects(struct);

        GroupAI group = null;
        for (BattleFieldObject object : objects) {
            if (type.waitTime > 0) {
                WaitMaster.WAIT(type.waitTime);
            }
            if (group == null) {
                group = new GroupAI(awaken(object));
                group.setType(aiType);
            } else
                group.add(awaken(object));

        }
        // new Encounter() adjust level? Or max awakened count
        if (type.shake) {
            // GuiEventManager.trigger(GuiEventType.CAMERA_SHAKE, arg)
        }
        if (type.sound != null) {
            DC_SoundMaster.playStandardSound(type.sound);
        }
        if (type.engage) {
            //how to make it fair? 
            //how to make it effective? insta move forward? 
            game.getDungeonMaster().getExplorationMaster().event(
                    new EngageEvent(EngageEvent.ENGAGE_EVENT.combat_start));
        }
    }

    private Set<BattleFieldObject> findObjects(LevelStruct struct) {
        Set<BattleFieldObject> set = new LinkedHashSet<>();
        for (Object c : struct.getCoordinatesSet()) {
            for (BattleFieldObject object : game.getObjectsOnCoordinateNoOverlaying((Coordinates) c)) {
                if (checkAwaken(object)) {
                    set.add(object);
                }
            }
        }
        return set;
    }

    public Unit awaken(BattleFieldObject guard) {
        animate(guard);

        ObjType type = DataManager.getType(getUnitName(guard), DC_TYPE.UNITS);
        Coordinates c = guard.getCoordinates();
        Unit unit = (Unit) game.createObject(type, c, game.getPlayer(false));
        map.put(unit, guard);
        game.softRemove(guard);
        //retain % of health, flip,
        return unit;
    }

    private String getUnitName(BattleFieldObject guard) {
        String property = guard.getProperty(PROPS.LINKED_UNIT);
        if (!property.isEmpty()) {
            return property;
        }
        switch (guard.getName()) {
        }
        return "Living " + guard.getName();
    }

    private boolean checkAwaken(BattleFieldObject object) {
        return DataManager.isTypeName(getUnitName(object), DC_TYPE.UNITS);
    }


    private void animate(BattleFieldObject guard) {
        GraphicData data = new GraphicData("");
        GuiEventManager.triggerWithParams(GuiEventType.GRID_OBJ_ANIM, guard, data);
    }


    public enum awaken_type {
        animate_stone,
        animate_mech,
        animate_wood,
        undead,
        undead_wraith,

        ;

        public int waitTime;
        SoundMaster.STD_SOUNDS sound;
        boolean engage;
        boolean shake;
        int timelimit; //after X seconds in real/combat, they'll freeze again
    }
}
