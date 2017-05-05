package main.client.cc.gui.misc;

import main.client.cc.CharacterCreator;
import main.client.cc.HeroManager;
import main.client.cc.gui.MainViewPanel.HERO_VIEWS;
import main.content.*;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.swing.PointX;
import main.swing.components.buttons.CustomButton;
import main.swing.components.panels.DC_BuffPanel;
import main.swing.components.panels.page.info.DC_PagedInfoPanel;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.GraphicComponent;
import main.swing.generic.components.misc.GraphicComponent.STD_COMP_IMAGES;
import main.swing.generic.services.listener.MouseClickListener;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager;
import main.system.math.Formula;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class AddRemoveComponent extends G_Panel {

    protected static final int FULL_WIDTH = 285;
    protected static final int FULL_HEIGHT = 218;
    // protected static final int ADD_REMOVE_BORDER_Y = 64;

    protected static final int POOL_Y = 0;

    protected static final int ADD_X = 9;
    protected static final int ADD_Y = 4;

    protected static final int REMOVE_X = 1;
    protected static final int REMOVE_Y = VISUALS.ADD.getHeight() + 7;

    protected HERO_VIEWS view;
    protected PARAMETER param;
    protected PARAMETER cost_param;
    protected DC_PagedInfoPanel upperPanel;
    protected DC_PagedInfoPanel lowerPanel;
    protected Entity hero;
    DC_BuffPanel buffComp = new DC_BuffPanel(8);
    DC_BuffPanel buffComp2 = new DC_BuffPanel(8);
    private JLabel upperIcon;
    private JLabel lowerIcon;

    public AddRemoveComponent(DC_PagedInfoPanel upperPanel, DC_PagedInfoPanel lowerPanel,
                              Entity hero) {
        panelSize = new Dimension(FULL_WIDTH, FULL_HEIGHT);
        this.upperPanel = upperPanel;
        this.lowerPanel = lowerPanel;
        this.hero = hero;
        buffComp.setCustomMouseListener(new MouseClickListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                PointX p = new PointX(e.getPoint(), getX(), getY());
                p.translate(buffComp.getX(), buffComp.getY());
            }
        });
        buffComp2.setCustomMouseListener(new MouseClickListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                PointX p = new PointX(e.getPoint(), getX(), getY());
                p.translate(buffComp2.getX(), buffComp2.getY());
            }
        });
        addComps();
    }

    protected void addComps() {
        addBuffComps();
        if (CharacterCreator.getInfoSelected() != null) {
            if (CharacterCreator.getInfoSelected().getOBJ_TYPE_ENUM() == DC_TYPE.ACTIONS) {
                addIconComps();
                return;
            }
        }
        addPools();
        addIconComps();
        if (!isBuffPanelNeeded(upperPanel.getEntity())) {
            add(new GraphicComponent(VISUALS.ADD_REMOVE.getImage()), "id arc, pos " + FULL_WIDTH
                    + "/2-" + VISUALS.ADD_REMOVE.getWidth() + "/2 " + GuiManager.getSmallObjSize());
        }

        addRemoveComp();
        addAddComp();
    }

    private void addBuffComps() {
        if (isBuffPanelNeeded(upperPanel.getEntity())) {
            add(buffComp, "id buffComp, pos @center_x 0");
            buffComp.setObj((Obj) upperPanel.getEntity());
            buffComp.refresh();
        }
        if (isBuffPanelNeeded(lowerPanel.getEntity())) {
            add(buffComp2, "id buffComp2, pos @center_x " + FULL_HEIGHT + "-32");
            buffComp2.setObj((Obj) lowerPanel.getEntity());
            buffComp2.refresh();
        }

    }

    protected void addRemoveComp() {
        boolean blocked = getView() == HERO_VIEWS.LIBRARY || getView() == HERO_VIEWS.CLASSES
                || getView() == HERO_VIEWS.SKILLS;
        // verbatim and other cases?
        // add(getRemoveComp(blocked), "id remove, pos add.x+" + REMOVE_X +
        // " add.y+" + REMOVE_Y);

        String relative = "add";

        String offsetY = "" + ADD_Y;
        String offsetX = "" + ADD_X;

        if (isBuffPanelNeeded(upperPanel.getEntity())) {
            relative = "poolhero";
            offsetY = "poolhero.h";
            offsetX = "(poolhero.w-remove.w)/2";
        }
        add(getRemoveComp(blocked), "id remove, pos " + relative + ".x+" + offsetX + " " + relative
                + ".y+" + offsetY);
    }

    protected void addAddComp() {
        // blocked = item.checkRequirements();
        String relative = "arc";
        String offsetY = "" + ADD_Y;
        String offsetX = "" + ADD_X;
        if (isBuffPanelNeeded(upperPanel.getEntity())) {
            relative = "pool";
            offsetY = "pool.h";
            offsetX = "(pool.w-add.w)/2";
        }
        add(getAddComp(false), "id add, pos " + relative + ".x+" + offsetX + " " + relative + ".y+"
                + offsetY);
    }

    @Override
    public void refresh() {
        removeAll();
        addComps();
        revalidate();
        repaint();
    }

    public void refreshNoRemove() {

    }

    protected void addPools() {
        param = getParam();
        cost_param = ContentManager.getCostParam(param);
        addHeroPool();
        // if (upperPanel.getEntity()!=null )
        // if (upperPanel.getEntity() instanceof ObjType)
        addItemPool(upperPanel.getEntity(), false);
        // if (lowerPanel.getEntity() instanceof ObjType)
        addItemPool(lowerPanel.getEntity(), true);
    }

    private void addHeroPool() {
        PoolComp heroPool = new PoolComp(hero, param, param.getName(), false);
        String pos = "id symbol, pos " + getLeftSymbolX() + " buffComp.y2";// +
        // (POOL_Y);
        add(getPoolSymbol(), pos);
        pos = "id poolhero, pos " + getLeftPoolX(heroPool) + " " + "symbol.y2 ";
        add(heroPool, pos);
    }

    private String getLeftSymbolX() {
        return "(icon.x-" + getPoolSymbol().getImg().getWidth(null) + ")/2";
    }

    private String getLeftPoolX(PoolComp pool) {
        return "(icon.x-" + pool.getVisuals().getWidth() + ")/2";
    }

    private void addItemPool(Entity item, boolean heroItem) {
        if (item == null) {
            return;
        }
        CostPoolComp pool = new CostPoolComp(item, cost_param);
        String X = "icon.x2+("
                + (FULL_WIDTH + "-icon.x2-" + getPoolSymbol().getImg().getWidth(null)) + ")/2";
        // if (heroItem)
        // if (isOnTheLeft(item))
        // X = getLeftSymbolX();

        String id = "symbol2" + (heroItem ? "hero" : "");
        String pos = "id "
                + id
                + ", pos "
                + X
                + " "
                + (heroItem ? "icon2.y2-" + getPoolSymbol().getImg().getHeight(null) + "-"
                + pool.getVisuals().getHeight() : "buffComp.y2");
        add(getPoolSymbol(), pos);

        X = "(icon.x-" + pool.getVisuals().getWidth() + ")/2";
        // if (heroItem)
        // if (isOnTheLeft(item))
        // X = getLeftPoolX(pool);

        pos = "id pool" + (heroItem ? "second" : "") + ", pos " +

                "icon.x2+" + X +

                " " + "" + id + ".y2 ";
        add(pool, pos);
    }

    public boolean isBuffPanelNeeded(Entity item) {
        return item instanceof Obj;
    }

    private boolean isOnTheLeft(Entity item) {
        return !C_OBJ_TYPE.ITEMS.equals(item.getOBJ_TYPE_ENUM());
    }

    protected void addIconComps() {
        String pos = "id icon, pos " + FULL_WIDTH + "/2" + "-32 buffComp.y2";
        if (isBuffPanelNeeded(upperPanel.getEntity())) {
            pos += "-20";
        }
        if (upperPanel.getEntity() != null) {
            upperIcon = new JLabel(upperPanel.getEntity().getIcon());
            add((upperIcon), pos);
        } else {
            add(new JLabel(ImageManager.getIcon(ImageManager.getDefaultEmptyListIcon())), pos);
        }
        pos = "id icon2, pos " + +FULL_WIDTH + "/2-" + "32 64+" + VISUALS.ADD_REMOVE.getHeight();
        if (lowerPanel.getEntity() != null) {
            lowerIcon = new JLabel(lowerPanel.getEntity().getIcon());
            add(lowerIcon, pos);
        } else {
            add(new JLabel(ImageManager.getIcon(ImageManager.getDefaultEmptyListIcon())), pos);
        }

    }

    protected GraphicComponent getPoolSymbol() {
        if (getView() == HERO_VIEWS.SHOP) {
            return new GraphicComponent(STD_COMP_IMAGES.GOLD.getImg());
        }
        return new GraphicComponent(STD_COMP_IMAGES.XP.getImg());
    }

    protected CustomButton getAddComp(boolean blocked) {
        final AddRemoveComponent arc = this;
        CustomButton comp = new CustomButton((blocked ? VISUALS.ADD_BLOCKED : VISUALS.ADD)) {
            @Override
            public void handleClick() {
                if (!isEnabled()) {
                    playDisabledSound();
                } else {
                    arc.add();
                }
            }
        };
        comp.setEnabled(!blocked);
        return comp;
    }

    protected CustomButton getRemoveComp(boolean blocked) {
        final AddRemoveComponent arc = this;
        CustomButton comp = new CustomButton((blocked ? VISUALS.REMOVE_BLOCKED : VISUALS.REMOVE)) {
            @Override
            public void handleClick() {
                if (!isEnabled()) {
                    playDisabledSound();
                } else {
                    arc.remove();
                }
            }
        };
        comp.setEnabled(!blocked);
        return comp;
    }

    public void add() {
        CharacterCreator.getHeroPanel((Unit) hero).getMvp().getItemManager().addType(
                (ObjType) upperPanel.getEntity());
    }

    public void remove() {
        CharacterCreator.getHeroPanel((Unit) hero).getMvp().getItemManager().removeType(
                lowerPanel.getEntity());
    }

    protected PARAMETER getParam() {
        if (getView() == HERO_VIEWS.SHOP) {
            return PARAMS.GOLD;
        }
        return PARAMS.XP;
    }

    public HERO_VIEWS getView() {
        return view;
    }

    public void setView(HERO_VIEWS view) {
        this.view = view;
    }

    public JLabel getUpperIcon() {
        return upperIcon;
    }

    public JLabel getLowerIcon() {
        return lowerIcon;
    }

    public class CostPoolComp extends PoolComp {

        private OBJ_TYPE TYPE;
        private PROPERTY PROP; // hero selected item pool as well then, with
        // Verbatim cost pool
        private boolean heroItem;
        private boolean ready;

        public CostPoolComp(Entity entity, PARAMETER p) {
            this(entity, p, null, null);

        }

        public CostPoolComp(Entity entity, PARAMETER p, OBJ_TYPE TYPE, PROPERTY PROP) {
            super(entity, p, p.getName(), false);
            if (TYPE != null) {
                heroItem = true;
            }
            this.TYPE = TYPE;
            this.PROP = PROP;
            ready = true;
            update();
            x = getDefaultX();
        }

        public void update() {
            text = getText();
        }

        @Override
        protected String getText() {
            if (!ready) {
                return "";
            }
            int discount;
            discount = new Formula(getEntity().getParam(cost_param)
                    + "-"
                    + (!heroItem ? HeroManager.getCost(getEntity(), hero) : HeroManager.getCost(
                    getEntity(), hero, TYPE, PROP))).getInt(hero.getRef());
            return getEntity().getParamRounded(cost_param, false)
                    + (discount > 0 ? "-" + discount : "");
        }
    }
}
