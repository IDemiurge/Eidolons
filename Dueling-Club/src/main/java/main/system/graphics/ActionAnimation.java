package main.system.graphics;

import main.content.CONTENT_CONSTS.DAMAGE_TYPE;
import main.content.PARAMS;
import main.content.parameters.PARAMETER;
import main.data.XLinkedMap;
import main.elements.costs.Cost;
import main.elements.costs.Costs;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_HeroSlotItem;
import main.entity.obj.Obj;
import main.entity.obj.top.DC_ActiveObj;
import main.game.DC_Game;
import main.game.battlefield.ArmorMaster;
import main.game.battlefield.AttackCalculator.MOD_IDENTIFIER;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.attack.Attack;
import main.system.auxiliary.*;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.auxiliary.secondary.GeometryMaster;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.graphics.AnimationManager.ANIM_TYPE;
import main.system.graphics.AnimationManager.MOUSE_ITEM;
import main.system.images.ImageManager;
import main.system.images.ImageManager.ALIGNMENT;
import main.system.images.ImageManager.BORDER;
import main.system.math.Formula;
import main.system.math.MathMaster;
import main.system.math.PositionMaster;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.threading.WaitMaster;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ActionAnimation extends Animation {
    protected DC_ActiveObj action;

    public ActionAnimation(DC_ActiveObj action) {
        super(ANIM_TYPE.ACTION);
        this.action = action;

        setSource(action.getOwnerObj());
        if (action.getRef().getTargetObj() == null)
            setTarget(action.getRef().getSourceObj());
        else
            setTarget(action.getRef().getTargetObj());
        game = action.getGame();
        setKey(generateKey());
        action.setAnimation(this);
    }

    public Object generateKey() {
        return action.getAnimationKey();
    }

    @Override
    protected boolean drawGenerics() {
        if (source == null)
            return false;

        return super.drawGenerics();
    }

    @Override
    protected boolean drawPhase(AnimPhase phase) {
        switch (phase.getType()) {
            case DEATH:
                return drawDeath(phase);
            case COSTS_PAID:
                return drawCosts(phase);
            case ACTION:
                return drawActivate(phase);
            case ACTION_RESOLVES:
                return drawResolves(phase);
            case MISSED:
                return drawMissed(phase);
            case INTERRUPTED:
                return drawInterrupted(phase);

            case REDUCTION_ARMOR:
                return drawArmorReduction(phase);
            case REDUCTION_NATURAL:
                return drawNaturalReduction(phase);
            case REDUCTION_SHIELD:
                return drawShieldReduction(phase);
            case DAMAGE_DEALT:
                return drawDamageDealt(phase);
            case DAMAGE_FORMULA:
                return drawDamageFormulaBonuses(phase);
            case DAMAGE_FORMULA_MODS:
                return drawDamageFormulaMods(phase);
        }
        return false;
    }

    private boolean drawFallUnconscious(AnimPhase phase) {
        return true;

    }

    private boolean drawDeath(AnimPhase phase) {
        int x = 0;
        int y = 0;
        // DC_HeroObj killer = (DC_HeroObj) phase.getArgs()[0];
        // source?
        // Image image = BORDER.PETRIFIED.getImage();
        // drawOnTarget(image, x, y);

        Image image = ImageManager.getDeadIconBig();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        drawOnTarget(image, 0, 0);
        g2d.setComposite(AlphaComposite.getInstance(1));

        String str = "Kills " + getTarget().getNameIfKnown();
        drawTextOnSource(str, font, x, y, ColorManager.getStandardColor(true));
        // dropped items, xp/glory reward,
        return true;
    }

    protected boolean drawDamageDealt(AnimPhase phase) {
        List<Object> list = new LinkedList<>(Arrays.asList(phase.getArgs()));
        int i = 0;
        while (list.size() >= 3) {
            int t_damage = (int) list.get(0);
            int e_damage = (int) list.get(1);
            DAMAGE_TYPE damage_type = (DAMAGE_TYPE) list.get(2);
            drawDamageDealt(t_damage, e_damage, damage_type, i);
            list.remove(0);
            list.remove(0);
            list.remove(0);
            i++;
        }
        return true;
    }

    protected boolean drawNaturalReduction(AnimPhase phase) {
        int e_armor = (int) phase.getArgs()[0];
        int e_res = (int) phase.getArgs()[1];
        int e_dmg = (int) phase.getArgs()[2];
        int t_armor = (int) phase.getArgs()[3];
        int t_res = (int) phase.getArgs()[4];
        int t_dmg = (int) phase.getArgs()[5];
        DAMAGE_TYPE dmg_type = (DAMAGE_TYPE) phase.getArgs()[6];
        boolean separate = false;
        if (e_armor != t_armor)
            separate = true;
        else if (e_res != t_res)
            separate = true;
        else if (e_dmg != t_dmg)
            separate = true;

        // check separate or not!
        // center damage if not and
        if (!separate) {
            drawNaturalReductionColumn(null, dmg_type, e_dmg, e_res, e_armor);
        } else {
            drawNaturalReductionColumn(false, dmg_type, e_dmg, e_res, e_armor);
            drawNaturalReductionColumn(true, dmg_type, t_dmg, t_res, t_armor);
        }

        return true;
    }

    protected void drawNaturalReductionColumn(Boolean left_right_center, DAMAGE_TYPE dmg_type, int dmg, int res,
                                              int armor) {
        // if separate, will draw two columns of reductions
        // if not, an offset column or a row...
        int x = 0;
        int y = 0;
        int base_y = 0;
        String text = null;
        if (left_right_center == null || BooleanMaster.isTrue(left_right_center)) {
            x = MigMaster.getCenteredPosition(w, 32);
            drawOnTarget(ImageManager.getDamageTypeImage(dmg_type.getName()), x, 0);
            text = StringMaster.wrapInBraces("" + dmg);
            addMouseItem(false, x, y, 32, 32, new MouseItemImpl(MOUSE_ITEM.SUB_PHASE, PHASE_TYPE.DAMAGE_FORMULA));
            y += 32;
            y += drawTextOnTarget(text, font, MigMaster.getCenteredTextPosition(text, font, w), 32);

            base_y = y;
        }
        if (left_right_center != null)
            y = 0;

        y = drawNaturalReductionModifier(left_right_center, dmg, armor, y, false);

        if (left_right_center == null)
            y = base_y;
        drawNaturalReductionModifier(left_right_center, dmg, res, y, true);
        // image = AttackCalculator.getImageDynamic(MOD_IDENTIFIER.ARMOR,
        // armor);
        // width = 32 + (left_right_center == null ? -32 : 0);
        // x = MigMaster.getOptimalPosition(left_right_center, w, width);
        // drawOnTarget(image, x, y);
        // y += 32;
        // modifier = -Math.min(dmg, armor);
        // text = StringMaster.getBonusString(modifier);
        // width = FontMaster.getStringWidth(font, text) + (left_right_center ==
        // null ? -32 : 0);
        // x = MigMaster.getOptimalPosition(left_right_center, w, width);
        // drawTextOnTarget(text, font, x, y, getColorForModifier(-modifier));
    }

    protected int drawNaturalReductionModifier(Boolean left_right_center, int dmg, int value, int y,
                                               boolean res_armor) {
        int x;
        String text;
        Image image = DC_ImageMaster.getImageDynamic(res_armor ? MOD_IDENTIFIER.RESISTANCE : MOD_IDENTIFIER.ARMOR,
                value);
        int width = 32;
        if (left_right_center == null)
            width += res_armor ? 32 : -32;
        x = MigMaster.getOptimalPosition(left_right_center, w, width);
        drawOnTarget(image, x, y);
        y += 32 + FontMaster.getFontHeight(font) / 2;
        int modifier = res_armor ? -MathMaster.getFractionValueCentimal(dmg, value) : -Math.min(dmg, value);
        text = StringMaster.getBonusString(modifier);
        width = FontMaster.getStringWidth(font, text);
        if (left_right_center == null)
            width += res_armor ? 32 : -32;

        x = MigMaster.getOptimalPosition(left_right_center, w, width);

        y += drawTextOnTarget(text, font, x, y, getColorForModifier(-modifier));
        return y;
    }

    protected boolean drawArmorReduction(AnimPhase phase) {
        return drawReduction(false);
    }

    protected boolean drawShieldReduction(AnimPhase phase) {
        // Shield/armor  chance, numbers, dmg type, durability
        return drawReduction(true);

    }

    protected boolean drawReduction(boolean shield) {
        int percentage = (int) phase.getArgs()[0];
        int blocked = (int) phase.getArgs()[1];
        int durability = (int) phase.getArgs()[2];
        int amount = (int) phase.getArgs()[3];

        DAMAGE_TYPE dmg_type = (DAMAGE_TYPE) phase.getArgs()[4];
        DC_HeroSlotItem armor = (DC_HeroSlotItem) phase.getArgs()[5];

        int y = 4; // calculate centered
        Image image = armor.getIcon().getImage();
        int x = (w - image.getWidth(null)) / 2;
        drawOnTarget(image, x, y);
        Font font = getFontNeutral();
        y += 5 + image.getHeight(null);

        image = ImageManager.getDamageTypeImage(dmg_type.getName());
        drawOnTarget(image, 0, y - (image.getHeight(null) - FontMaster.getFontHeight(font)) / 2);

        String string = StringMaster.wrapInBraces(blocked + "") + " blocked "
                + StringMaster.wrapInParenthesis(ArmorMaster.getArmorValue(armor, dmg_type) + " max.");
        x = (w - FontMaster.getStringWidth(font, string)) / 2;
        y += drawTextOnTarget(string, font, x, y) - 6;

        string = StringMaster.wrapInParenthesis(percentage + "%" + ((shield) ? " chance" : " of attack"));

        x = (w - FontMaster.getStringWidth(font, string)) / 2;
        y += drawTextOnTarget(string, font, x, y) - 6;

        // Shield/armor  chance, numbers, dmg type, durability
        if (durability <= 0)
            return true;
        font = getFontNegative();
        string = StringMaster.wrapInBraces(durability + "") + " durability lost";
        x = (w - FontMaster.getStringWidth(font, string)) / 2;
        drawTextOnTarget(string, font, x, y, ColorManager.CRIMSON);
        // show initial amount/dmg_type?
        return true;
    }

    // TODO include RESISTANCE; add EXTENDED MAP that will show things like
    // SIDE_MOD, CLOSE_QUARTERS...
    protected boolean drawDamageFormulaBonuses(AnimPhase phase) {
        Attack attack = (Attack) phase.getArgs()[0];
        Map<MOD_IDENTIFIER, Integer> modsMap = (Map<MOD_IDENTIFIER, Integer>) phase.getArgs()[1];
        Map<MOD_IDENTIFIER, Integer> bonusesMap = (Map<MOD_IDENTIFIER, Integer>) phase.getArgs()[2];
        drawIconMap(getImageMapFromIds(attack, new ListMaster<MOD_IDENTIFIER>().getStringMap(bonusesMap)));

        Integer totalMod = 0;
        for (Integer mod : modsMap.values()) {
            totalMod += mod;
        }
        if (totalMod == 0)
            return true;
        String text = StringMaster.getModifierString(totalMod);
        int y = (h - fontHeight) / 2;
        drawTextOnTarget(text, font, w, y, getColorForModifier(totalMod));
        addMouseItem(false, w, y, FontMaster.getStringWidth(font, text), fontHeight,
                new MouseItemImpl(MOUSE_ITEM.SUB_PHASE, PHASE_TYPE.DAMAGE_FORMULA_MODS));
        return true;
    }

    protected boolean drawDamageFormulaMods(AnimPhase phase) {
        Attack attack = (Attack) phase.getArgs()[0];
        Map<MOD_IDENTIFIER, Integer> modsMap = (Map<MOD_IDENTIFIER, Integer>) phase.getArgs()[1];
        drawIconMap(getImageMapFromIds(attack, new ListMaster<MOD_IDENTIFIER>().getStringMap(modsMap)));
        return true;

    }

    protected void drawDamageDealt(int t_damage, int e_damage, DAMAGE_TYPE damage_type, int i) {
        Image image1 = ImageManager.getValueIcon(PARAMS.TOUGHNESS);
        Image image2 = ImageManager.getValueIcon(PARAMS.ENDURANCE);
        Point point1 = GeometryMaster.getFarthestPointInRectangleForImage(w, h, image1, DIRECTION.UP_LEFT);
        Point point2 = GeometryMaster.getFarthestPointInRectangleForImage(w, h, image2, DIRECTION.UP_RIGHT);
        // TODO attack icon at center?
        int y = point1.y + (32 + fontHeight) * i;
        drawOnTarget(image1, point1.x, y);
        drawOnTarget(image2, point2.x, y);
        y = y + image1.getHeight(null);

        int x = drawOnTargetCenterX(ImageManager.getDamageTypeImage(damage_type.getName()), y - fontHeight / 2);

        Font font = getFontNeutral();
        String text = StringMaster.wrapInBraces("-" + t_damage);
        x = point1.x;
        drawTextOnTarget(text, font, x, y, ColorManager.CRIMSON);
        text = StringMaster.wrapInBraces("-" + e_damage);
        x = MigMaster.getFarRightTextPosition(text, font, w);
        drawTextOnTarget(text, font, x, y, ColorManager.CRIMSON);

    }

    protected boolean drawMissed(AnimPhase phase) {
        int chance = (int) phase.getArgs()[0];
        boolean concealment_evasion = (boolean) phase.getArgs()[1];
        PARAMS param = concealment_evasion ? PARAMS.CONCEALMENT : PARAMS.EVASION;
        Image val_image = ImageManager.getValueIcon(param);

        // drawValueImageText(param, null, null, ALIGNMENT.WEST);

        setAlignmentX(ALIGNMENT.NORTH);
        setDrawOnTargetOrSource(true);
        drawTextOnTarget("Chance: " + chance, 0, 0);
        Image image = action.getIcon().getImage();
        image = ImageManager.applyBorder(image, BORDER.SPELL_UNAVAILABLE);
        drawOnTargetCenter(image);

        return true;
    }

    private void drawValueImageText(boolean source, PARAMETER param, Boolean bonus_mod, Boolean negative,
                                    ALIGNMENT al) {
        Image val_image = ImageManager.getValueIcon(param);
        // drawOn(source, val_image, al);

        resetDrawingParams();

    }

    protected Map<Image, String> getImageMapFromParams(Map<PARAMETER, String> map) {
        Map<Image, String> iconMap = new HashMap<>();
        for (PARAMETER param : map.keySet()) {
            Image image = ImageManager.getValueIcon(param);
            // TODO check w
            getSubPhaseTooltipMap().put(image, getTooltip(param));

            iconMap.put(image, map.get(param));
        }
        return iconMap;
    }

    protected boolean drawIconMap(Map<Image, String> valMap) {
        return drawIconMap(valMap, true);
    }

    protected boolean drawIconMap(Map<Image, String> valMap, boolean mouseMap) {
        return drawIconMap(valMap, mouseMap, false);
    }

    protected boolean drawIconMap(Map<Image, String> valMap, boolean mouseMap, boolean source) {
        return drawIconMap(valMap, mouseMap, source, getDefaultIconMapOffsetY(), false);

    }

    protected int getDefaultIconMapOffsetY() {
        return 4;
    }

    protected boolean drawIconMap(Map<Image, String> valMap, boolean mouseMap, boolean source, int base,
                                  boolean inverse) {
        drawTextBackground = false;
        try {
            drawIconMap(valMap, true, 3, mouseMap, getFontNeutral(), base, 2, !source, inverse, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            drawTextBackground = isDrawTextBackgroundAutomatically();
        }

        return true;
    }

    protected void drawIconMap(Map<Image, String> map, boolean horizontal, int wrap, boolean mouseMap, Font font,
                               int base, int rows, boolean onTarget, boolean inverse, Boolean bonus_mod_other) {
        // if (map.isEmpty())
        // return;

        List<Map<Image, String>> splitMaps = new MapMaster<Image, String>().splitMap(map, wrap);

        // fillTextRowWithDarkBackground(!onTarget, base + MAX_MINI_ICON_SIZE +
        // getIconMapOffsetY(),
        // font);
        for (Map<Image, String> map1 : splitMaps) {
            fillTextRowWithDarkBackground(!onTarget, base + MAX_MINI_ICON_SIZE + getIconMapOffsetY(), font);
            int i = 0;
            for (Image image : map1.keySet()) {
                // string += map.get(s) + "" +
                // StringMaster.wrapInParenthesis(s);
                if (image == null) {
                    main.system.auxiliary.LogMaster.log(1, "********** null image in " + map1);
                    continue;
                }
                Loop loop = new Loop(10);
                while (image.getWidth(null) < 1 && loop.continues()) {
                    WaitMaster.WAIT(50);
                }
                if (image.getWidth(null) < 1) {
                    main.system.auxiliary.LogMaster.log(1, "********** invalid image for " + map1.get(image));
                    continue;
                }

                String s = map1.get(image);
                // ImageManager.getImageForDamageFormulaKey(s);
                int height = image.getHeight(null);
                int width = image.getWidth(null);
                int y = horizontal ? base : 0; // TODO finish

                int itemWidth = w / wrap;
                int x = itemWidth * i;

                if (map1.size() < wrap)
                    x = MigMaster.getCenteredPosition(w, itemWidth * map1.size()) + (i) * itemWidth;
                drawOn(!onTarget, image, x, y);
                if (mouseMap) {
                    PHASE_TYPE arg = null;
                    if (subPhaseTypeMap != null)
                        arg = subPhaseTypeMap.get(image);
                    if (arg == null)
                        addMouseItem(false, x, y, width, height,
                                new MouseItemImpl(MOUSE_ITEM.TOOLTIP, getSubPhaseTooltipMap().get(image)));
                    else
                        addMouseItem(false, x, y, width, height, new MouseItemImpl(MOUSE_ITEM.SUB_PHASE, arg));
                }

                // addToolTip(s+modsMap.get(s));

                y +=
                        // ((inverse) ? -1 : 1) *
                        (MAX_MINI_ICON_SIZE + getIconMapOffsetY());

                Integer mod = StringMaster.getInteger(s);
                Color color = getColorForModifier(mod);
                String text = s;
                if (bonus_mod_other != null)
                    text = bonus_mod_other ? StringMaster.getBonusString(mod) : StringMaster.getModifierString(mod);
                x += (width - FontMaster.getStringWidth(font, text)) / 2;
                drawTextOn(!onTarget, text, font, x, y - fontHeight / 2, color);
                i++;

            }
            base = base + ((!inverse) ? 1 : -1)
                    * (MAX_MINI_ICON_SIZE + getIconMapOffsetY() + FontMaster.getFontHeight(font) / 2);

        }
    }

    protected int getIconMapOffsetY() {
        return 5;
    }

    protected boolean drawInterrupted(AnimPhase phase) {
        // TODO Auto-generated method stub
        return true;
    }

    protected String getThumbnailText() {
        return action.getName();
    }

    protected int getOffsetBase(boolean positive) {
        int offset = (positive ? 1 : -1) * GuiManager.getCellHeight()
                * game.getBattleField().getGrid().getGridComp().getZoom() / 100;
        if (!positive)
            offset += 12;
        else
            offset += 12;

        return offset;

    }

    protected boolean drawCosts(AnimPhase phase) {
        int base = getDefaultIconMapOffsetY();
        boolean invert = false;
        Boolean edge = null;
        boolean positive = false;

        if (getTarget() == source || phase.isDrawOnSource()) {

            edge = game.getBattleField().getGrid().isOnEdgeY(source.getCoordinates());
            // TODO
            positive = BooleanMaster.isTrue(edge) || edge == null;
            base = getOffsetBase(positive);
            if (base < 0)
                invert = true;
        }
        if (getTarget().getCoordinates().isAdjacent(source.getCoordinates())) {
            if (PositionMaster.inLine(source, target)) {
                if (PositionMaster.isAbove(target, source))
                    invert = true;
                if (PositionMaster.isAbove(source, target))
                    invert = false;
            }
        }
        Costs costs = (Costs) phase.getArgs()[0];
        Map<Image, String> map = new XLinkedMap<Image, String>();
        for (Cost s : costs.getCosts()) {
            if (s.isPaidAlt())
                s = s.getAltCost();
            Formula formula = s.getPayment().getAmountFormula();
            PARAMETER param = s.getPayment().getParamToPay();

            Image image = ImageManager.getValueIcon(param);
            if (image == null)
                continue;
            String string = "" + (-formula.getInt(action.getRef()));
            map.put(image, string);
            List<String> list = new LinkedList<>();
            list.add(string);
            getSubPhaseTooltipMap().put(image, list);
        }

        drawIconMap(map, false, true, base, invert);
        return true;
    }

    protected boolean drawResolves(AnimPhase phase) {
        return drawActivate(phase); // TODO ?
    }

    protected boolean drawActivate(AnimPhase phase) {
        if (action.isAttackOrStandardAttack())
            return false;
        Image image = action.getIcon().getImage();
        Coordinates c = getTargetCoordinates();
        if (c == null)
            c = getSourceCoordinates();

        // TODO can we get the cellComp's graphics?
        int zoom = action.getGame().getBattleField().getGrid().getGridComp().getZoom();
        int h = GuiManager.getCellHeight() * zoom / 100;
        int w = GuiManager.getCellWidth() * zoom / 100;
        Point point = action.getGame().getBattleField().getGrid().getGridComp().getPointForCoordinateWithOffset(c);
        int x = (int) (point.getX() + (w - image.getWidth(null)) / 2);
        int y = (int) (point.getY() + (h - image.getHeight(null)) / 2);
        // try it!
        g.drawImage(image, x, y, null);

        return true;
    }

    protected Map<Image, String> getImageMapFromIds(Attack attack, Map<MOD_IDENTIFIER, String> map) {
        Map<Image, String> iconMap = new XLinkedMap<>();
        subPhaseTypeMap = new XLinkedMap<>();
        for (MOD_IDENTIFIER modId : map.keySet()) {
            Image image = modId.getImage(map.get(modId), attack);
            PHASE_TYPE phaseType = getSubPhaseTypeForKey(modId);
            if (phaseType == null) {
                getSubPhaseTooltipMap().put(image, getTooltip(modId));
            }
            subPhaseTypeMap.put(image, phaseType);
            iconMap.put(image, map.get(modId));
        }
        return iconMap;
    }

    @Override
    public void playSound() {
        if (phase != null)
            switch (phase.getType()) {
                case MISSED:
                    SoundMaster.playStandardSound(STD_SOUNDS.MISSED);
                    break;
            }
    }

    public void setTarget(Obj target) {
        this.target = target;
        game = (DC_Game) target.getGame();
        setTargetCoordinates((target.getCoordinates()));
        targetPoint = game.getBattleField().getGrid().getGridComp()
                .getPointForCoordinateWithOffset(getTargetCoordinates());
    }

    @Override
    protected boolean isGhostDrawn(Boolean target) {
        if (isMove())
            return true;

        return super.isGhostDrawn(target);
    }

    private boolean isMove() {
        return action.isMove();
    }

    public void setSource(DC_HeroObj source) {
        this.source = source;
        game = source.getGame();
        setSourceCoordinates((source.getCoordinates()));
        sourcePoint = getPointForCoordinate(getSourceCoordinates());
    }

    @Override
    public String getArgString() {
        return action.getName();
    }

    @Override
    public Object getArg() {
        return action;
    }

    @Override
    public Animation clone() {
        return new ActionAnimation(action);
    }

    @Override
    protected Image getThumbnailImage() {
        return ImageManager.getSizedVersion(action.getImagePath(), AnimationManager.THUMBNAIL_SIZE).getImage();
    }

    // public void activates() {
    // statuses.add(ACTIVATES);
    //
    // }
    //
    // public void activated() {
    // statuses.add(ACTIVATED);
    //
    // }
    //
    // public void interrupted() {
    // statuses.add(INTERRUPTED);
    //
    // }
    //
    // public void missed() {
    // statuses.add(MISSED);
    //
    // }
    //
    // public void resolves() {
    // statuses.add(RESOLVES);
    //
    // }
    //
    // public void resolved() {
    // statuses.add(RESOLVED);
    //
    // }
}
