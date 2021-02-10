package eidolons.game.netherflame.boss.logic;

import eidolons.entity.TypeCombiner;
import eidolons.game.netherflame.boss.BossHandler;
import eidolons.game.netherflame.boss.BossManager;
import eidolons.game.netherflame.boss.BossModel;
import eidolons.game.netherflame.boss.logic.entity.BossUnit;
import main.content.DC_TYPE;
import main.content.enums.system.MetaEnums;
import main.data.DataManager;
import main.entity.type.ObjType;

public abstract class BossCycle<T extends BossModel> extends BossHandler<T> {
    /*
    controls ATB freezing, PHASES
    entity is DIFFERENT in active/passive
    yet atb is retained and no remove/add is possible.
    It's exactly more like applying type to it... or even 'buff' ?
     */
    BOSS_TYPE[] cycle;
    private int index;

    public BossCycle(BossManager<T> manager) {
        super(manager);
    }

    @Override
    public void init() {
        cycle = getModel().getCycle();
    }

    public void battleStarted() {
        index = -1;
        BOSS_TYPE type = getNextEntity();
        for (BOSS_TYPE boss_type : cycle) {
            if (boss_type == type) {
                unfreeze(type);
            } else {
                freeze(type);
            }
        }
    }

    public void entityBecomesActive(BOSS_TYPE type) {
        //atb reached 100
        BossUnit entity = getEntity(type);
        //caster would only just then channel!
    }

    public void entityActed(BOSS_TYPE type) {
        // if (allowParallelActive) {
        // }
        BOSS_TYPE next = getNextEntity();
        unfreeze(next);
        freeze(type);
    }

    private void unfreeze(BOSS_TYPE type) {
        toggleActive(type, true);
        BossUnit entity = getEntity(type);
        entity.removeBuff(getFreezeBuffName());
    }

    private String getFreezeBuffName() {
        return MetaEnums.STD_BUFF_NAME.Disabled.getName();
    }

    private void freeze(BOSS_TYPE type) {
        toggleActive(type, false);
        BossUnit entity = getEntity(type);
       getGame().getManager().getBuffMaster() .applyStdBuff(MetaEnums.STD_BUFF_NAME.Disabled, entity);
        // entity.addBuff();
    }

    private void toggleActive(BOSS_TYPE type, boolean active) {
        BossUnit entity = getEntity(type);
        TypeCombiner.applyType(entity, getSubType(type, active));
        getAnimHandler().toggleActive(type, active);
    }

    public void entityDestroyed(BOSS_TYPE type) {
        //comment, signal to anims
        //enrage the other parts
        //remove from cycle?
    }

    public void entityDisabled(BOSS_TYPE type) {
        /*
        toughness on 0 - force freeze? Apply debuff?
        when and how is toughness restored?
         */
    }

    private BOSS_TYPE getNextEntity() {
        index++;
        if (index >= cycle.length) {
            index = 0;
        }
        return cycle[index];
    }

    private ObjType getSubType(BOSS_TYPE type, boolean active) {
        return DataManager.getType(getModel().getName(type) + (active ? " Active" : ""), DC_TYPE.BOSS);
    }



    public enum BOSS_TYPE {
        melee,
        caster,
        mirror,
        clone,
        environ,
    }
}
