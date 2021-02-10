package libgdx.gui.panels.headquarters.creation;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.game.core.EUtils;
import libgdx.GdxMaster;
import libgdx.TiledNinePatchGenerator;
import libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import libgdx.gui.menu.selection.SelectableItemDisplayer;
import libgdx.gui.panels.TablePanelX;
import libgdx.gui.panels.headquarters.HqElement;
import libgdx.gui.panels.headquarters.creation.preview.HcPreview;
import libgdx.gui.panels.headquarters.creation.selection.HcDeitySelectionPanel;
import eidolons.libgdx.gui.panels.headquarters.creation.selection.misc.*;
import libgdx.gui.panels.headquarters.creation.selection.race.HcRaceSelectPanel;
import libgdx.gui.panels.headquarters.creation.selection.skillset.HcSkillsetPanel;
import libgdx.gui.panels.headquarters.creation.selection.misc.*;
import main.content.enums.entity.HeroEnums.RACE;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 6/5/2018.
 */
public class HeroCreationWorkspace extends HqElement implements SelectableItemDisplayer {
    private final Cell displayableContainer;
    private SelectableItemData item;
    private SelectableItemData previousItem;
    private Group displayed;
    private HcPreview preview;
    private Map<HeroCreationSequence.HERO_CREATION_ITEM, TablePanelX> cache = new HashMap<>();

    public static final int WIDTH=1100;
    public static final int PREVIEW_WIDTH=350;
    public static final int PREVIEW_HEIGHT=800;
    public static final int SELECTION_WIDTH=625;
    public static final int SELECTION_HEIGHT=850;
    public static final int HEIGHT=950;
    private boolean newSelection;

    public HeroCreationWorkspace() {
        super(WIDTH, HEIGHT);
        addActor(
         new Image(TiledNinePatchGenerator.getOrCreateNinePatch(TiledNinePatchGenerator.NINE_PATCH.SAURON,
          TiledNinePatchGenerator.BACKGROUND_NINE_PATCH.PATTERN, (int) (WIDTH* GdxMaster.getWidthModSquareRoot(1.3f)),
                 (int) (HEIGHT* GdxMaster.getHeightModSquareRoot(1.2f)))));

        displayableContainer = add(displayed = new HcIntro()).width(SELECTION_WIDTH)
         .top().pad(50,20,20,20)
        ;
        add(preview = new HcPreview()).width(PREVIEW_WIDTH).height(PREVIEW_HEIGHT)
         .top().pad(20,20,20,80);
//        debug();
    }

    @Override
    public void subItemClicked(SelectableItemData item, String sub) {
        GuiEventType eventType = null;
        PROPERTY prop = null;
        HeroCreationSequence.HERO_CREATION_ITEM hcItem = new EnumMaster<HeroCreationSequence.HERO_CREATION_ITEM>().retrieveEnumConst(
         HeroCreationSequence.HERO_CREATION_ITEM.class, item.getName());
        Object arg=sub ;
        switch (hcItem) {
            case RACE:
                prop = G_PROPS.RACE;
                eventType = GuiEventType.HC_RACE_CHOSEN;
                arg = new EnumMaster<RACE>().retrieveEnumConst(
                 RACE.class, sub);
                break;
            case GENDER:
                prop = G_PROPS.GENDER;
                eventType = GuiEventType.HC_GENDER_CHOSEN;
                break;
            case DEITY:
                //TODO aspect
                eventType = GuiEventType.HC_DEITY_ASPECT_CHOSEN;
                break;
            case SKILLSET:
            case FINALIZE:
                break;
        }
        if (prop!=null )
            HeroCreationMaster.modified(prop, sub);

        updateAct(0);

        if (eventType!=null )
            EUtils.event(eventType, arg);
    }

    @Override
    public void setDoneDisabled(boolean doneDisabled) {

    }

    @Override
    public void initStartButton(String doneText, Runnable o) {

    }

    @Override
    protected void update(float delta) {
        if (item == previousItem)
            return;
        displayed.remove();
        displayableContainer.setActor(displayed = getOrCreateDisplayable(new EnumMaster<HeroCreationSequence.HERO_CREATION_ITEM>().retrieveEnumConst(
         HeroCreationSequence.HERO_CREATION_ITEM.class, item.getName())));
        displayed.setUserObject(getUserObject());
        preview.setUserObject(getUserObject());
        newSelection = false;
    }

    private Group getOrCreateDisplayable(HeroCreationSequence.HERO_CREATION_ITEM item) {
        TablePanelX panel = cache.get(item);
        if (panel == null || (HeroCreationMaster.TEST_MODE&&HeroCreationMaster.FAST_MODE)) {
            panel = createDisplayable(item);
            cache.put(item, panel);
        } else {
            if (newSelection) {
                panel.update();
            }
        }

        return panel;
    }

    private TablePanelX createDisplayable(HeroCreationSequence.HERO_CREATION_ITEM item) {
        switch (item) {
            case INTRODUCTION:
                return new HcIntro();
            case RACE:
                return new HcRaceSelectPanel();
            case GENDER:
                return new HcNameAndGenderPanel();
            case PORTRAIT:
                return HcPortraitPanel.FULL_PORTRAITS
                  ? new HcFullPortraitPanel():
                 new HcPortraitPanel();
            case PERSONALITY:
                return new HcPersonalityPanel();
            case DEITY:
                return new HcDeitySelectionPanel();
            case SKILLSET:
                return new HcSkillsetPanel();
            case FINALIZE:
                return new HcFinalizePanel();
        }
        return null;
    }

    @Override
    public void setItem(SelectableItemData sub) {
        previousItem = this.item;
        this.item = sub;
        setUpdateRequired(true);
        newSelection = true;
        //        setUserObject(HqDataMaster.getHeroDataSource((Unit) sub.getEntity()));
    }

    @Override
    public void setUserObject(Object userObject) {
        if (userObject==null )
            return;
        super.setUserObject(userObject);

    }

    @Override
    public Actor getActor() {
        return this;
    }
}
