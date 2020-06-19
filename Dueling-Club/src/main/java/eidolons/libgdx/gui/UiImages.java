package eidolons.libgdx.gui;

import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;

public class UiImages {
    public static final String BOTTOM_PANEL_BG = StrPathBuilder.build(PathFinder.getComponentsPath(), "dc", "bottom panel", "background.png");
    public static final String BOTTOM_PANEL_BG_ALT = StrPathBuilder.build(PathFinder.getComponentsPath(), "dc", "bottom panel", "background alt.png");
    public static final String BOTTOM_OVERLAY = StrPathBuilder.build(PathFinder.getComponentsPath(), "dc", "bottom panel", "bottom overlay.png");
    public static final String HP_BAR_BG = StrPathBuilder.build("ui", "components",
            "dc", "unit", "hp bar empty.png");
}
