package eidolons.libgdx.screens.map.town.navigation;

import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.gui.panels.ScrollPaneX;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.screens.map.town.navigation.data.Navigable;
import eidolons.libgdx.screens.map.town.navigation.data.NavigationMaster;
import eidolons.libgdx.screens.map.town.navigation.data.Nested;
import eidolons.macro.MacroGame;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by JustMe on 11/21/2018.
 * <p>
 * use roll in/out for everything...
 * <p>
 * if we go forward, roll out the header first, then the place downwards
 */
public class PlaceNavigationPanel extends TablePanelX {

    ScrollPaneX horizontalScroll;
    TablePanelX scrolledTable;
    Map<Navigable, NavigationJoint> jointCache = new HashMap<>();
    Set<NavigationJoint> displayed = new LinkedHashSet<>();
    private NavigationMaster navigationMaster;
    private Navigable root;
    private NavigatedPlaceView view;
    private Navigable tip;

    public PlaceNavigationPanel(NavigatedPlaceView view) {
        this.view = view;
        navigationMaster = new NavigationMaster();
        horizontalScroll = new ScrollPaneX(scrolledTable = new TablePanelX());
        }

    public void show(Nested root) {
        if (root == null) {
            root = MacroGame.getGame().getPlayerParty().getCurrentLocation();
        }
        this.root = navigationMaster.getNavigable(root);
        selected(this.root);
    }

    public void back() {
        int level = displayed.size() - 1;
        NavigationJoint joint = jointCache.get(tip);
        animateNewJoint(joint, level, true);

        selected(tip.getParent());
    }

    public void selected(Navigable navigable) {
        if (!navigable.isLeaf()) {
            NavigationJoint joint = jointCache.get(navigable);
            if (joint == null) {
                joint = new NavigationJoint(navigable);
                jointCache.put(navigable, joint);
            }
            tip = navigable;
            displayed.add(joint);
            scrolledTable.add(joint);

            int level = displayed.size();
            animateNewJoint(joint, level, false);
        }
        navigable.interact();
        view.selected(navigable);
    }

    private void animateNewJoint(NavigationJoint joint, int level, boolean remove) {
        if (remove) {
            joint.rollUp();
        } else
            joint.rollDown();
        float x = getOffsetX(level);
        if (remove) {
            x = -x;
            displayed.remove(joint);
        }
        ActorMaster.addMoveByAction(joint, x, 0, x / getRollSpeed());
        if (remove) {
            ActorMaster.addFadeOutAction(joint, x / getRollSpeed());
            ActorMaster.addHideAfter(joint);
        }
    }

    private float getRollSpeed() {
        return 120;
    }

    private float getOffsetX(int level) {
        return 200;
    }


}
