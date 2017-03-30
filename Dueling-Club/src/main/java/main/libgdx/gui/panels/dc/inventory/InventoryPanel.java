package main.libgdx.gui.panels.dc.inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.StyleHolder;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class InventoryPanel extends TablePanel {
    private final InventorySlotsPanel inventorySlotsPanel;
    private final InventoryQuickSlotPanel quickSlot;
    private Cell mainWeapon;
    private Cell offWeapon;
    private RingSlotsPanel leftRingSlotsPanel;
    private RingSlotsPanel rightRingSlotsPanel;
    private Cell avatarPanel;
    private Cell armorSlot;
    private Cell amuletSlot;
    private Cell<Actor> actionPointsText;
    private Cell<Actor> applyButton;
    private Cell<Actor> cancelButton;
    private Cell<Actor> revertButton;

    public InventoryPanel() {
        TextureRegion textureRegion = new TextureRegion(getOrCreateR("UI/components/inventory_background.png"));
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        setBackground(drawable);

        //debug();

        TablePanel upper = new TablePanel();
        inventorySlotsPanel = new InventorySlotsPanel();

        addElement(upper)
                .height(340)
                .pad(20, 20, 0, 20)
                .top().expand(1, 0);
        row();
        quickSlot = new InventoryQuickSlotPanel();
        addElement(quickSlot);
        row();
        addElement(inventorySlotsPanel).pad(0, 20, 0, 20);
        row();

        TablePanel equip = new TablePanel();

        upper.addElement(equip);

        TablePanel left = new TablePanel();
        TablePanel mid = new TablePanel();
        TablePanel right = new TablePanel();

        equip.addElement(left);
        equip.addElement(mid);
        equip.addElement(right);

        mainWeapon = left.addElement(null).fill(0, 0).bottom();
        left.row();

        leftRingSlotsPanel = new RingSlotsPanel();

        left.addElement(leftRingSlotsPanel).fill(0, 0);

        avatarPanel = mid.addElement(null).fill(0, 0);
        mid.row();
        armorSlot = mid.addElement(null).fill(0, 0).expand(0, 0).top();
        mid.row();
        amuletSlot = mid.addElement(null).fill(0, 0).top().pad(10, 0, 0, 0);
        mid.row();


        offWeapon = right.addElement(null).fill(0, 0).bottom();
        right.row();

        rightRingSlotsPanel = new RingSlotsPanel();

        right.addElement(rightRingSlotsPanel).fill(0, 0);

        final TablePanel<Actor> lower = new TablePanel<>();
        addElement(lower).pad(0, 20, 20, 20);

        actionPointsText = lower.addElement(null).left();


        revertButton = lower.addElement(null).fill(false).expand(0, 0)
                .right().pad(20, 0, 20, 0).size(50, 50);
        cancelButton = lower.addElement(null).fill(false).expand(0, 0)
                .right().pad(20, 10, 20, 0).size(50, 50);
        applyButton = lower.addElement(null).fill(false).expand(0, 0)
                .right().pad(20, 10, 20, 10).size(50, 50);

        clear();
    }

    @Override
    public void clear() {
        inventorySlotsPanel.clear();
        quickSlot.clear();
        mainWeapon.setActor(new ValueContainer(getOrCreateR("UI/components/2017/generic/inventory/empty weapon.png")));
        offWeapon.setActor(new ValueContainer(getOrCreateR("UI/components/2017/generic/inventory/empty weapon.png")));
        leftRingSlotsPanel.clear();
        rightRingSlotsPanel.clear();
        avatarPanel.setActor(new ValueContainer(getOrCreateR("/mini/unit/empty_avatar.jpg")));
        armorSlot.setActor(new ValueContainer(getOrCreateR("/mini/item/armor/empty.jpg")));
        amuletSlot.setActor(new ValueContainer(getOrCreateR("/mini/item/jewelry/talisman/amulet_empty.jpg")));
        actionPointsText.setActor(new ValueContainer("action available", "2/2"));


        applyButton.setActor(new Button(StyleHolder.getCustomButtonStyle("UI/components/small/ok.png")));
        cancelButton.setActor(new Button(StyleHolder.getCustomButtonStyle("UI/components/small/no.png")));
        revertButton.setActor(new Button(StyleHolder.getCustomButtonStyle("UI/components/small/back2.png")));
    }
}
