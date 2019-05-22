package eidolons.game.module.dungeoncrawl.objects;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.Spell;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.ItemFactory;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.objects.InteractiveObjMaster.INTERACTION;
import eidolons.game.module.herocreator.logic.HeroLevelManager;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.type.ObjType;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;

import java.util.List;

/**
 * Created by JustMe on 10/10/2018.
 */
public class InteractiveObjMaster extends DungeonObjMaster<INTERACTION> {
    public InteractiveObjMaster(DungeonMaster dungeonMaster) {
        super(dungeonMaster);
    }

    public static INTERACTIVE_OBJ_TYPE chooseTypeForInteractiveObj(ObjType type) {
        if (DataManager.getType(getConsumableItemName(type.getName()), DC_TYPE.ITEMS)!=null ) {
            return INTERACTIVE_OBJ_TYPE.CONSUMABLE;
        }
        if (type.getName().contains("Key")) {
            return INTERACTIVE_OBJ_TYPE.KEY;
        }
        return INTERACTIVE_OBJ_TYPE.RUNE;
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


        Ref ref = obj.getRef();
        ref.setTarget(obj.getId());
        ref.setID(KEYS.ITEM, obj.getId());
        obj.getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.INTERACTIVE_OBJ_USED, ref));

        boolean off = obj.isOff();
        if (off) {
            return;
        }
        //check scripts, throw event
        switch (type) {
            case KEY:
                pickup(obj, unit);
                break;

            case RUNE:
            case MAGE_CIRCLE:
                //random spell?
                doMagic(obj, unit);
                obj.kill(unit, false, false);
                break;
            case MECHANISM:
                break;
            case BUTTON:
                break;
            case LEVER:
                break;
            case CONSUMABLE:
                pickup(obj, unit);
                break;
        }
        obj.setOff(!obj.isOff());
//        if (!off) {
//            GuiEventManager.trigger(GuiEventType.INTERACTIVE_OBJ_ON, obj);
//        } else {
//            GuiEventManager.trigger(GuiEventType.INTERACTIVE_OBJ_OFF, obj);
//        }
    }

    private void pickup(InteractiveObj obj, Unit unit) {
        DC_HeroItemObj item = createItemFromObj(obj);
        if (!unit.addItemToInventory(item)){
            return;
        }
        obj.kill(unit, false, false);

        Ref ref = unit.getRef().getCopy();
        ref.setTarget(obj.getId());
        ref.setID(KEYS.ITEM, obj.getId());
        obj.getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.INTERACTIVE_OBJ_PICKED_UP, ref));

        ref.setObj(KEYS.ITEM, item);
        ref.setObj(KEYS.TARGET, item);
        unit .getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.ITEM_ACQUIRED, ref));
    }

    private static DC_HeroItemObj createItemFromObj(InteractiveObj obj) {
        String name =getConsumableItemName(obj.getName());

        return ItemFactory.createItemObj(name, DC_TYPE.ITEMS,  true);
    }
    public static String getConsumableItemName(String name) {
        if (name.contains("Key")){
            //TODO one time vs permanent?
            return  name  ;
        }
        return  name +" " + StringMaster.wrapInParenthesis("Consumable");
    }

    private void doMagic(InteractiveObj obj, Unit unit) {
        Ref ref = obj.getRef();
        ref.setTarget(obj.getId());
        ref.setID(KEYS.ITEM, obj.getId());
        obj.getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.INTERACTIVE_OBJ_MAGIC_USED, ref));
        if (RandomWizard.chance(80)) {
        EUtils.showInfoText("Ancient lore has lessons to teach you...");
        HeroLevelManager.addXp(unit, RandomWizard.getRandomIntBetween(5, 5*unit.getLevel()));
            return;
        }
        String name = RandomWizard.getRandomListObject(ListMaster.toStringList(
         "Arcane Bolt", "Fire Bolt", "Celestial Bolt", "Death Bolt", "Shadow Bolt",
         "Feral Impulse", "Scare", "Hallucinations", "Cure", "Heal",
         "Warp Shock", "Cripple", "Wraith Touch", "Fire Ball", "Shock Grasp",
         "Cure", "Cure", "Cure", "Cure", "Cure",
         "Fortify", "Touch of Warp", "Cure", "Cure", "Cure"
        )).toString();
        ObjType type = DataManager.getType(name, DC_TYPE.SPELLS);
        obj.setParam(PARAMS.SPELLPOWER, RandomWizard.getRandomIntBetween(20, 50));
        EUtils.showInfoText("Ancient spell awakens... the " + name);

        if (type == null) {
            return;
        }
        ref.setTarget(unit.getId());
        ref.setSource(obj.getId());
        Spell spell = new Spell(type, DC_Player.NEUTRAL, obj.getGame(), ref);
        spell.activateOn(unit);
    }

    public enum INTERACTION implements DUNGEON_OBJ_ACTION {
        USE,

    }

    public enum INTERACTIVE_OBJ_TYPE {
        RUNE,
        MAGE_CIRCLE,
        MECHANISM,

        BUTTON,
        LEVER, CONSUMABLE, KEY,


    }
}
