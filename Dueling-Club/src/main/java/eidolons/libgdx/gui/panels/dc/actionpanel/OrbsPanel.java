package eidolons.libgdx.gui.panels.dc.actionpanel;

import eidolons.content.PARAMS;
import eidolons.libgdx.gui.panels.dc.TablePanel;
import eidolons.libgdx.gui.panels.dc.ValueContainer;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.ResourceSource;
import eidolons.libgdx.gui.tooltips.ValueTooltip;

import java.util.Arrays;

/**
 * Created by JustMe on 9/21/2017.
 */
public class OrbsPanel extends TablePanel {
    private PARAMS[] params;
    private OrbElement[] orbs;

    public OrbsPanel(PARAMS... params) {
        this.params = params;
        orbs = new OrbElement[params.length];
        for (int i = 0; i < params.length; i++) {
            orbs[i] = null;

        }

    }

    public static void addTooltip(OrbElement el, String name, String val) {
        ValueTooltip tooltip = new ValueTooltip();
        tooltip.setUserObject(Arrays.asList(new ValueContainer(el.getIconRegion(), name, val)));
        el.addListener(tooltip.getController());
    }

    //TODO smooth update?
    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);

        final ResourceSource source = (ResourceSource) getUserObject();

        if (source == null)
            return;
        int i = 0;
        for (OrbElement orb : orbs) {
            if (orb == null) {
                orb = (new OrbElement(params[i]
                 , source.getParam(params[i]))
                );
                orbs[i] = orb;
                orb.setPosition(i * 100, 0);
                addActor(orb);
            } else
                orb.updateValue(source.getParam(params[i]));
            orb.act(delta);
//            if (orb.getActor() != null)

//

            addTooltip(orb, params[i].getName(), source.getParam(params[i]));
            i++;
        }
    }

}
