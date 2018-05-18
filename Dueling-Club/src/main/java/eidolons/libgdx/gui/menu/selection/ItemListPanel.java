package eidolons.libgdx.gui.menu.selection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.TextButtonX;
import eidolons.libgdx.gui.panels.ScrollPanel;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.texture.TextureCache;
import main.entity.Entity;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;
import main.system.math.MathMaster;

import java.util.*;

/**
 * Created by JustMe on 11/29/2017.
 */
public abstract class ItemListPanel extends TablePanel {
    protected SelectableItemData currentItem;
    protected List<SelectableItemData> items;
    protected ScrollPanel scrollPanel;
    protected TextButton lastChecked;
    private ItemInfoPanel infoPanel;
    private Map<SelectableItemData, TextButton> cache = new HashMap<>();
    private List<TextButton> buttons = new ArrayList<>();


    public ItemListPanel() {
        super();
        //scroll?
//     TODO    setBackground(new NinePatchDrawable(NinePatchFactory.getMainMenuFrame()));
        setBackground(TextureCache.getOrCreateTextureRegionDrawable(getBackgroundPath()));
        setHeight(MathMaster.minMax(GdxMaster.getHeight(), 800, 1200));
//        scrollPanel = new ScrollPanel();
//        scrollPanel.setSize();
//        addActor(scrollPanel);

    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        clear();
        addElements();
    }

    private Map<SelectableItemData, TextButton> getCache() {
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
            button.setSize(STD_BUTTON.GAME_MENU.getTexture().getMinWidth(),
             STD_BUTTON.GAME_MENU.getTexture().getMinHeight());
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
        return STD_BUTTON.GAME_MENU;
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

    private void clicked(int index) {
        index = Math.min( buttons.size()-1, index);
        clicked(buttons.get(index), getItems().get(index));
    }

    private boolean clicked(TextButton textButton, SelectableItemData sub) {
        currentItem = (sub);
        getParent().setUserObject(sub);
        infoPanel.setItem(sub);
        if (lastChecked != null)
            lastChecked.setChecked(false);
        lastChecked = textButton;
        textButton.setChecked(true);
//                textButton.getClickListener().clicked(event, x, y);
//                button.down=true;

//              getListeners()  clicked(event, x, y);
        return false;
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
        if (GdxMaster.getHeight()>900)
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

    public ItemInfoPanel getInfoPanel() {
        return infoPanel;
    }

    public void setInfoPanel(ItemInfoPanel infoPanel) {
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
        currentItem=null;
    }


    public static class SelectableItemData {
        public String previewImagePath;
        public String description;
        String name;
        String imagePath;
        String borderSelected;
        String borderDisabled;
        Entity entity;

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
    }
}
