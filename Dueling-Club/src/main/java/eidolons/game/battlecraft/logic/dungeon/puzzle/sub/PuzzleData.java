package eidolons.game.battlecraft.logic.dungeon.puzzle.sub;

import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure.EDIT_VALUE_TYPE;
import main.system.auxiliary.EnumMaster;
import main.system.data.DataUnit;

import java.util.regex.Pattern;

public class PuzzleData extends DataUnit<PuzzleData.PUZZLE_VALUE> {

    private boolean alt;

    public PuzzleEnums.PUZZLE_ACTION_BASE getCounterActionBase() {
        PuzzleEnums.PUZZLE_ACTION_BASE base = new EnumMaster<PuzzleEnums.PUZZLE_ACTION_BASE>().
                retrieveEnumConst(PuzzleEnums.PUZZLE_ACTION_BASE.class, getValue(PUZZLE_VALUE.COUNTER_TYPE));
        if (base == null) {
            base = PuzzleEnums.PUZZLE_ACTION_BASE.FACING;
        }
        return base;
    }

    @Override
    public Class<? extends PUZZLE_VALUE> getEnumClazz() {
        return PUZZLE_VALUE. class;
    }

    public PuzzleData() {
    }

    @Override
    protected String getSeparator() {
        if (alt)
            return Pattern.quote("|");
        return ",";
    }

    @Override
    protected String getPairSeparator() {
        if (alt)
            return Pattern.quote("::");
        return ">>";
    }
    public PuzzleData(String text) {
        super(text);
    }

    public enum PUZZLE_VALUE implements LevelStructure.EditableValue {
        TYPE(EDIT_VALUE_TYPE.enum_const){
            @Override
            public Object getArg() {
                return PuzzleEnums.puzzle_type.class;
            }
        },
        DIFFICULTY_COEF,

        WIDTH(EDIT_VALUE_TYPE.number),
        HEIGHT(EDIT_VALUE_TYPE.number),

        ENTRANCE(EDIT_VALUE_TYPE.coordinates){
            @Override
            public Object getArg() {
                return true; //offset
            }
        },
        EXIT(EDIT_VALUE_TYPE.coordinates){
            @Override
            public Object getArg() {
                return true; //offset
            }
        },

        PUNISHMENT,
        RESOLUTION,

        TIP(EDIT_VALUE_TYPE.enum_const){
            @Override
            public Object getArg() {
                return eidolons.game.netherflame.main.event.TIP.class;
            }
        },
        TIP_FAIL(EDIT_VALUE_TYPE.enum_const){
            @Override
            public Object getArg() {
                return eidolons.game.netherflame.main.event.TIP.class;
            }
        },
        ARG,

        COUNTER_TYPE, COUNTERS_MAX,
        AMBIENCE,
        SOULFORCE_REWARD,
        ;


        private final EDIT_VALUE_TYPE type;

        PUZZLE_VALUE(EDIT_VALUE_TYPE type) {
            this.type = type;
        }

        PUZZLE_VALUE() {
            this(EDIT_VALUE_TYPE.text);
        }

        @Override
        public EDIT_VALUE_TYPE getEditValueType() {
            return type;
        }
    }
}
