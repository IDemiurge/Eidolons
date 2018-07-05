package eidolons.libgdx.gui.menu.selection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.TiledNinePatchGenerator;
import eidolons.libgdx.TiledNinePatchGenerator.BACKGROUND_NINE_PATCH;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH;
import eidolons.libgdx.gui.RollDecorator;
import eidolons.libgdx.gui.RollDecorator.RollableGroup;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.TextButtonX;
import eidolons.libgdx.gui.panels.ScrollPanel;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.game.bf.directions.FACING_DIRECTION;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;

import java.util.*;

/**
 * Created by JustMe on 11/29/2017.
 */
public abstract class ItemListPanel extends TablePanel {
    protected SelectableItemData currentItem;
    protected List<SelectableItemData> items;
    protected ScrollPanel scrollPanel;
    protected TextButton lastChecked;
    protected SelectableItemDisplayer infoPanel;
    protected Map<SelectableItemData, TextButton> cache = new HashMap<>();
    protected List<TextButton> buttons = new ArrayList<>();
    protected Map<SelectableItemData, RollableGroup> subCache = new HashMap<>();

    public ItemListPanel() {
        super();
        initBg();

    }

    protected void initBg() {
        //        setBackground(TextureCache.getOrCreateTextureRegionDrawable(getBackgroundPath()));

        TextureRegion generated = new TextureRegion(
         TiledNinePatchGenerator.getOrCreateNinePatch(NINE_PATCH.FRAME,
          BACKGROUND_NINE_PATCH.SEMI,
          (int) GdxMaster.adjustSize(410)
          , GdxMaster.getHeight() * 6 / 7));
        setSize(generated.getRegionWidth(), generated.getRegionHeight());
        setFixedSize(true);
        setBackground(new TextureRegionDrawable(generated));
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        clear();
        addElements();
    }

    protected Map<SelectableItemData, TextButton> getCache() {
        return cache;
    }

    protected TextButton getOrCreateElement(SelectableItemData sub) {
        TextButton button = getCache().get(sub);

        if (button == null || sub == null) {
            button = new TextButtonX((sub.name),
             StyleHolder.getTextButtonStyle(getButtonStyle(),
              getFontStyle(), getFontColor(), getFontSize())) {

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
        currentItem = (sub);
//        getParent().setUserObject(sub); what for?
        infoPanel.setItem(sub);
        if (lastChecked != null)
            lastChecked.setChecked(false);
        lastChecked = textButton;
        textButton.setChecked(true);
        if (sub.getSubItems() != null) {
            if (sub.getSubItems().length > 0)
                showSubItemPanel(sub );
        }
        //                textButton.getClickListener().clicked(event, x, y);
        //                button.down=true;

        //              getListeners()  clicked(event, x, y);
        return false;
    }

    protected void showSubItemPanel(SelectableItemData item) {
        for (RollableGroup sub: subCache.values())
        {
            sub.toggle(false);
        }
        RollableGroup rollable = subCache.get(item);
        if (rollable==null ){

            SelectionSubItemsPanel subItemsPanel = new SelectionSubItemsPanel(
             item.getSubItems(), item, this);
            rollable = RollDecorator.decorate(subItemsPanel, FACING_DIRECTION.WEST, false);
            subCache.put(item, rollable);
            addActor(rollable);
            rollable.setPosition(getX()+getWidth()-subItemsPanel.getWidth(),
             cache.get(item).getY()-subItemsPanel.getHeight()/2);
            rollable.setZIndex(0);
        }
    }

    protected EventListener getSubPanelListener(SelectableItemData item) {
        return new SmartClickListener(this){

        };
    }

    protected void addElements() {
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
        updateRequired = true;
    }

    public void setInfoPanel(SelectableItemDisplayer infoPanel) {
        this.infoPanel = infoPanel;
    }

    public List<SelectableItemData> toDataList(List<? extends Entity> objTypes) {
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
    }


    public static class SelectableItemData {
        public String previewImagePath;
        public String description;
        String name;
        String imagePath;
        String borderSelected;
        String borderDisabled;
        String emblem;
        Entity entity;

        String[] subItems;

        public SelectableItemData(String name,
                                  String description, String previewImagePath, String imagePath) {
            this.previewImagePath = previewImagePath;
            this.description = description;
            this.name = name;
            this.imagePath = imagePath;
        }

        public SelectableItemData(Entity entity) {
            name = entity.getName();
            imagePath = entity.getImagePath();
            previewImagePath = ImageManager.getFullSizeImage(entity);
            borderSelected = BORDER.HIGHLIGHTED.getImagePath();
            borderDisabled = BORDER.HIDDEN.getImagePath();
            description = entity.getDescription();
            this.entity = entity;
        }

        public SelectableItemData(String name, Entity entity) {
            this.name = name;
            this.entity = entity;
            imagePath=entity.getImagePath();
            description=entity.getDescription();
            previewImagePath = StringMaster.getAppendedImageFile(entity.getImagePath(), " full", true);
            emblem = entity.getEmblemPath();
        }

        public String[] getSubItems() {
            return subItems;
        }

        public String getEmblem() {
            return emblem;
        }

        public void setSubItems(String[] subItems) {
            this.subItems = subItems;
        }

        public Entity getEntity() {
            return entity;
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

        public String getPreviewImagePath() {
            return previewImagePath;
        }

        public void setPreviewImagePath(String previewImagePath) {
            this.previewImagePath = previewImagePath;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setEntity(ObjType entity) {
            this.entity = entity;
        }

        public void setEmblem(String emblem) {
            this.emblem = emblem;
        }
    }
}
