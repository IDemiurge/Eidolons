package main.client.cc.gui.workers;

import main.client.cc.gui.lists.VendorListsPanel;
import main.data.DataManager;
import main.entity.type.ObjType;

import javax.swing.*;
import java.util.List;

public class TypeListWorker extends SwingWorker<List<ObjType>, String> {

    private VendorListsPanel panel;
    private String listName;

    public TypeListWorker(VendorListsPanel panel, String listName) {
        this.panel = panel;
        this.listName = listName;
    }

    @Override
    protected List<ObjType> doInBackground() throws Exception {

        if (!panel.isResponsive()) {
            if (!panel.checkList(listName, false)) {
                return null;
            }
        }
        if (!panel.isShowAll()) {
            if (!panel.checkList(listName, true)) {
                return null;
            }
        }

        List<String> types = DataManager
         .getTypesSubGroupNames(panel.getTYPE(), listName);
        if (types == null) {
            return null;
        }
        if (types.isEmpty()) {
            return null;
        }
        List<ObjType> data = DataManager.toTypeList(types, panel
         .getTYPE());

        if (panel.getFilter() != null) {
            data = panel.getFilter().filter(data);
        }

        return data;
    }

    // public void initMap(List<ObjType> data, String listName) {
    // Map<String, HC_PagedListPanel> map = new HashMap<>();
    // putList(listName, data, map);
    // G_Panel tab = tabMap.getOrCreate(listName);
    //
    // for (String name : map.keySet()) {
    // // String pos = "sg listHeader";
    // // JLabel header = new JLabel(name); // TODO +icon?
    // // tab.add(header, pos);
    // tab.add(map.getOrCreate(name), "wrap");
    // }
    // listMaps.add(map);
    // initCache.add(listName);
    // }

    @Override
    protected void done() {
        /*try {
            panel.initMap(getOrCreate(), listName);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            main.system.ExceptionMaster.printStackTrace(e);
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            main.system.ExceptionMaster.printStackTrace(e);
        }*/
    }

}
