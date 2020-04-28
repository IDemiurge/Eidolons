package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import main.data.tree.LayeredData;
import main.system.auxiliary.StringMaster;
import main.system.data.DataUnit;

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
        return getValueConsts();
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

    public LevelStruct<LevelStruct, LevelStruct> getLevelStruct() {
        if (levelStruct == null) {
            if (structure instanceof LevelStruct) {
                return (LevelStruct<LevelStruct, LevelStruct>) structure;
            }
        }
        return levelStruct;
    }

    public StructureData<T, S> setLevelStruct(LevelStruct levelStruct) {
        this.levelStruct = levelStruct;
        return this;
    }
}
