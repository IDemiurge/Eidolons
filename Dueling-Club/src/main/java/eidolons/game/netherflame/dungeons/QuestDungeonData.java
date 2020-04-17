package eidolons.game.netherflame.dungeons;

import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import main.system.data.DataUnit;

import static eidolons.game.netherflame.dungeons.QD_Enums.*;

public class QuestDungeonData extends DataUnit<QuestDungeonData.QuestDungeonValue> {

    // could it be that in real campaign we will use the same pipeline -
    // gen some CD data and viola - a Quest Dungeon?

    public QD_QUEST getMainQuest() {
        return getEnum(getValue(QuestDungeonValue.main_quest), QD_QUEST.class);
    }

    public QD_QUEST getSubQuest() {
        return getEnum(getValue(QuestDungeonValue.sub_quest), QD_QUEST.class);
    }

    public QD_LENGTH getLength() {
        return getEnum(getValue(QuestDungeonValue.length), QD_LENGTH.class);
    }

    public QD_LOCATION getLocation() {
        return getEnum(getValue(QuestDungeonValue.location), QD_LOCATION.class);
    }

    public QD_DIFFICULTY getDifficulty() {
        return getEnum(getValue(QuestDungeonValue.difficulty), QD_DIFFICULTY.class);
    }

    public enum QuestDungeonValue implements LevelStructure.EditableValue {
        main_quest,
        sub_quest,
        location,
        length,
        difficulty,
        modes,
        boss,
        puzzles,
        eidolon_lord,
        events,
        ;

        private LevelStructure.EDIT_VALUE_TYPE type;
        private Object arg;

        QuestDungeonValue() {
        }

        QuestDungeonValue(LevelStructure.EDIT_VALUE_TYPE type) {
            this.type = type;
        }

        @Override
        public Object getArg() {
            return arg;
        }

        @Override
        public LevelStructure.EDIT_VALUE_TYPE getEditValueType() {
            return type;
        }
    }
}
