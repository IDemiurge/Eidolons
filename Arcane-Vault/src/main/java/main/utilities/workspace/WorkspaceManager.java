package main.utilities.workspace;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.entity.ActionEnums;
import main.content.enums.system.MetaEnums;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Writer;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.launch.ArcaneVault;
import main.swing.generic.components.G_Panel;
import main.system.PathUtils;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

import javax.swing.*;
import java.io.File;
import java.util.*;

/*
 * multiple workspaces? 
 * with minor prefs/configs? Named, saved and loaded?
 * 
 *  perhaps filters should also result into workspaces! 
 *  newWorkspace *dialog*! 
 *  the *select* button will be either for default or will prompt if multiple WS
 *  
 *  functions on WS - sort, filter, group by [value] (tabs layer), subgroup by [value] 
 */

public class WorkspaceManager {
    public static final String SEARCH_PATH = "\\searches\\";
    // TODO rather, there must be some data on *which* workspace *is* default!
    private static final String DEFAULT_WORKSPACE_NAME = "default_workspace.xml";
    private static final boolean LAYER_DOWN = false;
    private static final String METADATA = "METADATA: ";
    public static boolean ADD_WORKSPACE_TAB_ON_INIT = false;
    private static List<Workspace> workspaces = new ArrayList<>();
    private Workspace activeWorkspace;

    private boolean defaultTypeWorkspacesOn = true;
    private boolean defaultGroupWorkspacesOn = true;
    private boolean autosave = true;
    private Boolean macro;

    public WorkspaceManager(Boolean macro, Game game) {
        this.macro = macro;
        // TODO
    }

    public static String getFolderPath(boolean search) {

        return search ? PathFinder.getXML_PATH() + SEARCH_PATH : (PathFinder.getWorkspacePath());
    }

    // should support default workspace for simplicity!
    public void newWorkspaceForParty() {
        ObjType party = ArcaneVault.getSelectedType();
        Set<ObjType> set = new HashSet<>();
        for (String sub : ContainerUtils.open(party.getProperty(PROPS.MEMBERS))) {
            ObjType type = DataManager.getType(sub, DC_TYPE.CHARS);

            for (String item : ContainerUtils.open(type.getProperty(PROPS.SKILLS))) {
                set.add(DataManager.getType(item, DC_TYPE.SKILLS));
            }
            for (String item : ContainerUtils.open(type.getProperty(PROPS.CLASSES))) {
                set.add(DataManager.getType(item, DC_TYPE.CLASSES));
            }

            // for (String item:
            // StringMaster.openContainer(type.getProperty(PROPS.VERBATIM_SPELLS)))
            // set.add(DataManager.getType(item, OBJ_TYPES. SPELLS));
            // for (String item:
            // StringMaster.openContainer(type.getProperty(PROPS.SPELLBOOK)))
            // set.add(DataManager.getType(item, OBJ_TYPES. SPELLS));
        }
        List<ObjType> typeList = new ArrayList<>(set);
        Workspace ws = new Workspace(party.getName(), typeList);
        addWorkspace(ws);
        initWorkspace(ws);
    }

