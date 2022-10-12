package libgdx.gui.dungeon.panels.headquarters.datasource;

import eidolons.entity.unit.Unit;
import eidolons.game.core.Core;
import eidolons.game.core.game.DC_Game;
import eidolons.system.libgdx.datasource.HeroDataModel;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.core.game.Game;
import main.game.logic.battle.player.Player;

/**
 * Created by JustMe on 10/18/2018.
 * Used for what???
 */
public class HeroWrapper extends HeroDataModel {
    public HeroWrapper(Unit hero) {
        super(new ObjType(hero.getType(), true), hero.getX(), hero.getY(),
         hero.getOriginalOwner(),
         hero.getGame(), hero.getRef().getCopy());
        setHero(hero);
    }

    public void setHero(Unit hero) {
        this.hero = hero;
    }
    @Override
    public DC_Game getGame() {
        return Core.getGame();
    }

    @Override
    public Ref getRef() {
        return getHero().getRef();
    }

    @Override
    public void setRef(Ref ref) {
        getHero().setRef(ref);
        this.ref=ref;
    }
    @Override
    public void setGame(DC_Game game) {
        // ?
    }

    public boolean isSimulation() {
        return false;
    }

    @Override
    public void init() {

    }

    protected void preInit(Game game, ObjType type, Player owner, Ref ref) {

    }

    @Override
    public boolean isHero() {
        return true;
    }


    @Override
    public Coordinates getCoordinates() {
        return getHero().getCoordinates();
    }

    @Override
    public void setCoordinates(Coordinates coordinates) {
    }

