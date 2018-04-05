package eidolons.libgdx.screens.map.editor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import eidolons.libgdx.anims.particles.EmitterActor;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.panels.dc.TabbedPanel;
import eidolons.libgdx.gui.panels.dc.ValueContainer;
import eidolons.libgdx.gui.panels.dc.logpanel.ScrollPanel;
import eidolons.libgdx.screens.map.editor.EditorControlPanel.MAP_EDITOR_MOUSE_MODE;
import eidolons.libgdx.screens.map.editor.EditorInfoPanel.EditorInfoTab;
import eidolons.libgdx.screens.map.editor.EditorPalette.EDITOR_PALETTE;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.MapEvent;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 2/20/2018.
 */
public class EditorInfoPanel extends TabbedPanel<EditorInfoTab> {
    Map<Object, Actor> elementMap = new HashMap<>();
    private Actor selected;

    public EditorInfoPanel() {
        init();
        GuiEventManager.bind(MapEvent.LOCATION_ADDED, p -> {
            add(p.get());
        });
        GuiEventManager.bind(MapEvent.EMITTER_CREATED, p -> {
            add(p.get());
        });
        GuiEventManager.bind(MapEvent.EMITTER_REMOVED, p -> {
            remove(p.get());
            //pack tab

//            tabsToNamesMap.get(type).
        });
        setSize(300, 800);
        debugAll();
    }

    @Override
    public void tabSelected(String tabName) {
        super.tabSelected(tabName);
        EDITOR_PALETTE palette = new EnumMaster<EDITOR_PALETTE>().retrieveEnumConst(EDITOR_PALETTE.class, tabName);
        if (palette != null)
            EditorMapView.getInstance().getGuiStage().getPalette().tabSelected(palette.name());
        INFO_TABS tabs = new EnumMaster<INFO_TABS>().retrieveEnumConst(INFO_TABS.class, tabName);
        if (tabs != null)
            switch (tabs) {
                case EMITTERS:
                    break;
                case PLACES:
                    break;
                case PARTIES:
                    break;
                case AREAS:
                    break;
                case POINTS:
                    EditorManager.setMode(MAP_EDITOR_MOUSE_MODE.POINT);
                    break;
                case SCRIPTS:
                    break;
            }
    }

    private void add(Object o) {
        String type = getType(o);
        EditorInfoTab tab = tabsToNamesMap.get(type);
        tab.add(o);
    }

    private String getType(Object o) {
        switch (o.getClass().getSimpleName()) {
            case "EmitterActor":
                return INFO_TABS.EMITTERS.name();
            case "ImmutablePair":
                return INFO_TABS.POINTS.name();
        }
        return null;
    }

    private void remove(Object o) {
//
        elementMap.get(o).remove();
    }


    public void init() {
        for (INFO_TABS sub : INFO_TABS.values()) {
            EditorInfoTab tab = new EditorInfoTab(sub);
            addTab(tab, sub.name());
        }

    }

    private void selected(Object o, Actor element_) {
        selected = element_;
    }

    //centerOnSelected
    //removeSelected
    //trigger highlight
    public enum INFO_TABS {
        EMITTERS, POINTS, PLACES, PARTIES, AREAS, SCRIPTS,
    }

    public class EditorInfoTab extends TabbedPanel {
        private final ScrollPanel scroll;
        INFO_TABS sub;

        public EditorInfoTab(INFO_TABS sub) {
            this.sub = sub;
            //scrolled!
            scroll = new ScrollPanel<>();
            add(scroll);
            scroll.setBackground(new NinePatchDrawable(NinePatchFactory.getTooltip()));
            scroll.setSize(300, 700);
            setSize(300, 700);
        }

        public void add(final Object o) {
            Actor element = null;
            switch (sub) {
                case EMITTERS:
                    EmitterActor emitterActor = (EmitterActor) o;
                    String s = emitterActor.getX() + "-" + emitterActor.getY();
                    element = new ValueContainer(
                     StringMaster.cropByLength(20,
                      StringMaster.getLastPathSegment(((EmitterActor) o).getPath()))

                     , s);
                    break;
                case PLACES:
                    break;
                case PARTIES:
                    break;
                case AREAS:
                    break;
                case POINTS:
                    Pair<String, Coordinates> pair = (Pair<String, Coordinates>) o;
                    element = new ValueContainer(
                     StringMaster.cropByLength(20,
                      pair.getKey())
                     , pair.getValue().toString());
                    break;
                case SCRIPTS:
                    break;
            }
            elementMap.put(o, element);
            scroll.addElement(element);
            Actor element_ = element;
            element.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    //select, show on map, remove, ...
                    selected(o, element_);
                    return super.touchDown(event, x, y, pointer, button);
                }
            });
        }
    }


}
