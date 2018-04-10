package eidolons.libgdx.gui.panels.dc.actionpanel.weapon;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.game.core.ActionInput;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.generic.btn.TextButtonX;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.tooltips.DynamicTooltip;
import main.data.filesys.PathFinder;
import main.game.logic.action.context.Context;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by JustMe on 3/29/2018.
 * <p>
 * set default extra attacks
 */
public class QuickWeaponPanel extends TablePanel {

    ImageContainer border;
    ImageContainer weapon;
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
          "2018", "quick weapon", "background.png")));
        addActor(weapon = new ImageContainer());
        addActor(border = new ImageContainer(
         StrPathBuilder.build(PathFinder.getComponentsPath(),
          "2018", "quick weapon", "border.png")));
        addActor(radial = new QuickAttackRadial(this, offhand));
//   TODO      addActor(toggleUnarmed = new TextButtonX(STD_BUTTON.SPEED_UP));
        addListener(getListener());
        weapon.addListener(new DynamicTooltip(() -> dataSource.getName()).getController());
//        weapon.addListener(new WeaponTooltip().getController());
        background.setPosition(10, 10);
        weapon.setPosition(15, 0);
    }

    @Override
    public void updateAct(float delta) {

        Pair<WeaponDataSource, WeaponDataSource> pair =
         (Pair<WeaponDataSource, WeaponDataSource>) getUserObject();

        this.dataSource = pair.getKey();
        this.dataSourceAlt = pair.getValue();

        if (dataSource != null) {
            if (unarmed)
                toggleUnarmed();
            else
                initWeapon(dataSource);
        } else {
            return;
        }
//        toggleUnarmed.clearListeners();
//        if (dataSourceAlt != null) {
//            toggleUnarmed.addListener(new Clicker(() -> toggleUnarmed()));
//            toggleUnarmed.addListener(
//             new ScaleAndTextTooltip(toggleUnarmed, () -> (unarmed
//              ? dataSource.getName() : dataSourceAlt.getName()))
//              .getController());
//            toggleUnarmed.setDisabled(false);
//        } else {
//            toggleUnarmed.setDisabled(true);
//        }
//        toggleUnarmed.addListener(new ClickListener() {
//            @Override
//            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                return super.touchDown(event, x, y, pointer, button);
//            }
//        });

    }

    private void toggleUnarmed() {
        unarmed = !unarmed;
        initWeapon(getActiveWeaponDataSource());
    }

    public WeaponDataSource getActiveWeaponDataSource() {
        return unarmed ? dataSourceAlt : dataSource;
    }

    private void initWeapon(WeaponDataSource dataSource) {
        weapon.setImage(
         StringMaster.getAppendedImageFile(
          dataSource.getSpriteImagePath(), " small"));

//        new Image(sprite = new Sprite(TextureCache.getOrCreateR(path)))
    }

    private EventListener getListener() {
        return new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == 1) {
                    GuiEventManager.trigger(GuiEventType.RADIAL_MENU_CLOSE);
                    GuiEventManager.trigger(
                     GuiEventType.CREATE_RADIAL_MENU,
                     dataSource.getWeapon());

                } else {
                    WaitMaster.receiveInput(WAIT_OPERATIONS.ACTION_INPUT,
                     new ActionInput(getDataSource().getOwnerObj().getAttackAction(offhand)
                    , new Context(getDataSource().getOwnerObj(), null ))
                    );
                }
                return false;

            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                return super.mouseMoved(event, x, y);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                weapon.setZIndex(getChildren().size - 2);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                weapon.setZIndex(1);
            }
        };
    }

    public WeaponDataSource getDataSource() {
        return dataSource;
    }

    public WeaponDataSource getDataSourceAlt() {
        return dataSourceAlt;
    }

    public boolean isUnarmed() {
        return unarmed;
    }

}
