package eidolons.entity.feat.active;

import main.ability.Abilities;
import main.ability.AbilityObj;
import main.content.OBJ_TYPE;
import main.content.enums.GenericEnums.STD_BOOLS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.elements.targeting.Targeting;
import main.entity.Ref;
import main.entity.group.GroupImpl;
import main.entity.obj.IActiveObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;

import java.util.List;

/**
 * Created by JustMe on 11/27/2017.
 */
public class DummyAction implements IActiveObj {
    @Override
    public Ref getRef() {
        return null;
    }

    @Override
    public void setRef(Ref ref) {

    }

    @Override
    public void playCancelSound() {

    }

    @Override
    public Obj getOwnerUnit() {
        return null;
    }

    @Override
    public boolean isFree() {
        return false;
    }

    @Override
    public boolean isEffectSoundPlayed() {
        return false;
    }

    @Override
    public void setEffectSoundPlayed(boolean effectSoundPlayed) {

    }

    @Override
    public Abilities getAbilities() {
        return null;
    }

    @Override
    public Targeting getTargeting() {
        return null;
    }

    @Override
    public boolean isForcePresetTarget() {
        return false;
    }

    @Override
    public void setForcePresetTarget(boolean b) {

    }

    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public boolean isZone() {
        return false;
    }

    @Override
    public boolean isMissile() {
        return false;
    }

    @Override
    public boolean isOffhand() {
        return false;
    }

    @Override
    public boolean isAttackGeneric() {
        return false;
    }

    @Override
    public boolean isBlocked() {
        return false;
    }

    @Override
    public boolean isRanged() {
        return false;
    }

    @Override
    public boolean isMelee() {
        return false;
    }

    @Override
    public boolean isMove() {
        return false;
    }

    @Override
    public boolean isTurn() {
        return false;
    }

    @Override
    public Obj getTargetObj() {
        return null;
    }

    @Override
    public GroupImpl getTargetGroup() {
        return null;
    }

    @Override
    public boolean isConstructed() {
        return false;
    }

    @Override
    public void setConstructed(boolean b) {

    }

    @Override
    public void setOnComplete(Runnable b) {

    }

    @Override
    public String getOBJ_TYPE() {
        return null;
    }

    @Override
    public OBJ_TYPE getOBJ_TYPE_ENUM() {
        return null;
    }

    @Override
    public String getProp(String string) {
        return null;
    }

    @Override
    public void setParam(PARAMETER param, int i) {

    }

    @Override
    public void toBase() {

    }

    @Override
    public Integer getId() {
        return null;
    }

    @Override
    public void init() {

    }

    @Override
    public Integer getCounter(String value_ref) {
        return null;
    }

    @Override
    public void removeCounter(String name) {

    }

    @Override
    public List<IActiveObj> getActives() {
        return null;
    }

    @Override
    public List<AbilityObj> getPassives() {
        return null;
    }

    @Override
    public void construct() {

    }

    @Override
    public boolean setCounter(String name, int defaultValue) {
        return false;
    }

    @Override
    public boolean modifyCounter(String name, int defaultValue) {
        return false;
    }

    @Override
    public Integer getIntParam(PARAMETER param, boolean base) {
        return null;
    }

    @Override
    public void setProperty(PROPERTY name, String value) {

    }

    @Override
    public boolean modifyParameter(PARAMETER param, Number amount) {
        return false;
    }

    @Override
    public boolean modifyParamByPercent(PARAMETER param, int perc, boolean base) {
        return false;
    }

    @Override
    public boolean multiplyParamByPercent(PARAMETER param, int perc, boolean base) {
        return false;
    }

    @Override
    public ObjType getType() {
        return null;
    }

    @Override
    public String getProperty(PROPERTY prop, boolean base) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean checkBool(STD_BOOLS bool) {
        return false;
    }

    @Override
    public boolean checkSingleProp(PROPERTY PROP, String value) {
        return false;
    }

    @Override
    public boolean checkProperty(PROPERTY PROP, String value) {
        return false;
    }

    @Override
    public boolean modifyParameter(PARAMETER param, Number amount, Integer minmax) {
        return false;
    }

    @Override
    public boolean isFull(PARAMETER p) {
        return false;
    }

    @Override
    public String getParam(PARAMETER param) {
        return null;
    }

    @Override
    public Integer getIntParam(PARAMETER param) {
        return null;
    }

    @Override
    public boolean isMine() {
        return false;
    }

    @Override
    public void run() {

    }

    @Override
    public void setCancelled(Boolean c) {

    }

    @Override
    public Boolean isCancelled() {
        return null;
    }

    @Override
    public boolean activatedOn(Ref ref) {
        return false;
    }

    @Override
    public boolean activate() {
        return false;
    }

    @Override
    public boolean canBeActivated(Ref ref) {
        return false;
    }

    @Override
    public boolean isInterrupted() {
        return false;
    }
}
