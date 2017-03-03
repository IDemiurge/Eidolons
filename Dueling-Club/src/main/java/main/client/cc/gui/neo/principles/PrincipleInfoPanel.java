package main.client.cc.gui.neo.principles;

import main.client.cc.gui.neo.points.HC_InfoTextPanel;
import main.content.PARAMS;
import main.content.enums.entity.HeroEnums;
import main.content.enums.entity.HeroEnums.PRINCIPLES;
import main.entity.obj.unit.Unit;
import main.rules.rpg.IntegrityRule;
import main.rules.rpg.IntegrityRule.ALIGNMENT_LEVEL;
import main.system.auxiliary.StringMaster;

import java.util.LinkedList;

public class PrincipleInfoPanel extends HC_InfoTextPanel {

    private PRINCIPLES principle;

    public PrincipleInfoPanel(Unit hero) {
        super(VISUALS.INFO_PANEL_TEXT_SMALL, hero, null);

    }

    public void init() {
        refresh();
        // w/o selection? all identity/alignments?
    }

    public void setPrinciple(PRINCIPLES principle) {
        this.principle = principle;
        refresh();
    }

    @Override
    public void refresh() {
        textLines = new LinkedList<>();
        if (principle == null) {
            for (PRINCIPLES p : HeroEnums.PRINCIPLES.values()) {
                addStatusLines(p);
            }
        } else {
            addStatusLines(principle);
        }

        repaint();
    }

    private void addStatusLines(PRINCIPLES principle) {
        // String identityStatus = getIdentityStatus(principle);
        // if (identityStatus != null)
        // textLines.add(identityStatus);
        // String alignmentStatus = getAlignmentStatus(principle);
        // if (alignmentStatus != null)
        // textLines.add(alignmentStatus);
        String description = getDescription(principle);
        if (description != null) {
            textLines.add(description);
        }

    }

    private String getDescription(PRINCIPLES principle) {
        ALIGNMENT_LEVEL alignmentLevel = IntegrityRule.getAlignmentLevel(principle, hero);
        String s = "";
        if (alignmentLevel != null) {
            s = "" + IntegrityRule.getAdjective(principle, alignmentLevel);
        }
        s += " " + IntegrityRule.getNoun(principle, hero);
        if (StringMaster.isEmpty(s)) {
            return null;
        }
        int value2 = IntegrityRule.getIntegrityMod(hero, principle);
        String value3 = value2 + " Integrity";
        if (value2 > 0) {
            value3 = "+" + value3;
        }
        s += StringMaster.wrapInParenthesis(value3);
        return s;
    }

    private String getIdentityStatus(PRINCIPLES principle) {

        // StringMaster.wrapInParenthesis(hero.getParam(param))
        String s = IntegrityRule.getNoun(principle, hero);
        if (StringMaster.isEmpty(s)) {
            return null;
        }

        return principle.toString() + " Associated Identity: " + s;
    }

    private String getAlignmentStatus(PRINCIPLES principle) {
        // TODO
        String s = principle.toString() + " Alignment Status: ";
        ALIGNMENT_LEVEL alignmentLevel = IntegrityRule.getAlignmentLevel(principle, hero);
        if (alignmentLevel == null) {
            return null;
        }
        s += "" + IntegrityRule.getAdjective(principle, alignmentLevel);
        return s;
    }

    protected String getSpecialDescription(PARAMS param) {
        switch (param) {
            case INTEGRITY:

                break;
        }
        return null;
    }

}
