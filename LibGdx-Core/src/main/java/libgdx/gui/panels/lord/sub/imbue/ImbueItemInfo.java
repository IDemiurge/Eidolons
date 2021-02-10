package libgdx.gui.panels.lord.sub.imbue;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import eidolons.entity.item.DC_ArmorObj;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.DC_WeaponObj;
import libgdx.ActorMaster;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.generic.NoHitImage;
import libgdx.gui.panels.ScrollPaneX;
import libgdx.gui.panels.TablePanelX;
import libgdx.gui.panels.dc.unitinfo.tooltips.ArmorTooltip;
import libgdx.gui.panels.dc.unitinfo.tooltips.SlotItemTooltip;
import libgdx.gui.panels.dc.unitinfo.tooltips.WeaponTooltip;
import libgdx.texture.Images;

public class ImbueItemInfo extends TablePanelX {

    private   TablePanelX  table;
    private final FadeImageContainer img;
    private final Cell cell;
    private   ScrollPaneX scroll;

    public ImbueItemInfo() {
        super(400, 800);
        setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());

//        add(scroll = new ScrollPaneX(table = new TablePanelX<>()));
//        scroll.setSize(300, 500);
//        add( table = new TablePanelX<>(getWidth(), getHeight())) ;
//        table.setFixedMinSize(true);
//        table.setFixedMaxSize(true);
        cell = add(new Actor()).width(getWidth()).height(getHeight());
        ActorMaster.addCenterY(this,  new NoHitImage(Images.CIRCLE_BORDER));
        ActorMaster.addCenterY(this,  img = new FadeImageContainer(Images.EMPTY_LIST_ITEM));

    }

    @Override
    public float getMaxHeight() {
        return getHeight();
    }

    @Override
    public float getMaxWidth() {
        return super.getWidth();
    }

    @Override
    protected void setUserObjectForChildren(Object userObject) {
    }
    protected boolean invalidateOnUpdate() {
        return true;
    }
    @Override
    public void updateAct(float delta) {
//        table.clearChildren();
        boolean updating = true;
        DC_HeroItemObj item = (DC_HeroItemObj) getUserObject();
        if (item == null) {
            img.setImage("");
            cell.setActor(null);
            return;
        }
        img.setImage(item.getImagePath().replace("icons", "sprites"));
        if (item instanceof DC_WeaponObj) {
            SlotItemTooltip tooltip = new WeaponTooltip((DC_WeaponObj) item);
//            table. add(tooltip).row();
            cell.setActor(tooltip);
            tooltip.setWidth(getWidth());
            tooltip.updateAct(delta);
        } else {
            if (item instanceof DC_ArmorObj) {
                SlotItemTooltip tooltip = new ArmorTooltip((DC_ArmorObj) item);
//                table.  add(tooltip).row();
                cell.setActor(tooltip);
                tooltip.setWidth(getWidth());
                tooltip.updateAct(delta);
            }
        }
        updating = false;
        super.updateAct(delta);

    }

    @Override
    public void invalidateHierarchy() {
//        if (updating)
//            return;
        super.invalidateHierarchy();
    }

    @Override
    protected void childrenChanged() {
        super.childrenChanged();
    }
}
