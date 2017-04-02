package main.libgdx.gui.panels.dc.inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.game.core.Eidolons;
import main.libgdx.StyleHolder;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.inventory.datasource.EquipDataSource;
import main.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;

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
    //TODO extract all below this into *DIALOG*, the rest is same for HC inventory
    private Cell<Actor> actionPointsText;
    private Cell<Actor> doneButton;
    private Cell<Actor> cancelButton;
    private Cell<Actor> undoButton;

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

        final TablePanel<Actor> lower = new TablePanel<>();
        addElement(lower).pad(0, 20, 20, 20);

        actionPointsText = lower.addElement(null).left();


        undoButton = lower.addElement(null).fill(false).expand(0, 0)
         .right().pad(20, 0, 20, 0).size(50, 50);
        cancelButton = lower.addElement(null).fill(false).expand(0, 0)
         .right().pad(20, 10, 20, 0).size(50, 50);
        doneButton = lower.addElement(null).fill(false).expand(0, 0)
         .right().pad(20, 10, 20, 10).size(50, 50);

        clear();

    }

    public static String getOperationsString() {
        return Eidolons.game.getInventoryManager().getOperationsLeft() + "/" +
         Eidolons.game.getInventoryManager().getOperationsPool();
    }

    public void initButtonListeners(InventoryClickHandler handler) {
//        undoButton.getActor().removeListener()
        undoButton.getActor().addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                handler.undoClicked();
            }
        });
        doneButton.getActor().addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                handler.doneClicked();
            }
        });
        cancelButton.getActor().addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                handler.cancelClicked();
            }
        });
    }

    @Override
    public void clear() {

        actionPointsText.setActor(new ValueContainer("Actions available:", getOperationsString()) {
            @Override
            public void afterUpdateAct(float delta) {
                valueContainer.setActor(new Label(getOperationsString(),
                 StyleHolder.getDefaultLabelStyle()));
                super.afterUpdateAct(delta);
            }
        });
        doneButton.setActor(new Button(StyleHolder.getCustomButtonStyle
                             ("UI/components/small/ok.png")) {
                                public boolean isDisabled() {
                                    Object obj = getUserObject();
                                    if (obj instanceof InventoryDataSource) {
                                        return !((InventoryDataSource) obj).
                                         getHandler().isDoneEnabled();
                                    }
                                    return super.isDisabled();

                                }
                            }
        );
        cancelButton.setActor(new Button(StyleHolder.getCustomButtonStyle
         ("UI/components/small/no.png")) {
            public boolean isDisabled() {
                Object obj = getUserObject();
                if (obj instanceof InventoryDataSource) {
                    return !((InventoryDataSource) obj).
                     getHandler().isCancelEnabled();
                }
                return super.isDisabled();

            }
        });
        undoButton.setActor(new Button(StyleHolder.getCustomButtonStyle
         ("UI/components/small/back2.png")) {
            public boolean isDisabled() {
                Object obj = getUserObject();
                if (obj instanceof InventoryDataSource) {
                    return !((InventoryDataSource) obj).
                     getHandler().isUndoEnabled();
                }
                return super.isDisabled();

            }
        });
    }

    @Override
    public void setUserObject(Object userObject) {
        if (userObject == null)
            return;
        super.setUserObject(userObject);
    }

    @Override
    public void afterUpdateAct(float delta) {
        clear();
        super.afterUpdateAct(delta);

        final EquipDataSource source = (EquipDataSource) getUserObject();

        mainWeapon.setActor(source.mainWeapon());
        offWeapon.setActor(source.offWeapon());

        avatarPanel.setActor(source.avatar());

        armorSlot.setActor(source.armor());

        amuletSlot.setActor(source.amulet());

    }

}
