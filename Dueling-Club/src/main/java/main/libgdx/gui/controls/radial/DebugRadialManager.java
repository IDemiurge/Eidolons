package main.libgdx.gui.controls.radial;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.entity.obj.DC_Obj;
import main.game.core.game.DC_Game;
import main.game.core.game.Game;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.auxiliary.EnumMaster;
import main.test.debug.DebugMaster;
import main.test.debug.DebugMaster.DEBUG_FUNCTIONS;
import main.test.debug.DebugMaster.HIDDEN_DEBUG_FUNCTIONS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 12/28/2016.
 */
public class DebugRadialManager {


    private static TextureRegion defaultTexture;

    static {
        DEBUG_CONTROL.FUNC_OTHER.objects = getUnlistedFunctions().toArray();
        defaultTexture =
                new TextureRegion(
                        new Texture(RadialMenu.class.getResource("/data/marble_green.png").getPath())
                );
    }


    public static List<RadialValueContainer> getDebugNodes(DC_Obj obj) {
        List<RadialValueContainer> list = new LinkedList<>();

        Arrays.stream(DEBUG_CONTROL.values()).forEach(c -> {
            if (c.isRoot()) {
                list.add(createNodeBranch(c, obj));
            }
        });

        return list;
    }

    private static RadialValueContainer createNodeBranch(Object object, DC_Obj obj) {
        RadialValueContainer node;
        List<RadialValueContainer> list = new ArrayList<>();

        if (object instanceof DEBUG_CONTROL) {
            DEBUG_CONTROL c = (DEBUG_CONTROL) object;
            if (c.getChildObjects() != null) {
                list = Arrays.stream(c.getChildObjects())
                        .map(el -> createNodeBranch(el, obj))
                        .collect(Collectors.toList()
                        );
            } else if (c.getChildren() != null) {
                list = Arrays.stream(c.getChildren())
                        .map(el -> createNodeBranch(el, obj))
                        .collect(Collectors.toList()
                        );
            }
        }
        if (list.size() != 0) {

            node = new RadialValueContainer(defaultTexture, () -> {
                DebugMaster.setTarget(obj);
                try {
                    if (object instanceof DEBUG_CONTROL) {
                        handleDebugControl((DEBUG_CONTROL) object);
                    } else {
                        handleDebugControl(object);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            node = new RadialValueContainer(defaultTexture, null);
            node.setChilds(list);
        }

        RadialManager.addSimpleTooltip(node, object.toString());

        return node;
    }

    private static List<DEBUG_FUNCTIONS> getUnlistedFunctions() {
        return Arrays.stream(DEBUG_FUNCTIONS.values())
                .filter(func -> !isListed(func)).collect(Collectors.toList());
    }

    private static boolean isListed(DEBUG_FUNCTIONS func) {
        for (DEBUG_CONTROL c : DEBUG_CONTROL.values()) {
            if (c.getChildObjects() != null) {
                if (Arrays.asList(c.getChildObjects()).contains(func)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void handleDebugControl(Object control) {
        if (control instanceof DEBUG_FUNCTIONS) {
            DC_Game.game.getDebugMaster()
                    .executeDebugFunctionNewThread(((DEBUG_FUNCTIONS) control));
        }

        if (control instanceof HIDDEN_DEBUG_FUNCTIONS) {
            DC_Game.game.getDebugMaster()
                    .executeHiddenDebugFunction(((HIDDEN_DEBUG_FUNCTIONS) control));
        }
    }

    public static void handleDebugControl(DEBUG_CONTROL control) {
        new Thread(() -> {
            switch (control) {
                case SET_VALUE:
                    Game.game.getValueHelper().promptSetValue();
                    break;
                case PICK:
                    DC_Game.game.getDebugMaster().executeDebugFunction(new EnumMaster<DEBUG_FUNCTIONS>()
                            .retrieveEnumConst(DEBUG_FUNCTIONS.class,
                                    ListChooser.chooseEnum(DEBUG_FUNCTIONS.class)));
                    break;
                case TYPE:
                    DC_Game.game.getDebugMaster().promptFunctionToExecute();
                    break;
                case PICK_HIDDEN:
                    DC_Game.game.getDebugMaster().executeHiddenDebugFunction(new EnumMaster<HIDDEN_DEBUG_FUNCTIONS>()
                            .retrieveEnumConst(HIDDEN_DEBUG_FUNCTIONS.class,
                                    ListChooser.chooseEnum(HIDDEN_DEBUG_FUNCTIONS.class)));
                    break;
            }
        }, "debug radial click handle").start();
    }

    public enum DEBUG_CONTROL {
        //    DYNAMIC_PARAMETER(),
//    PARAMETER(),
//    PROPERTY(),
//    POSITION(),
//
//
//    SET(PROPERTY,PARAMETER,DYNAMIC_PARAMETER, POSITION){
//        @Override
//        boolean isRoot() {
//            return true;
//        }
//    },
        SET_VALUE {
            @Override
            public boolean isRoot() {
                return true;
            }
        },

        REF(),
        INFO(),
        SHOW(DebugMaster.group_display
//         REF, INFO
        ) {
            @Override
            public boolean isRoot() {
                return true;
            }
        },

        FUNC_TOGGLE(DebugMaster.group_toggle),
        FUNC_STANDARD(DebugMaster.group_basic),
        FUNC_ADD_BF(DebugMaster.group_add_bf_obj),
        FUNC_ADD_NON_BF(DebugMaster.group_add),
        FUNC_GLOBAL(DebugMaster.group_bf),
        //        FUNC_GRAPHICS(DebugMaster.group_graphics),
//        FUNC_SFX(DebugMaster.group_sfx),
        FUNC_OTHER(
        ),
        PICK(),
        TYPE(),
        PICK_HIDDEN(),
        FUNCTION(FUNC_STANDARD, FUNC_ADD_BF, FUNC_ADD_NON_BF, FUNC_GLOBAL,
                FUNC_OTHER, FUNC_TOGGLE,
                PICK, TYPE, PICK_HIDDEN) {
            @Override
            public boolean isRoot() {
                return true;
            }
        },;


        public Object[] objects;
        public DEBUG_CONTROL[] children;

        DEBUG_CONTROL(Object... children) {
            this.objects = children;

        }

        DEBUG_CONTROL(DEBUG_CONTROL... children) {
            this.children = children;

        }

        public boolean
        isRoot() {
            return false;
        }

        public DEBUG_CONTROL[] getChildren() {
            return children;
        }

        public Object[] getChildObjects() {
            return objects;
        }
    }
}
