package libgdx.gui.dungeon.panels.dc.unitinfo.neo;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.unit.Unit;
import libgdx.gui.dungeon.controls.radial.RadialContainer;
import libgdx.gui.dungeon.panels.dc.actionpanel.weapon.QuickAttackRadial;
import libgdx.gui.dungeon.panels.dc.unitinfo.tooltips.ActionTooltip;
import libgdx.gui.dungeon.panels.headquarters.datasource.hero.HqHeroDataSource;
import libgdx.assets.texture.TextureCache;
import libgdx.gui.generic.btn.ButtonStyled;
import main.entity.Entity;
import main.entity.obj.IActiveObj;

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
        closeButton.setVisible(false);

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
    protected void setCurrentNode(RadialContainer node) {
        super.setCurrentNode(node);
    }

    @Override
    protected String getCloseNodePath() {
        return super.getCloseNodePath();
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return super.hit(x, y, touchable);
    }

    @Override
    public void hover(Entity entity) {
        super.hover(entity);
    }

    @Override
    public void hoverOff(Entity entity) {
        super.hoverOff(entity);
    }

    @Override
    public void open() {
        currentNode.setChildVisible(true);
        setVisible(true);
        updatePosition();
        closeButton.setVisible(false);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
     getColor().a=1;
     closeButton.setVisible(false);
     closeButton. getColor().a=0;
        super.draw(batch, parentAlpha);
    }

    @Override
    protected List<RadialContainer> createNodes(Unit source, List<? extends IActiveObj> attacks) {
        List<RadialContainer> list = super.createNodes(source, attacks);
        int i=0;
        for (RadialContainer node : list) {
            IActiveObj a = attacks.get(i++);
            processNode(node, a);
        }
//        for (int j = list.size(); j < SLOTS; j++) {
//            list.add(createSlotNode());
//        }
        return list;
    }

    public void processNode(RadialContainer node, IActiveObj a){
        ActionTooltip tooltip = new ActionTooltip((ActiveObj) a);
        node.clearListeners();
        node.addListener(tooltip.getController());
        TextureRegion underlay= TextureCache.getOrCreateR(ButtonStyled.STD_BUTTON.CIRCLE.getPath() );
        node.setUnderlay_(underlay);
        node.setTextOverlayOn(false);
    }

    protected double getRadiusBase() {
        return 139;
    }
    @Override
    protected Vector2 getInitialPosition() {
        return new Vector2( 114,  114);
    }

    @Override
    protected int getStartDegree() {
        return offhand ? 10 : 155;
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
        return 150;
    }
    private RadialContainer createSlotNode() {
      return   new RadialContainer(TextureCache.getOrCreateR(ButtonStyled.STD_BUTTON.CIRCLE.getPath()), ()->{
        });
    }

//    @Override
//    public void init(List<RadialValueContainer> nodes) {
//        super.init(nodes);
//    }

//    protected double getRadiusBase() {
//        return 72;
//    }

//    protected boolean isMakeSecondRing(int size) {
//        return size > 15;
//    }


//    protected boolean isClockwise() {
//        return false;
//    }
//
//    protected int getStartDegree() {
//        return 90;
//    }
//
//    protected int getSpectrumDegrees() {
//        return 360;
//    }
//
//    protected float getAnimationDuration() {
//        return 0.65f;
//    }
}
