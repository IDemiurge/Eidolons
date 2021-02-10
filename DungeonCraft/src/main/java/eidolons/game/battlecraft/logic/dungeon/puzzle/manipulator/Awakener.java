package eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator;

import eidolons.content.PROPS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.GroupAI;
import eidolons.game.battlecraft.ai.advanced.engagement.EngageEvent;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.struct.LevelBlock;
import eidolons.game.module.dungeoncrawl.struct.LevelStruct;
import eidolons.content.consts.GraphicData;
import eidolons.system.audio.DC_SoundMaster;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.launch.Flags;
import main.system.sound.AudioEnums;
import main.system.threading.WaitMaster;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static main.content.enums.EncounterEnums.UNIT_GROUP_TYPE;

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
    private final Map<BattleFieldObject, BattleFieldObject> map = new HashMap<>();

    public Awakener(DC_Game game) {
        this.game = game;
    }

    public void awaken(Ref ref, Object[] args) {
        LevelStruct struct;
        if (args[0] instanceof LevelBlock) {
            struct = (LevelStruct) args[0];
        } else
            struct = game.getDungeonMaster().getStructMaster().findBlockByName(args[0].toString());

        UNIT_GROUP_TYPE ai;
        if (args[1] instanceof UNIT_GROUP_TYPE) {
            ai = (UNIT_GROUP_TYPE) args[1];
        } else
            ai = new EnumMaster<UNIT_GROUP_TYPE>().retrieveEnumConst(UNIT_GROUP_TYPE.class, (args[1].toString()));

        awaken_type type;
        if (args[1] instanceof awaken_type) {
            type = (awaken_type) args[2];
        } else
            type = new EnumMaster<awaken_type>().retrieveEnumConst(awaken_type.class, (args[1].toString()));

        awaken(struct, ai, type);
    }

    public void awaken(LevelStruct struct, UNIT_GROUP_TYPE aiType, awaken_type type) {
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
        if (Flags.TESTER_VERSION || type.engage) {
            //how to make it fair? 
            //how to make it effective? insta move forward? 
            game.getDungeonMaster().getExplorationMaster().event(
                    new EngageEvent(EngageEvent.ENGAGE_EVENT.combat_start, group.getMembers().size()));
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
        guard.kill();
        // game.remove(guard);
        // game.softRemove(guard);
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
        AudioEnums.STD_SOUNDS sound;
        boolean engage;
        boolean shake;
        int timelimit; //after X seconds in real/combat, they'll freeze again
    }
}
