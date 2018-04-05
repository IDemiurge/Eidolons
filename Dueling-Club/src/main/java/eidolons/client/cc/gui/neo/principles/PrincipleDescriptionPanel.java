package eidolons.client.cc.gui.neo.principles;

import eidolons.client.cc.gui.neo.points.HC_InfoTextPanel;
import eidolons.game.battlecraft.rules.rpg.PrincipleMaster;
import main.content.enums.entity.HeroEnums.PRINCIPLES;
import main.content.values.properties.G_PROPS;
import main.entity.type.ObjType;

import java.awt.*;

public class PrincipleDescriptionPanel extends HC_InfoTextPanel {

    private PRINCIPLES principle;
    private ObjType type;

    public PrincipleDescriptionPanel() {
        super(VISUALS.INFO_PANEL_TEXT, null, null);
    }

    public void init() {
        refresh();
        // w/o selection? all identity/alignments?
    }

    @Override
    public void paint(Graphics g) {
        // TODO item/principle icon?
        super.paint(g);
    }

    @Override
    public void refresh() {
        if (type != null) {
            setText(type.getProperty(G_PROPS.LORE));
        } else if (principle == null) {
            setText(PrincipleMaster.getHelpInfo());
        } else {
            setText(principle.getDescription());
        }
        wrapTextLines();
        repaint();
    }

    public void setPrinciple(PRINCIPLES principle) {
        this.principle = principle;
        type = null;
        refresh();
    }

	/*
     * system info, integrity/principles description-lore
	 */

    public ObjType getType() {
        return type;
    }

    public void setType(ObjType type) {
        this.type = type;
        refresh();
    }
}
