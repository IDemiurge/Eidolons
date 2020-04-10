package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import main.data.tree.LayeredData;
import main.system.auxiliary.StringMaster;
import main.system.data.DataUnit;

import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class StructureData<T extends Enum<T>, S extends LayeredData> extends DataUnit<T> {
    protected S structure;
    protected LevelStruct levelStruct;

    public StructureData(S structure) {
        this.structure = structure;
        init();

    }

    protected abstract void init();

    public abstract Class<? extends T> getEnumClazz();

    @Override
    public String[] getRelevantValues() {
        return Arrays.stream(getEnumClazz().getEnumConstants()).map(constant -> constant.toString()).
                collect(Collectors.toList()).toArray(new String[0]);
    }

    public S getStructure() {
        return structure;
    }

    public void apply() {
    }

    @Override
    protected String getSeparator() {
        return super.getSeparator();
    }

    @Override
    protected String getPairSeparator() {
        return StringMaster.ALT_PAIR_SEPARATOR;
    }

    public LevelStruct<LevelStruct> getLevelStruct() {
        return levelStruct;
    }

    public StructureData<T, S> setLevelStruct(LevelStruct levelStruct) {
        this.levelStruct = levelStruct;
        return this;
    }
}
