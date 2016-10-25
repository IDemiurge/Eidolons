package main.client.cc.gui.neo.choice;

import main.client.cc.gui.misc.PoolComp;
import main.client.cc.gui.neo.points.HC_PointComp;
import main.content.CONTENT_CONSTS.PRINCIPLES;
import main.content.DC_ContentManager;
import main.content.PARAMS;
import main.entity.obj.DC_HeroObj;
import main.entity.type.ObjType;

import javax.swing.*;
import java.awt.*;

public class IdentificationChoiceView extends PrincipleChoiceView {

    public IdentificationChoiceView(ChoiceSequence sequence, DC_HeroObj hero) {
        super(sequence, hero);

    }

    @Override
    protected void addInfoPanels() {
        super.addInfoPanels();

        new PoolComp(hero, PARAMS.IDENTITY_POINTS, PARAMS.IDENTITY_POINTS.getName()
                + " to spend", true);

    }

    @Override
    public Component getListCellRendererComponent(JList<? extends PRINCIPLES> list,
                                                  PRINCIPLES value, int index, boolean isSelected, boolean cellHasFocus) {

        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }

    public class PrincipleIdentificationComp extends HC_PointComp {

        public PrincipleIdentificationComp(boolean editable, DC_HeroObj hero, ObjType buffer,
                                           PRINCIPLES principle) {
            // if using Renderer technique, gonna have to cache model to support
            // down()
            super(editable, hero, buffer, DC_ContentManager
                            .getIdentityParamForPrinciple(principle), PARAMS.IDENTITY_POINTS,
                    VISUALS.ENUM_CHOICE_COMP, true);
            // VISUALS.ENUM_CHOICE_COMP_SELECTED : VISUALS.ENUM_CHOICE_COMP;
            // DC_ContentManager.getAlignmentForPrinciple(principle) ;

        }

    }

	/*
     * 5x2
	 * big arrows up/down? 
	 * 
	 * pointView? 
	 * spinnerModel? 
	 * 
	 * ID points param
	 * 
	 * 
	 * 
	 * 
	 */

}
