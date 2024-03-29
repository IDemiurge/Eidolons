package libgdx.gui.dungeon.panels.dc.actionpanel.weapon;

import com.badlogic.gdx.math.Vector2;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.unit.Unit;
import libgdx.gui.dungeon.controls.radial.RadialContainer;
import libgdx.gui.dungeon.controls.radial.RadialManager;
import libgdx.gui.dungeon.controls.radial.RadialMenu;
import main.entity.obj.IActiveObj;
import main.system.EventCallbackParam;

import java.util.List;

/**
 * Created by JustMe on 3/29/2018.
 */
public class QuickAttackRadial extends RadialMenu {
    protected final boolean offhand;
    protected QuickWeaponPanel quickWeaponPanel;


    public QuickAttackRadial(QuickWeaponPanel quickWeaponPanel, boolean offhand) {
        this.offhand = offhand;
        this.quickWeaponPanel = quickWeaponPanel;
    }

    @Override
    protected void initBackground() {
    }
    @Override
    protected void triggered(EventCallbackParam obj) {
        if (!(obj.get() instanceof WeaponItem)) {
            return;
        }
        if (((WeaponItem) obj.get()).isOffhand() != offhand) {
            return;
        }
        openMenu();
    }

    @Override
    protected void adjustPosition() {
        super.adjustPosition();
    }

    @Override
    public void close() {
        super.close();
    }

    public void openMenu() {
        Unit source =getSource();
        List<? extends IActiveObj> attacks = quickWeaponPanel.
         getActiveWeaponDataSource().getActions();
        List<RadialContainer> nodes;
        try {
            nodes =createNodes(source, attacks);
//        getDualNode();
//        getSpecialActionNodes();
            init(nodes);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

    }

    protected Unit getSource() {
        return  quickWeaponPanel.getDataSource().getOwnerObj();
    }

    protected List<RadialContainer> createNodes(Unit source,
                                                List<? extends IActiveObj> attacks) {
        return RadialManager.createNodes(source, null, attacks, false);
    }

    @Override
    protected Vector2 getInitialPosition() {
        return new Vector2(64, 64);
    }

    @Override
    protected int getStartDegree() {
        return offhand ? 140 : 40;
    }

    @Override
    protected boolean isClockwise() {
        return offhand;
    }

    @Override
    protected int getSpectrumDegrees() {
        return 180;
    }
}
