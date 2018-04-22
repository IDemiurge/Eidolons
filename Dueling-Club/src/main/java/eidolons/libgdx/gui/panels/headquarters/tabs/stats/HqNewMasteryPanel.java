package eidolons.libgdx.gui.panels.headquarters.tabs.stats;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import eidolons.content.DC_ContentValsManager;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.TextButtonX;
import eidolons.libgdx.gui.panels.headquarters.HqActor;
import eidolons.libgdx.gui.panels.headquarters.ValueTable;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import eidolons.libgdx.stage.Closable;
import eidolons.libgdx.stage.StageWithClosable;
import eidolons.system.math.DC_MathManager;
import main.content.values.parameters.PARAMETER;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.images.ImageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 4/18/2018.
 */
public class HqNewMasteryPanel extends ValueTable<PARAMETER,
 GroupX>
 implements HqActor, Closable {
    private final TextButtonX cancelButton;

    public HqNewMasteryPanel() {
        super(4, DC_ContentValsManager.getMasteries().size());
        setVisible(false);
        cancelButton = new TextButtonX(STD_BUTTON.CANCEL, ()->{
            close();
        });
        initDefaultBackground();
        GuiEventManager.bind(GuiEventType.SHOW_MASTERY_LEARN, p -> {
            if (p.get() == null) {
                close();
                return;
            }
            open();
            setUserObject(p.get());
        });
    }

    protected Vector2 getElementSize() {
        return new Vector2(40, 40);
    }
    @Override
    public void open() {
        ((StageWithClosable) getStage()).closeDisplayed();
        ((StageWithClosable)   getStage()) .setDisplayedClosable(this);
//        fadeIn();
        setVisible(true);
    }

    public void close() {
//        fadeOut();
        setVisible(false);
    }

    @Override
    public void init() {
        super.init();
        row();
        add(cancelButton).colspan(4).right();
    }

    @Override
    public HqHeroDataSource getUserObject() {
        return (HqHeroDataSource) super.getUserObject();
    }

    @Override
    protected GroupX createElement(PARAMETER datum) {
        FadeImageContainer container =
         new FadeImageContainer(ImageManager.getValueIconPath(datum));
        GroupX group = new GroupX();
        group.addActor(container);
//        group.addActor(overlay);
        group.addListener(getListener(datum, container));
        container.setSize(getElementSize().x, getElementSize().y);
        group.setSize(getElementSize().x, getElementSize().y);
        return group;
    }

    private EventListener getListener(PARAMETER datum, FadeImageContainer container) {
        return new SmartClickListener(container) {
            @Override
            protected void onTouchDown(InputEvent event, float x, float y) {
                if (HqStatMaster.learnMastery(getUserObject().getEntity(), datum)) {
                    modelChanged();
                    close();
                }
            }
        };
    }


    @Override
    protected GroupX[] initActorArray() {
        return new GroupX[size];
    }


    @Override
    protected PARAMETER[] initDataArray() {
        List<PARAMETER> availableMasteries = new ArrayList<>(
         DC_ContentValsManager.getMasteries());
        List<PARAMETER> unlocked = DC_MathManager.getUnlockedMasteries(getUserObject().getEntity());
        availableMasteries.removeIf(p ->
         unlocked.contains(p));
        return availableMasteries.toArray(new PARAMETER[availableMasteries.size()]);
    }

}
