package main.level_editor.backend.functions.advanced;

import eidolons.content.consts.GraphicData;
import eidolons.content.consts.VisualEnums;
import eidolons.libgdx.bf.grid.GridPanel;
import main.level_editor.LevelEditor;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class LE_GridAnimTester extends GridAnimHandler {
    public LE_GridAnimTester(GridPanel panel) {
        super(panel);

        GuiEventManager.bind(GuiEventType.CHOOSE_GRID_ANIM, p -> {
            animated = findView(LevelEditor.getManager().getSelectionHandler().getObject());
            VisualEnums.VIEW_ANIM view_anim = (VIEW_ANIM) p.get();
            GraphicData data = new GraphicData("dur:1;interpolation:fade");
            switch (view_anim) {
                case displace:
                    data.setValue(GraphicData.GRAPHIC_VALUE.y, "100");
                    break;
                case screen:
                    data.setValue(GraphicData.GRAPHIC_VALUE.alpha, "0.5f");
                    break;
                case color:
                    data.setValue(GraphicData.GRAPHIC_VALUE.color, "green");
                    break;
                case attached:
                    break;
            }
//            LE_Screen.getInstance().getGuiStage().getDataEditors().ed(data);
            handleAnim(animated, view_anim, data);
        });
    }
}
