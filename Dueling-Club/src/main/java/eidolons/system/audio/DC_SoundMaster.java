package eidolons.system.audio;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.Spell;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.rules.magic.ChannelingRule;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.anims.Anim;
import eidolons.libgdx.anims.construct.AnimResourceFinder;
import eidolons.libgdx.anims.construct.AnimConstructor.ANIM_PART;
import eidolons.libgdx.anims.CompositeAnim;
import eidolons.libgdx.audio.SoundPlayer;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.system.content.ContentGenerator;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.SoundOptions.SOUND_OPTION;
import main.content.CONTENT_CONSTS;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums;
import main.content.enums.entity.HeroEnums;
import main.content.enums.entity.HeroEnums.GENDER;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.UnitEnums;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.PathUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;
import main.system.launch.CoreEngine;
import main.system.sound.Player;
import main.system.sound.SoundMaster;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static eidolons.libgdx.anims.construct.AnimConstructor.ANIM_PART.IMPACT;

//import main.game.logic.battle.player.Player;

public class DC_SoundMaster extends SoundMaster {

    public static final PROPERTY[] propsExact = {
            G_PROPS.NAME, G_PROPS.SPELL_SUBGROUP,
            G_PROPS.SPELL_GROUP, G_PROPS.ASPECT,
    };
    public static final PROPERTY[] props = {
            PROPS.DAMAGE_TYPE,
            G_PROPS.SPELL_TYPE,
    };
    private static SoundPlayer soundPlayer;
    private SoundController controller;
    private DungeonScreen screen;

    public DC_SoundMaster(DungeonScreen screen) {
        this.screen = screen;
        controller = new SoundController(this);
    }

    public static void playRangedAttack(DC_WeaponObj weapon) {
        // TODO double weapon sound
        if (weapon.getWeaponGroup() == ItemEnums.WEAPON_GROUP.CROSSBOWS) {
            getPlayer().playRandomSoundVariant("soundsets/weapon/crossbow/" + weapon.getWeaponSize(), false);
        } else if (weapon.getWeaponGroup() == ItemEnums.WEAPON_GROUP.BOLTS) {
            getPlayer().playRandomSoundVariant("soundsets/weapon/bow/" + weapon.getWeaponSize(), false);
        } else {
            getPlayer().playRandomSoundVariant("soundsets/weapon/throw/" + weapon.getWeaponSize(), false);
        }

    }

    public static void play(String s) {
        getSoundPlayer().play(s);
    }

    public static void playTurnSound(BattleFieldObject unit) {
//         setPositionFor(unit.getCoordinates());
//        getSoundPlayer().play(STD_SOUNDS.SLING.getPath());

        playMoveSound(unit);
    }

    public static void playMoveSound(BattleFieldObject unit) {
        if (!unit.isMine())
            if (!unit.isPlayerDetected())
                if (RandomWizard.chance(99))
                    return;
        if (OptionsMaster.getSoundOptions().getBooleanValue(SOUND_OPTION.FOOTSTEPS_OFF)) {
            return;
        }
        setPositionFor(unit.getCoordinates());
//        unit.getGame().getDungeon().isSurface()
        if (unit.isPale() || unit .isImmaterial()) {
            if (!unit.isMine()) {
                getPlayer().playEffectSound(SOUNDS.ALERT, unit);
            } else
            getPlayer().playRandomSoundFromFolder(
                    "std/move pale/");
        } else
        getPlayer().playRandomSoundFromFolder(
                "std/move/");

    }

    public static void playMissedSound(BattleFieldObject attacker, DC_WeaponObj attackWeapon) {

        getPlayer().playRandomSoundVariant("soundsets/combat/miss/", false);

    }

