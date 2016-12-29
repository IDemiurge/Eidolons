package main.test.debug;

import main.game.DC_Game;
import main.game.Game;
import main.libgdx.gui.radial.RadialMenu;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 12/28/2016.
 */
public class DebugRadialManager {

    public static void clicked(Object obj){
        if (obj instanceof DEBUG_CONTROL){
            clickedControl((DEBUG_CONTROL) obj);
        } else
            handleDebugControl(obj);
    }
    public static void clickedControl(DEBUG_CONTROL c){
        if (c.getChildObjects()!=null ){
            show(c.getChildObjects());
            return ;
        }
        if (c.getChildren()!=null ){
            show(c.getChildren());
            return ;
        }
         handleDebugControl(c);
    }

    private static void show(Object[] children) {
    }



    public static void show(RadialMenu radialMenu) {
        radialMenu.init(getDebugNodes());
    }
    public static List< RadialMenu.CreatorNode> getDebugNodes(){
        List< RadialMenu.CreatorNode> list = new LinkedList<>();
        new LinkedList<DebugRadialManager.DEBUG_CONTROL>(
         Arrays.asList(DebugRadialManager.DEBUG_CONTROL.values())).forEach(c->{
           createNodeBranch(c, list);


        });
        return list;
    }

    private static void createNodeBranch(Object object, List<RadialMenu.CreatorNode> list) {
        RadialMenu.CreatorNode node = new RadialMenu.CreatorNode();
//        node.texture = TextureCache.;TODO
        node.name =  StringMaster.getWellFormattedString(object.toString());
        node.action = new Runnable() {
            @Override
            public void run() {
                DebugRadialManager.clicked(object);
            }
        };

        list.add(node);

if (object instanceof  DEBUG_CONTROL){
    DEBUG_CONTROL c= (DEBUG_CONTROL) object;
    if (c.getChildObjects()!=null ){
        node.childNodes=    new LinkedList<>() ;
        for (Object o : c.getChildObjects()){
            createNodeBranch(o,  node.childNodes);
        }
    } else
    if (c.getChildren()!=null ){
        node.childNodes=    new LinkedList<>() ;
        for (Object o : c.getChildren()){
            createNodeBranch(o,  node.childNodes);
        }
    }
    }

    }

    public enum DEBUG_CONTROL{
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
        SET_VALUE{
            @Override
            public    boolean isRoot() {
                return true;
            }
        },

        REF(),
        INFO(),
        SHOW(REF,INFO){
            @Override
            public   boolean isRoot() {
                return true;
            }
        },

        FUNC_STANDARD(DebugMaster. group_basic),
        FUNC_ADD_BF(DebugMaster.group_add_bf_obj),
        FUNC_ADD_NON_BF(DebugMaster.group_add),
        FUNC_GLOBAL(DebugMaster.group_basic),
        FUNC_GRAPHICS(DebugMaster.group_graphics),

        PICK(),
        TYPE(),
        PICK_HIDDEN(),
        FUNCTION(FUNC_STANDARD,FUNC_ADD_BF,FUNC_ADD_NON_BF,FUNC_GLOBAL,

         PICK,TYPE,PICK_HIDDEN){
            @Override
            public    boolean isRoot() {
                return true;
            }
        },

        ;


        private   Object[] objects;
        private   DEBUG_CONTROL[] children;

        DEBUG_CONTROL(Object...children){
            this.objects=children;

        }
        DEBUG_CONTROL(DEBUG_CONTROL...children){
            this.children=children;

        }
        public boolean
        isRoot(){
            return false;
        }
        public  DEBUG_CONTROL[] getChildren(){
            return children;
        }
        public   Object[] getChildObjects(){
            return objects;
        }
    }
    public  static void handleDebugControl(Object control) {
        if (control instanceof DebugMaster.DEBUG_FUNCTIONS){
            DebugMaster.DEBUG_FUNCTIONS func_control = ((DebugMaster.DEBUG_FUNCTIONS) control);
            DC_Game.game.getDebugMaster(). executeDebugFunctionNewThread(func_control);
        }
    }
    public static  void handleDebugControl(DEBUG_CONTROL control) {
        switch(control){
            case SET_VALUE:   Game.game.getValueHelper().promptSetValue();
                break;
//            case DYNAMIC_PARAMETER:
//                break;
//            case PARAMETER:
//                break;
//            case PROPERTY:
//                break;
//            case POSITION:
//                break;
//            case SET:
//                break;
            case REF:
                break;
            case INFO:
                break;
            case SHOW:

                break;


            case PICK:

                DC_Game.game.getDebugMaster(). executeDebugFunction(new EnumMaster<DebugMaster.DEBUG_FUNCTIONS>()
                 .retrieveEnumConst(DebugMaster.DEBUG_FUNCTIONS.class,
                  ListChooser.chooseEnum(DebugMaster.DEBUG_FUNCTIONS.class) ));
                break;
            case TYPE:
                DC_Game.game.getDebugMaster().promptFunctionToExecute();
                break;
            case PICK_HIDDEN:
             DC_Game.game.getDebugMaster(). executeHiddenDebugFunction(new EnumMaster<DebugMaster.HIDDEN_DEBUG_FUNCTIONS>()
                 .retrieveEnumConst(DebugMaster.HIDDEN_DEBUG_FUNCTIONS.class,
                  ListChooser.chooseEnum(DebugMaster.HIDDEN_DEBUG_FUNCTIONS.class) ));
                break;
            case FUNCTION:
                break;
        }
    }


}
