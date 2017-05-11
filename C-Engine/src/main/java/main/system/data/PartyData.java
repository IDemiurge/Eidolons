package main.system.data;

import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.data.PartyData.PARTY_VALUES;

import java.util.Map;

//work with obj initializer
public class PartyData extends DataUnit<PARTY_VALUES> {

    private ObjType heroObjType;

    private Map<Coordinates, ObjType> objMap;

    public PartyData(String partydata) {
        this(partydata, false);
    }

    public PartyData() {
    }

    public PartyData(String partydata, boolean b) {
        super(partydata);
        this.setObjMap(buildObjCoordinateMapFromString(getValue(PARTY_VALUES.UNITS)));
        setValue(PARTY_VALUES.UNITS, createObjCoordinateString(getObjMap(), b));

        this.setObjMap(buildObjCoordinateMapFromString(getValue(PARTY_VALUES.UNITS)));

        initHeroType();
    }

    private void initHeroType() {
        setHeroObjType(DataManager
                .getType(getValue(PARTY_VALUES.HERO_TYPE), DC_TYPE.CHARS));

    }

    public ObjType getHeroObjType() {
        if (heroObjType == null) {
            initHeroType();
        }
        return heroObjType;
    }

    public void setHeroObjType(ObjType heroObjType) {
        this.heroObjType = heroObjType;
    }

    public String getObjData() {
        return getValue(PARTY_VALUES.UNITS);
    }

    public void setObjData(String objData) {
        setValue(PARTY_VALUES.UNITS, objData);

    }

    public Map<Coordinates, ObjType> getObjMap() {
        return objMap;
    }

    public void setObjMap(Map<Coordinates, ObjType> objMap) {
        this.objMap = objMap;
    }

    public enum PARTY_VALUES {
        HERO_TYPE,
        UNITS,
        DIVINED_SPELL_POOL,
        KNOWN_SPELL_POOL,
        MEMORIZED_SPELL_POOL,
        PREPARED_SPELL_POOL,

        // ITEMS,

    }
}