    public static CONTENT_CONSTS.SOUNDSET getSoundset(Obj obj) {

        CONTENT_CONSTS.SOUNDSET soundset = new EnumMaster<CONTENT_CONSTS.SOUNDSET>().retrieveEnumConst(CONTENT_CONSTS.SOUNDSET.class,
                obj.getProperty(G_PROPS.SOUNDSET));
        if (soundset != null) {
            return soundset;
        }
        if (!(obj instanceof Unit)) {
            return null;
        }
        Unit unit = (Unit) obj;
//        if (unit.getRace()== HeroEnums.RACE.HUMAN) {
//            return CONTENT_CONSTS.SOUNDSET.HUMAN;
//        }
//        if (unit.getRace()== HeroEnums.RACE.DWARF) {
//            return CONTENT_CONSTS.SOUNDSET.DWARF;
//        }
//        if (unit.checkClassification(UnitEnums.CLASSIFICATIONS.WRAITH)) {
//            return CONTENT_CONSTS.SOUNDSET.WRAITH;
//        }
        for (CONTENT_CONSTS.SOUNDSET value : CONTENT_CONSTS.SOUNDSET.values()) {
            switch (value) {
                case dark_elf:
                    break;
                case bone_knight:
                    if (unit.checkClassification(UnitEnums.CLASSIFICATIONS.UNDEAD))
                        break;
                case dwarf:
                    if (unit.getRace() == HeroEnums.RACE.DWARF)
                        return value;
                    if (unit.getUnitGroup() == UnitEnums.UNIT_GROUPS.DWARVES)
                        return value;
                    break;
                case knight:
                    break;
                case lad:
                    if (unit.getRace() == HeroEnums.RACE.HUMAN)
                        break;
                case skeleton:
                    break;
                case skeleton_archer:
                    break;
                case thug:
                        if (unit.getUnitGroup() == UnitEnums.UNIT_GROUPS.BANDITS)
                            return value;
                            break;
                case wraith:
                    if (unit.checkClassification(UnitEnums.CLASSIFICATIONS.WRAITH))
                        return value;
                    break;
                case zombie:
                    break;
            }

        }

        return CONTENT_CONSTS.SOUNDSET.dwarf;
    }

    public static String getSoundsetPath(Unit unit) {


        return "";


    }

    public static String getEffectSoundName(SOUNDS sound_type) {
        switch (sound_type) {
            case TAUNT:
            case THREAT:
            case ALERT:
                return "warned";
            case SPOT:
                return "spoted ";


        }
        return sound_type.name();
    }

    public static void playEffectSound(SOUNDS sound_type, Obj obj) {
        setPositionFor(obj.getCoordinates());
        getPlayer().playEffectSound(sound_type, obj);
    }

    public static void playParrySound(BattleFieldObject attacked, DC_WeaponObj attackWeapon) {
        // TODO double weapon sound
        setPositionFor(attacked.getCoordinates());
//        DC_WeaponObj parryWeapon = attacked.getActiveWeapon(true);
//        getPlayer().playRandomSoundFromFolder("soundsets/" + "weapon/"
//                + attackWeapon.getWeaponGroup() + "/");
        getPlayer().playRandomSoundVariant("soundsets/" + "weapon/" + "sword/", true);
    }

