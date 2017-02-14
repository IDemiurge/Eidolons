package main.utilities.workspace;

import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.entity.type.ObjType;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.data.ListMaster;

import java.util.LinkedList;
import java.util.List;

/*
 * save format: <TYPE>[types]</TYPE> to ensure name collisions don't hurt
 *  

 */
public class Workspace {

    String name;
    List<ObjType> typeList;
    boolean search;
    private G_Panel tabComp;
    private boolean dirty;
    private List<String> subgroups;
    private PROPERTY subgroupingProp;
    private String metadata;

    public Workspace(String name, List<ObjType> typeList, boolean search) {
        this.search = search;
        ListMaster.removeNullElements(typeList);
        this.typeList = typeList;
        this.name = name;
    }

    public Workspace(String name, List<ObjType> typeList) {
        this.name = name;
        this.typeList = typeList;
    }

    public String getTabName() {
        return name.charAt(0) + "";
    }

    public G_Panel getTabComp() {
        return tabComp;
    }

    public void setTabComp(G_Panel tabComp) {
        this.tabComp = tabComp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ObjType> getTypeList() {
        if (typeList == null) {
            typeList = new LinkedList<>();
        } else {
            ListMaster.removeNullElements(typeList);
        }
        return typeList;
    }

    public void setTypeList(List<ObjType> typeList) {
        this.typeList = typeList;
    }

    public OBJ_TYPE getOBJ_TYPE(String typeName, String parent) {
        // TODO if 2 types with same name? Perhaps types should finally getOrCreate
        // *IDs* too!
        OBJ_TYPE TYPE = null;
        for (ObjType type : getTypeList()) {
            if (type.getName().equals(typeName)) {
                TYPE = type.getOBJ_TYPE_ENUM();
                if (OBJ_TYPES.getType(parent) != null) {
                    if (TYPE == OBJ_TYPES.getType(parent)) {
                        return TYPE;
                    }
                }
            }
        }
        return TYPE;
    }

    /**
     * will remove if list already contains the type
     *
     * @param selectedType
     * @return
     */
    public boolean addType(ObjType selectedType) {
        if (selectedType == null) {
            return true;
        }
        setDirty(true);
        if (getTypeList().contains(selectedType)) {
            selectedType.setProperty(G_PROPS.WORKSPACE_GROUP, "");
            typeList.remove(selectedType);
            return false;
        } else {
            typeList.add(selectedType);
        }
        return true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isSearch() {
        return search;
    }

    public void setSearch(boolean search) {
        this.search = search;
    }

    public List<String> getSubgroups() {
        return subgroups;
    }

    public void setSubgroups(List<String> subgroups) {
        this.subgroups = subgroups;
    }

    public PROPERTY getSubgroupingProp() {
        return subgroupingProp;
    }

    public void setSubgroupingProp(PROPERTY subgroupingProp) {
        this.subgroupingProp = subgroupingProp;
    }

    public String getMetaData() {
        return metadata;
    }

    public void setMetaData(String metadata) {
        this.metadata = metadata;

    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

}
