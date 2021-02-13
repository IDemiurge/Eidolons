package libgdx.map.town.navigation;

import libgdx.anims.actions.ActionMaster;
import libgdx.gui.panels.ScrollPaneX;
import libgdx.gui.panels.TablePanelX;
import libgdx.map.town.navigation.data.Navigable;
import libgdx.map.town.navigation.data.NavigationDataSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 11/21/2018.
 */
public class NavigationJoint extends TablePanelX{
    private final TablePanelX childTable;
    ScrollPaneX verticalScroll;
    NavigationPoint rootActor;
    Navigable root;
    List<NavigationPoint> children=    new ArrayList<>() ;

    public NavigationJoint(Navigable  root) {
        this.root = root;
        add(rootActor=new NavigationPoint(root)).row();
        add(verticalScroll = new ScrollPaneX(childTable = new TablePanelX()));
        //rly scroll?
    }

    @Override
    public void updateAct(float delta) {
        NavigationDataSource dataSource = (NavigationDataSource) getUserObject();
        if (children.isEmpty())
        for (Navigable navigable : dataSource.getChildren()) {
            NavigationPoint point = new NavigationPoint(navigable);
            children.add(point);
            point.setWidth(getWidth()*getChildWidthCoef());
        }
//        for (NavigationPoint child : children) {
//            child.setDisabled()
//        }
    }

    private float getChildWidthCoef() {
        return 0.8f;
    }

    public void rollDown(){
        //scissors later
        ActionMaster.addMoveToAction(childTable, 0,  150, 1f);
    }

    public void rollUp() {
        ActionMaster.addMoveToAction(childTable, 0, 450, 1f);
    }
}
