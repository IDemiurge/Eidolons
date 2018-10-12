package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.gui.controls.radial.RadialValueContainer;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.panels.dc.actionpanel.weapon.QuickAttackRadial;
import eidolons.libgdx.gui.panels.dc.unitinfo.tooltips.ActionTooltip;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
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
    private static final int SLOTS = 5;

    public InfoAttackRadial(UnitInfoWeapon panel, boolean offhand) {
        super(panel, offhand);

    }

    @Override
    protected void bindEvents() {

    }
    protected Unit getSource() {
        return  ((HqHeroDataSource)getUserObject()).getEntity();
    }
    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
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
        int i=0;
        for (RadialValueContainer node : list) {
            ActiveObj a = attacks.get(i++);
            processNode(node, a);
        }
        for (int j = list.size(); j < SLOTS; j++) {
            list.add(createSlotNode());
        }
        return list;
    }

    public void processNode(RadialValueContainer node, ActiveObj a){
        ActionTooltip tooltip = new ActionTooltip((DC_ActiveObj) a);
        node.clearListeners();
        node.addListener(tooltip.getController());
        TextureRegion underlay= TextureCache.getOrCreateR(STD_BUTTON.CIRCLE.getPath() );
        node.setUnderlay_(underlay);
    }

    protected double getRadiusBase() {
        return 112;
    }
    @Override
    protected Vector2 getInitialPosition() {
        return new Vector2(64, 64);
    }

    @Override
    protected int getStartDegree() {
        return offhand ? 60 : 100;
    }

    @Override
    protected boolean isClockwise() {
        return  offhand;
    }

    @Override
    protected float getOffsetX() {
        return super.getOffsetX();
    }

    @Override
    protected int getSpectrumDegrees() {
        return 250;
    }
    private RadialValueContainer createSlotNode() {
      return   new RadialValueContainer(TextureCache.getOrCreateR(STD_BUTTON.CIRCLE.getPath()), ()->{
        });
    }

}
