package main.music.gui;

import main.data.XLinkedMap;
import main.enums.StatEnums.MUSIC_TAGS;
import main.enums.StatEnums.MUSIC_TYPE;
import main.music.MusicCore;
import main.music.ahk.AHK_Master;
import main.swing.components.TextComp;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.panels.G_ScrolledPanel;
import main.swing.generic.services.listener.MouseClickListener;
import main.system.auxiliary.StringMaster;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MusicViewsPanel extends G_ScrolledPanel<String> {
    // scrolled panel!
    Map<String, MusicListPanel> viewMap = new XLinkedMap<>();
    MusicListPanel panel;
    String key;
    private G_Panel view;
    private Integer viewIndexForCycle;

    public MusicViewsPanel(MusicListPanel panel) {
        super(true, 3, new Dimension(200, 450));
        this.panel = panel;
        this.key = panel.getKey();
        addMouseListener(new MouseClickListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                for (Component c : getComponents()) {
                    if (new Rectangle(c.getLocation(), new Dimension(c.getWidth(), c.getHeight()))
                            .contains(e.getPoint())) {
                        viewClicked(((TextComp) c).getTextDisplayed());
                    }
                }

            }
        });
    }

    public void addView(String key, MusicListPanel panel) {

        viewMap.put(key, panel);
    }

    public void addView(MusicListPanel panel) {
        viewMap.put(panel.getView().getName(), panel);
    }

    @Override
    public List<String> getData() {
        return new LinkedList<>(viewMap.keySet());
    }

    public void cycleView() {
        List<String> names = new LinkedList<>(viewMap.keySet());

        if (viewIndexForCycle == null) {
            String name = panel.getView().getName();
            viewIndexForCycle = names.indexOf(name);
        }
        viewIndexForCycle++;
        if (viewIndexForCycle >= viewMap.size()) {
            viewIndexForCycle = 0;
        }

        viewClicked(names.get(viewIndexForCycle));

    }

    public void viewClicked(String key) {
        if (viewMap.get(key) == null) {
            if (!key.contains(".")) {
                viewClicked(key + ".ahk");
            }
            return;
        }
        MusicListPanel wrappingPanel = viewMap.get(key);
        view = wrappingPanel.getView();
        this.key = key;
        panel.setViewAndRefresh(view);

        // view = panel.getControlPanel().doDialog()

        MusicCore.getGroupedView(view.getName(), view.getName().contains("Tags") ? MUSIC_TAGS.class
                : MUSIC_TYPE.class);

        AHK_Master.setWrappingPanel(wrappingPanel);
        AHK_Master.setPanel(panel);
        refresh();
    }

    // isArrowAlwaysShown(){
    // return

    @Override
    protected G_Panel createComponent(final String key) {
        // return new CustomButton(key) {
        // @Override
        // public void handleClick() {
        // viewClicked(key);
        // }
        // };
        final TextComp comp = new TextComp(Color.black) {
            @Override
            public int getPanelHeight() {
                return super.getPanelHeight() / 3 * 2;
            }
        };
        comp.setText(StringMaster.cropFormat(key));
        // comp.addMouseListener(new MouseClickListener() {
        //
        // @Override
        // public void mouseClicked(MouseEvent e) {
        // viewClicked(comp.getText());
        //
        // }
        // });
        return comp;

    }

    // @Override
    // public void refresh() {
    // super.refresh();
    // removeAll();
    // for (String key : viewMap.keySet()) {
    // button = new TextComp(key);
    // add(button);
    // }
    // }

}
