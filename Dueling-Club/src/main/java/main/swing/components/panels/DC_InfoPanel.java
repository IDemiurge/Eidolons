package main.swing.components.panels;

import main.content.*;
import main.entity.Entity;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.DC_UnitModel;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battlefield.vision.VisionManager;
import main.game.core.game.DC_Game;
import main.game.core.state.MicroGameState;
import main.swing.generic.components.panels.G_InfoPanel;
import main.swing.renderers.DC_InfoPanelRenderer;
import main.system.auxiliary.StringMaster;
import main.test.debug.DebugPanel;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

/**
 * text boxes with name/values? jtable ?? jlist ? scrollable?
 *
 * @author Regulus
 */
public class DC_InfoPanel extends G_InfoPanel implements TableModelListener {

    private MicroGameState state;

    public DC_InfoPanel(Entity infoObj, Collection<String> displayedValues,
                        MicroGameState state) {
        super(infoObj);
        this.state = state;
        initialized = true;
        refresh();

    }

    @Override
    protected boolean isEditable() {

        return state.getGame().isDebugMode() || state.getGame().isSimulation();
    }

    @Override
    public void setInts() {
        // height= height*3/2;
        sizeInfo = "w " + DebugPanel.getPanelWidth() / 2
                // "2*" + GuiManager.getCellSize()
                + "!, h " + DebugPanel.getPanelHeight()
        // +"("
        // + DC_PriorityListPanel.PLP_MIN_ITEMS + "/2-1)*"
        // + GuiManager.getSmallObjSize() + "!"
        ;
        // sizeInfo = "";
    }

    @Override
    protected Vector<Vector<String>> initData() {
        if (infoObj instanceof ObjType) {
            infoObj = state.getGame().getBattleField().getInfoSelectedObj();
        }

        if (infoObj == null) {
            this.displayedValues = DC_ContentManager
                    .getInfoPanelValueList(obj_type);
            return super.initData();
        }
        if (infoObj.getGame().isSimulation()) {
            this.displayedValues = ContentManager.getFullValueList(
                    infoObj.getOBJ_TYPE(), true);
        } else if (VisionManager.checkDetected(getInfoObj()) || isNonUnit()
                || isEditable()) {
            try {
                displayedValues = null;
                List<VALUE> values = ValuePageManager.getValuesForDC(DC_TYPE
                        .getType(obj_type));
                displayedValues = StringMaster.convertToStringList(values);
            } catch (Exception e) {
                // e.printStackTrace();
            }
            if (displayedValues == null) {
                this.displayedValues = DC_ContentManager
                        .getInfoPanelValueList(obj_type);
            }
        } else {
            if (!((DC_Game) infoObj.getGame()).getVisionMaster()
                    .getDetectionMaster().checkKnown(getInfoObj())) {
                this.displayedValues = DC_ContentManager
                        .getLimitedInfoPanelValueList(obj_type);
            } else {
                infoObj = (getInfoObj().getType());
                this.displayedValues = DC_ContentManager
                        .getInfoPanelValueList(obj_type);
            }
        }

        return super.initData();
    }

    private boolean isNonUnit() {
        return !(infoObj instanceof DC_UnitModel);
    }

    public DC_Obj getInfoObj() {
        return (DC_Obj) infoObj;
    }

    @Override
    public void refresh() {
        if (!initialized) {
            return;
        }
        removeAll();
        addTable();
        if (isEditable()) {
            TableModel model = table.getTable().getModel();
            model.addTableModelListener(this);
        }
        // JLabel specVals = new JLabel("");
        // if (infoObj != null)
        // specVals = new JLabel(infoObj.getName());
        // add(specVals, "id specVals, pos 0 lbl.y2");
        // specVals.setBackground(Color.black);
        // specVals.setForeground(Color.white);

        if (infoObj != null) {
            this.table.getTable().setDefaultRenderer(String.class,
                    new DC_InfoPanelRenderer(infoObj));
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        final String newValue = (String) table.getTable().getValueAt(
                e.getFirstRow(), e.getColumn());
        final String valName = (String) table.getTable().getValueAt(
                e.getFirstRow(), e.getColumn() - 1);
        new Thread(new Runnable() {

            @Override
            public void run() {
                infoObj.getType().setValue(valName, newValue);
                infoObj.setValue(valName, newValue);
                state.getGame().getManager().reset();
                state.getGame().getManager().refreshAll();
            }
        }).start();

    }
}
