package eidolons.client.cc.gui.neo.choice;

import eidolons.client.cc.gui.neo.choice.panels.CustomInfoPanel;
import eidolons.entity.obj.unit.Unit;

public class HeroChoiceView<E> extends ChoiceView<E> {

    CustomInfoPanel cip;
    ChoiceHeroSidePanel chesp;

    public HeroChoiceView(ChoiceSequence sequence, Unit hero) {
        super(sequence, hero);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void addInfoPanels() {
        // TODO Auto-generated method stub
        super.addInfoPanels();
    }

    public void addHeroPanel() {

    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void applyChoice() {
        // TODO Auto-generated method stub

    }

    @Override
    public String getInfo() {
        // TODO Auto-generated method stub
        return null;
    }
    /*
     * has right-side panel, left-side heroPanel and visuals for the middle
	 * pages
	 */
}
