package main.libgdx.screens.map.editor;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.content.ContentManager;
import main.content.OBJ_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.libgdx.GdxMaster;
import main.libgdx.bf.generic.SuperContainer;
import main.libgdx.gui.panels.dc.TabbedPanel;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.logpanel.ScrollPanel;
import main.libgdx.gui.tooltips.ValueTooltip;
import main.libgdx.texture.TextureCache;
import main.system.images.ImageManager.BORDER;

import java.util.ArrayList;

/**
 * Created by JustMe on 2/10/2018.
 */
public class EditorPalette extends TabbedPanel {
    private ValueContainer selected;
    private SuperContainer selectionBorder;

    public EditorPalette() {
        updateRequired = true;
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        init();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public void init() {
        clearChildren();
        clearListeners();
        addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//               updateRequired=true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        debugAll();
        setSize(GdxMaster.getWidth() / 3 * 2, 256);
        int columns = (int) (getWidth() / 64);
        for (EDITOR_PALETTE sub : EDITOR_PALETTE.values()) {
            TabbedPanel tabbedPanel = new TabbedPanel();
            OBJ_TYPE TYPE = ContentManager.getOBJ_TYPE(sub.name());
            TablePanel<Actor> table;
            if (TYPE == null)
                continue;
            for (String group : DataManager.getTabsGroup(TYPE)) {
                table = new TablePanel<>();
                table.defaults().width(64).height(64);
                table.top().left().padLeft(64).padTop(64);
                table.setFillParent(true);
                ArrayList<ObjType> types = new ArrayList<>(DataManager.getTypesGroup(TYPE, group));
                int i = 0;
                int rows = 0;
                for (ObjType type : types) {
                    TextureRegion texture = TextureCache.getOrCreateR(type.getImagePath());
                    ValueContainer item = new ValueContainer(texture);
                    item.overrideImageSize(64, 64);
                    item.addListener(new ValueTooltip(type.getName()).getController());
                    item.addListener(getItemListener(item));
                    table.add(item).left();
                    item.setUserObject(type);
                    i++;
                    if (i >= columns) {
                        i = 0;
                        table.row();
                        rows++;
                    }
                }
                table.pack();
                table.debugAll();
                if (rows > 3) {
                    ScrollPanel scrollPanel = new ScrollPanel();
                    scrollPanel.addElement(table);
                    tabbedPanel.addTab(scrollPanel, group);
                } else {
                    tabbedPanel.addTab(table, group);
                }
            }
            addTab(tabbedPanel, sub.name());
        }
        //scrollable?
    }

    private EventListener getItemListener(ValueContainer item) {
        return new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                itemClicked(item);
                return super.touchDown(event, x, y, pointer, button);
            }
        };
    }

    private void itemClicked(ValueContainer item) {
        if (selected != null) {
            getSelectionBorder().remove();
        }
        selected = item;
        selected.add(getSelectionBorder());
    }

    private Actor getSelectionBorder() {
        if (selectionBorder==null )
          selectionBorder = new SuperContainer(new Image(
         TextureCache.getOrCreateR(BORDER.NEO_INFO_SELECT_HIGHLIGHT_SQUARE_64.getImagePath()
         )), true);
//        border.setSize(75, 75);
        return selectionBorder;
    }

    public ObjType getSelectedType() {
        if (selected == null)
            return null;
        return (ObjType) selected.getUserObject();
    }

    public ValueContainer getSelected() {
        return selected;
    }

    public enum EDITOR_PALETTE {
        PLACE, SHOP, TAVERN, PARTY, ENCOUNTER,
    }
}
