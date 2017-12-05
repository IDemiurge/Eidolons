package main.client.cc.gui.neo.choice;

import main.client.dc.HC_SequenceMaster;
import main.client.dc.Launcher;
import main.client.dc.Launcher.VIEWS;
import main.client.dc.SequenceManager;
import main.entity.Entity;
import main.swing.generic.components.G_Panel;
import main.system.audio.DC_SoundMaster;
import main.system.graphics.ColorManager;
import main.system.graphics.GuiManager;
import main.system.hotkey.HC_KeyManager;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ChoiceSequence {
    final public int NO_SELECTION = -1;
    protected ChoiceView view;
    protected List<ChoiceView> views;
    protected int i = NO_SELECTION;
    protected SequenceManager manager;
    protected String value;
    protected Stack<Object> results; // perhaps a map
    private G_Panel panel;
    private boolean active;
    // better

    public ChoiceSequence(ChoiceView... views) {
        this(new ArrayList<>(Arrays.asList(views)));
    }

    public ChoiceSequence(List<ChoiceView> views) {
        this.views = views;
        panel = new G_Panel();
        panel.setPanelSize(GuiManager.DEF_DIMENSION);
        panel.setBackground(ColorManager.BACKGROUND);
        panel.setOpaque(true);

    }

    public ChoiceSequence() {
        this(new ArrayList<>());
    }

    public void start() {
        for (ChoiceView v : views) {
            v.setSequence(this);
        }
        Launcher.setView(getPanel(), VIEWS.CHOICE);
        try {
            ((HC_KeyManager) Launcher.getKeyListener(VIEWS.HC)).setSequence(this);
            ((HC_KeyManager) Launcher.getKeyListener(VIEWS.HC)).setSequenceManager(manager);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        active = true;
        results = new Stack<>();
        next();
    }

    public void selected(int index) {

        if (index != -1) {
            Object object = view.getData().get(index);
            if (object != null) {
                view.applyChoice();
                setSelection(object);
                results.push(object);
            }
        }
        next();
    }

    protected void setSelection(Object object) {
        this.value = object.toString();
        if (object instanceof Entity) {
            this.value = (((Entity) object).getName());
        }
    }

    protected void next() {
        i++;
        if (views.size() <= i) {
            done();
        } else {
            try {
                views.get(i).activate();
                view = views.get(i);
                if (view.getSelectedIndex() == -1) {
                    if (view.getData().size() > 0) {
                        view.itemIndexSelected(0);
                    }
                }
                refresh();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                try {
                    next();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    return;
                }
            }
        }
    }

    protected void done() {
        manager.doneSelection();
        active = false;
    }

    public void tryBack() {
        back();
    }

    public void tryNext() {
        // if (view.getSelectedIndex()==-1) return;
        if (!view.checkBlocked(view.getSelectedIndex())) {
            DC_SoundMaster.playStandardSound(STD_SOUNDS.SLING);
            selected(view.getSelectedIndex());
        } else {
            DC_SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ERROR);
        }

    }

    protected void refresh() {
        getPanel().removeAll();
        getPanel().add(view, "pos 0 0 ");
        getPanel().revalidate();
        panel.repaint();
    }

    public void back() {
        i--;
        if (i < 0) {
            manager.cancelSelection();

        } else {
            if (!results.isEmpty()) {
                results.pop();
            }
            view = views.get(i);
            try {
                view.activate();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                back();
            }
            refresh();
        }

    }

    public ChoiceView getView() {
        return view;
    }

    public List<ChoiceView> getViews() {
        return views;
    }

    public int getI() {
        return i;
    }

    public String getValue() {
        return value;
    }

    public void setManager(SequenceManager manager) {
        this.manager = manager;
        if (manager instanceof HC_SequenceMaster) {
            HC_SequenceMaster sequenceMaster = (HC_SequenceMaster) manager;
            sequenceMaster.setSequence(this);
        }
    }

    public G_Panel getPanel() {
        return panel;
    }

    public void setPanel(G_Panel panel) {
        this.panel = panel;
    }

    public void addView(ChoiceView choiceView) {
        if (!views.contains(choiceView)) {
            views.add(choiceView);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Stack<Object> getResults() {
        return results;
    }

}
