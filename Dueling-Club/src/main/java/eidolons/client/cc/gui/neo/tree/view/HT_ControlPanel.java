package eidolons.client.cc.gui.neo.tree.view;

import eidolons.client.cc.gui.neo.tree.logic.HT_MapBuilder;
import eidolons.swing.components.buttons.CustomButton;
import main.swing.generic.components.CompVisuals;
import main.swing.generic.components.G_Panel;
import main.system.images.ImageManager.STD_IMAGES;

import java.awt.*;

public class HT_ControlPanel extends G_Panel {

    protected static final String VIEW_MODE = "VIEW_MODE";
    CustomButton viewModeButton;
    private HT_View view;

    public HT_ControlPanel(HT_View view) {
        this.view = view;
        addControls();
        panelSize = new Dimension(HT_MapBuilder.defTreeWidth - 40, 40);
    }

    private void addControls() {
        viewModeButton = new CustomButton(new CompVisuals(STD_IMAGES.SEARCH.getImage())) {
            @Override
            public void handleClick() {
                handleControl(VIEW_MODE);
            }
        };
        viewModeButton.removeMouseListener(viewModeButton);
        viewModeButton.addMouseListener(view);
        add(viewModeButton);

    }

    public void handleControl(String control) {
        switch (control) {
            case VIEW_MODE:
                view.toggleViewMode();
                break;

        }
    }

}
