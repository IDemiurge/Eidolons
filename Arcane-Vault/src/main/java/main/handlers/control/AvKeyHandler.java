package main.handlers.control;

import eidolons.content.ValueHelper;
import eidolons.game.core.game.DC_Game;
import main.handlers.AvHandler;
import main.handlers.AvManager;
import main.handlers.mod.AvModelHandler;
import main.handlers.util.FilterMaster;
import main.launch.ArcaneVault;
import main.utilities.search.SearchMaster;
import main.utilities.search.TypeFinder;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class AvKeyHandler extends AvHandler implements KeyListener {

    private static final char DELETE_HOTKEY_CHAR = 'w';
    private static final char COPY_VALUES_HOTKEY_CHAR = 'c';

    public AvKeyHandler(AvManager manager) {
        super(manager);
    }

    @Override
    public void loaded() {
        ArcaneVault.getMainBuilder().setKeyListener (this);
    }

    private void copyValues() {
        ArcaneVault.getMainBuilder().getEditViewPanel().copySelectedValues();

    }

    private boolean checkCustomHotkey(KeyEvent e) {
        char keyChar = ("" + e.getKeyChar()).toLowerCase().charAt(0);

        if ( e.isControlDown()){
            switch (keyChar) {
                case 'v':
                    return manager.getTableHandler().paste();
                case 'c':
                    return manager.getTableHandler().copy(); //value, type, ..?
        }
        }
            if ( e.isShiftDown())
        switch (keyChar) {
            case 'w':
                toggleWorkspace();
                return true;
            case 'c':
            case 'x':
                // case 'v':
                break;
            case 'y':
                return true;
            // case 'x':
            // ArcaneVault.getMainBuilder().getEditViewPanel().getTreeViewComp().adjustOffset(
            // e.isAltDown(), true);
            // return true;

            case ValueHelper.HOTKEY_CHAR: {
                runValueHelper();
                return true;
            }
            case FilterMaster.HOTKEY_CHAR: {
                runFilterMaster();
                return true;
            }
            case TypeFinder.HOTKEY_CHAR: {
                runTypeFinder();
                return true;
            }
            case SearchMaster.HOTKEY_CHAR: {
                runSearchMaster();
                return true;
            }
        }

        return false;
    }

    private void toggleWorkspace() {
        // new Thread(() -> ArcaneVault.getMainBuilder().getButtonPanel().handleButtonClick(false,
        //          "WS Add"), "WS_TOGGLE thread").start();
    }

    private void delete() {
        // TODO Auto-generated method stub

    }

    private void runFilterMaster() {
        FilterMaster.newFilter();

    }

    private void runSearchMaster() {
        SearchMaster.newSearch();
    }

    private void runTypeFinder() {
        AvModelHandler.findType();
    }

    private void runValueHelper() {
        DC_Game. game.getValueHelper().setEntity(ArcaneVault.getSelectedType());
        DC_Game. game.getValueHelper().promptSetValue();
        ArcaneVault.getMainBuilder().refresh();
        ArcaneVault.getMainBuilder().getEditViewPanel().refresh();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (checkCustomHotkey(e)) {
            return;
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F1) {
            copyValues();
        }

    }

}
