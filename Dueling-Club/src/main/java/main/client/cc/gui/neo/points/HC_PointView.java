package main.client.cc.gui.neo.points;

import main.client.cc.HC_Master;
import main.client.cc.gui.views.HeroView;
import main.client.cc.logic.AttributeMaster;
import main.content.ContentManager;
import main.content.DC_ContentManager;
import main.content.DC_ContentManager.ATTRIBUTE;
import main.content.PARAMS;
import main.content.values.parameters.PARAMETER;
import main.data.XLinkedMap;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.swing.components.panels.page.log.WrappedTextComp;
import main.swing.generic.components.misc.GraphicComponent;
import main.system.graphics.FontMaster;
import main.system.graphics.FontMaster.FONT;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * beware of heady refreshing
 */
public class HC_PointView extends HeroView {
    // buffer type?
    private boolean attributes = false; // final and base+spinner
    // full param view?
    private int columns = 2;
    private Map<PARAMETER, HC_PointComp> compMap = new XLinkedMap<>();
    private List<PARAMETER> params;
    private int X;
    private int Y;
    private int i;

    private ObjType buffer;
    private boolean all;
    private WrappedTextComp paramBonusInfoComp;

    public HC_PointView(List<PARAMETER> params, Unit hero) {
        this(params, hero, false);
    }

    public HC_PointView(List<PARAMETER> params, Unit hero, boolean all) {
        super(hero);
        this.params = params;
        this.all = all;
        init();
    }

    public HC_PointView(boolean attributes, Unit hero) {
        super(hero);
        this.attributes = attributes;
        init();
    }

    @Override
    public boolean isAutoSizingOn() {
        return true;
    }

    // size? visuals?
    @Override
    public void init() {
        initValues();
        addComps();
    }

    private void addComps() {
        paramBonusInfoComp = new WrappedTextComp(null
                // VISUALS.OPTION_PANEL_3
                , false) {
            @Override
            public Dimension getPanelSize() {
                return new Dimension(400, 140);
            }
        };
        // wtc.setPanelSize(size)
        paramBonusInfoComp.setDefaultSize(new Dimension(400, getDefaultY() - 15));
        paramBonusInfoComp.setDefaultFont(FontMaster.getFont(FONT.AVQ, 18, Font.PLAIN));
        add(paramBonusInfoComp, "id wtc, pos " + 0 + " " + 0); // centering?
        add(new GraphicComponent(VISUALS.DRAGON_DIVIDER.getImage()), "id div, @pos center_x+24 wtc.y2-24");

        // DIVIDER
        X = getDefaultX();
        Y = getDefaultY();
        i = 0;
        if (!attributes) {
            if (editable) {
                DC_ContentManager.sortMasteries(hero, params);
            }
        }
        for (PARAMETER p : params) {
            boolean editable = isEditable(p);
            HC_PointComp comp = new HC_PointComp(editable, hero, buffer, p,
                    (attributes) ? PARAMS.ATTR_POINTS : PARAMS.MASTERY_POINTS);
            addComponent(comp, p);
        }
    }

    private void addComponent(HC_PointComp comp, PARAMETER p) {
        add(comp, "pos " + X + " " + Y);
        X += HC_PointComp.VALUE_BOX.getWidth();
        i++;
        if (i >= columns) {
            X = getDefaultX();
            Y += HC_PointComp.VALUE_BOX.getHeight();
            i = 0;
        }
        compMap.put(p, comp);

    }

    private int getDefaultX() {
        return 26;
    }

    private int getDefaultY() {
        if (all) {
            return 0;
        }
        return 170 + VISUALS.DRAGON_DIVIDER.getHeight();
    }

    private boolean isEditable(PARAMETER p) {
        if (attributes) {
            return ContentManager.isBase(p);
        }
        return true; // preCheck locked!!!
    }

    private void initValues() {
        if (params != null) {
            return;
        }

        this.params = new LinkedList<>();
        List<PARAMETER> list = (attributes) ? ContentManager.getAttributes() : ContentManager.getMasteries();

        for (PARAMETER p : list) {
            // if (checkValue(portrait))
            params.add(p);
        }

        if (attributes) {
            LinkedList<PARAMETER> newList = new LinkedList<>();

            for (int i = 0; i < list.size() / 2; i++) {
                newList.add(i * 2, list.get(i));
                newList.add(i * 2 + 1, list.get(list.size() / 2 + i));

            }
            params = newList;
        }
    }

    @Override
    public void refresh() {
        for (HC_PointComp comp : compMap.values()) {
            // if (comp.getParam().isMastery())
            // comp.setEntity(new ObjType(hero.getType()));
            // else //not so easy! I'll probably need another set of comps for
            // *final mastery* scores
            comp.setEntity((comp.isEditable()) ? buffer : hero); //
            comp.refresh();
        }
        ATTRIBUTE attr = null;
        try {
            attr = HC_Master.getSelectedAttribute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> list = new LinkedList<>();
        if (attr != null) {
            list = AttributeMaster.getAttributeBonusInfoStrings(attr, hero);
        }

        paramBonusInfoComp.setTextLines(list);
        paramBonusInfoComp.refresh();
    }

    @Override
    public void activate() {
    }

    @Override
    public PARAMS getPoolParam() {
        return null;
    }

    public void setBuffer(ObjType buffer) {
        this.buffer = buffer;
    }

    public void reset() {
        for (HC_PointComp comp : compMap.values()) {
            comp.reset();
        }
    }

}
