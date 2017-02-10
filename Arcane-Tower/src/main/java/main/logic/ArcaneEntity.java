package main.logic;

import main.ArcaneTower;
import main.content.DC_ContentManager;
import main.content.parameters.PARAMETER;
import main.content.properties.PROPERTY;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.enums.StatEnums.TASK_STATUS;
import main.game.player.Player;
import main.io.PromptMaster;
import main.logic.ArcaneRef.AT_KEYS;
import main.session.Session;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.TimeMaster;
import main.time.ZeitMaster;
import main.time.ZeitMaster.STATUS_MARK;

import java.util.LinkedList;
import java.util.List;

public abstract class ArcaneEntity extends Entity {

    public ArcaneEntity(ObjType type) {
        super(type, Player.NEUTRAL, ArcaneTower.getSimulation(), ArcaneTower.getRef());
    }

    @Override
    public String toString() {
        return getName();
    }

    public void beforeSave() {
        setParam(AT_PARAMS.TIME_TOTAL_ACTIVE, ZeitMaster.getTotalTimeFromTimeMarks(
                getProperty(AT_PROPS.TIME_MARKS), STATUS_MARK.STARTED.toString()), true);
        setParam(AT_PARAMS.TIME_TOTAL_PAUSED, ZeitMaster.getTotalTimeFromTimeMarks(
                getProperty(AT_PROPS.TIME_MARKS), STATUS_MARK.PAUSED.toString()), true);

    }

    @Override
    public boolean isTypeLinked() {
        return true;
    }

    public void modified() {
        ArcaneTower.setNonTest(this);
        ArcaneTower.saveEntity(this);
        type.setParam(AT_PARAMS.TIME_LAST_MODIFIED, TimeMaster.getTime() + "");
        toBase();
        ArcaneTower.getSessionWindow(this).refresh();
    }

    public void addChildren() {
        PromptMaster.add(this);

    }

    @Override
    public void init() {
        DC_ContentManager.addDefaultValues(this);
        ZeitMaster.initTimeOfCreation(getType());
        toBase();
        getRef().getGame().add(this);
    }

    @Override
    public void setProperty(PROPERTY prop, String value) {
        super.setProperty(prop, value);
        type.setProperty(prop, value);
    }

    @Override
    public void setParam(PARAMETER param, int i) {
        super.setParam(param, i);
        type.setParam(param, i);
    }

    @Override
    public AT_OBJ_TYPE getOBJ_TYPE_ENUM() {
        return (AT_OBJ_TYPE) super.getOBJ_TYPE_ENUM();
    }

    public Session getSession() {
        return ArcaneTower.getSessionWindow(this).getSession();
        // return (Session) getRef().getObj(AT_KEYS.SESSION);
    }

    public ArcaneEntity getParent() {
        return getRef().getObj(
                new EnumMaster<AT_KEYS>().retrieveEnumConst(AT_KEYS.class, getOBJ_TYPE_ENUM()
                        .getParentType().toString()));
    }

    public boolean canHaveChildren() {
        return true;
    }

    @Override
    public ArcaneRef getRef() {
        return (ArcaneRef) super.getRef();
    }

    @Override
    public void setRef(Ref ref) {
        // super.setRef(ref);
        this.ref = ref;
        this.ref = new ArcaneRef(this.ref);
    }

    public List<ArcaneEntity> addChildrenTo(List<ArcaneEntity> branch) {
        for (ArcaneEntity sub : getChildren()) {
            sub.addChildrenTo(branch);
        }
        branch.add(this);
        return branch;
    }

    public List<? extends ArcaneEntity> getChildren() {
        return new LinkedList<>();
    }

    public boolean isPending() {
        return ArcaneTower.isTestMode()
                || checkProperty(AT_PROPS.TASK_STATUS, TASK_STATUS.ACTIVE.name())
                || checkProperty(AT_PROPS.TASK_STATUS, TASK_STATUS.PENDING.name());
    }

    public int getGlory() {
        return getIntParam(AT_PARAMS.GLORY);
    }

}
