
package eidolons.libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import eidolons.content.PARAMS;
import eidolons.game.EidolonsGame;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.generic.VerticalValueContainer;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.ResourceSource;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import eidolons.system.text.DescriptionTooltips;
import main.content.ContentValsManager;
import main.system.launch.CoreEngine;

import java.util.Arrays;
import java.util.Collections;

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
        String description = DescriptionTooltips.tooltip(el.getParameter());
        ValueContainer container = new VerticalValueContainer(el.getIconRegion(), name + ": " + val, description);
        container.setSize(600, 400);
        container.setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
        tooltip.setUserObject(Collections.singletonList(container));
        el.clearListeners();
        el.addListener(tooltip.getController());
        el.addListener(new SmartClickListener(el) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (CoreEngine.isIDE()) {
                    int perc = 25;
                    if (event.getButton() == 1 || getTapCount() > 1) {
                        perc = -25;
                    }
                    int finalPerc = perc;
                    new Thread(() -> {
                        Eidolons.getMainHero().modifyParamByPercent(ContentValsManager.getCurrentParam(
                                el.getParameter()), finalPerc);
                        Eidolons.getGame().getManager().reset();
                    }, " thread").start();

                }
                super.clicked(event, x, y);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            protected void onDoubleClick(InputEvent event, float x, float y) {
                super.onDoubleClick(event, x, y);
            }
        });
    }

    //TODO smooth update?
    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);

        final ResourceSource source = (ResourceSource) getUserObject();

        if (source == null)
            return;
        int i = 0;
//        for (PARAMS param : params) {
//            OrbElement orb=orbs[i];
//        }
        for (OrbElement orb : orbs) {
            PARAMS param = params[i];
            if (orb == null) {
                orb = (new OrbElement(param
                        , source.getParam(param))
                );
                orbs[i] = orb;
                orb.setPosition(i * 100, 0);
                addActor(orb);
                i++;
                continue;
            }
            orb.act(delta);
            i++;
            boolean disabled = EidolonsGame.isAltControlPanel() &&
                    !EidolonsGame.getVar(ContentValsManager.getBaseParameterFromCurrent(param).getName());

            if (disabled) {
                    if (orb.isVisible()) {
                        orb.fadeOut();
                    }
                    orb.clearListeners();
                    continue;
            } else {
                if (orb.getColor().a==0) {
                    orb.fadeIn();
                }
            }
            if (!orb.updateValue(source.getParam(param)) &&
                    orb.getListeners().size > 0) {
                continue;
            }
            if (!disabled)
                addTooltip(orb, param.getName(), source.getParam(param));
        }
    }

}
