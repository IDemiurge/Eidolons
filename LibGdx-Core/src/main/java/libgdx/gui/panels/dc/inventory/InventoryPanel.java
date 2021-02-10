package libgdx.gui.panels.dc.inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import libgdx.GDX;
import libgdx.TiledNinePatchGenerator;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.panels.TablePanel;
import libgdx.gui.panels.dc.inventory.datasource.EquipDataSource;

public class InventoryPanel extends TablePanel {

    private final Cell mainWeapon;
    private final Cell offWeapon;
    private final Cell offWeaponReserve;
    private final Cell mainWeaponReserve;
    private final Cell avatarPanel;
    private final Cell armorSlot;
    private final Cell amuletSlot;

    public InventoryPanel() {
        TextureRegion textureRegion = new TextureRegion(
         TiledNinePatchGenerator.getOrCreateNinePatch(TiledNinePatchGenerator.NINE_PATCH.SAURON, TiledNinePatchGenerator.BACKGROUND_NINE_PATCH.PATTERN,
          (int) GDX.size(525),(int) GDX.size(880)));
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        setBackground(drawable);

        TablePanel upper = new TablePanel();
        InventorySlotsPanel inventorySlotsPanel = new InventorySlotsPanel();

        addElement(upper)
         .height(340)
         .pad(20, 20, 0, 20)
         .top().expand(1, 0);
        row();

        InventoryQuickSlotPanel quickSlot = new InventoryQuickSlotPanel();
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
        mainWeaponReserve = left.addElement(null).fill(0, 0).bottom();
        left.row();

        RingSlotsPanel leftRingSlotsPanel = new RingSlotsPanel(true);

        left.addElement(leftRingSlotsPanel).fill(0, 0);

        avatarPanel = mid.addElement(null).fill(0, 0).colspan(2).width(128);
        mid.row();
        armorSlot = mid.addElement(null).fill(0, 0).expand(0, 0).top();
        mid.row();
        amuletSlot = mid.addElement(null).fill(0, 0).top().pad(10, 0, 0, 0);
        mid.row();
        right.setBackground(NinePatchFactory.getLightPanelDrawable());
        left.setBackground(NinePatchFactory.getLightPanelDrawable());
        mid.setBackground(NinePatchFactory.getLightDecorPanelDrawable());


        offWeapon = right.addElement(null).fill(0, 0).bottom();
        right.row();
        offWeaponReserve = right.addElement(null).fill(0, 0).bottom();
        right.row();

        RingSlotsPanel rightRingSlotsPanel = new RingSlotsPanel(false);

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
//                event.stop();
                return true;
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
//                event.stop();
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

        mainWeaponReserve.setActor(source.mainWeaponReserve());
        offWeaponReserve.setActor(source.offWeaponReserve());

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
