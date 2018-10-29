package eidolons.libgdx.gui.menu.selection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.TiledNinePatchGenerator;
import eidolons.libgdx.TiledNinePatchGenerator.BACKGROUND_NINE_PATCH;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.RollDecorator;
import eidolons.libgdx.gui.RollDecorator.RollableGroup;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.game.bf.directions.FACING_DIRECTION;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.util.*;

/**
 * Created by JustMe on 11/29/2017.
 */
public abstract class ItemListPanel extends TablePanel {
    protected SelectableItemData currentItem;
    protected List<SelectableItemData> items;
    protected TextButton lastChecked;
    protected SelectableItemDisplayer infoPanel;
    protected Map<SelectableItemData, TextButton> cache = new HashMap<>();
    protected List<TextButton> buttons = new ArrayList<>();
    protected Map<SelectableItemData, RollableGroup> subCache = new HashMap<>();
    private RollableGroup displayedSubitemPanel;

    public ItemListPanel() {
        super();
        initBg();

    }

    protected void initBg() {
        //        setBackground(TextureCache.getOrCreateTextureRegionDrawable(getBackgroundPath()));

        if (getNinePatch() != null) {
            TextureRegion generated = new TextureRegion(
             TiledNinePatchGenerator.getOrCreateNinePatch(getNinePatch(),
              getNinePatchBackground(),
             getDefaultWidth()
              , getDefaultHeight()));
            setSize(generated.getRegionWidth(), generated.getRegionHeight());
            setFixedSize(true);
            addActor(new Image(generated));
        } else {
            setBackground(NinePatchFactory.getLightPanelFilledDrawable());
            setSize( getDefaultWidth()
             , getDefaultHeight());
        }

    }

    protected BACKGROUND_NINE_PATCH getNinePatchBackground() {
        return BACKGROUND_NINE_PATCH.SEMI;
    }
    protected NINE_PATCH getNinePatch() {
        return NINE_PATCH.FRAME;
    }

    protected int getDefaultHeight() {
        return GdxMaster.getHeight() * 6 / 7;
    }

    protected int getDefaultWidth() {
        return (int) GdxMaster.adjustSize(410);
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        addButtons();
    }

    protected Map<SelectableItemData, TextButton> getCache() {
        return cache;
    }

