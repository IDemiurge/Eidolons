package main.client.cc.gui.neo.points;

import main.client.cc.CharacterCreator;
import main.client.cc.gui.MiddlePanel;
import main.client.cc.gui.misc.PoolComp;
import main.content.PARAMS;
import main.content.VALUE;
import main.entity.obj.unit.DC_HeroObj;
import main.entity.type.ObjType;
import main.swing.components.buttons.CustomButton;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.GraphicComponent;
import main.swing.generic.components.misc.GraphicComponent.STD_COMP_IMAGES;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.MigMaster;
import main.system.math.DC_MathManager;

public class StatsControlComponent extends G_Panel {

    private static final String XP = " xp";
    private static final String GOLD = " gold";
    private static final String ATTR = "Attribute points";
    private static final String MSTR = "Mastery points";
    private static Integer Y_GOLD;
    private static Integer Y_POINTS;
    private ObjType bufferType;
    private ObjType backupBuffer;
    private PoolComp attrPool;
    private PoolComp mstrPool;

    private PoolComp xpPool;
    private PoolComp goldPool;
    private PoolComp mstrXpCostPool;
    private PoolComp mstrGoldCostPool;
    private PoolComp attrXpCostPool;
    private PoolComp attrGoldCostPool;
    private DC_HeroObj hero;
    private HC_InfoTextPanel attrPanel;
    private HC_InfoTextPanel mstrPanel;
    private CustomButton okButton;
    private CustomButton cancelButton;
    private MiddlePanel mp;

    private BuyButton buyAttrGold;
    private BuyButton buyMstrGold;
    private BuyButton buyAttrXP;
    private BuyButton buyMstrXP;

    private boolean panelSwitch;

    public StatsControlComponent(DC_HeroObj hero) {
        this.hero = hero;
        bufferType = new ObjType(hero.getType());
        backupBuffer = new ObjType(hero.getType());
        Y_POINTS = HC_InfoTextPanel.V.getHeight() + 100;
        Y_GOLD = HC_InfoTextPanel.V.getHeight() + 200;

    }

    public void init() {
        initComps();
        addComps();
    }

    @Override
    public boolean isAutoSizingOn() {
        return true;
    }

    protected void addComps() {
        add(mstrPanel, "id mstrPanel, pos 0 0");
        add(attrPanel, "id attrPanel, pos 0 mstrGoldCostPool.y2");

        add(buyAttrXP, "id buyAttrXP, pos 0 mstrPanel.y2");

        add(attrXpCostPool, "id attrXpCostPool, pos buyAttrXP.x2 mstrPanel.y2");

        add(xpPool, "id xpPool, pos attrXpCostPool.x2 mstrPanel.y2");
        add(new GraphicComponent(STD_COMP_IMAGES.XP), "id xp, pos attrXpCostPool.x2+"
                + (xpPool.getVisuals().getWidth() - STD_COMP_IMAGES.XP.getWidth()) / 2
                + " mstrPanel.y2+" + STD_COMP_IMAGES.XP.getHeight());
        add(mstrXpCostPool, "id mstrXpCostPool, pos xpPool.x2 mstrPanel.y2");

        add(buyMstrXP, "id buyMstrXP, pos mstrXpCostPool.x2 mstrPanel.y2");

        add(attrPool, "id attrPool, pos 0 " + Y_POINTS);
        add(mstrPool, "id mstrPool, pos "
                + (attrPanel.getVisuals().getWidth() - mstrPool.getVisuals().getWidth()) + " "
                + Y_POINTS);
        add(okButton, "id okButton, pos "
                + MigMaster.getCenteredPosition(attrPanel.getVisuals().getWidth(), okButton
                .getVisuals().getWidth() * 2) + " " + (Y_POINTS - 15));
        add(cancelButton, "id cancelButton, pos okButton.x2 " + (Y_POINTS - 15));

        add(buyAttrGold, "id buyAttrGold, pos 0 " + Y_GOLD);

        add(attrGoldCostPool, "id attrGoldCostPool, pos buyAttrGold.x2 " + Y_GOLD);

        add(goldPool, "id goldPool, pos attrGoldCostPool.x2 " + Y_GOLD);
        add(new GraphicComponent(STD_COMP_IMAGES.GOLD), "id gold, pos attrGoldCostPool.x2+"
                + (goldPool.getVisuals().getWidth() - STD_COMP_IMAGES.GOLD.getWidth()) / 2 + " "
                + (Y_GOLD - STD_COMP_IMAGES.GOLD.getHeight()));
        add(mstrGoldCostPool, "id mstrGoldCostPool, pos goldPool.x2 " + Y_GOLD);
        add(buyMstrGold, "id buyMstrGold, pos mstrGoldCostPool.x2 " + Y_GOLD);

    }

    private void initComps() {
        initInfoPanels();
        initBuyButtons();
        initPools();
        initYesNoButtons();

    }

    public void refreshPools() {
        attrPool.refresh();
        mstrPool.refresh();
        xpPool.refresh();
        goldPool.refresh();
    }

