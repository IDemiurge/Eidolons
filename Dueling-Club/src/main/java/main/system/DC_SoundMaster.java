package main.system;

import main.content.CONTENT_CONSTS.WEAPON_GROUP;
import main.content.CONTENT_CONSTS.WEAPON_SIZE;
import main.content.PROPS;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_WeaponObj;
import main.entity.obj.Obj;
import main.entity.obj.top.DC_ActiveObj;
import main.system.auxiliary.FileManager;
import main.system.auxiliary.RandomWizard;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class DC_SoundMaster {
    public static void playRangedAttack(DC_WeaponObj weapon) {
        // TODO double weapon sound
        if (weapon.getWeaponGroup() == WEAPON_GROUP.CROSSBOWS)
            SoundMaster
                    .playRandomSoundVariant("weapon\\crossbow\\" + weapon.getWeaponSize(), false);
        else if (weapon.getWeaponGroup() == WEAPON_GROUP.BOLTS)
            SoundMaster.playRandomSoundVariant("weapon\\bow\\" + weapon.getWeaponSize(), false);
        else
            SoundMaster.playRandomSoundVariant("weapon\\throw\\" + weapon.getWeaponSize(), false);

    }

    public static void playMissedSound(DC_HeroObj attacker, DC_WeaponObj attackWeapon) {

        SoundMaster.playRandomSoundVariant("weapon\\miss\\"
                + attackWeapon.getWeaponSize().toString().toLowerCase(), false);

    }

    public static void playParrySound(DC_HeroObj attacked, DC_WeaponObj attackWeapon) {
        // TODO double weapon sound
        DC_WeaponObj parryWeapon = attacked.getActiveWeapon(true);

        SoundMaster.playRandomSoundFromFolder("soundsets\\" + "weapon\\"
                + attackWeapon.getWeaponGroup() + "\\");
        SoundMaster.playRandomSoundVariant("soundsets\\" + "weapon\\" + "parry\\"
                + parryWeapon.getDamageType(), false);
    }

    public static void playBlockedSound(Obj attacker, Obj attacked, DC_WeaponObj shield,
                                        DC_WeaponObj weaponObj, Integer blockValue, Integer damage) {

        if (shield.getWeaponSize() == WEAPON_SIZE.TINY
                || shield.getWeaponSize() == WEAPON_SIZE.SMALL) {
            if (RandomWizard.chance(75, new Random()))
                SoundMaster.playRandomSoundVariant(
                        "armor\\buckler\\s " + weaponObj.getDamageType(), false);
            else
                SoundMaster.playRandomSoundVariant("soundsets\\" + "armor\\buckler\\s", false);
        } else {
            if (weaponObj != null)
                if (RandomWizard.chance(75))
                    SoundMaster.playRandomSoundVariant("soundsets\\" + "armor\\shield\\s "
                            + weaponObj.getDamageType(), false);
                else
                    SoundMaster.playRandomSoundVariant("soundsets\\" + "armor\\shield\\s", false);
        }

    }

    public static void playAttackImpactSound(DC_WeaponObj weapon, final DC_HeroObj attacker,
                                             final DC_HeroObj attacked, Integer final_amount, int blocked) {
        // TODO getDamageSeverity(fin
        String armor_type = attacked.getProperty(PROPS.OBJECT_ARMOR_TYPE);
        // int volume = 100;

        boolean natural = false;

        // cache = attackImpactSoundFilesCache.getOrCreate(armor_type+damage_type) TODO
        if (armor_type.isEmpty() && blocked > 0) {
            if (attacked.getRef().getObj(KEYS.ARMOR) != null)
                armor_type = attacked.getRef().getObj(KEYS.ARMOR).getProperty("ARMOR_GROUP");
        } else
            natural = true;

        if (armor_type.isEmpty())
            armor_type = ContentGenerator.getNaturalArmorTypeForUnit(attacked);

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
        if (on_off)
            SoundMaster.playStandardSound(STD_SOUNDS.ON_OFF);
            // SoundMaster.playStandardSound(STD_SOUNDS.CHAIN);
        else
            SoundMaster.playStandardSound(STD_SOUNDS.ButtonDown);

    }

    private static void playWeaponSound(DC_WeaponObj weapon) {
        // TODO Auto-generated method stub

    }

    private static void playArmorSound(final DC_HeroObj attacked, String armor_type,
                                       String damage_type, boolean natural) {
        String path = SoundMaster.getPath() + "soundsets\\" + (natural ? "obj\\" : "armor\\")
                + armor_type;
        File folder = FileManager.getFile(path);
        if (!folder.isDirectory())
            return;
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

}
