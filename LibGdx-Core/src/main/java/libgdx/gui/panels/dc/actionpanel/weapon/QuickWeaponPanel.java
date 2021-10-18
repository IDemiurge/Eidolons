package libgdx.gui.panels.dc.actionpanel.weapon;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.EidolonsGame;
import eidolons.game.core.ActionInput;
import eidolons.game.core.EUtils;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.anims.text.FloatingTextMaster;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.bf.generic.ImageContainer;
import libgdx.gui.controls.Clicker;
import libgdx.gui.generic.btn.SymbolButton;
import libgdx.gui.panels.TablePanelX;
import libgdx.gui.panels.dc.unitinfo.tooltips.SlotItemTooltip;
import libgdx.gui.panels.dc.unitinfo.tooltips.WeaponTooltip;
import libgdx.gui.tooltips.ScaleAndTextTooltip;
import libgdx.gui.tooltips.SmartClickListener;
import libgdx.gui.generic.btn.ButtonStyled;
import main.data.filesys.PathFinder;
import main.game.logic.action.context.Context;
import main.system.EventType;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by JustMe on 3/29/2018.
 * <p>
 * set default extra attacks
 */
public class QuickWeaponPanel extends TablePanelX {

    protected static final float WEAPON_POS_X = 12;
    protected ImageContainer border;
    protected FadeImageContainer weapon;
    protected ImageContainer background;
    protected WeaponDataSource dataSource;
    protected WeaponDataSource dataSourceAlt;
    protected SymbolButton toggleUnarmed;
    protected boolean unarmed;
    protected QuickAttackRadial radial;
    protected boolean offhand;

    public QuickWeaponPanel(boolean offhand) {
        super(100, 100);
        this.offhand = offhand;
        addActor(
                background = new ImageContainer(
                        StrPathBuilder.build(PathFinder.getComponentsPath(),
                                "dc", "quick weapon", "weapon background.png")));
        addActor(weapon = new FadeImageContainer());
        String suffix = offhand ? " offhand" : "";
        addActor(border = new ImageContainer(
                StrPathBuilder.build(PathFinder.getComponentsPath(),
                        "dc", "quick weapon", "border" +
                                suffix + ".png")));
        addActor(radial = createRadial(offhand));
        //   TODO      addActor(toggleUnarmed = new TextButtonX(STD_BUTTON.SPEED_UP));
        EventListener listener = getListener();
        if (listener != null) {
            addListener(listener);
        }

        initTooltip();

        background.setPosition(10, 10);
        weapon.setPosition(WEAPON_POS_X, 0);
        weapon.setFadeDuration(0.2f);
        addActor(toggleUnarmed = new SymbolButton(ButtonStyled.STD_BUTTON.UNARMED));
        toggleUnarmed.setVisible(false);
        //TODO useless for now?
        pack();
        if (offhand)
            toggleUnarmed.setPosition(0, background.getHeight() - toggleUnarmed.getHeight() / 2);
        else
            toggleUnarmed.setPosition(
                    background.getWidth() - toggleUnarmed.getWidth() / 2,
                    background.getHeight() - toggleUnarmed.getHeight() / 2);

    }

    protected QuickAttackRadial createRadial(boolean offhand) {
        return new QuickAttackRadial(this, offhand);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        super.draw(batch, parentAlpha);
    }

    protected void initTooltip() {
        if (getActiveWeaponDataSource() == null)
            return;
        weapon.clearListeners();
        SlotItemTooltip t = new WeaponTooltip(getActiveWeaponDataSource().getWeapon());
        weapon.addListener(t.getController());
    }

    public void setDataSource(WeaponDataSource source, boolean alt) {
        if (source != null) {
            if (source.getWeapon() == null) {
                //TODO clear
                setVisible(false);
                return;
            }
            if (source.equals(alt ? this.dataSourceAlt : this.dataSource)) {
            } else {
                if (alt) {
                    setDataSourceAlt(source);
                } else {
                    setDataSource(source);
                    initWeapon(source);
                    unarmed = false;
                }

            }
        } else {
            initWeapon(null);
        }
    }

    @Override
    public void updateAct(float delta) {

        Pair<WeaponDataSource, WeaponDataSource> pair =
                (Pair<WeaponDataSource, WeaponDataSource>) getUserObject();

        setDataSource(pair.getKey(), false);
        setDataSource(pair.getValue(), true);

        toggleUnarmed.clearListeners();
        if (dataSourceAlt != null) {
            toggleUnarmed.addListener(new Clicker(this::toggleUnarmed));
            toggleUnarmed.addListener(
                    new ScaleAndTextTooltip(toggleUnarmed, () -> (unarmed
                            ? dataSource.getName() : dataSourceAlt.getName()))
                            .getController());
            toggleUnarmed.setDisabled(false);
        } else {
            toggleUnarmed.setDisabled(true);
        }
        initTooltip();
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
    }

