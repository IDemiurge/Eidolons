package eidolons.libgdx.gui.panels.headquarters.hero;

import eidolons.libgdx.bf.DynamicLayeredActor;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.texture.Images;
import main.ability.AbilityObj;
import main.content.values.properties.G_PROPS;
import main.entity.Entity;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 4/13/2018.
 */
public class HqTraitsPanel extends HqElement {
    protected boolean expanded;

    @Override
    protected void update(float delta) {
        //slot based? to use fade 
        clear();
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
            add(actor);
        }

    }

    protected List<? extends Entity> getData() {

        List<AbilityObj> list = new ArrayList<>();
         for (AbilityObj obj : dataSource.getEntity().getPassives()) {
            if (obj.isDisplayed()) {
                if (StringUtils.isNoneEmpty(obj.getType().getProperty(G_PROPS.IMAGE))) {
                    list.add(obj);
                }
            }
        }
        return list;
//        return dataSource.getEntity().getPassives();
    }

    protected DynamicLayeredActor createActor(Entity sub) {
        DynamicLayeredActor actor = new DynamicLayeredActor( sub.getImagePath(), 
         getOverlay(sub), getUnderlay(sub));
//        container.setSize(getSize(), getSize());
        
        return actor;
    }

    protected String getUnderlay(Entity sub) {
        return Images.EMPTY_RANK_SLOT;
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
}
