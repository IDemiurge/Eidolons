package eidolons.libgdx.gui.panels.dc.inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.libgdx.GDX;
import eidolons.libgdx.TiledNinePatchGenerator;
import eidolons.libgdx.TiledNinePatchGenerator.BACKGROUND_NINE_PATCH;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.dc.inventory.datasource.EquipDataSource;

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

    public InventoryPanel() {
        TextureRegion textureRegion = new TextureRegion(
         TiledNinePatchGenerator.getOrCreateNinePatch(NINE_PATCH.SAURON, BACKGROUND_NINE_PATCH.PATTERN,
          (int) GDX.size(525),(int) GDX.size(880)));
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

        initListeners();

        setTouchable(Touchable.enabled);
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

    @Override
    public void updateAct(float delta) {
        if (getUserObject() == null)
            return;
        clear();
        super.updateAct(delta);

        final EquipDataSource source = (EquipDataSource) getUserObject();

        mainWeapon.setActor(source.mainWeapon());
        offWeapon.setActor(source.offWeapon());

        avatarPanel.setActor(source.avatar());

        armorSlot.setActor(source.armor());

        amuletSlot.setActor(source.amulet());

    }

    @Override
    public void afterUpdateAct(float delta) {
        clear();

    }

}