    // save all upon exit?
    public void save() {
        for (Workspace a : workspaces) {
            if (!a.isSearch()) {
                try {
                    saveWorkspace(a);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
        }
    }

    public void saveWorkspace(Workspace ws) {
        saveWorkspace(null, ws, "");
    }

    public void saveWorkspaceNew(Workspace ws) {

    }

    public void saveWorkspace(String path, Workspace ws, String metadata) {
        if (ws == null) {
            return;
        }
        if (path == null) {
            path = getFolderPath(ws.isSearch());
        }
        XML_Writer.write(XML_Converter.getXMLFromTypeList(ws.getTypeList()) + METADATA + metadata

                , path, ws.getName() + ".xml");
    }

    public void initWorkspace(Workspace ws) {
        setActiveWorkspace(ws);
        workspaces.add(ws);
        if (ArcaneVault.getMainBuilder() != null) {
            ArcaneVault.getMainBuilder().getTabBuilder().addWorkspaceTab(ws);
        }

    }

    public void initSearches() {
        for (File f : FileManager.getFilesFromDirectory(getFolderPath(true), false)) {
            Workspace ws = loadWorkspace(f.getName(), true);

        }

    }

    public Workspace loadWorkspace(String path) {
        return loadWorkspace(path, false);
    }

    public Workspace loadWorkspace(String path, boolean search) {
        if (!path.contains(".xml")) {
            path += ".xml";
        }
        File file = FileManager.getFile(path);
        if (!file.isFile()) {
            file = FileManager.getFile(getFolderPath(search) + path);
        }
        if (!file.isFile()) {
            return null;
        }
        String xml = FileManager.readFile(file);
        List<String> parts = ContainerUtils.openContainer(xml, METADATA);
        xml = parts.get(0);
        List<ObjType> typeList = null;
        try {
            typeList = XML_Converter.getTypeListFromXML(xml, LAYER_DOWN);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        path = PathUtils.getLastPathSegment(path);
        String name = StringMaster.cropFormat(path);
        Workspace workspace = new Workspace(name, typeList);
        initWorkspace(workspace);

        if (parts.size() > 1) {
            workspace.setMetaData(parts.get(1));
        }
        workspace.setSearch(search);
        return workspace;
    }

    public Workspace initAutoWorkspace() {
        List<ObjType> typeList = new ArrayList<>();
        for (ObjType type : DataManager.getTypes()) {
            if (checkTypeForAutoWs((DC_TYPE) type.getOBJ_TYPE_ENUM(), type)) {
                typeList.add(type);
            }
        }
        Workspace workspace = new Workspace("Auto Workspace", typeList);
        saveWorkspace(workspace);
        workspaces.add(workspace);
        return workspace;
    }

    private boolean checkTypeForAutoWs(DC_TYPE TYPE, ObjType type) {
        switch (TYPE) {
            case ACTIONS:
                if (type.getGroupingKey().equalsIgnoreCase(ActionEnums.ACTION_TYPE.STANDARD_ATTACK.toString())) {
                    if (type.getWorkspaceGroup() != MetaEnums.WORKSPACE_GROUP.COMPLETE) {
                        return true;
                    }
                }
            case SKILLS:
                if (type.getIntParam(PARAMS.CIRCLE) < 2) {
                    if (StringMaster.contains(DC_ContentValsManager.getFocusMasteries(), type
                            .getSubGroupingKey())) {
                        return true;
                    }
                }
            case CLASSES:
                if (type.getIntParam(PARAMS.CIRCLE) < 2) {
                    if (StringMaster.contains(DC_ContentValsManager.getFocusClassGroups(), type
                            .getSubGroupingKey())) {
                        return true;
                    }
                }
        }
        return false;
    }

    public Workspace initDefaultWorkspace() {
        Workspace defaultWorkspace = loadWorkspace(DEFAULT_WORKSPACE_NAME);
        if (defaultWorkspace == null) {
            defaultWorkspace = new Workspace(DEFAULT_WORKSPACE_NAME, new ArrayList<>());
            saveWorkspace(defaultWorkspace);
        }
        return defaultWorkspace;
    }

    public Workspace getActiveWorkspace() {
        return activeWorkspace;
    }

    public void setActiveWorkspace(Workspace activeWorkspace) {
        this.activeWorkspace = activeWorkspace;
    }

    public Workspace getWorkspaceByName(String typeName) {
        for (Workspace ws : workspaces) {
            if (ws.getName().equalsIgnoreCase(typeName)) {
                return ws;
            }
        }
        return null;
    }

    public Workspace getWorkspaceByTab(G_Panel tabComp) {
        for (Workspace ws : workspaces) {
            if (ws.getTabComp() == (tabComp)) {
                return ws;
            }
        }
        return null;
    }

    public void addWorkspace(Workspace ws) {
        getActiveWorkspaces().add(ws);
    }

    public boolean addTypeToActiveWorkspace(ObjType selectedType) {

        if (defaultTypeWorkspacesOn) {
            setActiveWorkspace(initTypeWorkspace(selectedType));
        } else if (getActiveWorkspace() == null) {
            setActiveWorkspace(initDefaultWorkspace());
        }
        if (defaultGroupWorkspacesOn) {
            if (!selectedType.checkProperty(G_PROPS.WORKSPACE_GROUP)) {
                if (!activeWorkspace.getTypeList().contains(selectedType)) {
                    int option = JOptionPane.showOptionDialog(null, "Workspace group?",
                            "Select...", JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, MetaEnums.WORKSPACE_GROUP.values(),
                            MetaEnums.WORKSPACE_GROUP.FOCUS);

                    if (option == JOptionPane.CLOSED_OPTION) {
                        return true;
                    }

                    selectedType.setProperty(G_PROPS.WORKSPACE_GROUP, StringMaster
                            .getWellFormattedString("" + MetaEnums.WORKSPACE_GROUP.values()[option]));
                }
            }
        }
        boolean result = activeWorkspace.addType(selectedType);
        if (autosave == true) {
            saveWorkspace(activeWorkspace);
        }
        return result;
    }

    private Workspace initTypeWorkspace(ObjType selectedType) {
        DEFAULT_TYPE_WORKSPACES DTW = DEFAULT_TYPE_WORKSPACES.MISC;
        for (DEFAULT_TYPE_WORKSPACES dtw : DEFAULT_TYPE_WORKSPACES.values()) {
            if (dtw.checkType(selectedType.getOBJ_TYPE_ENUM())) {
                DTW = dtw;
                break;
            }
        }
        for (Workspace ws : workspaces) {
            DEFAULT_TYPE_WORKSPACES dtw = new EnumMaster<DEFAULT_TYPE_WORKSPACES>()
                    .retrieveEnumConst(DEFAULT_TYPE_WORKSPACES.class, ws.getName());
            if (dtw == DTW) {
                return ws;
            }
        }

        Workspace typeWorkspace = loadWorkspace(getTypeWorkspaceName(selectedType
                .getOBJ_TYPE_ENUM()));
        if (typeWorkspace == null) {
            typeWorkspace = new Workspace(getTypeWorkspaceName(selectedType.getOBJ_TYPE_ENUM()),
                    new ArrayList<>());
            initWorkspace(typeWorkspace);
            saveWorkspace(typeWorkspace);
        }
        return typeWorkspace;
    }

    public void initDefaultWorkspaces() {
        for (DEFAULT_TYPE_WORKSPACES dtw : DEFAULT_TYPE_WORKSPACES.values()) {
            if (ArcaneVault.getTypes() != null) {
                if (dtw == DEFAULT_TYPE_WORKSPACES.MISC) {
                    continue;
                } else if (dtw.getTypes() == null) {
                    continue;
                } else {
                    String string = dtw.getTypes()[0].getName().toLowerCase();
                    if (!ArcaneVault.getTypes().contains(string)) {
                        continue;
                    }
                }
            }
            loadWorkspace(dtw.getWorkspaceName());
        }

    }

    private String getTypeWorkspaceName(OBJ_TYPE TYPE) {
        for (DEFAULT_TYPE_WORKSPACES ws : DEFAULT_TYPE_WORKSPACES.values()) {
            if (ws.checkType(TYPE)) {
                return ws.getWorkspaceName();
            }
        }
        return DEFAULT_TYPE_WORKSPACES.MISC.getWorkspaceName();
    }

    public List<Workspace> getActiveWorkspaces() {
        return workspaces;
    }

    public boolean isDefaultTypeWorkspacesOn() {
        return defaultTypeWorkspacesOn;
    }

    public void setDefaultTypeWorkspacesOn(boolean defaultTypeWorkspacesOn) {
        this.defaultTypeWorkspacesOn = defaultTypeWorkspacesOn;
    }

    public boolean isDefaultGroupWorkspacesOn() {
        return defaultGroupWorkspacesOn;
    }

    public void setDefaultGroupWorkspacesOn(boolean defaultGroupWorkspacesOn) {
        this.defaultGroupWorkspacesOn = defaultGroupWorkspacesOn;
    }

    public enum DEFAULT_TYPE_WORKSPACES {
        SPELLS(DC_TYPE.SPELLS), ACTIONS(DC_TYPE.ACTIONS), // type+group
        // filter
        // objects? e.g.
        // active
        ABILS(DC_TYPE.ABILS),

        SKILLS(DC_TYPE.SKILLS),
        CLASSES(DC_TYPE.CLASSES),
        UNITS(DC_TYPE.UNITS, DC_TYPE.CHARS),
        DUNGEONS(DC_TYPE.FLOORS),
        ENCOUNTERS(DC_TYPE.ENCOUNTERS),

        MISC;
        private OBJ_TYPE[] types;

        DEFAULT_TYPE_WORKSPACES(OBJ_TYPE... types) {
            this.types = types;
        }

        public boolean checkType(OBJ_TYPE TYPE) {
            return Arrays.asList(getTypes()).contains(TYPE);
        }

        public String getWorkspaceName() {
            return StringMaster.getWellFormattedString(name());
        }

        public OBJ_TYPE[] getTypes() {
            return types;
        }
    }

}
