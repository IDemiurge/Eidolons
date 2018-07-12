package eidolons.libgdx.gui.panels.headquarters.creation.selection.race;

import eidolons.game.core.EUtils;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.headquarters.creation.selection.DescriptionScroll;
import main.content.enums.entity.HeroEnums.RACE;
import main.system.GuiEventType;

/**
 * Created by JustMe on 7/3/2018.
 */
public class HcRaceSelectPanel extends TablePanelX {

    HcSubracePanel subracePanel;
    HcBackgroundPanel backgroundPanel;
    LabelX raceLabel;
    LabelX subraceLabel;
    LabelX backgroundLabel;
    DescriptionScroll scroll;

    public HcRaceSelectPanel() {
        add(raceLabel = new LabelX("", StyleHolder.getHqLabelStyle(18))).row();
        add(subracePanel = new HcSubracePanel()).row();
        add(subraceLabel = new LabelX("", StyleHolder.getHqLabelStyle(17))).row();
        add(backgroundPanel = new HcBackgroundPanel()).row();
        add(backgroundLabel = new LabelX("", StyleHolder.getHqLabelStyle(16))).row();
        add(scroll = new DescriptionScroll() );

        EUtils.bind(GuiEventType.HC_RACE_CHOSEN, p -> {
            RACE race = (RACE) p.get();
            raceLabel.setText(getRaceText(race));
            subracePanel.setUserObject(race);
            subracePanel.updateAct(0);
            subracePanel.select(0);
        });
        EUtils.bind(GuiEventType.HC_SUBRACE_CHOSEN, p -> {
            SelectableItemData data = (SelectableItemData) p.get();
            subraceLabel.setText(getSubraceText(data));

            backgroundPanel.setUserObject(data.getEntity());
            backgroundPanel.updateAct(0);
            SelectableItemData item = backgroundPanel.select(0);

            backgroundLabel.setText(getBackgroundText(item));
            scroll.setUserObject(data);
        });

        EUtils.bind(GuiEventType.HC_BACKGROUND_CHOSEN, p -> {
            SelectableItemData data = (SelectableItemData) p.get();
            backgroundLabel.setText(getBackgroundText(data));
            scroll.setUserObject(data);
        });
    }

    private String getRaceText(RACE race) {
        switch (race) {
            case HUMAN:
                return "Human Kingdoms";
            case ELF:
                return "Elvenkind";
            //houses?
            case DWARF:
                return "Dwarf Clans";

        }
        return race.name();
    }

    private String getBackgroundText(SelectableItemData item) {
        return "Background: "+item.getName();
    }

    private String getSubraceText(SelectableItemData data) {
        return data.getName();
    }

    @Override
    public void setUserObject(Object userObject) {
    }
}
