package eidolons.libgdx.gui.panels.headquarters.tabs.stats;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import eidolons.content.DC_ContentValsManager;
import eidolons.game.core.EUtils;
import eidolons.game.module.herocreator.logic.skills.SkillMaster;
import eidolons.game.netherflame.additional.IGG_Demo;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SymbolButton;
import eidolons.libgdx.gui.panels.headquarters.HqActor;
import eidolons.libgdx.gui.panels.headquarters.HqPanel;
import eidolons.libgdx.gui.panels.headquarters.ValueTable;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import eidolons.system.text.DescriptionTooltips;
import main.content.values.parameters.PARAMETER;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.Strings;
import main.system.images.ImageManager;
import main.system.launch.Flags;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 4/18/2018.
 */
public class HqNewMasteryPanel extends ValueTable<PARAMETER,
        GroupX>
        implements HqActor {
    private final SymbolButton cancelButton;

    public HqNewMasteryPanel() {
        super(12, DC_ContentValsManager.getMasteries().size());
        setVisible(false);
        cancelButton = new SymbolButton(STD_BUTTON.CANCEL, this::fadeOut);
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
        if (HqPanel.getActiveInstance() == null) {
            return;
        }
        setPosition(HqPanel.getActiveInstance().traits.getX(),
                HqPanel.getActiveInstance().traits.getY());

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
        add(cancelButton).colspan(wrap + 1).right();
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
        group.addListener(new ValueTooltip("Learn " + datum.getDisplayedName() + Strings.NEW_LINE +
                DescriptionTooltips.tooltip(datum)).getController());
        group.addListener(getListener(datum, container));

        container.setSize(getElementSize().x, getElementSize().y);
        group.setSize(getElementSize().x, getElementSize().y);
        return group;
    }

    private EventListener getListener(PARAMETER datum, FadeImageContainer container) {
        return new SmartClickListener(container) {

            @Override
            protected void onTouchDown(InputEvent event, float x, float y) {
                    EUtils.onConfirm("Learn " + datum+"?", true, ()-> {
                        if (HqStatMaster.learnMastery(getUserObject().getEntity(), datum)) {
                            modelChanged();
                            fadeOut();
                        }
                                        });
            }

        };
    }


    @Override
    protected GroupX[] initActorArray() {
        return new GroupX[size];
    }


    @Override
    protected PARAMETER[] initDataArray() {
        List<PARAMETER> unlocked = SkillMaster.getUnlockedMasteries(getUserObject().getEntity());
        if (Flags.isIggDemoRunning()) {
            List<PARAMETER> pool = IGG_Demo.getMasteriesForHero(getUserObject().getEntity().getName());
            pool.removeIf(unlocked::contains);
            return pool.toArray(new PARAMETER[0]) ;
        }
        List<PARAMETER> availableMasteries = new ArrayList<>(
                DC_ContentValsManager.getMasteries());

        availableMasteries.removeIf(unlocked::contains);

        availableMasteries.removeIf(p ->
                !SkillMaster.isMasteryAvailable(p, getUserObject().getEntity()));
        return availableMasteries.toArray(new PARAMETER[0]);
    }

}