    private static void setPositionFor(Coordinates c) {
        if (getSoundPlayer() != null)
            try {
                getSoundPlayer().setPosition(
                        GridMaster.getCenteredPos(c));
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
    }

    public static void playBlockedSound(Obj attacker, Obj attacked, DC_WeaponObj shield,
                                        DC_WeaponObj weaponObj, Integer blockValue, Integer damage) {

        if (shield.getWeaponSize() == ItemEnums.WEAPON_SIZE.TINY
                || shield.getWeaponSize() == ItemEnums.WEAPON_SIZE.SMALL) {
            if (RandomWizard.chance(75, new Random())) {
                getPlayer().playRandomSoundVariant(
                        "armor/buckler/s " + weaponObj.getDamageType(), false);
            } else {
                getPlayer().playRandomSoundVariant("soundsets/" + "armor/buckler/s", false);
            }
        } else {
            if (weaponObj != null) {
                if (RandomWizard.chance(75)) {
                    getPlayer().playRandomSoundVariant("soundsets/" + "armor/shield/s "
                            + weaponObj.getDamageType(), false);
                } else {
                    getPlayer().playRandomSoundVariant("soundsets/" + "armor/shield/s", false);
                }
            }
        }

    }

    public static void playAttackImpactSound(DC_WeaponObj weapon, final BattleFieldObject attacker,
                                             final BattleFieldObject attacked, Integer final_amount, int blocked) {
        // TODO getDamageSeverity(fin
        String armor_type = attacked.getProperty(PROPS.OBJECT_ARMOR_TYPE);
        // int volume = 100;

        boolean natural = false;

        // cache = attackImpactSoundFilesCache.getOrCreate(armor_type+damage_type) TODO
        if (armor_type.isEmpty() && blocked > 0) {
//            if (attacked.getRef().getObj(KEYS.ARMOR) != null) {
//                armor_type = attacked.getRef().getObj(KEYS.ARMOR).getProperty("ARMOR_GROUP");
//            }
        } else {
            natural = true;
        }

        if (armor_type.isEmpty()) {
            armor_type = ContentGenerator.getNaturalArmorTypeForUnit(attacked);
        }

        if (!armor_type.isEmpty()) {
            String damage_type = weapon.getProperty("damage_type");
            if (damage_type.isEmpty()) {
                damage_type = attacker.getProperty("damage_type");
            }
            playArmorSound(attacked, armor_type, damage_type, natural);
        }

        playWeaponSound(weapon);
    }

    public static void playSoundForModeToggle(boolean on_off, DC_ActiveObj action, String mode) {
        // STD_ACTION_MODES
        if (on_off) {
            getPlayer().playStandardSound(STD_SOUNDS.ON_OFF);
        }// soundPlayer.playStandardSound(STD_SOUNDS.CHAIN);
        else {
            getPlayer().playStandardSound(STD_SOUNDS.ButtonDown);
        }

    }

    private static void playWeaponSound(DC_WeaponObj weapon) {
        // TODO Auto-generated method stub
        switch (weapon.getWeaponType()) {

            case BLADE:
                getPlayer().playRandomSoundVariant("soundsets/" + "weapon/" + "sword/", true);
                break;
            case AXE:
                getPlayer().playRandomSoundVariant("soundsets/" + "weapon/" + "axe/", true);
                break;
            case BLUNT:
                getPlayer().playRandomSoundVariant("soundsets/" + "weapon/" + "blunt/", true);
                break;
            case POLE_ARM:
                getPlayer().playRandomSoundVariant("soundsets/" + "weapon/" + "spear/", true);
                break;
            case MAGICAL:
                getPlayer().playRandomSoundVariant("soundsets/" + "combat/" + "block/", true);
                break;
            case SHIELD:
                break;
            case RANGED:
                getPlayer().playRandomSoundVariant("soundsets/" + "weapon/" + "sword/", true);
                break;
            case AMMO:
                break;
            case NATURAL:
                break;
        }
    }

    private static void playArmorSound(final BattleFieldObject attacked, String armor_type,
                                       String damage_type, boolean natural) {
        String path = SoundMaster.getPath() + "soundsets/" + (natural ? "obj/" : "armor/")
                + armor_type;
        File folder = FileManager.getFile(path);
        if (!folder.isDirectory()) {
            return;
        }
        // filter -
        List<File> defaultSounds = FileManager.findFiles(folder, armor_type.charAt(0) + "", true,
                true);
        if (defaultSounds.isEmpty()) {
            defaultSounds = FileManager.findFiles(folder, armor_type, true, true);
        }
        List<File> specialSounds = FileManager.findFiles(folder, damage_type, true, false);
        List<File> files = new ArrayList<>(defaultSounds);
        files.addAll(specialSounds);
        // double chance
        files.addAll(specialSounds);
        if (files.isEmpty()) {
//            if (attacked.isLiving()) {
//            }

            return;
        }
        File file = FileManager.getRandomFile(files);
        getPlayer().play(file);
    }

    public static void bindEvents() {
        //TODO ON SEPARATE THREAD!!!!
          GuiEventManager.bind(GuiEventType.ANIMATION_STARTED, p -> {
            Anim anim = (Anim) p.get();
            DC_ActiveObj activeObj = (DC_ActiveObj) anim.getActive();
            try {
                playAnimStartSound(activeObj, anim.getPart());
            } catch (Exception e) {
//                main.system.ExceptionMaster.printStackTrace(e);
            }
        });
        GuiEventManager.bind(GuiEventType.COMPOSITE_ANIMATION_STARTED, p -> {
            CompositeAnim anim = (CompositeAnim) p.get();
            DC_ActiveObj activeObj = (DC_ActiveObj) anim.getActive();
            try {
                playActionStartSound(activeObj);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        });
    }

    private static void playActionStartSound(DC_ActiveObj activeObj) {
        //TODO
    }

    private static void playAnimStartSound(DC_ActiveObj activeObj, ANIM_PART part) {
        if (activeObj instanceof Spell)
            switch (part) {
                case PRECAST:
                case CAST:
                case RESOLVE:
                case MISSILE:
                case IMPACT:
                case AFTEREFFECT:
                    playNow(getActionEffectSoundPath((Spell) activeObj, part));
                    return;
            }
        if (activeObj.isMove()) {
            return;
        }
        switch (part) {
            case PRECAST:
                ChannelingRule.playChannelingSound(activeObj, activeObj.getOwnerUnit().getGender() == GENDER.FEMALE);
                getPlayer().playEffectSound(SOUNDS.PRECAST, activeObj);
                break;
            case CAST:
                getPlayer().playEffectSound(SOUNDS.CAST, activeObj);
                break;
            case RESOLVE:
                getPlayer().playEffectSound(SOUNDS.RESOLVE, activeObj);
                break;
            case MISSILE:
                getPlayer().playEffectSound(SOUNDS.EFFECT, activeObj);
                break;
            case IMPACT:
                playImpact(activeObj);
                break;
            case AFTEREFFECT:
                break;
        }
    }

    public static void playNow(String sound) {
        getPlayer().playNow(sound);
    }

    public static void preconstructEffectSounds() {
        for (ObjType type : DataManager.getTypes(DC_TYPE.SPELLS)) {
            Spell active = new Spell(type, DC_Player.NEUTRAL, DC_Game.game, new Ref());
            for (ANIM_PART part : ANIM_PART.values()) {
                preconstructSpell(active, part);
            }
        }
    }

    private static void preconstructSpell(Spell spell, ANIM_PART part) {
        String file = AnimResourceFinder.findResourceForSpell(spell,
                part.toString(), "", true,
                getSpellSoundPath(), false);
        if (file == null) {
            file = AnimResourceFinder.findResourceForSpell(spell,
                    part.toString(), "", false,
                    getSpellSoundPath(), true);
        }
        if (file == null) {
            file = getActionEffectSoundPath(spell, part);
        }
        if (file == null) {
            return;
        }
        file = file.replace(PathFinder.getSoundPath().toLowerCase(), "");
        PROPERTY prop = getProp(part);
        if (prop == null)
            return;
        spell.getType().setProperty(prop, file);
    }

    private static PROPERTY getProp(ANIM_PART part) {
        return ContentValsManager.findPROP("SOUND_" + part);
    }

    private static GenericEnums.DAMAGE_TYPE getDmgType(Spell spell) {
        GenericEnums.DAMAGE_TYPE dmg_type = spell.getDamageType();
        if (dmg_type!=null) {
            return dmg_type;
        }
        switch (spell.getSpellGroup()) {
            case AIR:
                return GenericEnums.DAMAGE_TYPE.LIGHTNING;
            case WATER:
                return GenericEnums.DAMAGE_TYPE.ACID;
            case EARTH:
                return GenericEnums.DAMAGE_TYPE.BLUDGEONING;
            case CONJURATION:
            case ENCHANTMENT:
            case SORCERY:
                return GenericEnums.DAMAGE_TYPE.ARCANE;
            case WITCHERY:
            case SHADOW:
                return GenericEnums.DAMAGE_TYPE.SHADOW;
            case PSYCHIC:
                return GenericEnums.DAMAGE_TYPE.PSIONIC;
            case AFFLICTION:
                return GenericEnums.DAMAGE_TYPE.ACID;
            case NECROMANCY:
                return GenericEnums.DAMAGE_TYPE.DEATH;
            case BLOOD_MAGIC:
                return GenericEnums.DAMAGE_TYPE.CHAOS;
            case WARP:
            case DEMONOLOGY:
            case DESTRUCTION:
                return GenericEnums.DAMAGE_TYPE.CHAOS;
            case CELESTIAL:
                return GenericEnums.DAMAGE_TYPE.LIGHT;
            case BENEDICTION:
            case REDEMPTION:
                return GenericEnums.DAMAGE_TYPE.HOLY;
            case FIRE:
                return GenericEnums.DAMAGE_TYPE.FIRE;
            case SYLVAN:
                break;
            case ELEMENTAL:
                break;
            case SAVAGE:
                return GenericEnums.DAMAGE_TYPE.CHAOS;
        }
        return dmg_type;
    }

    private static String getSpellSound(Spell spell, ANIM_PART part) {
        if (part !=IMPACT) {
            return "";
        }
        GenericEnums.DAMAGE_TYPE dmg_type =
                getDmgType(spell);

        return FileManager.getRandomFile(PathFinder.getSoundsetsPath() + "damage/" +
                dmg_type ).getPath();
    }

    private static String getActionEffectSoundPath(Spell spell, ANIM_PART part) {
        if (CoreEngine.isIggDemo()){
            return getSpellSound(spell, part);
        }

        String file = spell.getProperty(getProp(part));
        String identifier;
        String path;
        if (!file.isEmpty()) {
            file = PathFinder.getSoundPath() + file;
            identifier = PathUtils.getLastPathSegment(file);
            path = PathUtils.cropLastPathSegment(file);
        } else {
            identifier = spell.getName();
            path = getSpellSoundPath();
            path = PathUtils.buildPath(path, spell.getAspect().toString(),
                    spell.getSpellGroup().toString(), part.toString());
        }
        int i = 0;
        while (i < 6) {
            i++;
            if (StringMaster.isEmpty(file))
                file =
                        FileManager.findFirstFile(path, identifier, true);
            if (file != null) {
                String corePath = StringMaster.cropFormat(StrPathBuilder.build(path,
                        StringMaster.cropFormat(file)));
                try {
                    file = FileManager.getRandomFilePathVariant(corePath, StringMaster.getFormat(file), false);
                } catch (Exception e) {
//            main.system.ExceptionMaster.printStackTrace(e);
                }
            }
            if (file != null) {
                if (ListMaster.isNotEmpty(main.system.sound.Player.getLastplayed()))
                    if (main.system.sound.Player.getLastplayed().peek().equalsIgnoreCase(file)) {
                        i--;
                        continue;
                    }
                return file;
            }
            if (i % 2 == 1)
                identifier = PathUtils.getLastPathSegment(path);
            else
                path = PathUtils.cropLastPathSegment(path);
        }
        return null;
    }

    private static String getSpellSoundPath() {
        return getPath() + "soundsets/spells/";
    }

    private static void playImpact(DC_ActiveObj activeObj) {
        if (activeObj.isAttackAny()) {
            playAttackImpactSound(activeObj.getActiveWeapon(), activeObj.getOwnerUnit(), (Unit) activeObj.getRef().getTargetObj(),
                    activeObj.getIntParam(PARAMS.DAMAGE_LAST_DEALT),
                    activeObj.getIntParam(PARAMS.DAMAGE_LAST_AMOUNT) - activeObj.getIntParam(PARAMS.DAMAGE_LAST_DEALT)
            );
        } else {
            getPlayer().playEffectSound(SOUNDS.IMPACT, activeObj);
        }
//        AudioMaster.getInstance().playRandomSound();
    }

    public static SoundPlayer getSoundPlayer() {
        return soundPlayer;
    }

    private static main.system.sound.Player getPlayer() {
        if (soundPlayer == null)
            return new Player();
        return soundPlayer;
    }

    public static void playDamageSound(GenericEnums.DAMAGE_TYPE damageType) {
        playRandomSoundVariant(PathFinder.getSoundsetsPath()+"damage/"+damageType.getName(), true);
    }

    public void doPlayback(float delta) {
        if (soundPlayer == null)
            soundPlayer = new SoundPlayer(screen);
        soundPlayer.doPlayback(delta);
    }
}
