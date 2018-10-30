package eidolons.game.module.dungeoncrawl.objects;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_SpellObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.core.EUtils;
import eidolons.game.module.dungeoncrawl.objects.InteractiveObjMaster.INTERACTION;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;

import java.util.List;

/**
 * Created by JustMe on 10/10/2018.
 */
public class InteractiveObjMaster extends DungeonObjMaster<INTERACTION> {
    public enum INTERACTION implements DUNGEON_OBJ_ACTION {
        USE,

    }
    public InteractiveObjMaster(DungeonMaster dungeonMaster) {
        super(dungeonMaster);
    }

    @Override
    protected boolean actionActivated(INTERACTION sub, Unit unit, DungeonObj obj) {
        interact((InteractiveObj) obj, unit);
        return true;
    }

    @Override
    public List<DC_ActiveObj> getActions(DungeonObj obj, Unit unit) {
        return new ListMaster<DC_ActiveObj>().toList_(createAction(INTERACTION.USE, unit, obj));
    }

    @Override
    public void open(DungeonObj obj, Ref ref) {

    }

    @Override
    public DC_ActiveObj getDefaultAction(Unit source, DungeonObj target) {
        return createAction(INTERACTION.USE, source, target);
    }

    public void interact(InteractiveObj obj, Unit unit) {
        //sound
        INTERACTIVE_OBJ_TYPE type = obj.getTYPE();

        boolean off = obj.isOff();
        if (off) {
            return;
        }
        //check scripts, throw event
        switch (type) {
            case RUNE:
            case MAGE_CIRCLE:
                //random spell?
                doMagic(obj,unit);
                obj.kill(unit, false, false);
                break;
            case MECHANISM:
                break;
            case BUTTON:
                break;
            case LEVER:
                break;
        }
        obj.setOff(!obj.isOff());
        if (!off) {
            GuiEventManager.trigger(GuiEventType.INTERACTIVE_OBJ_ON, obj);
        } else {
            GuiEventManager.trigger(GuiEventType.INTERACTIVE_OBJ_OFF, obj);
        }
    }

    private void doMagic(InteractiveObj obj, Unit unit) {
        String name = RandomWizard.getRandomListObject(ListMaster.toStringList(
         "Arcane Bolt", "Fire Bolt", "Celestial Bolt","Death Bolt","Shadow Bolt",
         "Feral Impulse", "Scare", "Hallucinations", "Cure", "Heal",
         "Warp Shock", "Cripple", "Wraith Touch", "Fire Ball", "Shock Grasp",
         "Cure", "Cure", "Cure", "Cure", "Cure",
         "Fortify", "Touch of Warp", "Cure", "Cure", "Cure"
        )).toString();
        ObjType type = DataManager.getType(name, DC_TYPE.SPELLS);
        if (type == null) {
            return;
        }
        obj.setParam(PARAMS.SPELLPOWER, RandomWizard.getRandomIntBetween(20, 50));
        EUtils.showInfoText("Ancient spell awakens... the " + name);
        Ref ref = obj.getRef();
        ref.setTarget(unit.getId());
        ref.setSource(obj.getId());
        DC_SpellObj spell = new DC_SpellObj(type, DC_Player.NEUTRAL, obj.getGame(), ref);
        spell.activateOn(unit);
    }

    public enum INTERACTIVE_OBJ_TYPE {
        RUNE,
        MAGE_CIRCLE,
        MECHANISM,

        BUTTON,
        LEVER,


    }
}
