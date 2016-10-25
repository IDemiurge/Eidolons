package main.client.cc.gui.views;

import main.client.cc.CharacterCreator;
import main.client.cc.logic.PointMaster;
import main.content.ContentManager;
import main.content.DC_ContentManager.ATTRIBUTE;
import main.content.PARAMS;
import main.content.parameters.PARAMETER;
import main.entity.obj.DC_HeroObj;
import main.entity.type.ObjType;
import main.swing.generic.components.G_Panel;
import main.swing.generic.misc.PointComp;
import main.system.auxiliary.Chronos;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.GuiManager;
import main.system.auxiliary.StringMaster;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PointView extends HeroView implements ChangeListener,
        ActionListener {

    boolean attributes;
    int columns = 2;
    int columnWidth = GuiManager.getFullObjSize() * 3;
    private List<PARAMETER> values;
    private boolean params;
    private ObjType bufferType;
    private JButton doneButton;
    private G_Panel valPanel;
    private Font font;
    private Map<PARAMETER, Component> compMap = new HashMap<>();

    public PointView(DC_HeroObj hero, List<PARAMETER> values) {
        super(hero);
        setVisuals(VISUALS.INFO_PANEL);
        this.values = values;
        params = true;
        init();
        refresh();
    }

    public PointView(boolean editable, boolean attributes, DC_HeroObj hero) {
        super(hero);
        this.editable = editable;
        this.attributes = attributes;
        init();
        refresh();
        setIgnoreRepaint(true);
    }

    public Dimension getMaximumSize() {
        return VISUALS.INFO_PANEL.getSize();
    }

    public Dimension getMinimumSize() {
        return getMaximumSize();
    }

    public Dimension getPreferredSize() {
        return getMaximumSize();
    }

    @Override
    public void activate() {

    }

    @Override
    public void init() {
        initFont();
        initValues();
    }

    @Override
    public void refresh() {

        Chronos.mark("pv refresh" + values.get(0));

        if (!initialized) {
            removeAll();
            resetBuffer();
            if (editable) {
                initPoolComp();
                add(poolComp, "id pool, pos 0 0 ");
                addControls();
            }

            generateValuePanel();
            add(valPanel, "pos 0 db.y2");
            initialized = true;
            revalidate();
        } else {

            updateValuePanel();
            resetBuffer();
            updatePoolComp();
        }

        repaint();
        Chronos.logTimeElapsedForMark("pv refresh" + values.get(0));
    }

    @Override
    protected void updatePoolComp() {
        poolComp.setText(getBufferType().getParam(getPoolParam()));
    }

    private void updateValuePanel() {
        for (PARAMETER p : compMap.keySet()) {
            if (!getBufferType().getParam(p).equals(hero.getParam(p))) {
                Component comp = compMap.get(p);
                if (!editable)
                    ((JLabel) comp).setText(hero.getParam(p));
                else {
                    ((PointComp) comp).removeChangeListener(this);
                    ((PointComp) comp).setValue(hero.getIntParam(p));
                    ((PointComp) comp).addChangeListener(this);
                }
            }
        }

    }

    private void generateValuePanel() {

        String str = "";
        if (attributes) {
            str = "flowy";
            columns = ATTRIBUTE.values().length;
        }

        valPanel = new G_Panel(str);
        int i = 1;
        for (PARAMETER value : values) {

            addValueComponent(value, i, valPanel);
            i++;

        }

    }

    private void addValueComponent(PARAMETER value, int i, G_Panel valPanel) {
        String name = value.getName();
        if (editable && attributes)
            name = name.replace(StringMaster
                    .getWellFormattedString(StringMaster.BASE), "");
        JLabel lbl = new JLabel(name + ": ");
        valPanel.add(lbl, "id lbl" + i + ", sg lbls");
        if (editable) {
            PointComp pc = new PointComp(getBufferType().getIntParam(value),
                    attributes);

            // set minimum
            pc.setParam(value);
            pc.setFont(font);
            pc.addChangeListener(this);
            valPanel.add(pc, ((i % columns == 0) ? ",wrap" : "")
                    // ,"pos lbl" + i + ".x2 0"
            );
            compMap.put(value, pc);
        } else {
            JLabel comp = new JLabel(getBufferType().getParam(value));
            comp.setFont(font);
            valPanel.add(comp, ((i % columns == 0) ? ",wrap" : "")
                    // ,"pos lbl" + i
                    // + ".x2 0"
            );

            compMap.put(value, comp);
        }

    }

    @Override
    public void stateChanged(ChangeEvent e) {
        // subtract points from attr/mast? only down to X!
        int newValue = (int) ((PointComp) e.getSource()).getValue();
        PARAMETER param = ((PointComp) e.getSource()).getParam();
        int oldValue = getBufferType().getIntParam(param);

        int mod = newValue - oldValue;

        int cost = PointMaster
                .getPointCost(Math.max(newValue, oldValue), hero, param);
        if (mod < 0 || !checkCost(cost * mod)) {
            ((PointComp) e.getSource()).removeChangeListener(this);
            ((PointComp) e.getSource()).setValue(oldValue);
            ((PointComp) e.getSource()).addChangeListener(this);
            return;
        }

        // CharacterCreator.getHeroManager().modifyHeroParam(hero, param, mod);
        getBufferType().modifyParameter(param, mod);
        modifyPool(-mod, cost);
    }

    private void initValues() {
        if (params)
            return;
        this.values = new LinkedList<>();
        List<PARAMETER> list = (attributes) ? ContentManager.getAttributes()
                : ContentManager.getMasteries();
        for (PARAMETER p : list) {
            if (checkValue(p))
                values.add(p);
        }

    }

    private boolean checkCost(int cost) {
        return getBufferType().checkParam(getPoolParam(), cost + "");
    }

    private void modifyPool(int mod, int cost) {

        cost *= mod;
        getBufferType().modifyParameter(getPoolParam(), cost);
        // CharacterCreator.getHeroManager()
        // .modifyHeroParam(hero, getPoolParam(), cost);
        poolComp.setText(getBufferType().getParam(getPoolParam()));
    }

    private void commitChanges() {
        CharacterCreator.getHeroManager()
                .applyChangedType(hero, getBufferType());
    }

    private void resetBuffer() {
        setBufferType(new ObjType(hero.getType()));

    }

    @Override
    public PARAMS getPoolParam() {
        return ((attributes) ? PARAMS.ATTR_POINTS : PARAMS.MASTERY_POINTS);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        commitChanges();

    }

    public ObjType getBufferType() {
        if (bufferType == null)
            resetBuffer();
        return bufferType;
    }

    public void setBufferType(ObjType bufferType) {
        this.bufferType = bufferType;
    }

    private void addControls() {
        if (doneButton == null) {
            doneButton = new JButton("OK");
            doneButton.addActionListener(this);
        }
        add(doneButton, "id db, pos pool.x2 0 ");

    }

    private void initFont() {
        if (attributes)
            font = (FontMaster.getDefaultFont(FontMaster.SIZE + 2));
        else
            font = FontMaster.getDefaultFont();
    }

    private boolean checkValue(PARAMETER p) {
        if (attributes && editable) {
            if (!p.toString().contains(StringMaster.BASE))
                return false;
        } else {
            if (!params && editable) { // masteries
                if (hero.getIntParam(p) <= 0)
                    return false;
            }
        }
        return true;
    }

}
