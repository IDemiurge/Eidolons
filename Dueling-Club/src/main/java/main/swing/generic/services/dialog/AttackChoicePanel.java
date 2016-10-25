package main.swing.generic.services.dialog;

import main.ability.effects.AttackEffect;
import main.ability.effects.Effect;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.top.DC_ActiveObj;
import main.game.battlefield.*;
import main.game.battlefield.attack.Attack;
import main.swing.builders.DC_Builder;
import main.system.ai.logic.target.EffectMaster;
import main.system.ai.tools.future.FutureBuilder;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.FontMaster.FONT;
import main.system.graphics.AnimPhase;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.graphics.Animation;
import main.system.graphics.AttackAnimation;
import main.system.graphics.MigMaster;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;
import main.system.images.ImageManager.STD_IMAGES;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.text.TextItem;
import main.system.threading.WaitMaster;

import java.awt.*;
import java.util.List;

public class AttackChoicePanel extends ChoicePanel<DC_ActiveObj> {
    private static final Image scroll = STD_IMAGES.SCROLL_ATTACK_CHOICE.getIcon().getImage();
    private static final Image scroll_text = STD_IMAGES.SCROLL_ATTACK_TEXT.getIcon().getImage();
    private TextItem tooltipTextItem;
    private boolean animated;
    private AttackAnimation anim;
    private TextItem costTooltip;

    public AttackChoicePanel(List<DC_ActiveObj> itemData, DC_HeroObj target) {
        super(itemData, target);

    }

    @Override
    public Image getImage(DC_ActiveObj t) {
        Image image = t.getImage();
        if (t.isThrow()) {
            image = ImageManager.getImage("UI\\actions\\modes\\ranged2.png");
        }
        image = ImageManager.getSizedVersion(image, new Dimension(getObjWidth(), getObjHeight()));
        if (isSelected(t)) {
            return ImageManager.applyBorder(image, BORDER.CIRCLE_BEVEL, true);
        }
        return image;
    }

