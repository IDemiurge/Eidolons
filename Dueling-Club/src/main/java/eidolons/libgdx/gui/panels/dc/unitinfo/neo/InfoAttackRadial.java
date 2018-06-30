package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import com.badlogic.gdx.math.Vector2;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.gui.controls.radial.RadialValueContainer;
import eidolons.libgdx.gui.panels.dc.actionpanel.weapon.QuickAttackRadial;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import main.entity.obj.ActiveObj;

import java.util.List;

/**
 * Created by JustMe on 6/30/2018.
 *
 * always visible
 *
 */
public class InfoAttackRadial extends QuickAttackRadial {
    private static final int SLOTS = 6;

    public InfoAttackRadial(UnitInfoWeapon panel, boolean offhand) {
        super(panel, offhand);

    }

    @Override
    protected void bindEvents() {

    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        openMenu();
    }

    @Override
    public void close() {

    }

    @Override
    public void open() {
        currentNode.setChildVisible(true);
        setVisible(true);
        updatePosition();
    }

    @Override
    protected List<RadialValueContainer> createNodes(Unit source, List<? extends ActiveObj> attacks) {
        List<RadialValueContainer> list = super.createNodes(source, attacks);
        for (int i = list.size(); i < SLOTS; i++) {
            list.add(createSlotNode());
        }
        return list;
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
    private RadialValueContainer createSlotNode() {
      return   new RadialValueContainer(TextureCache.getOrCreateR(Images.EMPTY_SKILL_SLOT), ()->{
        });
    }

    public void processNode(RadialValueContainer node){
        node.getCaptureListeners();
        //tooltips

    }
}
