package eidolons.libgdx.gui.panels.headquarters.hero;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.kotcrab.vis.ui.layout.HorizontalFlowGroup;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.DynamicLayeredActor;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.panels.dc.unitinfo.tooltips.AttackTooltipFactory;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import main.ability.AbilityObj;
import main.content.values.properties.G_PROPS;
import main.entity.Entity;
import main.entity.obj.BuffObj;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 4/13/2018.
 */
public class HqTraitsPanel extends HqElement {
    protected boolean expanded;

    public HqTraitsPanel( ) {
        super();
//        expandButton;
    }

    @Override
    protected void update(float delta) {
        //slot based? to use fade 
        clear();
        setWidth(465);
        HorizontalFlowGroup group = new HorizontalFlowGroup(5);
        group.setSize(getWidth()*0.7f, getHeight());
        add(group).left();
        List<? extends Entity> list = getData();
        int i = 0;
        //fill with nulls? 
        for (Entity sub : list) {
            if (i++ >= getWrap()) {
                if (isExpanded()) {
                    row();
                    i = 0;
                } else
                    break;
            }
            DynamicLayeredActor actor = createActor(sub);
            group.addActor(actor);
        }

        setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
    }

    protected List<? extends Entity> getData() {

        List<  Entity> list = new ArrayList<>();
         for (AbilityObj obj : dataSource.getEntity().getPassives()) {
            if (obj.isDisplayed()) {
                if (StringUtils.isNoneEmpty(obj.getType().getProperty(G_PROPS.IMAGE))) {
                    list.add(obj);
                }
            }
        }
        for (BuffObj buff : dataSource.getEntity().getBuffs()) {
            if (buff.isDynamic()){
                continue;
            }
            if (buff.isDisplayed()){
                list.add(buff);
            }
        }
        return list;
//        return dataSource.getEntity().getPassives();
    }

    protected DynamicLayeredActor createActor(Entity sub) {
        DynamicLayeredActor actor = new DynamicLayeredActor( sub.getImagePath(), 
         getOverlay(sub), getUnderlay(sub)){
            protected void init() {
                setSize(getDefaultWidth(), getDefaultHeight() );
                GdxMaster.center(underlay);
                GdxMaster.center(image);
                GdxMaster.center(overlay);
            }
            protected float getDefaultWidth() {
                return 50;
            }
            protected float getDefaultHeight() {
                return 50;
            }
        };
//        container.setSize(getSize(), getSize());
        actor.setSize(50, 50);
        AttackTooltipFactory.createBuffTooltip(sub);
        actor.addListener(new ValueTooltip(sub.getName()).getController());
        return actor;
    }

    protected String getUnderlay(Entity sub) {
        return "";
    }

    protected String getOverlay(Entity sub) {
        return "";
    }
 
    protected int getWrap() {
        return 8;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
