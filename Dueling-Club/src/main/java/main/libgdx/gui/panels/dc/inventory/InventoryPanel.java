package main.libgdx.gui.panels.dc.inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.game.core.Eidolons;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class InventoryPanel extends TablePanel {

    private InventorySlotsPanel inventorySlotsPanel;
    private InventoryQuickSlotPanel quickSlot;
    private Cell mainWeapon;
    private Cell offWeapon;
    private RingSlotsPanel leftRingSlotsPanel;
    private RingSlotsPanel rightRingSlotsPanel;
    private Cell avatarPanel;
    private Cell armorSlot;
    private Cell amuletSlot;

    private Cell<Actor> actionPointsText;
    private Cell<Actor> doneButton;
    private Cell<Actor> cancelButton;
    private Cell<Actor> undoButton;

    public InventoryPanel() {
        TextureRegion textureRegion = new TextureRegion(getOrCreateR("UI/components/inventory_background.png"));
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        setBackground(drawable);

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

        leftRingSlotsPanel = new RingSlotsPanel(true);

        left.addElement(leftRingSlotsPanel).fill(0, 0);

        avatarPanel = mid.addElement(null).fill(0, 0);
        mid.row();
        armorSlot = mid.addElement(null).fill(0, 0).expand(0, 0).top();
        mid.row();
        amuletSlot = mid.addElement(null).fill(0, 0).top().pad(10, 0, 0, 0);
        mid.row();


        offWeapon = right.addElement(null).fill(0, 0).bottom();
        right.row();

        rightRingSlotsPanel = new RingSlotsPanel(false);

        right.addElement(rightRingSlotsPanel).fill(0, 0);

        initLowerBlock();

        initListeners();

        setTouchable(Touchable.enabled);
    }

    private static String getOperationsString() {
        return Eidolons.game.getInventoryManager().getOperationsLeft() + "/" +
                Eidolons.game.getInventoryManager().getOperationsPool();
    }

    @Override
    public void clear() {

    }

    private void initListeners() {
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return true;
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                event.stop();
                return true;
            }
        });
    }

    private void initLowerBlock() {
        final TablePanel<Actor> lower = new TablePanel<>();
        addElement(lower).pad(0, 20, 20, 20);

        actionPointsText = lower.addElement(null).left();

        undoButton = lower.addElement(new InventoryActionButton("UI/components/small/back2.png"))
                .fill(false).expand(0, 0).right()
                .pad(20, 0, 20, 0).size(50, 50);

        cancelButton = lower.addElement(new InventoryActionButton("UI/components/small/no.png"))
                .fill(false).expand(0, 0).right()
                .pad(20, 10, 20, 0).size(50, 50);

        doneButton = lower.addElement(new InventoryActionButton("UI/components/small/ok.png"))
                .fill(false).expand(0, 0).right()
                .pad(20, 10, 20, 10).size(50, 50);
    }

    @Override
    public void afterUpdateAct(float delta) {
        clear();
        super.afterUpdateAct(delta);

        final InventoryDataSource source = (InventoryDataSource) getUserObject();

        mainWeapon.setActor(source.mainWeapon());
        offWeapon.setActor(source.offWeapon());

        avatarPanel.setActor(source.avatar());

        armorSlot.setActor(source.armor());

        amuletSlot.setActor(source.amulet());

        actionPointsText.setActor(new ValueContainer("Actions available:", getOperationsString()));

        InventoryActionButton button = (InventoryActionButton) doneButton.getActor();
        button.addListener(source.getDoneHandler());
        button.setDisabled(source.isDoneDisabled());

        button = (InventoryActionButton) cancelButton.getActor();
        button.addListener(source.getCancelHandler());
        button.setDisabled(source.isCancelDisabled());

        button = (InventoryActionButton) undoButton.getActor();
        button.addListener(source.getUndoHandler());
        button.setDisabled(source.isUndoDisabled());
    }

}