    @Override
    public void refresh() {
        removeAll();
        initComps();
        addComps();
        revalidate();

    }

    private void initInfoPanels() {
        attrPanel = new HC_InfoTextPanel(bufferType, null);
        mstrPanel = new HC_InfoTextPanel(bufferType, null);

    }

    protected void initYesNoButtons() {
        okButton = new CustomButton(VISUALS.OK) {
            @Override
            public void handleClick() {
                // TODO enabled?
                OK();
                playClickSound();
            }
        };
        cancelButton = new CustomButton(VISUALS.CANCEL) {
            @Override
            public void handleClick() {
                // TODO dt flag for enabled()?
                resetHero();
                playDisabledSound();
            }
        };
        cancelButton.activateMouseListener();
    }

    public void resetHero() {
        LogMaster.log(1, "*** backupBuffer reset: " + backupBuffer);
        CharacterCreator.getHeroManager().applyChangedType(hero, backupBuffer);
        resetBuffer();
        resetBackupBuffer();
        CharacterCreator.getHeroPanel(hero).refresh();
    }

    public void resetBackupBuffer() {
        backupBuffer = new ObjType(hero.getType());
        LogMaster.log(1, "*** backupBuffer reset: " + backupBuffer);
    }

    public void resetBuffer() {
        setBufferType(new ObjType(hero.getType()));
        LogMaster.log(1, "*** buffer reset: " + bufferType);
        if (mp != null) {
            getMp().resetBuffer();
        }
    }

    protected void OK() {
        CharacterCreator.getHeroManager().applyChangedType(hero, getBufferType());
        resetBackupBuffer();
    }

    protected void initPools() {
        attrPool = new PoolComp(bufferType, PARAMS.ATTR_POINTS, ATTR, false);
        mstrPool = new PoolComp(bufferType, PARAMS.MASTERY_POINTS, MSTR, false);

        xpPool = new PoolComp(bufferType, PARAMS.XP, XP, false);
        goldPool = new PoolComp(bufferType, PARAMS.GOLD, GOLD, false);

        String cost = DC_MathManager.getBuyCost(false, false, hero) + XP;
        mstrXpCostPool = new PoolComp(cost);
        cost = DC_MathManager.getBuyCost(false, true, hero) + GOLD;
        mstrGoldCostPool = new PoolComp(cost);
        cost = DC_MathManager.getBuyCost(true, false, hero) + XP;
        attrXpCostPool = new PoolComp(cost);
        cost = DC_MathManager.getBuyCost(true, true, hero) + GOLD;
        attrGoldCostPool = new PoolComp(cost);

    }

    protected void initBuyButtons() {
        buyAttrGold = new BuyButton(this, true, true);

        buyMstrGold = new BuyButton(this, false, true);

        buyAttrXP = new BuyButton(this, true, false);

        buyMstrXP = new BuyButton(this, false, false);

    }

    ;

    protected boolean checkParam(int cost, boolean gold) {
        return bufferType.getIntParam((gold) ? PARAMS.GOLD : PARAMS.XP) >= cost;
    }

    public void buyPoints(boolean attr, boolean gold) {
        PARAMS cost_param = (gold) ? PARAMS.GOLD : PARAMS.XP;
        PARAMS param = (attr) ? PARAMS.ATTR_POINTS : PARAMS.MASTERY_POINTS;
        PARAMS buyParam = (attr) ? (gold) ? PARAMS.ATTR_BOUGHT_WITH_GOLD
                : PARAMS.ATTR_BOUGHT_WITH_XP : (gold) ? PARAMS.MASTERY_BOUGHT_WITH_GOLD
                : PARAMS.MASTERY_BOUGHT_WITH_XP;

        int amount = DC_MathManager.getBuyCost(attr, gold, bufferType);
        bufferType.modifyParameter(cost_param, -amount);
        bufferType.modifyParameter(param, 1);
        bufferType.modifyParameter(buyParam, 1);
        refresh();
    }

    public void setValueToolTip(VALUE value) {
        panelSwitch = !panelSwitch;
        if (panelSwitch) {
            getMstrPanel().setValue(value);
        } else {
            getMstrPanel().setValue(value);
        }
    }

    public ObjType getBufferType() {
        return bufferType;
    }

    public void setBufferType(ObjType bufferType) {
        this.bufferType = bufferType;
    }

    public MiddlePanel getMp() {
        return mp;
    }

    public void setMp(MiddlePanel mp) {
        this.mp = mp;
    }

    public HC_InfoTextPanel getAttrPanel() {
        return attrPanel;
    }

    public void setAttrPanel(HC_InfoTextPanel attrPanel) {
        this.attrPanel = attrPanel;
    }

    public HC_InfoTextPanel getMstrPanel() {
        return mstrPanel;
    }

    public void setMstrPanel(HC_InfoTextPanel mstrPanel) {
        this.mstrPanel = mstrPanel;
    }

}
