package main.client.battle;

import main.client.cc.logic.party.PartyObj;
import main.content.CONTENT_CONSTS.ENCOUNTER_TYPE;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.properties.G_PROPS;
import main.entity.Ref;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.DC_Game;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.logic.dungeon.building.MapBlock;
import main.game.logic.macro.travel.EncounterMaster;
import main.game.player.DC_Player;
import main.system.ai.GroupAI;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import java.util.LinkedList;
import java.util.List;

public class Wave extends DC_Obj {

    private List<ObjAtCoordinate> unitMap;
    private ENCOUNTER_TYPE waveType;
    private Integer power;
    private List<DC_HeroObj> units = new LinkedList<>();
    private DC_HeroObj leader;
    private PartyObj party;
    private MapBlock block;
    private int preferredPower;
    private boolean adjustmentDisabled;
    private boolean presetCoordinate;

    public Wave(DC_Player player) {
        super(new ObjType(), player, DC_Game.game, new Ref());
        adjustmentDisabled = true;
    }

    public Wave(Coordinates c, ObjType waveType, DC_Game game, Ref ref, DC_Player player) {
        super(waveType, player, game, ref);
        this.coordinates = c;
        if (coordinates != null)
            setPresetCoordinate(true);
    }

    public Wave(ObjType waveType, DC_Game game, Ref ref, DC_Player player) {
        super(waveType, player, game, ref);
    }

    @Override
    public String toString() {
        // unitMap
        String string = getNameAndCoordinate();
        return string;
    }

    public void initUnitMap() {
        getGame().getArenaManager().getWaveAssembler().assembleWave(this, !adjustmentDisabled,
                presetCoordinate);
        // unitMap = new HashMap<ObjType, Coordinates>();
        // for (String typeName : StringMaster
        // .openContainer(getProperty(PROPS.UNIT_TYPES))) {
        // // level up and place units
        // }
    }

    @Override
    public Coordinates getCoordinates() {
        return coordinates;
    }

    @Override
    public void setCoordinates(Coordinates coordinates) {
        if (coordinates != null)
            setPresetCoordinate(true);
        super.setCoordinates(coordinates);
    }

    // west or east
    // ai priorities
    // units
    // preset coordinates
    // effects
    // dynamic units
    // turn spawned
    // overlapping supported
    // wave level
    protected boolean isMicroGameObj() {
        return false;
    }

    @Override
    public void setRef(Ref ref) {
        super.setRef(ref);
        ref.setSource(getId());
    }

    public boolean isBoss(ObjType type) {
        return StringMaster.compare(type.getName(), getProperty(PROPS.BOSS_TYPE), true);
    }

    public int getUnitNumber() {
        return getIntParam(PARAMS.UNIT_NUMBER);
    }

    public int getMaxUnitsPerGroup() {
        return getIntParam(PARAMS.MAX_UNIT_PER_GROUP);
    }

    public FACING_DIRECTION getDefaultSide() {
        return FACING_DIRECTION.EAST;
    }

    public ENCOUNTER_TYPE getWaveType() {
        if (waveType == null) {
            waveType = new EnumMaster<ENCOUNTER_TYPE>().retrieveEnumConst(ENCOUNTER_TYPE.class,
                    getProperty(G_PROPS.ENCOUNTER_TYPE));
        }
        return waveType;
    }

    public Integer getBasePower() {
        return getIntParam(PARAMS.POWER_LEVEL);
    }

    public String getExtendedGroupTypes() {
        return getProperty(PROPS.EXTENDED_PRESET_GROUP);
    }

    public String getPresetGroupTypes() {
        return getProperty(PROPS.PRESET_GROUP);
    }

    public String getShrunkenGroupTypes() {
        return getProperty(PROPS.SHRUNK_PRESET_GROUP);
    }

    public List<ObjAtCoordinate> getUnitMap() {
        // DataManager.toMap()
        return unitMap;
    }

    public void setUnitMap(List<ObjAtCoordinate> unitMap) {
        this.unitMap = unitMap;
    }

    public Integer getPower() {
        if (power == 0)
            power = EncounterMaster.getPower(type, null);
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public FACING_DIRECTION getSide() {
        return new EnumMaster<FACING_DIRECTION>().retrieveEnumConst(FACING_DIRECTION.class,
                getProperty(PROPS.SPAWNING_SIDE));
    }

    public ENCOUNTER_TYPE getEncounterType() {
        return new EnumMaster<ENCOUNTER_TYPE>().retrieveEnumConst(ENCOUNTER_TYPE.class,
                getProperty(G_PROPS.ENCOUNTER_TYPE));
    }

    public void addUnit(DC_HeroObj unit) {
        units.add(unit);

    }

    public DC_HeroObj getLeader() {
        if (leader == null)
            leader = getUnits().get(0);
        return leader;
    }

    public List<DC_HeroObj> getUnits() {
        return units;
    }

    public void setUnits(List<DC_HeroObj> units) {
        this.units = units;
    }

    public PartyObj getParty() {
        return party;
    }

    public void setParty(PartyObj party) {
        this.party = party;

    }

    public MapBlock getBlock() {
        return block;
    }

    public void setBlock(MapBlock block) {
        this.block = block;

    }

    public int getPreferredPower() {
        return preferredPower;
    }

    public void setPreferredPower(int preferredPower) {
        this.preferredPower = preferredPower;

    }

    public void setAi(GroupAI groupAi) {
        for (DC_HeroObj unit : units) {
            unit.getUnitAI().setGroupAI(groupAi);
        }
    }

    public boolean isPresetCoordinate() {
        return presetCoordinate;
    }

    public void setPresetCoordinate(boolean presetCoordinate) {
        this.presetCoordinate = presetCoordinate;
    }

    public boolean isAdjustmentDisabled() {
        return adjustmentDisabled;
    }

    public void setAdjustmentDisabled(boolean adjustmentDisabled) {
        this.adjustmentDisabled = adjustmentDisabled;
    }

}