    protected void toggleUnarmed() {
        unarmed = !unarmed;
        initWeapon(getActiveWeaponDataSource());
    }

    public WeaponDataSource getActiveWeaponDataSource() {
        if (dataSource == null)
            if (dataSourceAlt == null)
                return null;

        if (dataSource == null)
            return dataSourceAlt;
        if (dataSourceAlt == null)
            return dataSource;
        return unarmed ? dataSourceAlt : dataSource;
    }

    protected void initWeapon(WeaponDataSource dataSource) {
        if (dataSource == null)
            weapon.setImage("");
        else
            weapon.setImage(      dataSource.getNormalImage());
    }


    protected EventListener getListener() {
        return new SmartClickListener(this) {
            @Override
            protected boolean isBattlefield() {
                return true;
            }

            @Override
            protected boolean checkActorExitRemoves(Actor toActor) {
                if (toActor == border)
                    return false;
                if (toActor == background)
                    return false;
                return super.checkActorExitRemoves(toActor);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (getDataSource() == null) {
                    return false;
                }
                if (getDataSource().getOwnerObj() == null) {
                    return false;
                }
                if (!getDataSource().getOwnerObj().isMine()) {
                    return false;
                }
                if (EidolonsGame.ATTACKS_DISABLED) {
                    EUtils.showInfoText("Attacks blocked - Chained!");
                    return false;
                }
                if (button == 1) {
                    // GuiEventManager.trigger(GuiEventType.RADIAL_MENU_CLOSE);
                    radial.openMenu();
                    // GuiEventManager.trigger(
                    //         getOpenEvent(),
                    //         dataSource.getWeapon());

                } else {
                    if (radial.isVisible())
                        if (radial.getColor().a > 0)
                            if (radial.getChildren().size > 0)
                                //                    if (DC_Game.game.getManager().isSelecting()){
                                return false;
                    //                    }
                    DC_ActiveObj attack = getDataSource().getOwnerObj().getAttackAction(offhand);
//                    if (attack != null && attack.isAttackGeneric()) {
//                        main.system.auxiliary.log.LogMaster.log(1, "GENERIC ATK WAS CHOSEN!");
//                        attack = null; TODO wrong place
//                    }
                    if (attack == null) {
                        FloatingTextMaster.getInstance().createFloatingText(VisualEnums.TEXT_CASES.REQUIREMENT,
                                "Cannot attack with this!",
                                getDataSource().getOwnerObj());
                        return false;
                    }
                    if (attack.getValidSubactions().isEmpty()) {

                        FloatingTextMaster.getInstance().createFloatingText(VisualEnums.TEXT_CASES.REQUIREMENT,
                                "Cannot make any attack with this!",
                                attack.getOwnerUnit());
                        return false;
                    }
                    attack.setAutoSelectionOn(true);
                    getActiveWeaponDataSource().getWeapon().getGame().getLoop().actionInputManual(
                            new ActionInput(attack
                                    , new Context(getDataSource().getOwnerObj(), null))
                    );
                }
                return false;

            }

            @Override
            public void entered() {
                if (getActiveWeaponDataSource() == null) {
                    return; //TODO igg hack
                }
                super.entered();
                weapon.setZIndex(getChildren().size - 1);
                radial.setZIndex(Integer.MAX_VALUE);
                weapon.setFadeDuration(0.25f);
                weapon.setImage(getActiveWeaponDataSource().getLargeImage());
                int i = !offhand ? -1 : 1;
                ActionMasterGdx.addMoveToAction(weapon, WEAPON_POS_X + 20 * i, 20, 0.75f);
            }

            @Override
            protected void exited() {
                if (getActiveWeaponDataSource() == null) {
                    return; //TODO igg hack
                }
                super.exited();
                weapon.setZIndex(1);
                radial.setZIndex(Integer.MAX_VALUE);
                weapon.setFadeDuration(0.5f);
                weapon.setImage(getActiveWeaponDataSource().getNormalImage());
                ActionMasterGdx.addMoveToAction(weapon, WEAPON_POS_X, 0, 0.75f);
            }
        };
    }

    protected EventType getOpenEvent() {
        return GuiEventType.QUICK_RADIAL;
    }

    public WeaponDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(WeaponDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public WeaponDataSource getDataSourceAlt() {
        return dataSourceAlt;
    }

    public void setDataSourceAlt(WeaponDataSource dataSourceAlt) {
        this.dataSourceAlt = dataSourceAlt;
    }

    public boolean isUnarmed() {
        return unarmed;
    }

}
