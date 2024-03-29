package eidolons.system.audio;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.content.consts.VisualEnums.ANIM_PART;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.feat.active.Spell;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.unit.Unit;
import eidolons.game.exploration.story.cinematic.Cinematics;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.SoundOptions.SOUND_OPTION;
import eidolons.system.utils.content.ContentGenerator;
import main.content.CONTENT_CONSTS.SOUNDSET;
import main.content.ContentValsManager;
import main.content.enums.GenericEnums;
import main.content.enums.entity.HeroEnums;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.UnitEnums;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.filesys.PathFinder;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.system.PathUtils;
import main.system.auxiliary.*;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;
import main.system.launch.Flags;
import main.system.sound.AudioEnums;
import main.system.sound.Player;
import main.system.sound.SoundMaster;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class DC_SoundMaster extends SoundMaster {
    private static  Player soundPlayer;

    public static  Player getSoundPlayer( ) {
        return soundPlayer;
    }

    public DC_SoundMaster(Player s) {
        SoundController controller = new SoundController(this);
        soundPlayer= s;
    }

    public static void playRangedAttack(WeaponItem weapon) {
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
        getSoundPlayer().play(AudioEnums.STD_SOUNDS.LAMP.getPath());

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
        if (Cinematics.ON)
            return;
        setPositionFor(unit.getCoordinates());
//        unit.getGame().getDungeon().isSurface()
        if ( unit.isImmaterial()) {
            if (!unit.isMine()) {
                getPlayer().playEffectSound(AudioEnums.SOUNDS.ALERT, unit);
            } else
                getPlayer().playRandomSoundFromFolder(
                        "std/move pale/");
        } else {
            if (unit.getIntParam(PARAMS.WEIGHT)<=100) {
                getPlayer().playRandomSoundFromFolder(
                        "std/move/light");
            }
            else
            getPlayer().playRandomSoundFromFolder(
                    "std/move/");
        }

    }

    public static void playMissedSound(BattleFieldObject attacker, WeaponItem attackWeapon) {

        getPlayer().playRandomSoundVariant("soundsets/combat/miss/", false);

    }

    public static SOUNDSET getSoundset(Obj obj) {

        SOUNDSET soundset = new EnumMaster<SOUNDSET>().retrieveEnumConst(SOUNDSET.class,
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
        for (SOUNDSET value : SOUNDSET.values()) {
            switch (value) {
                case dark_elf:
                case zombie:
                case skeleton_archer:
                case skeleton:
                case knight:
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
                case lad:
                    if (unit.getRace() == HeroEnums.RACE.HUMAN)
                        break;
                case thug:
                    if (unit.getUnitGroup() == UnitEnums.UNIT_GROUPS.BANDITS)
                        return value;
                    break;
                case wraith:
                    if (unit.checkClassification(UnitEnums.CLASSIFICATIONS.WRAITH))
                        return value;
                    break;
            }

        }

        return SOUNDSET.dwarf;
    }

    public static String getSoundsetPath(Unit unit) {


        return "";


    }

    public static String getEffectSoundName(AudioEnums.SOUNDS sound_type) {
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

    public static void playEffectSound(AudioEnums.SOUNDS sound_type, Obj obj) {
        setPositionFor(obj.getCoordinates());
        getPlayer().playEffectSound(sound_type, obj);
    }

    public static void playParrySound(BattleFieldObject attacked, WeaponItem attackWeapon) {
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
                getSoundPlayer().setPositionFor(c);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
    }

    public static void playBlockedSound(Obj attacker, Obj attacked, WeaponItem shield,
                                        WeaponItem weaponObj, Integer blockValue, Integer damage) {

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

    public static void playAttackImpactSound(WeaponItem weapon, final BattleFieldObject attacker,
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

    public static void playSoundForModeToggle(boolean on_off, ActiveObj action, String mode) {
        // STD_ACTION_MODES
        if (on_off) {
            getPlayer().playStandardSound(AudioEnums.STD_SOUNDS.ON_OFF);
        }// soundPlayer.playStandardSound(STD_SOUNDS.CHAIN);
        else {
            getPlayer().playStandardSound(AudioEnums.STD_SOUNDS.ButtonDown);
        }

    }

    private static void playWeaponSound(WeaponItem weapon) {
        // TODO Auto-generated method stub
        switch (weapon.getWeaponType()) {

            case BLADE:
            case RANGED:
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
            case NATURAL:
            case AMMO:
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

    public static void playNow(String sound) {
        getPlayer().playNow(sound);
    }


    public static PROPERTY getProp(ANIM_PART part) {
        return ContentValsManager.findPROP("SOUND_" + part);
    }

    private static GenericEnums.DAMAGE_TYPE getDmgType(Spell spell) {
        GenericEnums.DAMAGE_TYPE dmg_type = spell.getDamageType();
        if (dmg_type != null) {
            return dmg_type;
        }
        switch (spell.getSpellGroup()) {
            case AIR:
                return GenericEnums.DAMAGE_TYPE.LIGHTNING;
            case WATER:
            case AFFLICTION:
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
            case NECROMANCY:
                return GenericEnums.DAMAGE_TYPE.DEATH;
            case BLOOD_MAGIC:
            case SAVAGE:
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
            case ELEMENTAL:
                break;
        }
        return dmg_type;
    }

    private static String getSpellSound(Spell spell, ANIM_PART part) {
        if (part !=ANIM_PART. IMPACT) {
            return "";
        } //TODO
        if (!spell.getProperty("anim_sound_"+part.getPartPath()).isEmpty()) {
            return parseSound(spell.getProperty("anim_sound_" + part.getPartPath()));
        }
        if (Cinematics.ON) {
            return "";
        }
        if (part== ANIM_PART.CAST) {
            return getSpellSoundPath() +
                    spell.getProperty(G_PROPS.CUSTOM_SOUNDSET);
        }
//        if (!spell.getProperty(G_PROPS.CUSTOM_SOUNDSET).isEmpty()) {
//            return "";
//        }
//        GenericEnums.DAMAGE_TYPE dmg_type =
//                getDmgType(spell);
        return FileManager.getRandomFile(PathFinder.getSoundsetsPath() + "damage/dark"
//                +                dmg_type
        ).getPath();
    }

    private static String parseSound(String property) {
        StringBuilder parsedBuilder = new StringBuilder();
        for(String substring: ContainerUtils.openContainer( property)){
        GenericEnums.SOUND_CUE cue = new EnumMaster<GenericEnums.SOUND_CUE>().
                retrieveEnumConst(GenericEnums.SOUND_CUE.class, substring);
        if (cue!=null) {
            parsedBuilder.append(cue.getPath()).append(";");
        } else {
            parsedBuilder.append(PathFinder.getSoundCuesPath()).append(substring);
        }
        }
        String parsed = parsedBuilder.toString();
        if (parsed.isEmpty()) {
            return property;
        }
        return parsed;
    }

    public static String getActionEffectSoundPath(Spell spell, ANIM_PART part) {
        if (Flags.isIggDemo()) {
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

    public static String getSpellSoundPath() {
        return getPath() + "soundsets/spells/";
    }

    public static void playImpact(ActiveObj activeObj) {
        if (activeObj.isAttackAny()) {
            playAttackImpactSound(activeObj.getActiveWeapon(), activeObj.getOwnerUnit(), (Unit) activeObj.getRef().getTargetObj(),
                    activeObj.getIntParam(PARAMS.DAMAGE_LAST_DEALT),
                    activeObj.getIntParam(PARAMS.DAMAGE_LAST_AMOUNT) - activeObj.getIntParam(PARAMS.DAMAGE_LAST_DEALT)
            );
        } else {
            getPlayer().playEffectSound(AudioEnums.SOUNDS.IMPACT, activeObj);
        }
//        AudioMaster.getInstance().playRandomSound();
    }


    public static main.system.sound.Player getPlayer() {
        return soundPlayer;
    }

    public static void playDamageSound(GenericEnums.DAMAGE_TYPE damageType) {
         playRandomSoundVariant(PathFinder.getSoundsetsPath() + "damage/" + damageType.getName(), true);
    }

    public static void playRandomKeySound(String value) {
        String[] sounds = value.split(",");
        playKeySound(sounds[RandomWizard.getRandomInt(sounds.length)], 1, true);
    }
    public static void playKeySound(String value, float volume, boolean random) {
        if (value.contains(".")) {
            String[] parts = value.split(Pattern.quote("."));
            SOUNDSET set = new EnumMaster<SOUNDSET>().retrieveEnumConst(SOUNDSET.class, parts[0]);
            AudioEnums.SOUNDS type = new EnumMaster<AudioEnums.SOUNDS>().retrieveEnumConst(AudioEnums.SOUNDS.class, parts[1]);
            playEffectSound(type, set, (int) (volume * 100), 0);
        } else {
            if (value.contains("/")) {
                play(PathFinder.getSoundPath() + value + ".mp3", (int) (volume * 100), 0);
            } else
//            new EnumMaster<SOUND_CUE>().retrieveEnumConst(SOUND_CUE.class, value);
            {
                main.system.auxiliary.log.LogMaster.devLog("Sound cue played: " + PathFinder.getSoundCuesPath() + value + ".mp3");
                String path = PathFinder.getSoundCuesPath() + value + ".mp3";
             if (random){
                 DC_SoundMaster.playRandomSoundVariant(path, true, (int) (volume * 100), 0);
             } else {
                 play(path, (int) (volume * 100), 0);
             }
            }
        }
        /**
         *
         *
         */
    }

    public static void playCueSound(GenericEnums.SOUND_CUE soundCue) {
        play(soundCue.getPath());
    }


}
