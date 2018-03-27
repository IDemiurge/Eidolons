package main.game.module.adventure.gui.map;

import main.game.module.adventure.global.GameDate;
import main.swing.components.panels.page.log.WrappedTextComp;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;

public class TimeComp extends G_Panel {
    GameDate date;
    private WrappedTextComp textComp;

    // three text comps?

    public TimeComp(GameDate date) {
        this.date = date;
        init();
    }

    private void init() {
        // ++ area/region?
        removeAll();
        // new GraphicComponent(date.isDay_or_night() ? STD_IMAGES.DAY.getImg()
        // : STD_IMAGES.NIGHT.getImg());
        textComp = new WrappedTextComp(VISUALS.VALUE_BOX_BIG) {
            protected int getDefaultFontSize() {
                return 17;
            }

            public void refresh() {
                String line1 = "The "
                        + StringMaster.getOrdinalEnding(date.getDay())
                        + (date.getMonth() != null ? " of " + date.getMonth()
                        : "");
                line1 = date.getShortString();
                String line2 = date.getYear() + " of " + date.getEra();
                line2 = date.getStringExtension();
                textLines = new ListMaster<String>().getList(line1, line2);
                super.refresh();
            }

            @Override
            protected boolean isAutoWrapText() {
                return false;
            }
        };
        // textComp.setText(date.getShortString());
        // textComp.setPanelSize(size);
        add(textComp);
        revalidate();

    }

    @Override
    public void refresh() {
        textComp.refresh();
        super.refresh();
    }
}
