package eidolons.libgdx.gui.panels.headquarters.tabs.stats;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import eidolons.content.DC_ContentValsManager;
import eidolons.content.DescriptionMaster;
import eidolons.content.PARAMS;
import eidolons.game.module.herocreator.logic.skills.SkillMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.headquarters.HqActor;
import eidolons.libgdx.gui.panels.headquarters.ValueTable;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
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
 implements HqActor  {
    private final SmartButton cancelButton;

    public HqNewMasteryPanel() {
        super(6, DC_ContentValsManager.getMasteries().size());
        setVisible(false);
        cancelButton = new SmartButton(STD_BUTTON.CANCEL, ()->{
            fadeOut();
        });
        initDefaultBackground();
        GuiEventManager.bind(GuiEventType.SHOW_MASTERY_LEARN, p -> {
            if (p.get() == null) {
                fadeOut();
                return;
            }
            fadeIn();
            setPosition(getX(), GdxMaster.centerHeight(this));
            setUserObject(p.get());
        });
    }

    protected Vector2 getElementSize() {
        return new Vector2(50, 50);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
    }

    @Override
    public void clear() {
        clearChildren();
        clearListeners();
    }

    @Override
    public void clearActions() {
        super.clearActions();
    }

    @Override
    public void init() {
        super.init();
//        row();
        add(cancelButton).colspan(wrap+1).right();
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

        group.addListener(new ValueTooltip("Learn " + datum.getName()).getController());

        container.setSize(getElementSize().x, getElementSize().y);
        group.setSize(getElementSize().x, getElementSize().y);
        return group;
    }

    private EventListener getListener(PARAMETER datum, FadeImageContainer container) {
        return new SmartClickListener(container) {

            @Override
            protected void onTouchDown(InputEvent event, float x, float y) {
                if (event.getButton() == 1) {
                    GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP,
                     new ValueTooltip(DescriptionMaster.
                      getMasteryDescription((PARAMS) datum)));

                    return;
                }
                if (HqStatMaster.learnMastery(getUserObject().getEntity(), datum)) {
                    modelChanged();
                    fadeOut();
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
        List<PARAMETER> unlocked = SkillMaster.getUnlockedMasteries(getUserObject().getEntity());
        availableMasteries.removeIf(p ->
         unlocked.contains(p));
        return availableMasteries.toArray(new PARAMETER[availableMasteries.size()]);
    }

}
