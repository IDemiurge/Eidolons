package eidolons.system.hotkey;

import eidolons.client.cc.HC_Master;
import eidolons.client.cc.gui.neo.choice.ChoiceSequence;
import eidolons.client.cc.gui.neo.tabs.HC_TabPanel;
import eidolons.client.cc.gui.neo.tree.t3.T3UpperPanel;
import eidolons.client.cc.gui.neo.tree.view.TreeControlPanel;
import eidolons.client.dc.SequenceManager;
import eidolons.system.audio.DC_SoundMaster;
import main.swing.generic.components.panels.G_PagePanel;
import main.system.launch.CoreEngine;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// initiative on all components within hc, set page panel on focus (?) - default focus per View,
// plus 
public class HC_KeyManager implements KeyListener {
    G_PagePanel<?> pagePanel;
    SequenceManager sequenceManager;
    ChoiceSequence sequence;
    private HC_TabPanel tabPanel;

    // set page panel upon manual flip?
    // or maybe i will need to add a custom mouselistener...

    public void setProp(boolean alt) {
        TreeControlPanel.setProp(alt, HC_Master.getSelectedTreeNode().getType());
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // pagePanel = GuiManager.getActivePagePanel();
        if (pagePanel == null) {
            try {
                pagePanel = sequence.getView().getPages();
            } catch (Exception e2) {

            }
        }
        // TODO enter/esc for SelectionViews and Menu while in PartyView

        char keyChar = e.getKeyChar();
        // main.system.auxiliary.LogMaster.log(1, keyChar +
        // " typed for HC_KeyManager " + e);
        if (CoreEngine.isArcaneVault()) {
            switch (keyChar) {
                case 's':
                case 'r':
                    setProp(e.isAltDown());
                    return;
                case 'v':
                    T3UpperPanel.getLastInstance().handleControl(T3UpperPanel.SAVE, e.isAltDown());
                    return;
            }
        }
        switch (keyChar) {

            case 'a':
                tabScrollBackward();
                break;
            case 'd':
                tabScrollForward();
                break;
            case KeyEvent.VK_TAB:
                // cycle active component
                break;
            case KeyEvent.VK_ENTER:
                sequence.tryNext();
                break;
            case KeyEvent.VK_ESCAPE:
                // sequence.tryBack();
                break;

            // in dc as well! spell/QI/Info/Log! highlight pagepanel?
            case KeyEvent.VK_A:
            case KeyEvent.VK_W:
            case KeyEvent.VK_DOWN:
                if (pagePanel != null) {
                    // sound?
                    pagePanel.flipPage(false);
                }
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_S:
            case KeyEvent.VK_UP:
                if (pagePanel != null) {
                    // sound?
                    pagePanel.flipPage(true);
                }
                break;
        }

    }

    private void tabScrollForward() {
        tabPanel.select(tabPanel.getIndex() + 1);
        DC_SoundMaster.playStandardSound(STD_SOUNDS.ON_OFF);
    }

    private void tabScrollBackward() {
        tabPanel.select(tabPanel.getIndex() - 1);
        DC_SoundMaster.playStandardSound(STD_SOUNDS.CLOCK);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        char keyChar = e.getKeyChar();
        switch (keyChar) {
            case KeyEvent.VK_LEFT:
                tabScrollBackward();
                break;
            case KeyEvent.VK_RIGHT:
                tabScrollForward();
                break;
        }
        // main.system.auxiliary.LogMaster.log(1,
        // " keyPressed for HC_KeyManager " + e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // main.system.auxiliary.LogMaster.log(1,
        // " keyReleased for HC_KeyManager " + e);
        // int keyCode = e.getKeyCode();
        // switch (keyCode) {
        // case KeyEvent.VK_CONTROL:
        // tabPanel.select(tabPanel.getIndex() - 1);
        // break;
        // case KeyEvent.VK_ALT:
        // tabPanel.select(tabPanel.getIndex() + 1);
        // break;
        // case KeyEvent.VK_ENTER:
        // sequence.tryNext();
        // break;
        //
        // }

    }

    public G_PagePanel<?> getPagePanel() {
        return pagePanel;
    }

    public void setPagePanel(G_PagePanel<?> pagePanel) {
        this.pagePanel = pagePanel;
    }

    public SequenceManager getSequenceManager() {
        return sequenceManager;
    }

    public void setSequenceManager(SequenceManager sequenceManager) {
        this.sequenceManager = sequenceManager;
    }

    public ChoiceSequence getSequence() {
        return sequence;
    }

    public void setSequence(ChoiceSequence sequence) {
        this.sequence = sequence;
    }

    public void setTabPanel(HC_TabPanel tabPanel) {
        this.tabPanel = tabPanel;
    }

}
