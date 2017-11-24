package main.system.audio;

import main.content.ContentManager;
import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.enums.entity.HeroEnums.GENDER;
import main.content.enums.entity.ItemEnums;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_SpellObj;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battle.universal.DC_Player;
import main.game.battlecraft.rules.magic.ChannelingRule;
import main.game.bf.Coordinates;
import main.game.core.game.DC_Game;
import main.libgdx.anims.Anim;
import main.libgdx.anims.AnimationConstructor;
import main.libgdx.anims.AnimationConstructor.ANIM_PART;
import main.libgdx.anims.CompositeAnim;
import main.libgdx.audio.SoundPlayer;
import main.libgdx.bf.GridMaster;
import main.libgdx.screens.DungeonScreen;
import main.system.content.ContentGenerator;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.ListMaster;
import main.system.sound.Player;
import main.system.sound.SoundMaster;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
            getPlayer().playRandomSoundVariant("soundsets\\weapon\\crossbow\\" + weapon.getWeaponSize(), false);
        } else if (weapon.getWeaponGroup() == ItemEnums.WEAPON_GROUP.BOLTS) {
            getPlayer().playRandomSoundVariant("soundsets\\weapon\\bow\\" + weapon.getWeaponSize(), false);
        } else {
            getPlayer().playRandomSoundVariant("soundsets\\weapon\\throw\\" + weapon.getWeaponSize(), false);
        }

    }

    public static void play(String s) {
        getSoundPlayer().play(s);
    }

    public static void playTurnSound(Unit unit) {
//         setPositionFor(unit.getCoordinates());
//        getSoundPlayer().play(STD_SOUNDS.SLING.getPath());

        playMoveSound(unit);
    }

    public static void playMoveSound(Unit unit) {
        String type = "soft";
        setPositionFor(unit.getCoordinates());
//        unit.getGame().getDungeon().isSurface()
        getPlayer().playRandomSoundFromFolder(
         "effects\\movement\\" + type
//          + unit.getSize()
        );

    }

    public static void playMissedSound(Unit attacker, DC_WeaponObj attackWeapon) {

        getPlayer().playRandomSoundVariant("soundsets\\weapon\\miss\\"
         + attackWeapon.getWeaponSize().toString().toLowerCase(), false);

    }

    public static void playEffectSound(SOUNDS sound_type, Obj obj) {
        setPositionFor(obj.getCoordinates());
        getPlayer().playEffectSound(sound_type, obj);
    }

    public static void playParrySound(Unit attacked, DC_WeaponObj attackWeapon) {
        // TODO double weapon sound
        setPositionFor(attacked.getCoordinates());
        DC_WeaponObj parryWeapon = attacked.getActiveWeapon(true);

        getPlayer().playRandomSoundFromFolder("soundsets\\" + "weapon\\"
         + attackWeapon.getWeaponGroup() + "\\");
        getPlayer().playRandomSoundVariant("soundsets\\" + "weapon\\" + "parry\\"
         + parryWeapon.getDamageType(), false);
    }

    private static void setPositionFor(Coordinates c) {
        if (getSoundPlayer() != null)
            try {
                getSoundPlayer().setPosition(
                 GridMaster.getPosWithOffset(c));
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
                 "armor\\buckler\\s " + weaponObj.getDamageType(), false);
            } else {
                getPlayer().playRandomSoundVariant("soundsets\\" + "armor\\buckler\\s", false);
            }
        } else {
            if (weaponObj != null) {
                if (RandomWizard.chance(75)) {
                    getPlayer().playRandomSoundVariant("soundsets\\" + "armor\\shield\\s "
                     + weaponObj.getDamageType(), false);
                } else {
                    getPlayer().playRandomSoundVariant("soundsets\\" + "armor\\shield\\s", false);
                }
            }
        }

    }

    public static void playAttackImpactSound(DC_WeaponObj weapon, final Unit attacker,
                                             final Unit attacked, Integer final_amount, int blocked) {
        // TODO getDamageSeverity(fin
        String armor_type = attacked.getProperty(PROPS.OBJECT_ARMOR_TYPE);
        // int volume = 100;

        boolean natural = false;

        // cache = attackImpactSoundFilesCache.getOrCreate(armor_type+damage_type) TODO
        if (armor_type.isEmpty() && blocked > 0) {
            if (attacked.getRef().getObj(KEYS.ARMOR) != null) {
                armor_type = attacked.getRef().getObj(KEYS.ARMOR).getProperty("ARMOR_GROUP");
            }
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

    }

    private static void playArmorSound(final Unit attacked, String armor_type,
                                       String damage_type, boolean natural) {
        String path = SoundMaster.getPath() + "soundsets\\" + (natural ? "obj\\" : "armor\\")
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
        List<File> files = new LinkedList<>(defaultSounds);
        files.addAll(specialSounds);
        // double chance
        files.addAll(specialSounds);
        if (files.isEmpty()) {
            if (attacked.isLiving()) {

            }

            return;
        }
        File file = FileManager.getRandomFile(files);
        getPlayer().play(file);
    }

    public static void bindEvents() {
        GuiEventManager.bind(GuiEventType.ANIMATION_STARTED, p -> {
            Anim anim = (Anim) p.get();
            DC_ActiveObj activeObj = (DC_ActiveObj) anim.getActive();
            try { //TODO ON SEPARATE THREAD!!!!
                playAnimStartSound(activeObj, anim.getPart());
            } catch (Exception e) {
//                e.printStackTrace();
            }
        });
        GuiEventManager.bind(GuiEventType.COMPOSITE_ANIMATION_STARTED, p -> {
            CompositeAnim anim = (CompositeAnim) p.get();
            DC_ActiveObj activeObj = (DC_ActiveObj) anim.getActive();
            try {
                playActionStartSound(activeObj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void playActionStartSound(DC_ActiveObj activeObj) {
        //TODO
    }

    private static void playAnimStartSound(DC_ActiveObj activeObj, ANIM_PART part) {
        if (activeObj instanceof DC_SpellObj)
            switch (part) {
                case PRECAST:
                case CAST:
                case RESOLVE:
                case MAIN:
                case IMPACT:
                case AFTEREFFECT:
                    playNow(getActionEffectSoundPath((DC_SpellObj) activeObj, part));
                    return;
            }
        switch (part) {
            case PRECAST:
                ChannelingRule.playChannelingSound(activeObj, activeObj.getOwnerObj().getGender() == GENDER.FEMALE);
                getPlayer().playEffectSound(SOUNDS.PRECAST, activeObj);
                break;
            case CAST:
                getPlayer().playEffectSound(SOUNDS.CAST, activeObj);
                break;
            case RESOLVE:
                getPlayer().playEffectSound(SOUNDS.RESOLVE, activeObj);
                break;
            case MAIN:
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
            DC_SpellObj active = new DC_SpellObj(type, DC_Player.NEUTRAL, DC_Game.game, new Ref());
            for (ANIM_PART part : ANIM_PART.values()) {
                preconstructSpell(active, part);
            }
        }
    }

    private static void preconstructSpell(DC_SpellObj spell, ANIM_PART part) {
        String file = AnimationConstructor.findResourceForSpell(spell,
         part.toString(), "", propsExact,
         getSpellSoundPath(), false);
        if (file == null) {
            file = AnimationConstructor.findResourceForSpell(spell,
             part.toString(), "", props,
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
        return ContentManager.findPROP("SOUND_" + part);
    }

    private static String getActionEffectSoundPath(DC_SpellObj spell, ANIM_PART part) {
        String file = spell.getProperty(getProp(part));
        String identifier;
        String path;
        if (!file.isEmpty()) {
            file = PathFinder.getSoundPath() + file;
            identifier = StringMaster.getLastPathSegment(file);
            path = StringMaster.cropLastPathSegment(file);
        } else {
            identifier = spell.getName();
            path = getSpellSoundPath();
            path = StringMaster.buildPath(path, spell.getAspect().toString(),
             spell.getSpellGroup().toString(), part.toString());
        }
        int i = 0;
        while (i < 6) {
            i++;
            if (StringMaster.isEmpty(file))
                file =
                 FileManager.findFirstFile(path, identifier, true);
            if (file != null) {
                String corePath = StringMaster.cropFormat(StrPathBuilder.build(path, file));
                try {
                    file = FileManager.getRandomFilePathVariant(corePath, StringMaster.getFormat(file), false);
                } catch (Exception e) {
//            e.printStackTrace();
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
                identifier = StringMaster.getLastPathSegment(path);
            else
                path = StringMaster.cropLastPathSegment(path);
        }
        return null;
    }

    private static String getSpellSoundPath() {
        return getPath() + "soundsets\\spells\\";
    }

    private static void playImpact(DC_ActiveObj activeObj) {
        if (activeObj.isAttackAny()) {
            playAttackImpactSound(activeObj.getActiveWeapon(), activeObj.getOwnerObj(), (Unit) activeObj.getRef().getTargetObj(),
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

    public void doPlayback(float delta) {
        if (soundPlayer == null)
            soundPlayer = new SoundPlayer(screen);
        soundPlayer.doPlayback(delta);
    }
}
