package main.client.cc.gui.neo.tree.view;

import main.client.cc.HC_Master;
import main.content.CONTENT_CONSTS.MASTERY;
import main.content.ContentManager;
import main.content.DC_ContentManager;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.entity.type.ObjType;
import main.swing.components.buttons.CustomButton;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.editors.ImageChooser;
import main.swing.generic.components.editors.TextEditor;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.test.auto.AutoTestMaster;

import java.awt.*;
import java.util.List;

public class TreeControlPanel extends G_Panel {

    int maxColumns = 5;
    private HT_View view;

    public TreeControlPanel(HT_View view) {
        this.view = view;
        int i = 0;
        for (TREE_CONTROLS c : TREE_CONTROLS.values()) {
            i++;
            if (i >= maxColumns) {
                i = 0;
                add(createButton(c), "wrap");
            } else {
                add(createButton(c));
            }
        }
    }

    public TreeControlPanel() {
        this(null);

    }

    public static boolean setProp(final boolean alt, final ObjType selectedType) {
        return setProperty(alt, selectedType);

        // new Thread(new Runnable() {
        // public void run() {
        // setProperty(alt, selectedType);
        // }
        // }, "setProp thread").start();
    }

    // private static void setProp(boolean alt, ObjType selectedType) {
    //
    // }
    private static boolean setProperty(boolean alt, ObjType selectedType) {

        String name;

        String containerString = "Lore;Description;Flavor;Image;Actives;Passives;Attribute Bonuses;Parameter Bonuses;";
        // TODO v-flow buttons one-click!
        List<String> listData = StringMaster.openContainer(containerString);
        name = new ListChooser(SELECTION_MODE.SINGLE, listData, true).choose();
        if (name == null) {
            return false;
        }
        // name = new ButtonChoicePanel(SELECTION_MODE.SINGLE,
        // listData).choose();
        PROPERTY prop = ContentManager.getPROP(name);
        String value = null;
        if (alt) {
            value = new TextEditor().launch(selectedType.getProperty(prop));
        } else if (DC_ContentManager.getEditorMap() != null) {
            if (DC_ContentManager.getEditorMap().get(name) != null) {
                value = DC_ContentManager.getEditorMap().get(name).launch(value, name);
            }
        } else if (prop == G_PROPS.IMAGE) {
            value = new ImageChooser().launch(selectedType.getImagePath(), "");
            // tableMouse.getEditor(prop)
        } else {
            value = new TextEditor().launch(selectedType.getProperty(prop));
        }

        if (value != null) {
            selectedType.setProperty(prop, value);
        }

        return true;
        // TableMouseListener
        // if ()

    }

    private Component createButton(final TREE_CONTROLS c) {
        return new CustomButton(VISUALS.VALUE_BOX_TINY, StringMaster.getWellFormattedString(c
                .toString())) {
            @Override
            public void handleAltClick() {
                handleControl(c, true);
            }

            public void handleClick() {
                handleControl(c, false);
            }

            @Override
            protected Font getDefaultFont() {
                // FontMaster.getFont(f, size, style)
                return super.getDefaultFont();
            }
        };
    }

    public void handleControl(TREE_CONTROLS c, boolean alt) {
        ObjType selectedType = view.getTree().getSelectedType();

        switch (c) {

            case TEST:
                AutoTestMaster.testType(selectedType);
                break;
            case WS_CYCLE:
                workspaceCycle(alt);

                break;
            case WS_TOGGLE:
                if (alt) {
                    if (view instanceof SkillTreeView) {
                        SkillTreeView skillTreeView = (SkillTreeView) view;

                        String enums = new ListChooser(SELECTION_MODE.MULTIPLE, MASTERY.class)
                                .choose();
                        if (StringMaster.isEmpty(enums)) {
                            return;
                        }
                        List<MASTERY> list = new EnumMaster<MASTERY>().getEnumList(MASTERY.class,
                                enums);

                        skillTreeView.setWorkspace(list.toArray(new MASTERY[list.size()]));

                        view.setWorkspaceMode(true);
                        view.refresh();
                        return;
                    }
                }
                view.setWorkspaceMode(!view.isWorkspaceMode());
                view.refresh();

                break;

            case LINK:
                view.adjustLink(null, HC_Master.getSelectedTreeNode());
                break;
            // case LINK_H:
            // view.adjustLink(false, HC_Master.getSelectedTreeNode());
            // break;
            // case LINK_V:
            // view.adjustLink(true, HC_Master.getSelectedTreeNode());
            // break;
            case OFFSET_X:

                view.adjustOffset(alt, true, HC_Master.getSelectedTreeNode());
                break;
            case OFFSET_Y:
                view.adjustOffset(alt, false, HC_Master.getSelectedTreeNode());
                break;
            case REBUILD:
                view.rebuildAndSetTree();
                break;
            case ALT_BASE:
                if (alt) {
                    view.editAltBaseLink(selectedType);
                    break;
                }
                view.addAltBase(selectedType);
                break;
            case SET:
                setProp(alt, selectedType);
                view.rebuildAndSetTree();
                break;
            default:
                break;

        }
    }

    protected void workspaceCycle(boolean alt) {
        int i = view.getWorkspaces().indexOf(view.getWorkspace());
        i++;

        if (i < 0 || i >= view.getWorkspaces().size()) {
            i = 0;
        }

        view.setWorkspace((Object[]) view.getWorkspaces().get(i));
        view.setWorkspaceMode(true);
        view.refresh();
    }

    public void setView(HT_View treeViewComp) {
        view = treeViewComp;
    }

    public enum TREE_CONTROLS {

        REBUILD,

        LINK, OFFSET_Y, OFFSET_X, SET, WS_TOGGLE, WS_CYCLE, ALT_BASE,

        UNDO, TEST

    }
}
