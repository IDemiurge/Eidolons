package main.client.cc.gui.neo.tree.t3;

import main.client.cc.CharacterCreator;
import main.client.cc.HC_Master;
import main.client.cc.gui.neo.header.DeityComp;
import main.client.cc.gui.neo.header.PortraitComp;
import main.entity.obj.unit.Unit;
import main.swing.components.buttons.CustomButton;
import main.swing.generic.components.G_Panel;

public class T3UpperPanel extends G_Panel {

    public static final String UNDO = "Undo";
    public static final String TOGGLE_VIEW = "Toggle";
    public static final String NEXT_HERO = "Next";
    public static final String DONE = "Done";
    public static final String CANCEL = "Cancel";
    public final static String SAVE = "Save"; // ought to be via AV_ classes
    public final static String REMOVE = "Remove";
    public static final String ADD = "Add";
    public final static String[] STD_CONTROLS = {TOGGLE_VIEW, UNDO, NEXT_HERO, DONE};

    // private static final String CANCEL = "cancel";
    public final static String[] AV_CONTROLS = {

            TOGGLE_VIEW, SAVE, ADD, REMOVE,

    };
    private static T3UpperPanel lastInstance;

    // T3ClassComp c; // label?

	/*
     * New Design
	 * 
	 * No excess decor 
	 * Vertical masteries - necessary? Is there a better way? 
	 * 
	 * Classes - maybe for ClassT3 only! 
	 * 
	 * Controls - all good 
	 * 
	 * Hero-Info - name/lvl sure 
	 * 
	 * Deity Panel - optional 
	 * 
	 * 
	 * 
	 */
    PortraitComp heroPortrait;
    DeityComp dc;
    G_Panel controlPanel;
    VISUALS[] std_controls_1 = {

    };
    // VISUALS[] std_controls_2 = { VISUALS.CANCEL, VISUALS.OK,
    // VISUALS.NEXT_HERO, VISUALS.INFO,
    // VISUALS.BACK, VISUALS.NEXT_HERO,
    //
    // };
    private Unit hero;
    private ThreeTreeView view;
    private String[] controls;

    public T3UpperPanel(String[] controls, Unit hero, ThreeTreeView view) {
        this.hero = hero;
        this.view = view;
        this.controls = controls;
        lastInstance = this;
    }

    public T3UpperPanel(Unit hero, ThreeTreeView view) {
        this(STD_CONTROLS, hero, view);
        // if (isSkill()) {
        //
        // }
    }

    public static T3UpperPanel getLastInstance() {
        return lastInstance;
    }

    public void init() {
        heroPortrait = new PortraitComp(hero);
        controlPanel = new G_Panel();
        for (final String c : controls) {
            controlPanel.add(new CustomButton(VISUALS.BUTTON_NEW_TINY, (c)) {
                public void handleClick() {
                    handleControl(c, false);
                }

                @Override
                public void handleAltClick() {
                    handleControl(c, true);
                }
            });
        }
        add(controlPanel, "id cp, pos @center_x 0");
        add(heroPortrait.getComp(), "pos @center_x cp.y2");

    }

    public void handleControl(String c, boolean alt) {
        switch (c) {
            case DONE:
                HC_Master.toggleT3View();
                break;
            case TOGGLE_VIEW:
                HC_Master.toggleT3ClassSkillView();
                break;
            case NEXT_HERO:
                HC_Master.nextHero();
                break;
            // case OK:
            // HC_Master.toggleT3View();
            // break;
            case CANCEL:
                HC_Master.toggleT3View();
                break;
            case UNDO:
                CharacterCreator.getHeroManager().stepBack(hero);
                // refresh!
                view.refresh();
                break;
        }

    }

}
