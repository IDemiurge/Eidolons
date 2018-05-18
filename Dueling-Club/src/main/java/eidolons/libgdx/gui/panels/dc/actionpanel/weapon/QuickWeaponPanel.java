package eidolons.libgdx.gui.panels.dc.actionpanel.weapon;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.core.ActionInput;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.controls.Clicker;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.TextButtonX;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.dc.unitinfo.tooltips.WeaponTooltip;
import eidolons.libgdx.gui.tooltips.ScaleAndTextTooltip;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import main.data.filesys.PathFinder;
import main.game.logic.action.context.Context;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by JustMe on 3/29/2018.
 * <p>
 * set default extra attacks
 */
public class QuickWeaponPanel extends TablePanel {

    private static final float WEAPON_POS_X = 12;
    ImageContainer border;
    FadeImageContainer weapon;
    ImageContainer background;
    WeaponDataSource dataSource;
    WeaponDataSource dataSourceAlt;
    TextButtonX toggleUnarmed;
    boolean unarmed;
    QuickAttackRadial radial;
    boolean offhand;

    public QuickWeaponPanel(boolean offhand) {
        this.offhand = offhand;
        addActor(background = new ImageContainer(
         StrPathBuilder.build(PathFinder.getComponentsPath(),
          "dc", "quick weapon", "background.png")));
        addActor(weapon = new FadeImageContainer());
        String suffix = offhand ? " offhand" : "";
        addActor(border = new ImageContainer(
         StrPathBuilder.build(PathFinder.getComponentsPath(),
          "dc", "quick weapon", "border" +
           suffix + ".png")));
        addActor(radial = new QuickAttackRadial(this, offhand));
//   TODO      addActor(toggleUnarmed = new TextButtonX(STD_BUTTON.SPEED_UP));
        addListener(getListener());

        initTooltip();

        background.setPosition(10, 10);
        weapon.setPosition(WEAPON_POS_X, 0);
        weapon.setFadeDuration(0.2f);
        addActor(toggleUnarmed = new TextButtonX(STD_BUTTON.UNARMED));
        pack();
        if (offhand)
            toggleUnarmed.setPosition(0, background.getHeight() - toggleUnarmed.getHeight()/2);
        else
            toggleUnarmed.setPosition(
             background.getWidth() - toggleUnarmed.getWidth()/2,
             background.getHeight() - toggleUnarmed.getHeight()/2);

    }

    private void initTooltip() {
        if (getActiveWeaponDataSource()==null )
            return;
        weapon.clearListeners();
        WeaponTooltip t = new WeaponTooltip(getActiveWeaponDataSource().getWeapon());
        weapon.addListener(t.getController());
    }

    public void setDataSource(WeaponDataSource source, boolean alt) {
        if (source != null)
            if (source.equals(alt ? this.dataSourceAlt : this.dataSource)) {
                return;
            } else {
                if (alt) {
                    setDataSourceAlt(source);
                } else {
                    setDataSource(source);
                    initWeapon(source);
                    unarmed = false;
                }

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
            toggleUnarmed.addListener(new Clicker(() -> toggleUnarmed()));
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

    private void toggleUnarmed() {
        unarmed = !unarmed;
        initWeapon(getActiveWeaponDataSource());
    }

    public WeaponDataSource getActiveWeaponDataSource() {
        if (dataSource==null )
        if (dataSourceAlt==null )
            return null ;

        if (dataSource==null )
            return dataSourceAlt;
        if (dataSourceAlt==null )
            return dataSource;
        return unarmed ? dataSourceAlt : dataSource;
    }

    private void initWeapon(WeaponDataSource dataSource) {
        weapon.setImage(
         dataSource. getNormalImage());
    }




    private EventListener getListener() {
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
                if (button == 1) {
                    GuiEventManager.trigger(GuiEventType.RADIAL_MENU_CLOSE);
                    GuiEventManager.trigger(
                     GuiEventType.CREATE_RADIAL_MENU,
                     dataSource.getWeapon());

                } else {
                    if (radial.isVisible())
                        if (radial.getColor().a>0)
//                    if (DC_Game.game.getManager().isSelecting()){
                        return false;
//                    }
                    DC_ActiveObj attack = getDataSource().getOwnerObj().getAttackAction(offhand);
                    attack.setAutoSelectionOn(true);
                    getActiveWeaponDataSource().getWeapon().getGame().getLoop().actionInput(
                     new ActionInput(attack
                      , new Context(getDataSource().getOwnerObj(), null))
                    );
                }
                return false;

            }

            @Override
            public void entered() {
                super.entered();
                weapon.setZIndex(getChildren().size - 2);
                radial.setZIndex(Integer.MAX_VALUE);
                weapon.setFadeDuration(0.25f);
                weapon.setImage(getActiveWeaponDataSource(). getLargeImage());
                int i = !offhand ? -1 : 1;
                ActorMaster.addMoveToAction(weapon, WEAPON_POS_X + 20 * i, 20, 0.75f);
            }

            @Override
            protected void exited() {
                super.exited();
                weapon.setZIndex(1);
                radial.setZIndex(Integer.MAX_VALUE);
                weapon.setFadeDuration(0.5f);
                weapon.setImage(getActiveWeaponDataSource().getNormalImage());
                ActorMaster.addMoveToAction(weapon, WEAPON_POS_X , 0, 0.75f);
            }
        };
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
