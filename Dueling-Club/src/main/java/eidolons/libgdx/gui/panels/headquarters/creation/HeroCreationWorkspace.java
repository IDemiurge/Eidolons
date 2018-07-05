package eidolons.libgdx.gui.panels.headquarters.creation;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.game.core.EUtils;
import eidolons.libgdx.TiledNinePatchGenerator;
import eidolons.libgdx.TiledNinePatchGenerator.BACKGROUND_NINE_PATCH;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.menu.selection.SelectableItemDisplayer;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.gui.panels.headquarters.creation.HeroCreationSequence.HERO_CREATION_ITEM;
import eidolons.libgdx.gui.panels.headquarters.creation.general.HcDeitySelectionPanel;
import eidolons.libgdx.gui.panels.headquarters.creation.misc.HcNamePanel;
import eidolons.libgdx.gui.panels.headquarters.creation.misc.HcPortraitPanel;
import eidolons.libgdx.gui.panels.headquarters.creation.general.HcRaceSelectPanel;
import eidolons.libgdx.gui.panels.headquarters.creation.misc.HcPersonalityPanel;
import eidolons.libgdx.gui.panels.headquarters.creation.preview.HcIntro;
import eidolons.libgdx.gui.panels.headquarters.creation.preview.HcPreview;
import eidolons.libgdx.gui.panels.headquarters.creation.skillset.HcSkillsetPanel;
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
    private Map<HERO_CREATION_ITEM, Group> cache = new HashMap<>();

    public HeroCreationWorkspace() {
        super(1200, 900);
        addActor(
         new Image(TiledNinePatchGenerator.getOrCreateNinePatch(NINE_PATCH.SAURON,
          BACKGROUND_NINE_PATCH.PATTERN, 1200, 900)));
        float w = 600;
        displayableContainer = add(displayed = new HcIntro()).width(w);
        add(preview = new HcPreview()).width(w);
    }

    @Override
    public void subItemClicked(SelectableItemData item, String sub) {
        GuiEventType eventType = null;
        PROPERTY prop = null;
        HERO_CREATION_ITEM hcItem = new EnumMaster<HERO_CREATION_ITEM>().retrieveEnumConst(
         HERO_CREATION_ITEM.class, item.getName());
        switch (hcItem) {
            case RACE:
                prop = G_PROPS.RACE;
                eventType = GuiEventType.HC_RACE_CHOSEN;
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
                break;
            case FINALIZE:
                break;
        }

        HeroCreationMaster.modified(prop, sub);

        RACE race = new EnumMaster<RACE>().retrieveEnumConst(
         RACE.class, sub);
        EUtils.event(eventType, race);
    }

    @Override
    protected void update(float delta) {
        if (item == previousItem)
            return;
        displayed.remove();
        displayableContainer.setActor(displayed = getOrCreateDisplayable(new EnumMaster<HERO_CREATION_ITEM>().retrieveEnumConst(
         HERO_CREATION_ITEM.class, item.getName())));
        displayed.setUserObject(getUserObject());
    }

    private Group getOrCreateDisplayable(HERO_CREATION_ITEM item) {
        Group panel = cache.get(item);
        if (panel == null)
            panel = createDisplayable(item);

        cache.put(item, panel);
        return panel;
    }

    private Group createDisplayable(HERO_CREATION_ITEM item) {
        switch (item) {
            case RACE:
                return new HcRaceSelectPanel();
            case GENDER:
                return new HcNamePanel();
            case PORTRAIT:
                return new HcPortraitPanel();
            case PERSONALITY:
                return new HcPersonalityPanel();
            case DEITY:
                return new HcDeitySelectionPanel();
            case SKILLSET:
                return new HcSkillsetPanel();
        }
        return null;
    }

    @Override
    public void setItem(SelectableItemData sub) {
        previousItem = this.item;
        this.item = sub;
        updateRequired = true;
        //        setUserObject(HqDataMaster.getHeroDataSource((Unit) sub.getEntity()));
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);

    }

    @Override
    public Actor getActor() {
        return this;
    }
}
