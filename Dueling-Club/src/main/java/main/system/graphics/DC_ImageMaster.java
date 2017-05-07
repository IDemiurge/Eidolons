package main.system.graphics;

import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.values.properties.G_PROPS;
import main.entity.Ref.KEYS;
import main.entity.active.DC_SpellObj;
import main.entity.active.DC_UnitAction;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.rules.combat.attack.Attack;
import main.game.battlecraft.rules.combat.attack.AttackCalculator.MOD_IDENTIFIER;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;
import main.system.images.ImageManager.STD_IMAGES;
import main.system.math.roll.RollMaster;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DC_ImageMaster {
    public static ImageIcon getActionIcon(DC_UnitAction action, String property, boolean highlighted) {
        ImageIcon base_image = ImageManager.getIcon(property);
        if (highlighted) {
            return new ImageIcon(ImageManager.applyBorder(base_image.getImage(),
                    BORDER.SPELL_HIGHLIGHTED));
        }
        if (action.isBlocked()) {
            return getSpellVariant(SPELL_VARIANTS.BLOCKED, base_image);
        }
        if (!action.canBeActivated()) {
            return getSpellVariant(SPELL_VARIANTS.UNAVAILABLE, base_image);
        }
        return base_image;
    }

    public static Image getUnitEmblem(Unit obj, int size, boolean player) {
        Image unitEmblem = null;
        if (player && obj.getOwner().getEmblem() != null) {
            unitEmblem = ImageManager.getSizedVersion(obj.getOwner().getEmblem(), new Dimension(
                    size, size));
        } else if (obj.getOBJ_TYPE_ENUM() == DC_TYPE.CHARS) {
            unitEmblem = ImageManager.getSizedIcon(obj.getProperty(G_PROPS.EMBLEM),
                    new Dimension(size, size)).getImage();
        } else {
            if (obj.getDeity() != null) {
                ImageIcon icon = ImageManager.getSizedIcon(obj.getDeity().getType().getProperty(
                        G_PROPS.EMBLEM), new Dimension(size, size));
                if (icon == null) {
                    icon = ImageManager.getEmptyEmblem();
                }
                unitEmblem = icon.getImage();
            }
        }
        if (!ImageManager.isValidImage(unitEmblem)) {
            unitEmblem = ImageManager.getSizedVersion(ImageManager.getEmptyEmblem().getImage(),
                    new Dimension(size, size));
        }
        if (obj.isSelected()) {
            return unitEmblem;
        }
        Image glow = ImageManager.getSizedVersion(ImageManager.getGlowFrame(obj.getOwner()
                .getFlagColor(), 132), new Dimension(32, 32));
        BufferedImage glowAlpha = ImageManager.getBufferedImage(glow, 50);
        return ImageManager.getGlowOverlaidImage(unitEmblem, glowAlpha);

        // int x = ImageManager.getGlowOffsetForSize(size);
        // int y = ImageManager.getGlowOffsetForSize(size);
        // if (glow != null)
        // unitEmblem = ImageManager.applyImage(unitEmblem, glow, x, y, false);
        // return unitEmblem;
    }

    public static ImageIcon getSpellIcon(DC_SpellObj spell, String property, boolean highlighted) {
        ImageIcon base_image = ImageManager.getIcon(property);
        if (highlighted) {
            return new ImageIcon(ImageManager.applyBorder(base_image.getImage(),
                    BORDER.SPELL_HIGHLIGHTED));
        }
        if (spell.isBlocked()) {
            return getSpellVariant(SPELL_VARIANTS.BLOCKED, base_image);
        }
        if (!spell.isPrepared()) {
            return getSpellVariant(SPELL_VARIANTS.UNPREPARED, base_image);
        }
        if (!spell.canBeActivated()) {
            return getSpellVariant(SPELL_VARIANTS.UNAVAILABLE, base_image);
        }
        return base_image;
        // return new ImageIcon(
        // ImageManager.applyBorder(base_image.getEmitterPath(),
        // BORDER.SPELL_NORMAL));
    }

    private static ImageIcon getSpellVariant(SPELL_VARIANTS variant, ImageIcon base_image) {
        base_image = applyEffects(base_image, variant.getEffects());
        return new ImageIcon(ImageManager.applyBorder(base_image.getImage(), variant.getBorder()));
    }

    private static ImageIcon applyEffects(ImageIcon base_image, VISUAL_EFFECTS[] effects) {

        for (VISUAL_EFFECTS effect : effects) {
            switch (effect) {
                case DARKEN:

                    break;
                case FLARE_BLUE:
                    break;
                case FLARE_RED:
                    break;
                case GRAYSCALE:
                    break;
                default:
                    break;

            }
        }
        return base_image;

    }

    public static Image getProcessedImage(Image image) {
        image = ImageManager.getSizedVersion(image, new Dimension(PhaseAnimation.MAX_MINI_ICON_SIZE,
                PhaseAnimation.MAX_MINI_ICON_SIZE));
        // image = ImageTransformer.getCircleCroppedImage(image);
        // image = ImageManager.applyBorder(image, BORDER.CIRCLE_GLOW_40);
        return image;
    }

    // units - by status

    public static Image getImageDynamic(MOD_IDENTIFIER mod, Object... values) {
        Image image = null;
        int value = StringMaster.getInteger((values.length == 0 ? 0 : values[0]).toString());
        Attack attack = (Attack) (values.length < 2 ? null : values[1]);
        switch (mod) {
            case AMMO:
                return getProcessedImage(attack.getWeapon().getRef().getObj(KEYS.AMMO).getImage());
            case ATK_DEF:
                return value > 0 ? ImageManager.getValueIcon(PARAMS.ATTACK) : // ACCURACY
                        ImageManager.getValueIcon(PARAMS.DEFENSE);
            case RANDOM:
                int n = attack.getWeapon().getIntParam(PARAMS.DIE_SIZE)
                        * attack.getWeapon().getIntParam(PARAMS.DICE) / 2;
                Boolean luck = RollMaster.getLuck(value, n);
                return ImageManager.getDiceIcon(luck, true);
            case WEAPON:
                image = attack.getWeapon().getIcon().getImage();
                return getProcessedImage(image);
            case ACTION:
                image = attack.getAction().getIcon().getImage();
                return getProcessedImage(image);
            case UNIT:
                image = attack.getAttacker().getIcon().getImage();
                return getProcessedImage(image);
            case EXTRA_ATTACK:
                if (attack.isAttackOfOpportunity()) {
                    image = STD_IMAGES.ATTACK_OF_OPPORTUNITY.getImage();
                } else if (attack.isCounter()) {
                    image = STD_IMAGES.COUNTER_ATTACK.getImage();
                } else if (attack.isInstant()) {
                    image = STD_IMAGES.INSTANT_ATTACK.getImage();
                }
                image = ImageManager.applyBorder(image, BORDER.CIRCLE_GLOW_32);
                return image;

            case RESISTANCE:
                if (value > 0) {
                    return ImageManager.getValueIcon(PARAMS.RESISTANCE);
                }
                return ImageManager.getValueIcon(PARAMS.RESISTANCE_PENETRATION);

            case ARMOR:
                if (value > 0) {
                    return ImageManager.getValueIcon(PARAMS.ARMOR);
                }
                return ImageManager.getValueIcon(PARAMS.ARMOR_PENETRATION);

        }
        return ImageManager.getEmptyEmblem().getImage();
    }

    public enum VISUAL_EFFECTS {
        DARKEN, GRAYSCALE, FLARE_BLUE, FLARE_RED,

    }

    public enum SPELL_VARIANTS {
        BLOCKED(BORDER.SPELL_BLOCKED, VISUAL_EFFECTS.GRAYSCALE),
        UNPREPARED(BORDER.SPELL_UNPREPARED),
        UNAVAILABLE(BORDER.SPELL_UNAVAILABLE, VISUAL_EFFECTS.FLARE_BLUE),
        ACTIVATED(BORDER.SPELL_ACTIVATED),
        SELECTING(BORDER.SPELL_SELECTING),
        OBLIVIATED(BORDER.SPELL_OBLIVIATED, VISUAL_EFFECTS.FLARE_RED),;
        private BORDER border;
        private VISUAL_EFFECTS[] effects;

        SPELL_VARIANTS(BORDER border, VISUAL_EFFECTS... effects) {
            this.setBorder(border);
            this.setEffects(effects);
        }

        public BORDER getBorder() {
            return border;
        }

        public void setBorder(BORDER border) {
            this.border = border;
        }

        public VISUAL_EFFECTS[] getEffects() {
            return effects;
        }

        public void setEffects(VISUAL_EFFECTS[] effects) {
            this.effects = effects;
        }
    }

}
