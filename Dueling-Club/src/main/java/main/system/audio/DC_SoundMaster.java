package main.system.audio;

import main.content.PARAMS;
import main.content.PROPS;
import main.content.enums.entity.HeroEnums.GENDER;
import main.content.enums.entity.ItemEnums;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_SpellObj;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.rules.magic.ChannelingRule;
import main.libgdx.anims.Anim;
import main.libgdx.anims.AnimationConstructor.ANIM_PART;
import main.libgdx.anims.CompositeAnim;
import main.system.ContentGenerator;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.sound.SoundMaster;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class DC_SoundMaster extends SoundMaster {

    public static void playRangedAttack(DC_WeaponObj weapon) {
        // TODO double weapon sound
        if (weapon.getWeaponGroup() == ItemEnums.WEAPON_GROUP.CROSSBOWS) {
            SoundMaster
             .playRandomSoundVariant("weapon\\crossbow\\" + weapon.getWeaponSize(), false);
        } else if (weapon.getWeaponGroup() == ItemEnums.WEAPON_GROUP.BOLTS) {
            SoundMaster.playRandomSoundVariant("weapon\\bow\\" + weapon.getWeaponSize(), false);
        } else {
            SoundMaster.playRandomSoundVariant("weapon\\throw\\" + weapon.getWeaponSize(), false);
        }

    }

    public static void playMissedSound(Unit attacker, DC_WeaponObj attackWeapon) {

        SoundMaster.playRandomSoundVariant("weapon\\miss\\"
         + attackWeapon.getWeaponSize().toString().toLowerCase(), false);

    }

    public static void playParrySound(Unit attacked, DC_WeaponObj attackWeapon) {
        // TODO double weapon sound
        DC_WeaponObj parryWeapon = attacked.getActiveWeapon(true);

        SoundMaster.playRandomSoundFromFolder("soundsets\\" + "weapon\\"
         + attackWeapon.getWeaponGroup() + "\\");
        SoundMaster.playRandomSoundVariant("soundsets\\" + "weapon\\" + "parry\\"
         + parryWeapon.getDamageType(), false);
    }

    public static void playBlockedSound(Obj attacker, Obj attacked, DC_WeaponObj shield,
                                        DC_WeaponObj weaponObj, Integer blockValue, Integer damage) {

        if (shield.getWeaponSize() == ItemEnums.WEAPON_SIZE.TINY
         || shield.getWeaponSize() == ItemEnums.WEAPON_SIZE.SMALL) {
            if (RandomWizard.chance(75, new Random())) {
                SoundMaster.playRandomSoundVariant(
                 "armor\\buckler\\s " + weaponObj.getDamageType(), false);
            } else {
                SoundMaster.playRandomSoundVariant("soundsets\\" + "armor\\buckler\\s", false);
            }
        } else {
            if (weaponObj != null) {
                if (RandomWizard.chance(75)) {
                    SoundMaster.playRandomSoundVariant("soundsets\\" + "armor\\shield\\s "
                     + weaponObj.getDamageType(), false);
                } else {
                    SoundMaster.playRandomSoundVariant("soundsets\\" + "armor\\shield\\s", false);
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
            SoundMaster.playStandardSound(STD_SOUNDS.ON_OFF);
        }// SoundMaster.playStandardSound(STD_SOUNDS.CHAIN);
        else {
            SoundMaster.playStandardSound(STD_SOUNDS.ButtonDown);
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
        SoundMaster.play(file);
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
                case AFTEREFFECT:
                    playNow(getActionEffectSoundPath((DC_SpellObj) activeObj, part));
                    break;
                case IMPACT:
                    break;
            }
        switch (part) {
            case PRECAST:
                ChannelingRule.playChannelingSound(activeObj, activeObj.getOwnerObj().getGender() == GENDER.FEMALE);
                playEffectSound(SOUNDS.PRECAST, activeObj);
                break;
            case CAST:
                playEffectSound(SOUNDS.CAST, activeObj);
                break;
            case RESOLVE:
                playEffectSound(SOUNDS.RESOLVE, activeObj);
                break;
            case MAIN:
                playEffectSound(SOUNDS.EFFECT, activeObj);
                break;
            case IMPACT:
                playImpact(activeObj);
                break;
            case AFTEREFFECT:
                break;
        }
    }

    private static String getActionEffectSoundPath(DC_SpellObj spell, ANIM_PART part) {
        String path = getPath() + "soundset\\spells\\";
        path = StringMaster.buildPath(path,spell.getAspect().toString(),
         spell.getSpellGroup().toString(), part.toString() );

        String file =
         FileManager.findFirstFile(path, spell.getName(), true);
        if (file==null )
            return null ;
//        FileManager.findFirstFile(path, part.toString(), true);
        String corePath = StringMaster.cropFormat(file);
        try {
            return FileManager.getRandomFilePathVariant(corePath, "mp3", true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;

    }

    private static void playImpact(DC_ActiveObj activeObj) {
        if (activeObj.isAttackAny()) {
            playAttackImpactSound(activeObj.getActiveWeapon(), activeObj.getOwnerObj(), (Unit) activeObj.getRef().getTargetObj(),
             activeObj.getIntParam(PARAMS.DAMAGE_LAST_DEALT),
             activeObj.getIntParam(PARAMS.DAMAGE_LAST_AMOUNT) - activeObj.getIntParam(PARAMS.DAMAGE_LAST_DEALT)
            );
        } else {
            playEffectSound(SOUNDS.IMPACT, activeObj);
        }
//        AudioMaster.getInstance().playRandomSound();
    }

}
