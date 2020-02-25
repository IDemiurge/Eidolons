package eidolons.game.module.dungeoncrawl.objects;

import eidolons.ability.ignored.dialog.TownPortalEffect;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.Spell;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.ItemFactory;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.meta.igg.event.TipMessageSource;
import eidolons.game.core.EUtils;
import eidolons.game.module.dungeoncrawl.objects.InteractiveObjMaster.INTERACTION;
import eidolons.game.module.herocreator.logic.HeroLevelManager;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import eidolons.system.audio.MusicMaster;
import main.content.DC_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.ActiveObj;
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
    private InscriptionMaster inscriptionMaster;

    public InteractiveObjMaster(DungeonMaster dungeonMaster) {
        super(dungeonMaster);
    }

    public InscriptionMaster getInscriptionMaster() {
        if (inscriptionMaster == null) {
            inscriptionMaster = new InscriptionMaster(
                    dungeonMaster.getDungeonLevel().getLevelName());
        }
        return inscriptionMaster;
    }

    public static INTERACTIVE_OBJ_TYPE chooseTypeForInteractiveObj(ObjType type) {
        if (type.getName().contains("Inscription")) {
            return INTERACTIVE_OBJ_TYPE.INSCRIPTION;
        }
        if (type.getName().contains("Fungi")) {
            return INTERACTIVE_OBJ_TYPE.CONSUMABLE;
        }
        if (type.getName().contains("Key")) {
            return INTERACTIVE_OBJ_TYPE.KEY;
        }
        if (type.getProperty(G_PROPS.BF_OBJECT_CLASS).contains("Key")) {
            return INTERACTIVE_OBJ_TYPE.KEY;
        }
        if (type.getProperty(G_PROPS.BF_OBJECT_CLASS).contains("Glyph")) {
            return INTERACTIVE_OBJ_TYPE.MAGE_CIRCLE;
        }
        if (type.getProperty(G_PROPS.BF_OBJECT_CLASS).contains("Consumable")) {
            return INTERACTIVE_OBJ_TYPE.CONSUMABLE;
        }
        if (type.getProperty(G_PROPS.BF_OBJECT_GROUP).contains("Light Emitter")) {
            return INTERACTIVE_OBJ_TYPE.LIGHT_EMITTER;
        }

        if (DataManager.getType(getConsumableItemName(type.getName()), DC_TYPE.ITEMS) != null) {
            return INTERACTIVE_OBJ_TYPE.CONSUMABLE;
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
        boolean off = obj.isOff();
        if (off) {
            if (isToggleOffForObj(obj)){
                obj.setOff(false);
            }
            return;
        }
        boolean used = obj.isUsed();
        if (!used)
            obj.getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.INTERACTIVE_OBJ_USED, ref));

        //check scripts, throw event
        switch (type) {
            case INSCRIPTION:
                message(obj, unit);
                break;
            case KEY:
                pickup(obj, unit);
                break;
            case MAGE_CIRCLE:
                boolean kill = doSpecial(obj, unit);
                if (kill) {
                    obj.kill(unit, false, false);
                }
                break;

            case RUNE:
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
        obj.setUsed(true);
        if (isToggleOffForObj(obj)){
            obj.setOff(true);
        }
    }

    private boolean isToggleOffForObj(InteractiveObj obj) {
        switch (obj.getTYPE()) {
            case LIGHT_EMITTER:
                return true;
        }
        return false;
    }

    private void message(InteractiveObj obj, Unit unit) {
        String src = getInscriptionMaster().getTextForInscription(obj);
        //std tip with image?
        String image = src.split("|")[0];
        String text = src;
        if (!TextureCache.isImage(image)) {
            image = Images.SPELLBOOK;
        } else {
            text = src.split("|")[1];
        }

        MusicMaster.playMoment(RandomWizard.random()? MusicMaster.MUSIC_MOMENT.TOWN : MusicMaster.MUSIC_MOMENT.SAD);
        GuiEventManager.trigger(GuiEventType.TIP_MESSAGE, new TipMessageSource(
                text, image, "Continue", false, () ->
        {
        }));
    }

    private boolean doSpecial(InteractiveObj obj, Unit unit) {
        for (ActiveObj active : obj.getActives()) {
            Ref ref = obj.getRef().getCopy();
            ref.setTarget(unit.getId());
            active.activatedOn(ref);
            return true;
        }
        if (obj.getName().equalsIgnoreCase("gateway glyph")) {
            //TODO igg demo hack

            new TownPortalEffect().apply(new Ref(unit));
            return true;
        }
        return false;
    }

    private void pickup(InteractiveObj obj, Unit unit) {
        DC_HeroItemObj item = createItemFromObj(obj);
        if (!unit.addItemToInventory(item)) {
            return;
        }
        obj.kill(unit, false, false);

        Ref ref = unit.getRef().getCopy();
        ref.setTarget(obj.getId());
        ref.setID(KEYS.ITEM, obj.getId());
        obj.getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.INTERACTIVE_OBJ_PICKED_UP, ref));

        ref.setObj(KEYS.ITEM, item);
        ref.setObj(KEYS.TARGET, item);
        unit.getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.ITEM_ACQUIRED, ref));
    }

    private static DC_HeroItemObj createItemFromObj(InteractiveObj obj) {
        String name = getConsumableItemName(obj.getName());

        return ItemFactory.createItemObj(name, DC_TYPE.ITEMS, true);
    }

    public static String getConsumableItemName(String name) {
        if (name.contains("Key")) {
            //TODO one time vs permanent?
            return name.replace("Hanging ", "");
        }
        return name + " " + StringMaster.wrapInParenthesis("Consumable");
    }

    private void doMagic(InteractiveObj obj, Unit unit) {
        Ref ref = obj.getRef();
        ref.setTarget(obj.getId());
        ref.setID(KEYS.ITEM, obj.getId());
        obj.getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.INTERACTIVE_OBJ_MAGIC_USED, ref));
        if (RandomWizard.chance(80)) {
            EUtils.showInfoText("Ancient lore has lessons to teach you...");
            HeroLevelManager.addXp(unit, RandomWizard.getRandomIntBetween(5, 5 * unit.getLevel()));
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
        LEVER, CONSUMABLE, KEY, INSCRIPTION, LIGHT_EMITTER,


    }
}