    @Override
    public void clicked(DC_ActiveObj t) {
        super.clicked(t);

        SoundMaster.playStandardSound(STD_SOUNDS.CLICK);
        if (isInfoSelectionOn())
            t.getGame().getManager().infoSelect(t);

        try {
            anim = getAnim(t);
            animated = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        initTooltip(t);

    }

    private AttackAnimation getAnim(DC_ActiveObj t) {
        if (anim != null)
            target.getGame().getAnimationManager().getTempAnims().clear();

        Attack attack = EffectMaster.getAttackFromAction(t);
        AttackAnimation animation = new AttackAnimation(t);
        animation.setDrawMode(Animation.TARGET_ONLY);
        // animation.setGenericsAbove(true);
        t.getRef().setTarget(target.getId());
        t.getAbilities().getEffects().setRef(t.getRef());
        AttackCalculator calculator = new AttackCalculator(t, animation);
        attack.setDamage(calculator.calculateFinalDamage());

        // animation.setPhase(PHASE_TYPE.DAMAGE_FORMULA);
        List<Damage> rawDamage = DC_AttackMaster.precalculateRawDamage(attack);
        attack.setRawDamage(rawDamage);
        animation.addPhase(new AnimPhase(PHASE_TYPE.PRE_ATTACK, attack, rawDamage), 0);
        calculator.addSubPhase();

        animation.setPhase(PHASE_TYPE.PRE_ATTACK);
        animation.setFlipOver(true);
        animation.setStaticOffsetX(DC_Builder.getBfGridPosX());
        animation.setStaticOffsetY(DC_Builder.getBfGridPosY());
        return animation;
    }

    public boolean isInfoSelectionOn() {
        return true;
    }

    @Override
    public int getObjHeight() {
        return 50;
    }

    @Override
    public int getObjWidth() {
        return 50;
    }

    protected int getY(int n) {
        return row * getObjHeight() + 64;
    }

    protected int getX(int n) {
        return 48 + getObjWidth() * n;
    }

    public void initSize() {
        // if (data == null)
        // return;
        setPanelSize(new Dimension(IMG.getWidth(null) + scroll.getWidth(null), scroll.getHeight(null)));

    }

    private void drawTargetMark(Graphics g) {
        // TODO Auto-generated method stub

    }

    @Override
    public void paint(Graphics g) {

        g.drawImage(scroll, getLocation().x, getLocation().y, null);
        g.drawImage(scroll_text,
                getLocation().x + MigMaster.getCenteredPosition(scroll.getWidth(null), scroll_text.getWidth(null)),
                getLocation().y + scroll_text.getHeight(null) + 4, null);
        super.paint(g);
        if (!animated)
            drawTargetMark(g);
        else {
            anim.draw(g);
        }
        // g.drawImage(scroll, getLocation().x, getLocation().y, null);
        // Image img =
        // ImageTransformer.flipHorizontally(ImageManager.getBufferedImage(scroll));
        // g.drawImage(img, getLocation().x + getPanelWidth() -
        // IMG.getWidth(null)
        // - img.getWidth(null), getLocation().y, null);

        if (costTooltip != null)
            lastSelected.getGame().getToolTipMaster().drawTextItem(g, costTooltip);
        if (getTooltipTextItem() != null)
            lastSelected.getGame().getToolTipMaster().drawTextItem(g, getTooltipTextItem());

        if (anim != null)
            target.getGame().getAnimationManager().getTempAnims().add(anim);
    }

    private TextItem getTooltipTextItem() {
        return tooltipTextItem;
    }

    @Override
    protected int getBackgroundX() {
        return getLocation().x + scroll.getWidth(null);
    }

    @Override
    protected int getBackgroundY() {
        return getLocation().y + 12;
    }

    @Override
    protected int getBackgroundHeight() {
        return getPanelHeight() - 24;
    }

    @Override
    protected int getBackgroundWidth() {
        return getPanelWidth() - scroll.getWidth(null) * 2;
    }

    @Override
    protected void initTooltip(DC_ActiveObj t) {
        // draw on <?> drawCustomTooltip(...)?
        // Font font = TextItem.getDefaultTextItemFont().deriveFont(12.0f);
        Font font = FontMaster.getFont(FONT.NYALA, 14, Font.PLAIN);
        // t.getGame().getToolTipMaster().removeToolTips();

        PointX point = new PointX(getLocation(), 0, scroll.getHeight(null));
        costTooltip = t.getGame().getToolTipMaster().initActionToolTip(t, point);
        t.getGame().getToolTipMaster().removeToolTip(costTooltip);

        // PointX pointX = new PointX(getLocation(), scroll.getWidth(null), (row
        // + 1) * getObjHeight());
        PointX pointX = new PointX(point, 60, 24);
        tooltipTextItem = t.getGame().getToolTipMaster().getCustomTooltip(pointX, getTooltip(t), getPanelWidth() - 70,
                font);
    }

    @Override
    public void close() {
        WaitMaster.interrupt(getWaitOperation());
        super.close();
    }

    @Override
    protected String getTooltip(DC_ActiveObj t) {
        int damage = FutureBuilder.precalculateDamage(t, target, true);
        tooltip = "" + damage + " damage avrg.";
        List<Effect> effect = EffectMaster.getEffectsOfClass(t, AttackEffect.class);
        if (effect.size() > 0) {
            AttackCalculator attackCalculator = new AttackCalculator(((AttackEffect) effect.get(0)).getAttack(), true);
            int chance = attackCalculator.getCritOrDodgeChance();
            if (chance > 0)
                tooltip += ", % for ";

            if (DamageMaster.isLethal(damage, target))
                tooltip += "(lethal)"; // TODO possibly lethal
            else {
                if (((DC_HeroObj) target).canCounter(t))
                    tooltip += "(will retaliate)"; // TODO precalc dmg?
            }
        }
        return tooltip;
    }

}
