package eidolons.libgdx.gui.panels.dc.actionpanel.weapon;

import com.badlogic.gdx.math.Vector2;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.gui.controls.radial.RadialManager;
import eidolons.libgdx.gui.controls.radial.RadialMenu;
import eidolons.libgdx.gui.controls.radial.RadialValueContainer;
import main.entity.obj.ActiveObj;
import main.system.EventCallbackParam;

import java.util.List;

/**
 * Created by JustMe on 3/29/2018.
 */
public class QuickAttackRadial extends RadialMenu {
    private final boolean offhand;
    QuickWeaponPanel quickWeaponPanel;

    public QuickAttackRadial(QuickWeaponPanel quickWeaponPanel, boolean offhand) {
        this.offhand = offhand;
        this.quickWeaponPanel = quickWeaponPanel;
    }

    @Override
    protected void triggered(EventCallbackParam obj) {
        if (!(obj.get() instanceof DC_WeaponObj)) {
            return;
        }
        if (((DC_WeaponObj) obj.get()).isOffhand() != offhand) {
            return;
        }
        openMenu();
    }

    @Override
    public void close() {
        super.close();
    }

    public void openMenu() {
        Unit source = quickWeaponPanel.getDataSource().getOwnerObj();
        List<? extends ActiveObj> attacks = quickWeaponPanel.
         getActiveWeaponDataSource().getActions();
        List<RadialValueContainer> nodes = null;
        try {
            nodes =RadialManager.createNodes(source, null, attacks, false);
//        getDualNode();
//        getSpecialActionNodes();
            init(nodes);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

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
