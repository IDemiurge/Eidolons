package main.system.graphics;

import main.content.CONTENT_CONSTS.DAMAGE_TYPE;
import main.content.CONTENT_CONSTS.STANDARD_PASSIVES;
import main.content.PARAMS;
import main.content.parameters.PARAMETER;
import main.data.XLinkedMap;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.top.DC_ActiveObj;
import main.game.battlefield.AttackCalculator;
import main.game.battlefield.AttackCalculator.MOD_IDENTIFIER;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.Damage;
import main.game.battlefield.DirectionMaster;
import main.game.battlefield.attack.Attack;
import main.system.DC_SoundMaster;
import main.system.auxiliary.ColorManager;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.auxiliary.secondary.GeometryMaster;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.graphics.AnimationManager.ANIM_TYPE;
import main.system.graphics.AnimationManager.MOUSE_ITEM;
import main.system.images.ImageManager;
import main.system.images.ImageManager.ALIGNMENT;
import main.system.images.ImageManager.BORDER;
import main.system.images.ImageManager.STD_IMAGES;
import main.system.math.roll.RollMaster;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.SOUNDS;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class AttackAnimation extends ActionAnimation {
    protected DIRECTION direction;
    Attack attack;

    public AttackAnimation(DC_ActiveObj action) {
        super(action);
        this.type = ANIM_TYPE.ATTACK;
        setKey(generateKey(action));
    }

    public AttackAnimation(Attack attack) {
        this(attack.getAction());
        setAttack(attack);
    }

    public static Object generateKey(DC_ActiveObj action) {
        return Attack.getAnimationKey(action);
    }

    @Override
    public void start() {
        if (isRecalc()) {
            int damage = new AttackCalculator(attack, true).calculateFinalDamage();
            super.start();
            return;
        }
        super.start();
    }

    private boolean isRecalc() {
        return isReplay();
    }

    public void setAttack(Attack attack) {
        this.attack = attack;

        setTarget(attack.getAttacked());
        source = attack.getAttacker();
        game = attack.getAction().getGame();
        direction = DirectionMaster.flip(DirectionMaster.getRelativeDirection(source, getTarget()));
    }

    @Override
    protected boolean drawGenerics() {
        if (!super.drawGenerics()) {
            return false;
        }
        drawGeneralAttackInfoOnSource();
        return true;
    }

    @Override
    protected boolean drawPhase(AnimPhase phase) {

        if (super.drawPhase(phase)) {
            return true;
        }

        this.phase = phase; // TODO mass-add as argument instead!
        switch (phase.getType()) {
            case PRE_ATTACK:
                return drawPreAttack(phase);

            case ATTACK_CRITICAL:
                return drawCriticalAttack(phase);
            case ATTACK_DEFENSE:
                return drawAttackDefense(phase);
            case PARRY:
                return drawParry(phase);
            case ATTACK_DODGED:
                return drawDodged(phase);
            // case MISSED:
            // return drawMISSED();
            // case INTERRUPTED:
            // return drawINTERRUPTED();

            case ATTACK_POSITION_MODS:
                return drawPositionFormulaDetails(phase);
            case DICE_ROLL:
                return drawDiceRollDetails(phase);
            case ATTACK_ACTION_MODS:
                return drawActionFormulaDetails(phase);

            case ATTACK_EXTRA_MODS:
                return drawExtraAttackFormulaDetails(phase);
            case ATTACK_WEAPON_MODS:
                return drawWeaponFormulaDetails(phase);
        }
        return false;
    }

    @Override
    public boolean isAutoHandled() {
        return false;
    }

    public void playSound(AnimPhase phase) {
        if (phase != null) {
            switch (phase.getType()) {
                case PARRY:
                    DC_SoundMaster.playParrySound((DC_HeroObj) getTarget(), attack.getWeapon());
                    break;
                case PRE_ATTACK:
                    SoundMaster.playEffectSound(SOUNDS.ATTACK, source);
                    break;
                case DAMAGE_DEALT:
                    SoundMaster.playEffectSound(SOUNDS.HIT, getTarget());
                    break;
                case ATTACK_DODGED:
                    SoundMaster.playStandardSound(STD_SOUNDS.MISSED_MELEE);
                    DC_SoundMaster.playMissedSound((DC_HeroObj) getTarget(), attack.getWeapon());
                    break;
                case MISSED:
                    SoundMaster.playStandardSound(STD_SOUNDS.MISSED);
                    break;
                case REDUCTION_ARMOR:
                    DC_SoundMaster.playAttackImpactSound(attack.getWeapon(), source,
                            (DC_HeroObj) getTarget(), attack.getDamage(), (int) phase.getArgs()[1]);
                    break;
                case REDUCTION_SHIELD:
                    DC_SoundMaster.playBlockedSound(source, getTarget(), attack
                            .getShield(), attack.getWeapon(), (int) phase.getArgs()[1], (int) phase
                            .getArgs()[3]);
                    break;
            }
        }
    }

    protected boolean drawPreAttack(AnimPhase phase) {

        if (attack.getAction() != null) {
            Image image = attack.getAction().getIcon().getImage();
            if (image.getWidth(null) > 50) {
                image = ImageTransformer.getCircleCroppedImage(image);
            }
            // could be a 64x64 or 50 circle!
            image = ImageManager.applyGlowFrame(image, BORDER.CIRCLE_GLOW_50.getImage());
            Point point = GeometryMaster
                    .getFarthestPointInRectangleForImage(w, h, image, direction);
            drawOnTarget(image, point.x, point.y);
        }
        if (attack.isCritical()) {
            drawCriticalAttack(phase);
        }
        // if (attack.getWeapon() != null) {
        // Image image = attack.getWeapon().getIcon().getImage();
        // if (image.getWidth(null) > 50) {
        // image = ImageTransformer.getCircleCroppedImage(image);
        // }
        // // could be a 64x64 or 50 circle!
        // image = ImageManager.applyGlowFrame(image,
        // BORDER.CIRCLE_GLOW_50.getImage());
        // Point point = GeometryMaster
        // .getFarthestPointInRectangleForImage(w, h, image, direction);
        // drawOnSource(image, point.x, point.y);
        // }
        if (attack.isSneak()) {
            Image image = ImageManager.getImage(STANDARD_PASSIVES.NO_RETALIATION.getImagePath());
            image = ImageTransformer.getCircleCroppedImage(image);
            image = ImageTransformer.flipHorizontally(ImageManager.getBufferedImage(image));
            int x = w - image.getWidth(null) - 4; // targetPoint.x+w-image.getWidth(null);
            int y = 6;
            // exclam? numbers? mouse map? tooltip at least
            drawOnTarget(image, x, y);
        }
        List<Damage> rawDamage = (List<Damage>) phase.getArgs()[1];
        drawRawDamage(rawDamage);
        return true;
    }

    protected void drawGeneralAttackInfoOnSource() {
        Point point = null;
        if (attack.getWeapon() != null) {
            Image image = attack.getWeapon().getIcon().getImage();
            if (attack.isOffhand()) {
                image = ImageTransformer.flipHorizontally(ImageManager.getBufferedImage(image));
            }
            // image = ImageTransformer.getCircleCroppedImage(image);
            // image = ImageManager.applyGlowFrame(image,
            // BORDER.CIRCLE_GLOW_64.getImage());
            point = GeometryMaster.getFarthestPointInRectangleForImage(w, h, image, direction
                    .flip());
            drawOnSource(image, point.x, point.y);
            // image = STD_IMAGES.DIRECTION_POINTER.getImage(); TODO
            // image = ImageTransformer.rotate(image, direction.getDegrees());
            // drawOnSource(image, point.x, point.y);
        }
        Image specMark = null;
        if (attack.isAttackOfOpportunity()) {
            specMark = STD_IMAGES.ATTACK_OF_OPPORTUNITY.getImage();
        }
        if (attack.isCounter()) {
            specMark = STD_IMAGES.COUNTER_ATTACK.getImage();
        }
        if (attack.isInstant()) {
            specMark = STD_IMAGES.INSTANT_ATTACK.getImage();
        }

        if (specMark != null) {
            // Point offset =
            // GeometryMaster.getFarthestPointInRectangleForImage(w, h, 64 * 2,
            // 64 * 2,
            // direction);
            drawOnSource(specMark, point.x, point.y);
        }

    }

    protected void drawRawDamage(List<Damage> rawDamage) {
        Image image = ImageManager.getDamageTypeImage(DAMAGE_TYPE.PHYSICAL.getName());
        Point p = GeometryMaster.getFarthestPointInRectangleForImage(w, h, image, direction.flip());
        int y = p.y; // - rawDamage.size()*20
        if (BooleanMaster.isTrue((direction.isGrowY()))) {
            y -= 30;
        }
        int x = p.x;
        for (Damage dmg : rawDamage) {
            int damage = dmg.getDamage();
            if (damage >= 0) {
                DAMAGE_TYPE dmg_type = dmg.getDmg_type();
                image = ImageManager.getDamageTypeImage(dmg_type.getName());
                Font font = getFontNegative();
                int max = Math.max(image.getWidth(null), FontMaster.getFontHeight(font));

                drawOnTarget(image, x, y);
                addMouseItem(false, x, y, 32, 32, new MouseItemImpl(MOUSE_ITEM.SUB_PHASE,
                        PHASE_TYPE.DAMAGE_FORMULA));
                y = y + (image.getHeight(null));

                if (direction.isVertical()) {
                    y += drawTextOnTarget(StringMaster.wrapInBraces(damage + ""), font, x, y);
                } else {
                    drawTextOnTarget(StringMaster.wrapInBraces(damage + ""), font, x, y);
                }
                // x += max;

                // direction = direction.rotate45(true); TODO
                // GeometryMaster.getFarthestPointInRectangleForImage(w, h, 40,
                // 32+fontHeight, direction);
            }
        }
    }

    protected boolean drawDamageFormulaOld(AnimPhase phase) {
        // concise extendable form:
        // atk/def ; action dmg mods; weapon dmg mods;

        Map<MOD_IDENTIFIER, Integer> modsMap = (Map<MOD_IDENTIFIER, Integer>) phase.getArgs()[0];
        Map<MOD_IDENTIFIER, Integer> bonusesMap = (Map<MOD_IDENTIFIER, Integer>) phase.getArgs()[1];

        // key: +/- mod% (+/- bonus)
        // icon? clickable!

        int i = 0;
        int y_base = 0;
        int wrap = 3;
        Font font = getFontNeutral();
        for (MOD_IDENTIFIER id : bonusesMap.keySet()) {
            String text = StringMaster.getBonusString(bonusesMap.get(id));
            // string += map.getOrCreate(s) + "" + StringMaster.wrapInParenthesis(s);
            Image image = id.getImage(bonusesMap.get(id), attack);
            if (image == null) {
                image = ImageManager.getDeadIcon();
            }
            // ImageManager.getImageForDamageFormulaKey(s);
            int height = image.getHeight(null);
            int width = image.getWidth(null);
            int y = y_base;
            int x = 0;
            if (i == 1) {
                x = MigMaster.getCenteredPosition(w, width);
            } else if (i == 2) {
                x = w - width;
            }

            drawOnTarget(image, x, y);
            if (getSubPhaseTypeForKey(id) == null) {
                getSubPhaseTypeForKey(id);
            }
            addMouseItem(false, x, y, width, height, new MouseItemImpl(MOUSE_ITEM.SUB_PHASE,
                    getSubPhaseTypeForKey(id)));

            // addToolTip(s+modsMap.getOrCreate(s));

            y += MAX_MINI_ICON_SIZE + 3;
            Integer mod = bonusesMap.get(id);
            Color color = getColorForModifier(mod);
            x += (width - FontMaster.getStringWidth(font, StringMaster.getModifierString(mod))) / 2;

            drawTextOnTarget(text, font, x, y, color);
            i++;

            if (i >= wrap) {
                i = 0;
                y_base = h / 2;
            }
            // mods for main attack damage
        }
        // TODO ++ FINAL DAMAGE

        // TODO SECOND COLUM FOR OPTIONAL - SNEAK, DIAGONAL, ...
        // what about the additional / specialEffects?
        return true;
    }

    protected boolean drawPositionFormulaDetails(AnimPhase phase) {
        drawIdMap(phase);
        return true;

    }

    protected boolean isAutoFinishDefault() {
        if (attack.isAttackOfOpportunity()) {
            return false;
        }
        return !attack.isInstant();
    }

    protected boolean drawDiceRollDetails(AnimPhase phase) {
        // drawIdMap(phase);
        List<Integer> dice = (List<Integer>) phase.getArgs()[0];
        // Integer dieSize = (Integer) phase.getArgs()[1];
        Map<Image, String> map = new XLinkedMap<>();
        for (Integer die : dice) {
            int n = attack.getWeapon().getIntParam(PARAMS.DIE_SIZE);
            Boolean luck = RollMaster.getLuck(die, n / 2);
            Image img = ImageManager.getDiceIcon(luck, false);
            map.put(img, "" + die);
        }
        drawIconMap(map);

        return true;
    }

    private void drawIdMap(AnimPhase phase) {
        Map<MOD_IDENTIFIER, Integer> map = (Map<MOD_IDENTIFIER, Integer>) phase.getArgs()[0];
        drawIconMap(getImageMapFromIds(attack, new ListMaster<MOD_IDENTIFIER>().getStringMap(map)));
    }

    protected boolean drawActionFormulaDetails(AnimPhase phase) {
        drawParamMap(phase);
        return true;
    }

    private void drawParamMap(AnimPhase phase) {
        Map<PARAMETER, Integer> map = (Map<PARAMETER, Integer>) phase.getArgs()[0];
        drawIconMap(getImageMapFromParams(new ListMaster<PARAMETER>().getStringMap(map)));
    }

    protected boolean drawExtraAttackFormulaDetails(AnimPhase phase) {
        drawIdMap(phase);
        return true;
    }

    protected boolean drawWeaponFormulaDetails(AnimPhase phase) {
        drawParamMap(phase);
        return true;

    }

    protected boolean drawDodged(AnimPhase phase) {
        int chance = (int) phase.getArgs()[0];
        String string = StringMaster.wrapInParenthesis(chance + "% chance");
        Font font = getFontNegative();
        int y = FontMaster.getFontHeight(font);
        int x = MigMaster.getCenteredTextPosition(string, font, w);
        drawTextOnTarget(string, font, x, y);
        Image image = attack.getAction().getIcon().getImage();
        if (image.getWidth(null) == 50) {

            // image = ImageManager.applyBorder(image,
            // BORDER.DARKENING_CIRCLE_50);
            image = ImageManager.applyBorder(image, BORDER.CIRCLE_GLOW_50, false);
        } else {
            image = ImageManager.applyBorder(image, BORDER.SPELL_BLOCKED);
        }
        drawOnTargetCenterX(ImageManager.getValueIcon(PARAMS.DEFENSE), y); // TODO
        // mouse
        // item!
        addMouseItem(false, (w - 32) / 2, y, 32, 32, new MouseItemImpl(MOUSE_ITEM.SUB_PHASE,
                PHASE_TYPE.DODGE_FORMULA));
        y += 32;
        // image = ImageManager.applyImage(image,
        // ImageManager.getValueIcon(PARAMS.DEFENSE), (image
        // .getWidth(null) - 32) / 2, 0, false);
        drawOnTargetCenterX(image, y);
        drawOnTargetCenterY(STD_IMAGES.ENGAGER.getImage(), (w - 32) / 2);
        return true;

    }

    protected boolean drawParry(AnimPhase phase) {
        // drawWeaponOnTarget
        int chance = (int) phase.getArgs()[0];
        int main_durability_loss = (int) phase.getArgs()[1];
        int durability_loss = (int) phase.getArgs()[2];
        int durability_loss2 = -1; // dual?
        boolean nonDual = true;
        if (phase.getArgs().length > 3) {
            durability_loss2 = (int) phase.getArgs()[3];
            nonDual = false;
        }
        int y = MigMaster.getCenteredPosition(h, 64);
        Image image = attack.getAttacked().getActiveWeapon(false).getIcon().getImage();
        Image image2 = attack.getWeapon().getIcon().getImage();
        if (nonDual) {
            drawOnTarget(image, (w - 64 * 2) / 2, y);
            drawOnTarget(image2, w - 64 - (w - 64 * 2) / 2, y);
            // GeometryMaster.getFarthestPointInRectangleForImage(y, chance,
            // img, direction)
        } else {
            //
            drawOnTarget(image, 0, y);
            drawOnTarget(image2, w - image2.getWidth(null), y);
        }
        String string = Math.min(100, chance) + "% chance";
        int width = FontMaster.getStringWidth(font, string);
        int x = MigMaster.getCenteredTextPosition(string, font, w);
        drawTextOnTarget(string, font, x, -fontHeight);

        string = main_durability_loss + " durability lost";
        width = FontMaster.getStringWidth(font, string);
        x = !nonDual ? MigMaster.getCenteredTextPosition(string, font, w) : 0;
        drawTextColored(false, false, string, font, x, y - fontHeight);
        if (nonDual) {
            // ?
        }
        string = durability_loss + " durability lost";
        width = FontMaster.getStringWidth(font, string);
        x = !nonDual ? MigMaster.getCenteredTextPosition(string, font, w) : w - width;

        drawTextColored(false, true, string, font, x, y + 64);

        return true;
    }

    protected boolean drawAttackDefense(AnimPhase phase) {
        // attackValue, defense, diff, mod, amount, bonus
        int attackValue = (int) phase.getArgs()[0];
        int defense = (int) phase.getArgs()[1];
        int diff = (int) phase.getArgs()[2];
        float mod = (float) phase.getArgs()[3];
        int amount = (int) phase.getArgs()[4];
        int bonus = (int) phase.getArgs()[5];
        Image image1 = ImageManager.getValueIcon(PARAMS.ATTACK);
        Image image2 = ImageManager.getValueIcon(PARAMS.DEFENSE);
        // Atk/Def/Crit atk/def formula, difference/chance, final %/mod
        Point point1 = GeometryMaster.getFarthestPointInRectangleForImage(w, h, image1,
                DIRECTION.UP_LEFT);
        Point point2 = GeometryMaster.getFarthestPointInRectangleForImage(w, h, image2,
                DIRECTION.UP_RIGHT);
        String formula = StringMaster.wrapInBraces(attackValue + "") + "  vs  "
                + StringMaster.wrapInBraces(defense + "");
        String prefix = bonus > 0 ? "+" : "";
        String result = prefix + bonus + " damage "
                + StringMaster.wrapInParenthesis(diff + "*" + mod + "%*" + amount);
        drawOnTarget(image1, point1.x, point1.y);
        drawOnTarget(image2, point2.x, point2.y);
        int y = point1.y + image1.getHeight(null) + 8;
        Font font = getFontNeutral();
        int x = MigMaster.getCenteredTextPosition(formula, font, w);
        y += drawTextOnTarget(formula, font, x, y);
        x = MigMaster.getCenteredTextPosition(result, font, w);
        y += drawTextOnTarget(result, font, x, y, ColorManager.CRIMSON);

        int dodgeOrCritChance = 0;

        return true;
    }

    @Override
    protected boolean isGhostDrawn(Boolean target) {
        if (target) {
            if (attack != null) {
                if (attack.isInstant() || attack.isCounter() || attack.isAttackOfOpportunity()) {
                    return true;
                }
            }
        }

        return super.isGhostDrawn(target);
    }

    public void setPhase(PHASE_TYPE type) {
        this.phase = getPhase(type);

    }

    protected boolean drawCriticalAttack(AnimPhase phase) {
        Image image = ImageManager.getImage(STANDARD_PASSIVES.CRITICAL_IMMUNE.getImagePath());
        int y = 0;
        int x = 0;
        drawOnTargetCenterX(image, y);
        image = (STD_IMAGES.ENGAGER.getImage());
        setAlignmentX(ALIGNMENT.EAST);
        drawOnTarget(image, x, y);

        setAlignmentX(ALIGNMENT.CENTER);
        // int percent = ((Attack) phase.getArgs()[0]).getOrCreate;
        // int bonus = ((List) phase.getArgs()[1]).getOrCreate(0);
        int bonus = (int) phase.getArgs()[1];
        int chance = (int) phase.getArgs()[2];
        String str = StringMaster.getBonusString(bonus);
        Boolean negative = null;
        y += drawTextColored(false, negative, str, font, x, y);
        str = StringMaster.wrapInParenthesis(chance + "% chance");
        y += drawTextColored(false, null, str, font, x, y);
        y += drawTextColored(false, negative, str, font, x, y);

        return true;
    }

    @Override
    public Object getArg() {
        return attack;
    }

    @Override
    public String getArgString() {
        if (attack != null) {
            return attack.toString();
        } else {
            return super.getArgString();
        }
    }

}