    // @Override
    // public int getY() {
    //     return getHero().getY();
    // }
    //
    // @Override
    // public void setY(int y) {
    //     getHero().setY(y);
    // }
    //
    // @Override
    // public boolean canMove() {
    //     return getHero().canMove();
    // }
    //
    // @Override
    // public int getZ() {
    //     return getHero().getZ();
    // }
    //
    // @Override
    // public void setZ(int z) {
    //     getHero().setZ(z);
    // }
    // @Override
    // public String getNameAndCoordinate() {
    //     return getHero().getNameAndCoordinate();
    // }
    //
    // @Override
    // public int getX() {
    //     return getHero().getX();
    // }
    //
    // @Override
    // public void setX(int x) {
    //     getHero().setX(x);
    // }
    //
    // @Override
    // public DC_TYPE getOBJ_TYPE_ENUM() {
    //     return getHero().getOBJ_TYPE_ENUM();
    // }
    //
    // @Override
    // public void setOBJ_TYPE_ENUM(OBJ_TYPE TYPE_ENUM) {
    //     getHero().setOBJ_TYPE_ENUM(TYPE_ENUM);
    // }
    //
    // @Override
    // public boolean isOverlaying() {
    //     return getHero().isOverlaying();
    // }
    //
    // @Override
    // public void construct() {
    //     getHero().construct();
    // }
    //
    // @Override
    // public Boolean isLandscape() {
    //     return getHero().isLandscape();
    // }
    //
    // @Override
    // public boolean isWall() {
    //     return getHero().isWall();
    // }
    //
    // @Override
    // public boolean isObstructing(Obj obj) {
    //     return getHero().isObstructing(obj);
    // }
    //
    // @Override
    // public void cloneMaps(DataModel type) {
    //     getHero().cloneMaps(type);
    // }
    //
    // @Override
    // public String getDescription() {
    //     return getHero().getDescription();
    // }
    //
    // @Override
    // public String getDescription(Ref ref) {
    //     return getHero().getDescription(ref);
    // }
    //
    // @Override
    // public void cloneMaps(PropMap propMap, ParamMap paramMap) {
    //     getHero().cloneMaps(propMap, paramMap);
    // }
    //
    // @Override
    // public String getNameIfKnown() {
    //     return getHero().getNameIfKnown();
    // }
    //
    // @Override
    // public boolean checkGroupingProperty(String string) {
    //     return getHero().checkGroupingProperty(string);
    // }
    //
    // @Override
    // public String getCustomValue(String value_ref) {
    //     return getHero().getCustomValue(value_ref);
    // }
    //
    // @Override
    // public OutlineMapper getOutlineMapper() {
    //     return getHero().getOutlineMapper();
    // }
    //
    // @Override
    // public PlayerVisionMapper getPlayerVisionMapper() {
    //     return getHero().getPlayerVisionMapper();
    // }
    //
    // @Override
    // public void removeStatus(String value) {
    //     getHero().removeStatus(value);
    // }
    //
    // @Override
    // public String getCustomProperty(String value_ref) {
    //     return getHero().getCustomProperty(value_ref);
    // }
    //
    // @Override
    // public VisibilityLevelMapper getVisibilityLevelMapper() {
    //     return getHero().getVisibilityLevelMapper();
    // }
    //
    // @Override
    // public boolean setParam(PARAMETER param, String value, boolean quiety) {
    //     return getHero().setParam(param, value, quiety);
    // }
    //
    // @Override
    // public UnitVisionMapper getUnitVisionMapper() {
    //     return getHero().getUnitVisionMapper();
    // }
    //
    // @Override
    // public String getImagePath() {
    //     return getHero().getImagePath();
    // }
    //
    // @Override
    // public DetectionMapper getDetectionMapper() {
    //     return getHero().getDetectionMapper();
    // }
    //
    // @Override
    // public GammaMapper getGammaMapper() {
    //     return getHero().getGammaMapper();
    // }
    //
    // @Override
    // public String getDisplayedName() {
    //     return getHero().getDisplayedName();
    // }
    //
    // @Override
    // public String getToolTip() {
    //     return getHero().getToolTip();
    // }
    //
    // @Override
    // public Integer getCounter(String value_ref) {
    //     return getHero().getCounter(value_ref);
    // }
    //
    // @Override
    // public void addToState() {
    //     getHero().addToState();
    // }
    //
    // @Override
    // public List<Attachment> getAttachments() {
    //     return getHero().getAttachments();
    // }
    //
    // @Override
    // public void addBuff(BuffObj buff) {
    //     getHero().addBuff(buff);
    // }
    //
    // @Override
    // public void setGroup(String group, boolean base) {
    //     getHero().setGroup(group, base);
    // }
    //
    // @Override
    // public DC_Player getOwner() {
    //     return getHero().getOwner();
    // }
    //
    // @Override
    // public void setOwner(Player owner) {
    //     getHero().setOwner(owner);
    // }
    //
    // @Override
    // public Integer getCounter(COUNTER counter) {
    //     return getHero().getCounter(counter);
    // }
    //
    // @Override
    // public boolean setCounter(String name, int newValue) {
    //     return getHero().setCounter(name, newValue);
    // }
    //
    // @Override
    // public DequeImpl<BuffObj> getBuffs() {
    //     return getHero().getBuffs();
    // }
    //
    // @Override
    // public void setBuffs(DequeImpl<BuffObj> buffs) {
    //     getHero().setBuffs(buffs);
    // }
    //
    // @Override
    // public EntityMaster getMaster() {
    //     return getHero().getMaster();
    // }
    //
    // @Override
    // public boolean setCounter(String name, int newValue, boolean strict) {
    //     return getHero().setCounter(name, newValue, strict);
    // }
    //
    // @Override
    // public int getLastValidParamValue(PARAMETER parameter) {
    //     return getHero().getLastValidParamValue(parameter);
    // }
    //
    // @Override
    // public void removePassive(PassiveAbilityObj passive) {
    //     getHero().removePassive(passive);
    // }
    //
    // @Override
    // public  Map<PARAMETER, Integer> getValidParams() {
    //     return getHero().getValidParams();
    // }
    //
    // @Override
    // public VISION_MODE getVisionMode() {
    //     return getHero().getVisionMode();
    // }
    //
    // @Override
    // public boolean isEnemyTo(DC_Player player) {
    //     return getHero().isEnemyTo(player);
    // }
    //
    // @Override
    // public boolean checkInSight() {
    //     return getHero().checkInSight();
    // }
    //
    // @Override
    // public boolean isAlliedTo(DC_Player player) {
    //     return getHero().isAlliedTo(player);
    // }
    //
    // @Override
    // public boolean checkInSightForUnit(Unit unit) {
    //     return getHero().checkInSightForUnit(unit);
    // }
    //
    // @Override
    // public void addDynamicValues() {
    // }
    //
    // @Override
    // public void removeCounter(String name) {
    //     getHero().removeCounter(name);
    // }
    //
    // @Override
    // public boolean isObstructing() {
    //     return getHero().isObstructing();
    // }
    //
    // @Override
    // public UnitHandler getHandler() {
    //     return getHero().getHandler();
    // }
    //
    // @Override
    // public Boolean isPassable() {
    //     return getHero().isPassable();
    // }
    //
    // @Override
    // public boolean modifyCounter(String name, int modValue) {
    //     return getHero().modifyCounter(name, modValue);
    // }
    //
    // @Override
    // public void clicked() {
    //     getHero().clicked();
    // }
    //
    // @Override
    // public boolean isObstructing(Obj watcher, DC_Obj target) {
    //     return getHero().isObstructing(watcher, target);
    // }
    //
    // @Override
    // public boolean modifyCounter(COUNTER counter, int modValue) {
    //     return getHero().modifyCounter(counter, modValue);
    // }
    //
    // @Override
    // public List<String> getPassiveAbils() {
    //     return getHero().getPassiveAbils();
    // }
    //
    // @Override
    // public void setPassiveAbils(List<String> passiveAbils) {
    //     getHero().setPassiveAbils(passiveAbils);
    // }
    //
    // @Override
    // public void setGame(Game game) {
    //     getHero().setGame(game);
    // }
    //
    // @Override
    // public boolean modifyCounter(String name, int modValue, boolean strict) {
    //     return getHero().modifyCounter(name, modValue, strict);
    // }
    //
    // @Override
    // public boolean checkClassification(CLASSIFICATIONS PROP) {
    //     return getHero().checkClassification(PROP);
    // }
    //
    // @Override
    // public void invokeClicked() {
    //     getHero().invokeClicked();
    // }
    //
    // @Override
    // public boolean checkPassive(STANDARD_PASSIVES PROP) {
    //     return getHero().checkPassive(PROP);
    // }
    //
    // @Override
    // public BuffObj getBuff(String buffName) {
    //     return getHero().getBuff(buffName);
    // }
    //
    // @Override
    // public BuffObj getBuff(String buffName, boolean strict) {
    //     return getHero().getBuff(buffName, strict);
    // }
    //
    // @Override
    // public boolean isAgile() {
    //     return getHero().isAgile();
    // }
    //
    // @Override
    // public boolean isFlying() {
    //     return getHero().isFlying();
    // }
    //
    //
    // @Override
    // public boolean hasDoubleCounter() {
    //     return getHero().hasDoubleCounter();
    // }
    //
    // @Override
    // public boolean hasBludgeoning() {
    //     return getHero().hasBludgeoning();
    // }
    //
    // @Override
    // public boolean hasNoRetaliation() {
    //     return getHero().hasNoRetaliation();
    // }
    //
    // @Override
    // public boolean hasFirstStrike() {
    //     return getHero().hasFirstStrike();
    // }
    //
    // @Override
    // public DC_PassiveObj getFeat(ObjType type) {
    //     return getHero().getFeat(type);
    // }
    //
    // @Override
    // public boolean hasNoMeleePenalty() {
    //     return getHero().hasNoMeleePenalty();
    // }
    //
    // @Override
    // public boolean hasBuff(String buffName) {
    //     return getHero().hasBuff(buffName);
    // }
    //
    // public DC_PassiveObj getFeat(boolean skill, ObjType type) {
    //     return getHero().getFeat(skill, type);
    // }
    //
    // @Override
    // public String getParam(String p) {
    //     return getHero().getParam(p);
    // }
    //
    // @Override
    // public void removeBuff(String buffName) {
    //     getHero().removeBuff(buffName);
    // }
    //
    // @Override
    // public void setNaturalWeapon(boolean offhand, WeaponItem weapon) {
    //     getHero().setNaturalWeapon(offhand, weapon);
    // }
    //
    // @Override
    // public String getParam(PARAMETER param) {
    //     return getHero().getParam(param);
    // }
    //
    // @Override
    // public boolean isSmall() {
    //     return getHero().isSmall();
    // }
    //
    // @Override
    // public WeaponItem getNaturalWeapon() {
    //     return getHero().getNaturalWeapon();
    // }
    //
    // @Override
    // public WeaponItem getOffhandNaturalWeapon() {
    //     return getHero().getOffhandNaturalWeapon();
    // }
    //
    // @Override
    // public Double getParamDouble(PARAMETER param) {
    //     return getHero().getParamDouble(param);
    // }
    //
    // @Override
    // public boolean checkSelectHighlighted() {
    //     return getHero().checkSelectHighlighted();
    // }
    //
    // @Override
    // public WeaponItem getNaturalWeapon(boolean offhand) {
    //     return getHero().getNaturalWeapon(offhand);
    // }
    //
    // @Override
    // public boolean isShort() {
    //     return getHero().isShort();
    // }
    //
    // @Override
    // public Float getParamFloat(PARAMETER param) {
    //     return getHero().getParamFloat(param);
    // }
    //
    // @Override
    // public boolean isTall() {
    //     return getHero().isTall();
    // }
    //
    // @Override
    // public  Map<SPECIAL_EFFECTS_CASE, Effect> getSpecialEffects() {
    //     return getHero().getSpecialEffects();
    // }
    //
    // @Override
    // public void setSpecialEffects(Map<SPECIAL_EFFECTS_CASE, Effect> specialEffects) {
    //     getHero().setSpecialEffects(specialEffects);
    // }
    //
    // @Override
    // public void resetObjects() {
    //     getHero().resetObjects();
    // }
    //
    // @Override
    // public boolean removeJewelryItem(HeroItem itemObj) {
    //     return getHero().removeJewelryItem(itemObj);
    // }
    //
    // @Override
    // public Double getParamDouble(PARAMETER param, boolean base) {
    //     return getHero().getParamDouble(param, base);
    // }
    //
    // @Override
    // public void addBonusDamage(DAMAGE_CASE c, Damage d) {
    //     getHero().addBonusDamage(c, d);
    // }
    //
    // @Override
    // public void addQuickItem(QuickItem itemObj) {
    //     getHero().addQuickItem(itemObj);
    // }
    //
    // @Override
    // public boolean isAnnihilated() {
    //     return getHero().isAnnihilated();
    // }
    //
    // @Override
    // public void setAnnihilated(boolean annihilated) {
    //     getHero().setAnnihilated(annihilated);
    // }
    //
    // @Override
    // public Map<VALUE, String> getValueCache() {
    //     return getHero().getValueCache();
    // }
    //
    // @Override
    // public  Map<DAMAGE_CASE, List<Damage>> getBonusDamage() {
    //     return getHero().getBonusDamage();
    // }
    //
    // @Override
    // public String getCachedValue(VALUE value) {
    //     return getHero().getCachedValue(value);
    // }
    //
    // @Override
    // public String getDoubleParam(PARAMETER param) {
    //     return getHero().getDoubleParam(param);
    // }
    //
    // @Override
    // public void applySpecialEffects(SPECIAL_EFFECTS_CASE case_type, BattleFieldObject target, Ref REF) {
    //     getHero().applySpecialEffects(case_type, target, REF);
    // }
    //
    // @Override
    // public String getDoubleParam(PARAMETER param, boolean base) {
    //     return getHero().getDoubleParam(param, base);
    // }
    //
    // @Override
    // public boolean removeQuickItem(HeroItem itemObj) {
    //     return getHero().removeQuickItem(itemObj);
    // }
    //
    // @Override
    // public void constructConcurrently() {
    //     getHero().constructConcurrently();
    // }
    //
    // @Override
    // public void resetRef() {
    //     getHero().resetRef();
    // }
    //
    // @Override
    // public void addSpecialEffect(SPECIAL_EFFECTS_CASE case_type, Effect effects) {
    //     getHero().addSpecialEffect(case_type, effects);
    // }
    //
    // @Override
    // public void reset() {
    //     getHero().reset();
    // }
    //
    // @Override
    // public void addStatus(STATUS value) {
    //     getHero().addStatus(value);
    // }
    //
    // @Override
    // public boolean turnStarted() {
    //     return getHero().turnStarted();
    // }
    //
    // @Override
    // public void addStatus(String value) {
    //     getHero().addStatus(value);
    // }
    //
    // @Override
    // public BEHAVIOR_MODE getBehaviorMode() {
    //     return getHero().getBehaviorMode();
    // }
    //
    // @Override
    // public Integer getIntParam(String param) {
    //     return getHero().getIntParam(param);
    // }
    //
    // @Override
    // public void invokeRightClicked() {
    //     getHero().invokeRightClicked();
    // }
    //
    // @Override
    // public void run() {
    //     getHero().run();
    // }
    //
    // @Override
    // public String getStrParam(String param) {
    //     return getHero().getStrParam(param);
    // }
    //
    // @Override
    // public MODE getMode() {
    //     return getHero().getMode();
    // }
    //
    // @Override
    // public void setMode(MODE mode) {
    //     getHero().setMode(mode);
    // }
    //
    // @Override
    // public boolean kill() {
    //     return getHero().kill();
    // }
    //
    // @Override
    // public String getStrParam(PARAMETER param) {
    //     return getHero().getStrParam(param);
    // }
    //
    // @Override
    // public Integer getIntParam(PARAMETER param) {
    //     return getHero().getIntParam(param);
    // }
    //
    // @Override
    // public Integer getIntParam(PARAMETER param, boolean base) {
    //     return getHero().getIntParam(param, base);
    // }
    //
    // public void modified(ModifyValueEffect modifyValueEffect) {
    //     getHero().modified(modifyValueEffect);
    // }
    //
    // @Override
    // public PLAYER_VISION getActiveVisionStatus() {
    //     return getHero().getActiveVisionStatus();
    // }
    //
    // @Override
    // public void setActiveVisionStatus(PLAYER_VISION activeVisionStatus) {
    //     getHero().setActiveVisionStatus(activeVisionStatus);
    // }
    //
    // @Override
    // public String getDynamicInfo() {
    //     return getHero().getDynamicInfo();
    // }
    //
    // @Override
    // public Game getGenericGame() {
    //     return getHero().getGenericGame();
    // }
    //
    // @Override
    // public PLAYER_VISION getPlayerVisionStatus() {
    //     return getHero().getPlayerVisionStatus();
    // }
    //
    // @Override
    // public void setPlayerVisionStatus(PLAYER_VISION playerVisionStatus) {
    //     getHero().setPlayerVisionStatus(playerVisionStatus);
    // }
    //
    // @Override
    // public String getOBJ_TYPE() {
    //     return getHero().getOBJ_TYPE();
    // }
    //
    // @Override
    // public String getParamInfo() {
    //     return getHero().getParamInfo();
    // }
    //
    // @Override
    // public void resetPercentages() {
    //     getHero().resetPercentages();
    // }
    //
    // @Override
    // public UNIT_VISION getActiveUnitVisionStatus() {
    //     return getHero().getActiveUnitVisionStatus();
    // }
    //
    // @Override
    // public void setActiveUnitVisionStatus(UNIT_VISION activeUnitVisionStatus) {
    //     getHero().setActiveUnitVisionStatus(activeUnitVisionStatus);
    // }
    //
    // @Override
    // public DC_UnitAction getAttack() {
    //     return getHero().getAttack();
    // }
    //
    // @Override
    // public void addPassive(STANDARD_PASSIVES passive) {
    //     getHero().addPassive(passive);
    // }
    //
    // @Override
    // public void resetCurrentValues() {
    //     getHero().resetCurrentValues();
    // }
    //
    // @Override
    // public DC_UnitAction getOffhandAttack() {
    //     return getHero().getOffhandAttack();
    // }
    //
    // @Override
    // public void removed() {
    //     getHero().removed();
    // }
    //
    // @Override
    // public UNIT_VISION getUnitVisionStatus() {
    //     return getHero().getUnitVisionStatus();
    // }
    //
    // @Override
    // public void setUnitVisionStatus(UNIT_VISION activeUnitVisionStatus) {
    //     getHero().setUnitVisionStatus(activeUnitVisionStatus);
    // }
    //
    // @Override
    // public void addAction(String string) {
    //     getHero().addAction(string);
    // }
    //
    // @Override
    // public ObjectMap<PARAMETER, Integer> getIntegerMap() {
    //     return getHero().getIntegerMap();
    // }
    //
    // @Override
    // public boolean removeStatus(STATUS status) {
    //     return getHero().removeStatus(status);
    // }
    //
    // @Override
    // public ObjectMap<PARAMETER, Integer> getIntegerMap(boolean base) {
    //     return getHero().getIntegerMap(base);
    // }
    //
    // @Override
    // public boolean isDetected() {
    //     return getHero().isDetected();
    // }
    //
    // @Override
    // public void setDetected(boolean detected) {
    //     getHero().setDetected(detected);
    // }
    //
    // @Override
    // public void addPassive(String abilName) {
    //     getHero().addPassive(abilName);
    // }
    //
    // @Override
    // public void afterEffects() {
    //     getHero().afterEffects();
    // }
    //
    // @Override
    // public ParamMap getParamMap() {
    //     return getHero().getParamMap();
    // }
    //
    // @Override
    // public void setParamMap(ParamMap paramMap) {
    //     getHero().setParamMap(paramMap);
    // }
    //
    // @Override
    // public boolean isDetectedByPlayer() {
    //     return getHero().isDetectedByPlayer();
    // }
    //
    // @Override
    // public void setDetectedByPlayer(boolean detectedByPlayer) {
    //     getHero().setDetectedByPlayer(detectedByPlayer);
    // }
    //
    // @Override
    // public List<AbilityObj> getPassives() {
    //     return getHero().getPassives();
    // }
    //
    // @Override
    // public void setPassives(List<AbilityObj> passives) {
    //     getHero().setPassives(passives);
    // }
    //
    //
    // @Override
    // public boolean isOutsideCombat() {
    //     return getHero().isOutsideCombat();
    // }
    //
    // @Override
    // public void getBoolean(VALUE prop, Boolean b) {
    //     getHero().getBoolean(prop, b);
    // }
    //
    // @Override
    // public List<DC_QuickItemAction> getQuickItemActives() {
    //     return getHero().getQuickItemActives();
    // }
    //
    // @Override
    // public void regen() {
    //     getHero().regen();
    // }
    //
    // @Override
    // public ImageIcon getEmblem() {
    //     return getHero().getEmblem();
    // }
    //
    // @Override
    // public void setEmblem(ImageIcon emblem) {
    //     getHero().setEmblem(emblem);
    // }
    //
    // @Override
    // public OUTLINE_TYPE getOutlineType() {
    //     return getHero().getOutlineType();
    // }
    //
    // @Override
    // public void setOutlineType(OUTLINE_TYPE outlineTypeForPlayer) {
    //     getHero().setOutlineType(outlineTypeForPlayer);
    // }
    //
    // @Override
    // public String getProperty(String prop) {
    //     return getHero().getProperty(prop);
    // }
    //
    // @Override
    // public List<ActiveObj> getActives() {
    //     return getHero().getActives();
    // }
    //
    // @Override
    // public void setActives(List<ActiveObj> list) {
    //     getHero().setActives(list);
    // }
    //
    // @Override
    // public boolean isUpgrade() {
    //     return getHero().isUpgrade();
    // }
    //
    // @Override
    // public VISIBILITY_LEVEL getVisibilityLevel() {
    //     return getHero().getVisibilityLevel();
    // }
    //
    // @Override
    // public void setVisibilityLevel(VISIBILITY_LEVEL visibilityLevelForPlayer) {
    //     getHero().setVisibilityLevel(visibilityLevelForPlayer);
    // }
    //
    // @Override
    // public String getProp(String prop) {
    //     return getHero().getProp(prop);
    // }
    //
    //
    // @Override
    // public String getGroup() {
    //     return getHero().getGroup();
    // }
    //
    //
    // @Override
    // public void afterBuffRuleEffects() {
    //     getHero().afterBuffRuleEffects();
    // }
    //
    // @Override
    // public String getProperty(PROPERTY prop) {
    //     return getHero().getProperty(prop);
    // }
    //
    // @Override
    // public OUTLINE_TYPE getOutlineTypeForPlayer() {
    //     return getHero().getOutlineTypeForPlayer();
    // }
    //
    // @Override
    // public void setOutlineTypeForPlayer(OUTLINE_TYPE outlineTypeForPlayer) {
    //     getHero().setOutlineTypeForPlayer(outlineTypeForPlayer);
    // }
    //
    // @Override
    // public Player getOriginalOwner() {
    //     return getHero().getOriginalOwner();
    // }
    //
    // @Override
    // public void setOriginalOwner(Player originalOwner) {
    //     getHero().setOriginalOwner(originalOwner);
    // }
    //
    // @Override
    // public void setInfiniteValue(PARAMS param, float mod) {
    //     getHero().setInfiniteValue(param, mod);
    // }
    //
    // @Override
    // public boolean isFull() {
    //     return getHero().isFull();
    // }
    //
    // @Override
    // public boolean checkValue(VALUE v) {
    //     return getHero().checkValue(v);
    // }
    //
    // @Override
    // public  Map<ACTION_TYPE, DequeImpl<DC_UnitAction>> getActionMap() {
    //     return getHero().getActionMap();
    // }
    //
    // @Override
    // public void setActionMap(Map<ACTION_TYPE, DequeImpl<DC_UnitAction>> actionMap) {
    //     getHero().setActionMap(actionMap);
    // }
    //
    // @Override
    // public boolean isNeutral() {
    //     return getHero().isNeutral();
    // }
    //
    // @Override
    // public VISIBILITY_LEVEL getVisibilityLevelForPlayer() {
    //     return getHero().getVisibilityLevelForPlayer();
    // }
    //
    // @Override
    // public void setVisibilityLevelForPlayer(VISIBILITY_LEVEL visibilityLevelForPlayer) {
    //     getHero().setVisibilityLevelForPlayer(visibilityLevelForPlayer);
    // }
    //
    // @Override
    // public boolean isOwnedBy(Player player) {
    //     return getHero().isOwnedBy(player);
    // }
    //
    // @Override
    // public String getParamRounded(PARAMETER param, boolean base) {
    //     return getHero().getParamRounded(param, base);
    // }
    //
    // @Override
    // public boolean checkValue(VALUE v, String value) {
    //     return getHero().checkValue(v, value);
    // }
    //
    // @Override
    // public ASPECT getAspect() {
    //     return getHero().getAspect();
    // }
    //
    // @Override
    // public boolean checkActionCanBeActivated(String actionName) {
    //     return getHero().checkActionCanBeActivated(actionName);
    // }
    //
    // @Override
    // public ImageIcon getDefaultIcon() {
    //     return getHero().getDefaultIcon();
    // }
    //
    // @Override
    // public void activatePassives() {
    //     getHero().activatePassives();
    // }
    //
    // @Override
    // public boolean checkParam(PARAMETER param) {
    //     return getHero().checkParam(param);
    // }
    //
    // @Override
    // public Image getImage() {
    //     return getHero().getImage();
    // }
    //
    // @Override
    // public void setImage(String image) {
    //     getHero().setImage(image);
    // }
    //
    // @Override
    // public DC_UnitAction getAction(String name) {
    //     return getHero().getAction(name);
    // }
    //
    // @Override
    // public boolean checkParameter(PARAMETER param, int value) {
    //     return getHero().checkParameter(param, value);
    // }
    //
    // @Override
    // public void setWeapon(WeaponItem weapon) {
    //     getHero().setWeapon(weapon);
    // }
    //
    // @Override
    // public ImageIcon getIcon() {
    //     return getHero().getIcon();
    // }
    //
    // @Override
    // public boolean checkParam(PARAMETER param, String value) {
    //     return getHero().checkParam(param, value);
    // }
    //
    // @Override
    // public DC_UnitAction getAction(String action, boolean strict) {
    //     return getHero().getAction(action, strict);
    // }
    //
    // @Override
    // public DIRECTION getDirection() {
    //     return getHero().getDirection();
    // }
    //
    // @Override
    // public void setDirection(DIRECTION d) {
    //     getHero().setDirection(d);
    // }
    //
    // @Override
    // public boolean checkParam(PARAMETER param, int value) {
    //     return getHero().checkParam(param, value);
    // }
    //
    // @Override
    // public boolean checkProperty(PROPERTY p, String value) {
    //     return getHero().checkProperty(p, value);
    // }
    //
    // @Override
    // public void newRound() {
    //     getHero().newRound();
    // }
    //
    // @Override
    // public boolean isDead() {
    //     return getHero().isDead();
    // }
    //
    // @Override
    // public void setDead(boolean dead) {
    //     getHero().setDead(dead);
    // }
    //
    // @Override
    // public ObjectMap<PROPERTY, ObjectMap<String, Boolean>> getPropCache(boolean base) {
    //     return getHero().getPropCache(base);
    // }
    //
    // @Override
    // public WeaponItem getMainWeapon() {
    //     return getHero().getMainWeapon();
    // }
    //
    // @Override
    // public ArmorItem getArmor() {
    //     return getHero().getArmor();
    // }
    //
    // @Override
    // public WeaponItem getReserveMainWeapon() {
    //     return getHero().getReserveMainWeapon();
    // }
    //
    // @Override
    // public WeaponItem getReserveOffhandWeapon() {
    //     return getHero().getReserveOffhandWeapon();
    // }
    //
    // @Override
    // public void setArmor(ArmorItem armor) {
    //     getHero().setArmor(armor);
    // }
    //
    // @Override
    // public boolean isMine() {
    //     return getHero().isMine();
    // }
    //
    // @Override
    // public boolean checkProperty(PROPERTY p, String value, boolean base) {
    //     return getHero().checkProperty(p, value, base);
    // }
    //
    // @Override
    // public boolean isHuge() {
    //     return getHero().isHuge();
    // }
    //
    // @Override
    // public String getLargeImagePath() {
    //     return getHero().getLargeImagePath();
    // }
    //
    // @Override
    // public String getFullSizeImagePath() {
    //     return getHero().getFullSizeImagePath();
    // }
    //
    // @Override
    // public RACE getRace() {
    //     return getHero().getRace();
    // }
    //
    // @Override
    // public boolean isTurnable() {
    //     return getHero().isTurnable();
    // }
    //
    // public DAMAGE_TYPE getDamageType() {
    //     return getHero().getDamageType();
    // }
    //
    // @Override
    // public List<Spell> getSpells() {
    //     return getHero().getSpells();
    // }
    //
    // @Override
    // public void setSpells(List<Spell> spells) {
    //     getHero().setSpells(spells);
    // }
    //
    // @Override
    // public String getEmblemPath() {
    //     return getHero().getEmblemPath();
    // }
    //
    // @Override
    // public void resetRawValues() {
    //     getHero().resetRawValues();
    // }
    //
    // @Override
    // public WeaponItem getOffhandWeapon() {
    //     return getHero().getOffhandWeapon();
    // }
    //
    // @Override
    // public boolean checkSingleProp(String PROP, String value) {
    //     return getHero().checkSingleProp(PROP, value);
    // }
    //
    // @Override
    // public void setSecondWeapon(WeaponItem secondWeapon) {
    //     getHero().setSecondWeapon(secondWeapon);
    // }
    //
    // @Override
    // public boolean hasDoubleStrike() {
    //     return getHero().hasDoubleStrike();
    // }
    //
    // @Override
    // public boolean checkSingleProp(PROPERTY PROP, String value) {
    //     return getHero().checkSingleProp(PROP, value);
    // }
    //
    // @Override
    // public boolean isRawValuesOn() {
    //     return getHero().isRawValuesOn();
    // }
    //
    // @Override
    // public boolean isBfObj() {
    //     return getHero().isBfObj();
    // }
    //
    // @Override
    // public String getGroupingKey() {
    //     return getHero().getGroupingKey();
    // }
    //
    // @Override
    // public boolean isPlayerDetected() {
    //     return getHero().isPlayerDetected();
    // }
    //
    //
    // @Override
    // public EntityAnimator getAnimator() {
    //     return getHero().getAnimator();
    // }
    //
    // @Override
    // public boolean isLiving() {
    //     return getHero().isLiving();
    // }
    //
    // @Override
    // public Coordinates getBufferedCoordinates() {
    //     return getHero().getBufferedCoordinates();
    // }
    //
    // @Override
    // public void setBufferedCoordinates(Coordinates bufferedCoordinates) {
    //     getHero().setBufferedCoordinates(bufferedCoordinates);
    // }
    //
    // @Override
    // public boolean checkImmunity(IMMUNITIES type) {
    //     return getHero().checkImmunity(type);
    // }
    //
    // @Override
    // public boolean checkContainerProp(PROPERTY PROP, String value) {
    //     return getHero().checkContainerProp(PROP, value);
    // }
    //
    // @Override
    // public boolean checkContainerProp(PROPERTY PROP, String value, boolean any) {
    //     return getHero().checkContainerProp(PROP, value, any);
    // }
    //
    // @Override
    // public boolean canAttack() {
    //     return getHero().canAttack();
    // }
    //
    //
    // @Override
    // public boolean isSneaking() {
    //     return getHero().isSneaking();
    // }
    //
    // @Override
    // public void setSneaking(boolean sneaking) {
    //     getHero().setSneaking(sneaking);
    // }
    //
    // @Override
    // public boolean canCounter() {
    //     return getHero().canCounter();
    // }
    //
    // @Override
    // public boolean canCounter(DC_ActiveObj active) {
    //     return getHero().canCounter(active);
    // }
    //
    // @Override
    // public ImageIcon getCustomIcon() {
    //     return getHero().getCustomIcon();
    // }
    //
    // @Override
    // public void setCustomIcon(ImageIcon customIcon) {
    //     getHero().setCustomIcon(customIcon);
    // }
    //
    // @Override
    // public boolean isValidMapStored(PARAMETER p) {
    //     return getHero().isValidMapStored(p);
    // }
    //
    // @Override
    // public boolean canCounter(DC_ActiveObj active, boolean sneak) {
    //     return getHero().canCounter(active, sneak);
    // }
    //
    // @Override
    // public int getMaxVisionDistance() {
    //     return getHero().getMaxVisionDistance();
    // }
    //
    // @Override
    // public boolean checkSubGroup(String string) {
    //     return getHero().checkSubGroup(string);
    // }
    //
    // @Override
    // public List<Spell> getSpellbook() {
    //     return getHero().getSpellbook();
    // }
    //
    // @Override
    // public void setSpellbook(List<Spell> spellbook) {
    //     getHero().setSpellbook(spellbook);
    // }
    //
    // @Override
    // public boolean isSpotted() {
    //     return getHero().isSpotted();
    // }
    //
    // @Override
    // public boolean checkProperty(PROPERTY p) {
    //     return getHero().checkProperty(p);
    // }
    //
    // @Override
    // public Float getLastSeenTime() {
    //     return getHero().getLastSeenTime();
    // }
    //
    // @Override
    // public void setLastSeenTime(Float lastSeenTime) {
    //     getHero().setLastSeenTime(lastSeenTime);
    // }
    //
    // @Override
    // public boolean isDisabled() {
    //     return getHero().isDisabled();
    // }
    //
    // @Override
    // public void addFeat(DC_PassiveObj e) {
    //     getHero().addFeat(e);
    // }
    //
    // @Override
    // public boolean checkGroup(String string) {
    //     return getHero().checkGroup(string);
    // }
    //
    // @Override
    // public boolean checkStatusDisablesCounters() {
    //     return getHero().checkStatusDisablesCounters();
    // }
    //
    // @Override
    // public String getProperty(PROPERTY prop, boolean base) {
    //     return getHero().getProperty(prop, base);
    // }
    //
    // @Override
    // public boolean isUnconscious() {
    //     return getHero().isUnconscious();
    // }
    //
    // @Override
    // public DequeImpl<DC_PassiveObj> getSkills() {
    //     return getHero().getSkills();
    // }
    //
    // @Override
    // public void setSkills(DequeImpl<DC_PassiveObj> skills) {
    //     getHero().setSkills(skills);
    // }
    //
    // @Override
    // public boolean canAct() {
    //     return getHero().canAct();
    // }
    //
    // @Override
    // public boolean canActNow() {
    //     return getHero().canActNow();
    // }
    //
    // @Override
    // public PropMap getPropMap() {
    //     return getHero().getPropMap();
    // }
    //
    // @Override
    // public void setPropMap(PropMap propMap) {
    //     getHero().setPropMap(propMap);
    // }
    //
    // @Override
    // public Coordinates getLastCoordinates() {
    //     return getHero().getLastCoordinates();
    // }
    //
    // @Override
    // public boolean isInventoryFull() {
    //     return getHero().isInventoryFull();
    // }
    //
    // @Override
    // public boolean checkUncontrollable() {
    //     return getHero().checkUncontrollable();
    // }
    //
    // @Override
    // public OUTLINE_TYPE getLastSeenOutline() {
    //     return getHero().getLastSeenOutline();
    // }
    //
    // @Override
    // public void setLastSeenOutline(OUTLINE_TYPE lastSeenOutline) {
    //     getHero().setLastSeenOutline(lastSeenOutline);
    // }
    //
    //
    // @Override
    // public boolean isQuickSlotsFull() {
    //     return getHero().isQuickSlotsFull();
    // }
    //
    // @Override
    // public boolean checkStatusPreventsActions() {
    //     return getHero().checkStatusPreventsActions();
    // }
    //
    // @Override
    // public boolean isIncapacitated() {
    //     return getHero().isIncapacitated();
    // }
    //
    // @Override
    // public boolean isImmobilized() {
    //     return getHero().isImmobilized();
    // }
    //
    // @Override
    // public ObjType getOriginalType() {
    //     return getHero().getOriginalType();
    // }
    //
    // @Override
    // public void setOriginalType(ObjType originalType) {
    //     getHero().setOriginalType(originalType);
    // }
    //
    // @Override
    // public boolean checkModeDisablesCounters() {
    //     return getHero().checkModeDisablesCounters();
    // }
    //
    // @Override
    // public boolean isIndestructible() {
    //     return getHero().isIndestructible();
    // }
    //
    // @Override
    // public int getQuickSlotsMax() {
    //     return getHero().getQuickSlotsMax();
    // }
    //
    // @Override
    // public ObjType getType() {
    //     return getHero().getType();
    // }
    //
    // @Override
    // public void setType(ObjType type) {
    //     getHero().setType(type);
    // }
    //
    // @Override
    // public boolean checkModeDisablesActions() {
    //     return getHero().checkModeDisablesActions();
    // }
    //
    // @Override
    // public int getRemainingQuickSlots() {
    //     return getHero().getRemainingQuickSlots();
    // }
    //
    // @Override
    // public boolean isInvulnerable() {
    //     return getHero().isInvulnerable();
    // }
    //
    // @Override
    // public String getValue(String valName) {
    //     return getHero().getValue(valName);
    // }
    //
    // @Override
    // public boolean isTransparent() {
    //     return getHero().isTransparent();
    // }
    //
    // @Override
    // public int getOccupiedQuickSlots() {
    //     return getHero().getOccupiedQuickSlots();
    // }
    //
    // @Override
    // public DC_ActiveObj getDummyAction() {
    //     return getHero().getDummyAction();
    // }
    //
    // @Override
    // public String getValue(VALUE valName) {
    //     return getHero().getValue(valName);
    // }
    //
    // @Override
    // public String getValue(VALUE val, boolean base) {
    //     return getHero().getValue(val, base);
    // }
    //
    // @Override
    // public DequeImpl<QuickItem> getQuickItems() {
    //     return getHero().getQuickItems();
    // }
    //
    // @Override
    // public void setQuickItems(DequeImpl<QuickItem> quickItems) {
    //     getHero().setQuickItems(quickItems);
    // }
    //
    // @Override
    // public UnitResetter getResetter() {
    //     return getHero().getResetter();
    // }
    //
    // @Override
    // public HeroItem getItemFromInventory(String name) {
    //     return getHero().getItemFromInventory(name);
    // }
    //
    // @Override
    // public UnitChecker getChecker() {
    //     return getHero().getChecker();
    // }
    //
    // @Override
    // public boolean isHidden() {
    //     return getHero().isHidden();
    // }
    //
    // @Override
    // public void setHidden(boolean b) {
    //     getHero().setHidden(b);
    // }
    //
    // @Override
    // public QuickItem getQuickItem(String name) {
    //     return getHero().getQuickItem(name);
    // }
    //
    // @Override
    // public MODE getModeFinal() {
    //     return getHero().getModeFinal();
    // }
    //
    // @Override
    // public Entity getItem(String name) {
    //     return getHero().getItem(name);
    // }
    //
    // @Override
    // public boolean modifyParameter(PARAMETER param, Number amount, Integer minMax, boolean quietly, String modifierKey) {
    //     return getHero().modifyParameter(param, amount, minMax, quietly, modifierKey);
    // }
    //
    // @Override
    // public boolean modifyParameter(PARAMETER param, Number amount, Integer minMax, boolean quietly) {
    //     return getHero().modifyParameter(param, amount, minMax, quietly);
    // }
    //
    // @Override
    // public DequeImpl<HeroItem> getInventory() {
    //     return getHero().getInventory();
    // }
    //
    // @Override
    // public void setInventory(DequeImpl<HeroItem> inventory) {
    //     getHero().setInventory(inventory);
    // }
    //
    // @Override
    // public boolean modifyParameter(PARAMETER param, String amountString, Integer minMax, boolean quietly) {
    //     return getHero().modifyParameter(param, amountString, minMax, quietly);
    // }
    //
    // @Override
    // public boolean modifyParameter(PARAMETER param, String amountString, Integer minMax, boolean quietly, String modifierKey) {
    //     return getHero().modifyParameter(param, amountString, minMax, quietly, modifierKey);
    // }
    //
    // @Override
    // public DC_Masteries getMasteries() {
    //     return getHero().getMasteries();
    // }
    //
    // @Override
    // public void setMasteries(DC_Masteries masteries) {
    //     getHero().setMasteries(masteries);
    // }
    //
    // @Override
    // public DC_Attributes getAttrs() {
    //     return getHero().getAttrs();
    // }
    //
    // @Override
    // public void setAttrs(DC_Attributes attrs) {
    //     getHero().setAttrs(attrs);
    // }
    //
    // @Override
    // public boolean isMaxClassNumber() {
    //     return getHero().isMaxClassNumber();
    // }
    //
    // @Override
    // public DequeImpl<ClassRank> getClasses() {
    //     return getHero().getClasses();
    // }
    //
    // @Override
    // public void setClasses(DequeImpl<ClassRank> classes) {
    //     getHero().setClasses(classes);
    // }
    //
    // @Override
    // public WeaponItem getActiveWeapon(boolean offhand) {
    //     return getHero().getActiveWeapon(offhand);
    // }
    //
    // @Override
    // public WeaponItem getWeapon(boolean offhand) {
    //     return getHero().getWeapon(offhand);
    // }
    //
    // @Override
    // public boolean equip(HeroItem item, ITEM_SLOT slot) {
    //     return getHero().equip(item, slot);
    // }
    //
    // @Override
    // public boolean addItemToInventory(HeroItem item) {
    //     return getHero().addItemToInventory(item);
    // }
    //
    // @Override
    // public void applyType(ObjType type) {
    //     getHero().applyType(type);
    // }
    //
    // @Override
    // public boolean checkVisible() {
    //     return getHero().checkVisible();
    // }
    //
    // @Override
    // public boolean isFlippedImage() {
    //     return getHero().isFlippedImage();
    // }
    //
    // @Override
    // public void setItem(HeroItem item, ITEM_SLOT slot) {
    //     getHero().setItem(item, slot);
    // }
    //
    // @Override
    // public boolean checkStatus(STATUS STATUS) {
    //     return getHero().checkStatus(STATUS);
    // }
    //
    // @Override
    // public ObjectMap<PARAMETER, ObjectMap<String, Double>> getModifierMaps() {
    //     return getHero().getModifierMaps();
    // }
    //
    // @Override
    // public Integer getGamma() {
    //     return getHero().getGamma();
    // }
    //
    // @Override
    // public void setGamma(Integer gamma) {
    //     getHero().setGamma(gamma);
    // }
    //
    // @Override
    // public void outsideCombatReset() {
    //     getHero().outsideCombatReset();
    // }
    //
    // @Override
    // public boolean modifyParameter(PARAMETER param, Number amount, Integer minMax, String modifierKey) {
    //     return getHero().modifyParameter(param, amount, minMax, modifierKey);
    // }
    //
    // @Override
    // public PLAYER_VISION getActivePlayerVisionStatus() {
    //     return getHero().getActivePlayerVisionStatus();
    // }
    //
    // @Override
    // public boolean modifyParameter(PARAMETER param, Number amount, Integer minMax) {
    //     return getHero().modifyParameter(param, amount, minMax);
    // }
    //
    // @Override
    // public PLAYER_VISION getPlayerVisionStatus(boolean active) {
    //     return getHero().getPlayerVisionStatus(active);
    // }
    //
    // @Override
    // public HeroItem getItem(ITEM_SLOT slot) {
    //     return getHero().getItem(slot);
    // }
    //
    // @Override
    // public void modifyParameter(PARAMETER param, Number amount, boolean base) {
    //     getHero().modifyParameter(param, amount, base);
    // }
    //
    // @Override
    // public VISIBILITY_LEVEL getVisibilityLevel(boolean active) {
    //     return getHero().getVisibilityLevel(active);
    // }
    //
    // @Override
    // public void modifyParameter(PARAMETER param, Number amount, boolean base, String modifierKey) {
    //     getHero().modifyParameter(param, amount, base, modifierKey);
    // }
    //
    // @Override
    // public boolean isItemsInitialized() {
    //     return getHero().isItemsInitialized();
    // }
    //
    // @Override
    // public void setItemsInitialized(boolean itemsInitialized) {
    //     getHero().setItemsInitialized(itemsInitialized);
    // }
    //
    // @Override
    // public UNIT_VISION getUnitVisionStatus(BattleFieldObject object) {
    //     return getHero().getUnitVisionStatus(object);
    // }
    //
    // @Override
    // public boolean modifyParameter(PARAMETER param, Number amount, String modifierKey) {
    //     return getHero().modifyParameter(param, amount, modifierKey);
    // }
    //
    // @Override
    // public boolean isDetected(DC_Player owner) {
    //     return getHero().isDetected(owner);
    // }
    //
    // @Override
    // public boolean dropItemFromInventory(HeroItem item, Coordinates c) {
    //     return getHero().dropItemFromInventory(item, c);
    // }
    //
    // @Override
    // public Integer getGamma(Unit source) {
    //     return getHero().getGamma(source);
    // }
    //
    // @Override
    // public boolean modifyParameter(PARAMETER param, Number amount) {
    //     return getHero().modifyParameter(param, amount);
    // }
    //
    // @Override
    // public void setGamma(Unit source, Integer i) {
    //     getHero().setGamma(source, i);
    // }
    //
    // @Override
    // public void decrementParam(PARAMETER param) {
    //     getHero().decrementParam(param);
    // }
    //
    // @Override
    // public boolean dropItemFromInventory(HeroItem item) {
    //     return getHero().dropItemFromInventory(item);
    // }
    //
    // @Override
    // public int getContainerCount(PROPERTY p) {
    //     return getHero().getContainerCount(p);
    // }
    //
    // @Override
    // public boolean removeFromInventory(HeroItem item) {
    //     return getHero().removeFromInventory(item);
    // }
    //
    // @Override
    // public void incrementParam(PARAMETER param) {
    //     getHero().incrementParam(param);
    // }
    //
    // @Override
    // public void setVisibilityLevel(Unit source, VISIBILITY_LEVEL visibilityLevel) {
    //     getHero().setVisibilityLevel(source, visibilityLevel);
    // }
    //
    // @Override
    // public void fullReset(DC_Game newGame) {
    //     getHero().fullReset(newGame);
    // }
    //
    // @Override
    // public void setUnitVisionStatus(UNIT_VISION status, BattleFieldObject observer) {
    //     getHero().setUnitVisionStatus(status, observer);
    // }
    //
    // @Override
    // public boolean multiplyParamByPercent(PARAMETER param, int perc, boolean base) {
    //     return getHero().multiplyParamByPercent(param, perc, base);
    // }
    //
    // @Override
    // public boolean modifyParamByPercent(PARAMETER[] params, int perc) {
    //     return getHero().modifyParamByPercent(params, perc);
    // }
    //
    // @Override
    // public boolean modifyParamByPercent(PARAMETER param, int perc) {
    //     return getHero().modifyParamByPercent(param, perc);
    // }
    //
    //
    // @Override
    // public boolean modifyParamByPercent(PARAMETER param, int perc, boolean base) {
    //     return getHero().modifyParamByPercent(param, perc, base);
    // }
    //
    // @Override
    // public BACKGROUND getBackground() {
    //     return getHero().getBackground();
    // }
    //
    // @Override
    // public DequeImpl<JewelryItem> getJewelry() {
    //     return getHero().getJewelry();
    // }
    //
    // @Override
    // public void setJewelry(DequeImpl<JewelryItem> jewelry) {
    //     getHero().setJewelry(jewelry);
    // }
    //
    // @Override
    // public void addJewelryItem(JewelryItem item) {
    //     getHero().addJewelryItem(item);
    // }
    //
    // @Override
    // public HeroItem unequip(ITEM_SLOT slot) {
    //     getHero().unequip(slot);
    //     return null;
    // }
    //
    // @Override
    // public HeroItem unequip(ITEM_SLOT slot, Boolean drop) {
    //     getHero().unequip(slot, drop);
    //     return null;
    // }
    //
    // @Override
    // public boolean fireParamEvent(PARAMETER param, String amount, CONSTRUCTED_EVENT_TYPE event_type) {
    //     return getHero().fireParamEvent(param, amount, event_type);
    // }
    //
    // @Override
    // public void resetDynamicParam(PARAMETER param) {
    //     getHero().resetDynamicParam(param);
    // }
    //
    // @Override
    // public void applySpecialEffects(SPECIAL_EFFECTS_CASE case_type, BattleFieldObject target, Ref REF, boolean offhand) {
    //     getHero().applySpecialEffects(case_type, target, REF, offhand);
    // }
    //
    // @Override
    // public void setParam(PARAMETER param, int i, boolean quietly, boolean base) {
    //     getHero().setParam(param, i, quietly, base);
    // }
    //
    // @Override
    // public void setParam(PARAMETER param, int i, boolean quietly) {
    //     getHero().setParam(param, i, quietly);
    // }
    //
    // @Override
    // public void setParamDouble(PARAMETER param, double i, boolean quietly) {
    //     getHero().setParamDouble(param, i, quietly);
    // }
    //
    // @Override
    // public void setParameter(PARAMETER param, int i) {
    //     getHero().setParameter(param, i);
    // }
    //
    // @Override
    // public void setParam(PARAMETER param, int i) {
    //     getHero().setParam(param, i);
    // }
    //
    // @Override
    // public void unequip(HeroItem item, Boolean drop) {
    //     getHero().unequip(item, drop);
    // }
    //
    // @Override
    // public void setParam(String param, int i) {
    //     getHero().setParam(param, i);
    // }
    //
    // @Override
    // public void setParamMax(PARAMETER p, int i) {
    //     getHero().setParamMax(p, i);
    // }
    //
    // @Override
    // public void setParamMin(PARAMETER p, int i) {
    //     getHero().setParamMin(p, i);
    // }
    //
    // @Override
    // public void modifyParameter(String param, String string) {
    //     getHero().modifyParameter(param, string);
    // }
    //
    // @Override
    // public void modifyParamByPercent(String param, String string) {
    //     getHero().modifyParamByPercent(param, string);
    // }
    //
    // @Override
    // public boolean kill(Entity killer, boolean leaveCorpse, Boolean quietly) {
    //     return getHero().kill(killer, leaveCorpse, quietly);
    // }
    //
    // @Override
    // public List<AbilityObj> getPassivesFiltered() {
    //     return getHero().getPassivesFiltered();
    // }
    //
    // @Override
    // public List<HeroSlotItem> getSlotItems() {
    //     return getHero().getSlotItems();
    // }
    //
    // @Override
    // public boolean hasBroadReach() {
    //     return getHero().hasBroadReach();
    // }
    //
    // @Override
    // public boolean hasHindReach() {
    //     return getHero().hasHindReach();
    // }
    //
    // @Override
    // public boolean hasWeaponPassive(Boolean offhand, STANDARD_PASSIVES passive) {
    //     return getHero().hasWeaponPassive(offhand, passive);
    // }
    //
    // @Override
    // public void resetPercentage(PARAMETER p) {
    //     getHero().resetPercentage(p);
    // }
    //
    // @Override
    // public boolean checkAiMod(AI_MODIFIERS aiMod) {
    //     return getHero().checkAiMod(aiMod);
    // }
    //
    // @Override
    // public boolean setParam(PARAMETER param, String value) {
    //     return getHero().setParam(param, value);
    // }
    //
    // @Override
    // public Spell getSpell(String actionName) {
    //     return getHero().getSpell(actionName);
    // }
    //
    // @Override
    // public void setProperty(PROPERTY name, String value, boolean base) {
    //     getHero().setProperty(name, value, base);
    // }
    //
    // @Override
    // public boolean isAiControlled() {
    //     return getHero().isAiControlled();
    // }
    //
    // @Override
    // public void setAiControlled(boolean aiControlled) {
    //     getHero().setAiControlled(aiControlled);
    // }
    //
    // @Override
    // public void setProperty(String prop, String value) {
    //     getHero().setProperty(prop, value);
    // }
    //
    // @Override
    // public void setProperty(PROPERTY prop, String value) {
    //     getHero().setProperty(prop, value);
    // }
    //
    // @Override
    // public void modifyProperty(MOD_PROP_TYPE p, PROPERTY prop, String value) {
    //     getHero().modifyProperty(p, prop, value);
    // }
    //
    // @Override
    // public UnitAI getAI() {
    //     return getHero().getAI();
    // }
    //
    // @Override
    // public UnitAI getUnitAI() {
    //     return getHero().getUnitAI();
    // }
    //
    // @Override
    // public void removeLastPartFromProperty(PROPERTY prop) {
    //     getHero().removeLastPartFromProperty(prop);
    // }
    //
    //
    // @Override
    // public void removeFromProperty(PROPERTY prop, String value) {
    //     getHero().removeFromProperty(prop, value);
    // }
    //
    // @Override
    // public void appendProperty(PROPERTY prop, String value) {
    //     getHero().appendProperty(prop, value);
    // }
    //
    // @Override
    // public boolean addOrRemoveProperty(PROPERTY prop, String value) {
    //     return getHero().addOrRemoveProperty(prop, value);
    // }
    //
    // @Override
    // public GENDER getGender() {
    //     return getHero().getGender();
    // }
    //
    // @Override
    // public boolean addProperty(PROPERTY prop, String value) {
    //     return getHero().addProperty(prop, value);
    // }
    //
    // @Override
    // public boolean addProperty(PROPERTY prop, List<String> values, boolean noDuplicates) {
    //     return getHero().addProperty(prop, values, noDuplicates);
    // }
    //
    // @Override
    // public FLIP getFlip() {
    //     return getHero().getFlip();
    // }
    //
    // @Override
    // public void setFlip(FLIP flip) {
    //     getHero().setFlip(flip);
    // }
    //
    // @Override
    // public boolean isMainHero() {
    //     return getHero().isMainHero();
    // }
    //
    // @Override
    // public void setMainHero(boolean mainHero) {
    //     getHero().setMainHero(mainHero);
    // }
    //
    // @Override
    // public boolean addProperty(PROPERTY prop, String value, boolean noDuplicates) {
    //     return getHero().addProperty(prop, value, noDuplicates);
    // }
    //
    // @Override
    // public boolean addProperty(PROPERTY prop, String value, boolean noDuplicates, boolean addInFront) {
    //     return getHero().addProperty(prop, value, noDuplicates, addInFront);
    // }
    //
    // @Override
    // public boolean isPlayerCharacter() {
    //     return getHero().isPlayerCharacter();
    // }
    //
    // @Override
    // public boolean isHostileTo(DC_Player player) {
    //     return getHero().isHostileTo(player);
    // }
    //
    // @Override
    // public HeroItem findItem(String typeName, Boolean quick_inv_slot) {
    //     return getHero().findItem(typeName, quick_inv_slot);
    // }
    //
    // @Override
    // public boolean isTypeLinked() {
    //     return getHero().isTypeLinked();
    // }
    //
    // @Override
    // public void addProperty(String prop, String value) {
    //     getHero().addProperty(prop, value);
    // }
    //
    // @Override
    // public boolean clearProperty(PROPERTY prop) {
    //     return getHero().clearProperty(prop);
    // }
    //
    // @Override
    // public boolean removeProperty(PROPERTY prop) {
    //     return getHero().removeProperty(prop);
    // }
    //
    // @Override
    // public DC_ActiveObj getAttackAction(boolean offhand) {
    //     return getHero().getAttackAction(offhand);
    // }
    //
    // @Override
    // public boolean addProperty(boolean base, PROPERTY prop, String value) {
    //     return getHero().addProperty(base, prop, value);
    // }
    //
    // @Override
    // public Unit getEngagementTarget() {
    //     return getHero().getEngagementTarget();
    // }
    //
    // @Override
    // public void setEngagementTarget(Unit engaged) {
    //     getHero().setEngagementTarget(engaged);
    // }
    //
    // @Override
    // public boolean removeProperty(boolean base, PROPERTY prop, String value) {
    //     return getHero().removeProperty(base, prop, value);
    // }
    //
    // @Override
    // public ObjType getBackgroundType() {
    //     return getHero().getBackgroundType();
    // }
    //
    // @Override
    // public void setBackgroundType(ObjType backgroundType) {
    //     getHero().setBackgroundType(backgroundType);
    // }
    //
    //
    // @Override
    // public boolean removeProperty(PROPERTY prop, String value) {
    //     return getHero().removeProperty(prop, value);
    // }
    //
    // @Override
    // public boolean removeProperty(PROPERTY prop, String value, boolean all) {
    //     return getHero().removeProperty(prop, value, all);
    // }
    //
    // @Override
    // public boolean isPlayerControlled() {
    //     return getHero().isPlayerControlled();
    // }
    //
    // @Override
    // public boolean isEngagedWith(Unit attacker) {
    //     return getHero().isEngagedWith(attacker);
    // }
    //
    // @Override
    // public void resetQuickSlotsNumber() {
    //     getHero().resetQuickSlotsNumber();
    // }
    //
    // @Override
    // public void resetObjectContainers(boolean fromValues) {
    //     getHero().resetObjectContainers(fromValues);
    // }
    //
    // @Override
    // public void resetDefaultAttrs() {
    //     getHero().resetDefaultAttrs();
    // }
    //
    //
    // @Override
    // public UnitLogger getLogger() {
    //     return getHero().getLogger();
    // }
    //
    // @Override
    // public UnitCalculator getCalculator() {
    //     return getHero().getCalculator();
    // }
    //
    // @Override
    // public int calculateRemainingMemory() {
    //     return getHero().calculateRemainingMemory();
    // }
    //
    // @Override
    // public int calculateUsedMemory() {
    //     return getHero().calculateUsedMemory();
    // }
    //
    // @Override
    // public int getPower() {
    //     return getHero().getPower();
    // }
    //
    // @Override
    // public int calculateWeight() {
    //     return getHero().calculateWeight();
    // }
    //
    // @Override
    // public String getSubGroupingKey() {
    //     return getHero().getSubGroupingKey();
    // }
    //
    // @Override
    // public Integer calculateAndSetDamage(boolean offhand) {
    //     return getHero().calculateAndSetDamage(offhand);
    // }
    //
    // @Override
    // public boolean isSetThis() {
    //     return getHero().isSetThis();
    // }
    //
    // @Override
    // public Integer calculateDamage(boolean offhand) {
    //     return getHero().calculateDamage(offhand);
    // }
    //
    // @Override
    // public void setValue(VALUE valName, String value) {
    //     getHero().setValue(valName, value);
    // }
    //
    // @Override
    // public Integer calculateDamage(boolean offhand, boolean set) {
    //     return getHero().calculateDamage(offhand, set);
    // }
    //
    // @Override
    // public void setValue(VALUE valName, String value, boolean base) {
    //     getHero().setValue(valName, value, base);
    // }
    //
    // @Override
    // public int calculateCarryingWeight() {
    //     return getHero().calculateCarryingWeight();
    // }
    //
    // @Override
    // public UnitInitializer getInitializer() {
    //     return getHero().getInitializer();
    // }
    //
    // @Override
    // public void initSpells(boolean reset) {
    //     getHero().initSpells(reset);
    // }
    //
    // @Override
    // public void initSkills() {
    //     getHero().initSkills();
    // }
    //
    // @Override
    // public void initAttributes() {
    //     getHero().initAttributes();
    // }
    //
    // @Override
    // public void setValue(String name, String value) {
    //     getHero().setValue(name, value);
    // }
    //
    // @Override
    // public void initMasteries() {
    //     getHero().initMasteries();
    // }
    //
    // @Override
    // public void setValue(String name, String value, boolean base) {
    //     getHero().setValue(name, value, base);
    // }
    //
    // @Override
    // public void initNaturalWeapon(boolean offhand) {
    //     getHero().initNaturalWeapon(offhand);
    // }
    //
    // @Override
    // public boolean canUseItems() {
    //     return getHero().canUseItems();
    // }
    //
    // @Override
    // public Unit getEntity() {
    //     return getHero().getEntity();
    // }
    //
    // @Override
    // public boolean canUseArmor() {
    //     return getHero().canUseArmor();
    // }
    //
    // @Override
    // public boolean canUseWeapons() {
    //     return getHero().canUseWeapons();
    // }
    //
    // @Override
    // public boolean checkDualWielding() {
    //     return getHero().checkDualWielding();
    // }
    //
    // @Override
    // public void mergeValues(Entity type, VALUE... vals) {
    //     getHero().mergeValues(type, vals);
    // }
    //
    // @Override
    // public boolean isImmortalityOn() {
    //     return getHero().isImmortalityOn();
    // }
    //
    // @Override
    // public boolean isConstructAlways() {
    //     return getHero().isConstructAlways();
    // }
    //
    // @Override
    // public void resetFacing() {
    //     getHero().resetFacing();
    // }
    //
    // @Override
    // public DequeImpl<JewelryItem> getRings() {
    //     return getHero().getRings();
    // }
    //
    // @Override
    // public void addParam(PARAMETER parameter, String param, boolean base) {
    //     getHero().addParam(parameter, param, base);
    // }
    //
    // @Override
    // public JewelryItem getAmulet() {
    //     return getHero().getAmulet();
    // }
    //
    // @Override
    // public void copyValues(Entity type, List<VALUE> list) {
    //     getHero().copyValues(type, list);
    // }
    //
    // @Override
    // public void copyValues(Entity type, VALUE... vals) {
    //     getHero().copyValues(type, vals);
    // }
    //
    // @Override
    // public DC_Obj getLinkedObj(IdKey key) {
    //     return getHero().getLinkedObj(key);
    // }
    //
    // @Override
    // public void cloneMapsWithExceptions(DataModel type, VALUE... exceptions) {
    //     getHero().cloneMapsWithExceptions(type, exceptions);
    // }
    //
    // @Override
    // public WeaponItem getRangedWeapon() {
    //     return getHero().getRangedWeapon();
    // }
    //
    // @Override
    // public void setRangedWeapon(WeaponItem rangedWeapon) {
    //     getHero().setRangedWeapon(rangedWeapon);
    // }
    // @Override
    // public Integer getId() {
    //     if (getHero() == null) {
    //         return 0;
    //     }
    //     return getHero().getId();
    // }
    // @Override
    // public String toString() {
    //     if (getHero() == null) {
    //         return "";
    //     }
    //     return getHero().toString();
    // }
    //
    // @Override
    // public AI_TYPE getAiType() {
    //     return getHero().getAiType();
    // }
    //
    // @Override
    // public int getMaxVisionDistanceTowards(Coordinates c) {
    //     return getHero().getMaxVisionDistanceTowards(c);
    // }
    //
    // @Override
    // public int getSightRange(DC_Obj target) {
    //     return getHero().getSightRange(target);
    // }
    //
    // @Override
    // public int getSightRange(Coordinates coordinates) {
    //     return getHero().getSightRange(coordinates);
    // }
    //
    // @Override
    // public boolean isUsingStealth() {
    //     return getHero().isUsingStealth();
    // }
    //
    // @Override
    // public void setUsingStealth(boolean usingStealth) {
    //     getHero().setUsingStealth(usingStealth);
    // }
    //
    // @Override
    // public void toBase() {
    //     getHero().toBase();
    // }
    //
    //
    //
    // @Override
    // public void setId(Integer id) {
    //     getHero().setId(id);
    // }
    //
    // @Override
    // public DC_ActiveObj getTurnAction(boolean clockwise) {
    //     return getHero().getTurnAction(clockwise);
    // }
    //
    // @Override
    // public String getName() {
    //     return getHero().getName();
    // }
    //
    // @Override
    // public void setName(String name) {
    //     getHero().setName(name);
    // }
    //
    // @Override
    // public DequeImpl<Perk> getPerks() {
    //     return getHero().getPerks();
    // }
    //
    // @Override
    // public void setPerks(DequeImpl<Perk> perks) {
    //     getHero().setPerks(perks);
    // }
    //
    // @Override
    // public String getUniqueId() {
    //     return getHero().getUniqueId();
    // }
    //
    // @Override
    // public boolean isConstructed() {
    //     return getHero().isConstructed();
    // }
    //
    // @Override
    // public void setConstructed(boolean b) {
    //     getHero().setConstructed(b);
    // }
    //
    // public boolean isDirty() {
    //     return getHero().isDirty();
    // }
    //
    // @Override
    // public void setDirty(boolean dirty) {
    //     getHero().setDirty(dirty);
    // }
    //
    // @Override
    // public boolean isPassivesReady() {
    //     return getHero().isPassivesReady();
    // }
    //
    // @Override
    // public void setPassivesReady(boolean passivesReady) {
    //     getHero().setPassivesReady(passivesReady);
    // }
    //
    // @Override
    // public boolean isActivesReady() {
    //     return getHero().isActivesReady();
    // }
    //
    // @Override
    // public void setActivesReady(boolean activesReady) {
    //     getHero().setActivesReady(activesReady);
    // }
    //
    // @Override
    // public boolean checkBool(DYNAMIC_BOOLS bool) {
    //     return getHero().checkBool(bool);
    // }
    //
    // @Override
    // public boolean checkBool(STD_BOOLS bool) {
    //     return getHero().checkBool(bool);
    // }
    //
    // @Override
    // public boolean checkCustomProp(String name) {
    //     return getHero().checkCustomProp(name);
    // }
    //
    // @Override
    // public ObjectMap<String, String> getCustomPropMap() {
    //     return getHero().getCustomPropMap();
    // }
    //
    // @Override
    // public void addCustomProperty(String name, String value) {
    //     getHero().addCustomProperty(name, value);
    // }
    //
    // @Override
    // public void addCounter(String name, String value) {
    //     getHero().addCounter(name, value);
    // }
    //
    // @Override
    // public ObjectMap<String, String> getCustomParamMap() {
    //     return getHero().getCustomParamMap();
    // }
    //
    // @Override
    // public boolean equals(Object obj) {
    //     return getHero().equals(obj);
    // }
    //
    // @Override
    // public boolean replaceContainerPropItem(PROPERTY prop, String replacing, String replaced) {
    //     return getHero().replaceContainerPropItem(prop, replacing, replaced);
    // }
    //
    // @Override
    // public void copyValue(VALUE param, Entity entity) {
    //     getHero().copyValue(param, entity);
    // }
    //
    // @Override
    // public void setModifierKey(String modifierKey) {
    //     getHero().setModifierKey(modifierKey);
    // }
    //
    // @Override
    // public boolean isInitialized() {
    //     return getHero().isInitialized();
    // }
    //
    // @Override
    // public void setInitialized(boolean initialized) {
    //     getHero().setInitialized(initialized);
    // }
    //
    // @Override
    // public String getNameOrId() {
    //     return getHero().getNameOrId();
    // }
    //
    // @Override
    // public String getNameAndId() {
    //     return getHero().getNameAndId();
    // }
    //
    // @Override
    // public String getRawValue(VALUE value) {
    //     return getHero().getRawValue(value);
    // }
    //
    // @Override
    // public XLinkedMap<VALUE, String> getRawValues() {
    //     return getHero().getRawValues();
    // }
    //
    // @Override
    // public void setRawValues(XLinkedMap<VALUE, String> rawValues) {
    //     getHero().setRawValues(rawValues);
    // }
    //
    // @Override
    // public boolean isDefaultValuesInitialized() {
    //     return getHero().isDefaultValuesInitialized();
    // }
    //
    // @Override
    // public void setDefaultValuesInitialized(boolean defaultValuesInitialized) {
    //     getHero().setDefaultValuesInitialized(defaultValuesInitialized);
    // }
    //
    // @Override
    // public void cloned() {
    //     getHero().cloned();
    // }
    //
    // @Override
    // public int getLevel() {
    //     return getHero().getLevel();
    // }
    //
    // @Override
    // public String getOriginalName() {
    //     return getHero().getOriginalName();
    // }
    //
    // @Override
    // public void setOriginalName(String originalName) {
    //     getHero().setOriginalName(originalName);
    // }
    //
    // @Override
    // public WORKSPACE_GROUP getWorkspaceGroup() {
    //     return getHero().getWorkspaceGroup();
    // }
    //
    // @Override
    // public void setWorkspaceGroup(WORKSPACE_GROUP value) {
    //     getHero().setWorkspaceGroup(value);
    // }
    //
    // @Override
    // public int getTypeId() {
    //     return getHero().getTypeId();
    // }
    //
    // @Override
    // public List<ObjType> getListFromProperty(OBJ_TYPE TYPE, PROPERTY prop) {
    //     return getHero().getListFromProperty(TYPE, prop);
    // }
    //
    // @Override
    // public void resetPropertyFromList(PROPERTY prop, List<? extends Entity> list) {
    //     getHero().resetPropertyFromList(prop, list);
    // }
    //
    //
    // @Override
    // public int getSumOfParams(PARAMETER... params) {
    //     return getHero().getSumOfParams(params);
    // }
    //
    // @Override
    // public boolean isFull(PARAMETER p) {
    //     return getHero().isFull(p);
    // }
    //
    // @Override
    // public int getParamPercentage(PARAMETER parameter) {
    //     return getHero().getParamPercentage(parameter);
    // }
    //
    // @Override
    // public boolean isBeingReset() {
    //     return getHero().isBeingReset();
    // }
    //
    // @Override
    // public void setBeingReset(boolean beingReset) {
    //     getHero().setBeingReset(beingReset);
    // }
    //
    // @Override
    // public void shuffleContainerProperty(PROPERTY property) {
    //     getHero().shuffleContainerProperty(property);
    // }
    //
    // @Override
    // public void reverseContainerProperty(PROPERTY property) {
    //     getHero().reverseContainerProperty(property);
    // }
    //
    //
    // @Override
    // public boolean isLoaded() {
    //     return getHero().isLoaded();
    // }
    //
    // @Override
    // public void setLoaded(boolean loaded) {
    //     getHero().setLoaded(loaded);
    // }
}