    protected TextButton getOrCreateElement(SelectableItemData sub) {
        TextButton button = getCache().get(sub);

        if (button == null || sub == null) {
            button = new SmartButton((sub.name),
             StyleHolder.getTextButtonStyle(getButtonStyle(),
              getFontStyle(), getFontColor(), getFontSize())) {
                @Override
                protected STD_SOUNDS getSound() {
                    return STD_SOUNDS.CLICK;
                }

                @Override
                public boolean isFixedSize() {
                    return true;
                }
            };
            button.setSize(STD_BUTTON.MENU.getTexture().getMinWidth(),
             STD_BUTTON.MENU.getTexture().getMinHeight());
            getCache().put(sub, button);
            TextButton finalButton = button;
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    ItemListPanel.this.clicked(finalButton, sub);
                }
            });

        }
        return button;
    }

    protected Color getFontColor() {
        return GdxColorMaster.GOLDEN_WHITE;
    }

    protected int getFontSize() {
        return 20;
    }

    protected FONT getFontStyle() {
        return FONT.MAGIC;
    }

    protected STD_BUTTON getButtonStyle() {
        return STD_BUTTON.MENU;
    }

    public void previous() {
        selectWithOffset(-1);
    }

    public void select(int i) {
        clicked(i);
    }

    public void selectWithOffset(int offset) {
        int index = getItems().indexOf(currentItem);
        index += offset;
        if (index >= buttons.size())
            index = 0;
        else if (index < 0)
            index = buttons.size() - 1;
        clicked(index);
    }

    public void selectRandomItem() {
        List<SelectableItemData> available = new LinkedList<>(items);
        available.removeIf(btn -> isBlocked(btn));
        SelectableItemData item = new RandomWizard<SelectableItemData>().
         getRandomListItem(available);
        currentItem = item;
        if (ListMaster.isNotEmpty(buttons))
            clicked(items.indexOf(item));
    }

    public void next() {
        selectWithOffset(1);
    }

    protected void clicked(int index) {
        index = Math.min(buttons.size() - 1, index);
        clicked(buttons.get(index), getItems().get(index));
    }

    protected boolean clicked(TextButton textButton, SelectableItemData sub) {
        if (lastChecked != null)
            lastChecked.setChecked(false);
        lastChecked = textButton;
        textButton.setChecked(true);

        selected(sub);

        return false;
    }

    private void selected(SelectableItemData sub) {
        currentItem = (sub);
        infoPanel.setItem(sub);
        if (sub.getSubItems() != null) {
            if (sub.getSubItems().length > 0)
                showSubItemPanel(sub);
        } else {
            if (displayedSubitemPanel != null) {
                displayedSubitemPanel.toggle(false);
            }
        }
    }

    protected void showSubItemPanel(SelectableItemData item) {
        for (SelectableItemData sub : subCache.keySet()) {
            if (sub != item)
                subCache.get(sub).toggle(false);
        }
        displayedSubitemPanel = subCache.get(item);
        if (displayedSubitemPanel == null) {

            SelectionSubItemsPanel subItemsPanel = new SelectionSubItemsPanel(
             item.getSubItems(), item, this);
            displayedSubitemPanel = RollDecorator.decorate(subItemsPanel, FACING_DIRECTION.EAST, false);
            subCache.put(item, displayedSubitemPanel);
            addActor(displayedSubitemPanel);
            displayedSubitemPanel.setPosition(getX() + (getWidth() - subItemsPanel.getWidth()) / 2,
             cache.get(item).getY() - subItemsPanel.getHeight() / 2);
            displayedSubitemPanel.setZIndex(0);
        }
        displayedSubitemPanel.toggle(true);
    }

    protected EventListener getSubPanelListener(SelectableItemData item) {
        return new SmartClickListener(this) {

        };
    }

    protected void addButtons() {
        buttons.forEach(button -> button.remove());
        buttons.clear();
        for (SelectableItemData sub : items) {
            //            boolean selected = sub == currentItem;
            TextButton element = getOrCreateElement(sub);
            addNormalSize(element).top().pad(10, 10, 10, 10);
            buttons.add(element);
            element.setDisabled(isBlocked(sub));
            row();
        }
        //        float top= getTopPadding(items.size());
        //        float left= getLeftPadding();
        //        float botton= getBottonPadding(items.size());
        //        float right= getRightPadding();
        //        pad(top, left, botton, right);
        pad(50, 25, 50, 25);


    }

    protected String getBackgroundPath() {
        if (GdxMaster.getHeight() >= 900)
            return VISUALS.FRAME_BIG_FILLED.getImgPath();
        return VISUALS.FRAME_FILLED.getImgPath();
    }

    public SelectableItemData getCurrentItem() {
        return currentItem;
    }

    public List<SelectableItemData> getItems() {
        return items;
    }

    public void setItems(List<SelectableItemData> items) {
        this.items = items;
        if (!items.isEmpty())
            if (isAutopickFirstItem())
                if (currentItem == null) {
                    try {
                        selected(items.get(0));
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                        currentItem = items.get(0);
                    }

                }
        updateRequired = true;
    }

    protected boolean isAutopickFirstItem() {
        return true;
    }

    public void setInfoPanel(SelectableItemDisplayer infoPanel) {
        this.infoPanel = infoPanel;
    }

    public List<SelectableItemData> toDataList(Collection<? extends Entity> objTypes) {
        List<SelectableItemData> list = new LinkedList<>();
        for (Entity sub : objTypes) {
            list.add(new SelectableItemData(sub));
        }

        return list;
    }

    public boolean isBlocked(SelectableItemData item) {
        return false;
    }

    public boolean isBlocked() {
        return isBlocked(getCurrentItem());
    }

    public void deselect() {
        currentItem = null;
    }

    public void subItemClicked(SelectableItemData item, String sub) {
        infoPanel.subItemClicked(item, sub);
        subCache.get(item).toggle(false);
    }




    public static class SelectableItemData {
        public static final String DEFAULT_SELECTED_BORDER = BORDER.NEO_INFO_SELECT_HIGHLIGHT.getImagePath();
        public String fullsizeImagePath;
        public String description;
        String name;
        String imagePath;
        String borderSelected = DEFAULT_SELECTED_BORDER;
        String borderDisabled = BORDER.HIDDEN.getImagePath();
        String emblem;
        Entity entity;

        String[] subItems;
        private boolean selectionUnderneath;

        public SelectableItemData(String name,
                                  String description, String fullsizeImagePath, String imagePath) {
            this.fullsizeImagePath = fullsizeImagePath;
            this.description = description;
            this.name = name;
            this.imagePath = imagePath;
        }

        public SelectableItemData(Entity entity) {
            name = entity.getName();
            imagePath = entity.getImagePath();
            fullsizeImagePath = ImageManager.getFullSizeImage(entity);
            description = entity.getDescription();
            this.entity = entity;
        }

        public SelectableItemData(String name, String imagePath) {
            this.name = name;
            this.imagePath = imagePath;
        }

        public SelectableItemData(String name, Entity entity) {
            this.name = name;
            this.entity = entity;
            imagePath = entity.getImagePath();
            description = entity.getDescription();
            fullsizeImagePath = ImageManager.getFullSizeImage(entity);
            emblem = entity.getEmblemPath();
        }


        public String[] getSubItems() {
            return subItems;
        }

        public void setSubItems(String[] subItems) {
            this.subItems = subItems;
        }

        public String getEmblem() {
            return emblem;
        }

        public void setEmblem(String emblem) {
            this.emblem = emblem;
        }

        public Entity getEntity() {
            return entity;
        }

        public void setEntity(ObjType entity) {
            this.entity = entity;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public String getFullsizeImagePath() {
            return fullsizeImagePath;
        }

        public void setFullsizeImagePath(String fullsizeImagePath) {
            this.fullsizeImagePath = fullsizeImagePath;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getBorderSelected() {
            return borderSelected;
        }

        public void setBorderSelected(String borderSelected) {
            this.borderSelected = borderSelected;
        }

        public String getBorderDisabled() {
            return borderDisabled;
        }

        public void setBorderDisabled(String borderDisabled) {
            this.borderDisabled = borderDisabled;
        }

        public boolean isSelectionUnderneath() {
            return selectionUnderneath;
        }

        public void setSelectionUnderneath(boolean selectionUnderneath) {
            this.selectionUnderneath = selectionUnderneath;
        }
    }
}
