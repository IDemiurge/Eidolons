package eidolons.libgdx.gui.menu.selection.town;

import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH_PADDING;
import eidolons.libgdx.gui.menu.selection.SelectionPanel;
import eidolons.libgdx.gui.panels.headquarters.town.TownPanel;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

/**
 * Created by JustMe on 10/15/2018.
 */
public abstract class PlaceSelectionPanel extends SelectionPanel {

    @Override
    protected boolean isListOnTheRight() {
        return true;
    }
    @Override
    protected boolean isReadyToBeInitialized() {
        return false;
    }
    protected boolean isDoneSupported() {
        return false;
    }

    @Override
    protected boolean isAutoDoneEnabled() {
        return false;
    }

    @Override
    public void init() {
        super.init();
        getCell(listPanel). left(). top().padRight(50);
        addActor(infoPanel.getActor());
    }

    @Override
    public void layout() {
        super.layout();
        infoPanel.getActor().setPosition(
         NINE_PATCH_PADDING.SAURON.left,
         getTitlePosY() - infoPanel.getActor().getHeight());
        title.setVisible(false);
//        listPanel.addActor(title);
//        title.setPosition(GdxMaster.centerWidth(title),
//         getTitlePosY());
//        addActor(title);

    }

    @Override
    protected float getTitlePosY() {
        return GdxMaster.getHeight() - NINE_PATCH_PADDING.FRAME.top-5;
    }
    @Override
    public void cancel(boolean manual) {
        if (manual)
        {
            WaitMaster.receiveInput(TownPanel.DONE_OPERATION, false);
            GuiEventManager.trigger(GuiEventType.SHOW_TOWN_PANEL, null );
        }
        else
            super.cancel(false);
    }
    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        init();
        if (TownPanel.TEST_MODE) {
            debugAll();
        }
    }
}

