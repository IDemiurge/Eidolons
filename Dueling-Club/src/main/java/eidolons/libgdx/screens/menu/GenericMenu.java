package eidolons.libgdx.screens.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.stage.Blocking;
import eidolons.libgdx.stage.StageWithClosable;
import eidolons.libgdx.texture.TextureCache;
import eidolons.system.options.OptionsMaster;
import eidolons.system.options.OptionsWindow;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 11/28/2017.
 */
public abstract class GenericMenu<T extends MenuItem<T>> extends TablePanelX implements Blocking {
    protected Map<T, TextButton> cache = new HashMap<>();
    OptionsWindow optionsWindow;
    private T currentItem;
    private T previousItem;
    private List<MenuItem<T>> defaultItems;
    List<TextButton> buttons = new ArrayList<>();

    public GenericMenu() {
        TextureRegionDrawable texture = TextureCache.getOrCreateTextureRegionDrawable(StrPathBuilder.build(
                "UI", "components", "generic", "game menu", "background.png"));
        //TODO initResolutionScaling();
//        setSize(texture.getRegion().getRegionWidth()*getScaleX(), texture.getRegion().getRegionHeight()*getScaleY());
//        texture.setMinWidth(getWidth());
//        texture.setMinHeight(getHeight());
        setBackground(texture);
        addButtons();
    }

    public Map<T, TextButton> getCache() {
        return cache;
    }

    protected List<MenuItem<T>> getItems() {
        if (currentItem != null) {
            MenuItem[] items = currentItem.getItems();
            List<MenuItem<T>> list = new ArrayList<>();
            for (MenuItem sub : items)
                list.add(sub);
            list.add(new MenuItem<T>() {
                @Override
                public T[] getItems() {
                    return getBackItems();
                }

                @Override
                public String toString() {
                    return "Back";
                }


            });
            return list;
        }
        return getDefaultItems();
    }

    protected T[] getBackItems() {
        if (previousItem == null)
            return getDefaultItems().toArray((T[]) new MenuItem[getDefaultItems().size()]);
        return previousItem.getItems();
    }

    protected abstract List<MenuItem<T>> getFullItemList();

    protected List<MenuItem<T>> getDefaultItems() {
        if (defaultItems == null) {
            defaultItems = getFullItemList();
            defaultItems.removeIf(item ->
                    isHidden((T) item)
            );
        }

        return defaultItems;
    }

    protected boolean isHidden(T item) {
        return false;
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        clear();
        addButtons();
        updateRequired = false;
    }

    protected void addButtons() {
        List<MenuItem<T>> items = getItems();
        buttons.clear();
        for (MenuItem sub : items) {
            TextButton button = null;
            if (sub.toString().equalsIgnoreCase("Back")) {
                button = getBackButton();
            } else
                button = getButton((T) sub, StringMaster.getWellFormattedString(sub.toString()));
            add(button).top().pad(10, 10, 10, 10);
            buttons.add(button);
            row();
        }
        float top = getTopPadding(items.size());
        float left = getLeftPadding();
        float botton = getBottonPadding(items.size());
        float right = getRightPadding();
        pad(top, left, botton, right);


    }

    protected float getLeftPadding() {
        return 80;
    }

    protected float getRightPadding() {
        return 80;
    }

    protected abstract float getBottonPadding(int size);

    protected abstract float getTopPadding(int size);

    protected TextButton getBackButton() {
        return getButton(null, "Back");
    }

    protected TextButton getButton(T sub, String name) {
        TextButton button = getCache().get(sub);
        if (button == null || sub == null) {
            button = new TextButton((name),
                    StyleHolder.getTextButtonStyle(getButtonStyle(),
                            getFontStyle(), getFontColor(), getFontSize()));
            getCache().put(sub, button);
            button.addListener(getClickListener(sub));
        }
        return button;
    }

    protected Color getFontColor() {
        return GdxColorMaster.GOLDEN_WHITE;
    }

    protected int getFontSize() {
        return 20;
    }

    protected abstract FONT getFontStyle();

    protected abstract STD_BUTTON getButtonStyle();

    protected EventListener getClickListener(T sub) {
        return new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (sub == null) {
                    setCurrentItem(previousItem);
                    setPreviousItem(null);
                    updateRequired = true;
                    return true;
                }
                if (currentItem == sub) {
                    return true; //why duplicate events?!
                }
                Boolean result = GenericMenu.this.clicked(sub);
                if (result == null) {
                    close();
                    return true;
                }
                if (result) {
                    setPreviousItem(currentItem);
                    setCurrentItem(sub);
                    updateRequired = true;
                }
                return super.touchDown(event, x, y, pointer, button);
            }
        };
    }

    public void openOptionsMenu() {
        OptionsWindow.getInstance().open(OptionsMaster.getOptionsMap(), getStage());

    }

    protected void clicked() {
        for (TextButton sub : buttons) {
            sub.setChecked(false);
        }
    }

    protected abstract Boolean clicked(MenuItem sub);

    public void open() {
        setCurrentItem(null);
        setPreviousItem(null);
        getStageWithClosable().openClosable(this);
    }

    @Override
    public StageWithClosable getStageWithClosable() {
        return (StageWithClosable) super.getStage();
    }

    public void close() {
        getStageWithClosable().closeClosable(this);

    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }

    public void setCurrentItem(T currentItem) {
        main.system.auxiliary.log.LogMaster.log(1, "setCurrentItem " +
                this.currentItem +
                " to " +
                currentItem);
        this.currentItem = currentItem;
    }

    public void setPreviousItem(T previousItem) {
        main.system.auxiliary.log.LogMaster.log(1, "setPreviousItem " +
                this.previousItem +
                " to " +
                previousItem);
        this.previousItem = previousItem;
    }
}
