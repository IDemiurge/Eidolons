package eidolons.libgdx.gui.panels.headquarters.hero;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.content.DC_ContentValsManager;
import eidolons.libgdx.gui.panels.ScrollPanel;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import main.content.VALUE;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 6/13/2018.
 */
public class HqScrolledValuePanel extends HqElement{

    private final ScrollPanel scroll;
    private   HqVerticalValueTable valueTable2;
    private final HqVerticalValueTable valueTable;

    public HqScrolledValuePanel() {
        valueTable = new HqVerticalValueTable(getValuesOne());
//        valueTable2 = new HqVerticalValueTable(getValuesTwo());
        TablePanel<Actor> table = new TablePanel<>();
        table.add(valueTable);
//        table.add(valueTable2);
        add(scroll = new ScrollPanel () {
            @Override
            public int getDefaultOffsetY() {
                return  -200;
            }
        });
        scroll.addElement(table);

        scroll.pad(1, 10, 1, 10);
        scroll.fill();
    }

    private VALUE[] getValuesOne() {
         List<Object> list = new ArrayList<>();
      return   DC_ContentValsManager.getWeaponModifyingParams();
//        return list.toArray(new VALUE[list.size()]);
    }

    @Override
    protected void update(float delta) {
    }

}
