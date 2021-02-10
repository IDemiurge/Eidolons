package main.level_editor.backend.brush;

import main.level_editor.backend.handlers.operation.Operation;

public enum LE_BrushType {
    wall,
    alt_wall,
    none,
    toggle_void,

//    debris,
//    undergrowth,
//    overlays_mushrooms,
//
//    containers_common,
//    containers_graves,
//    containers_corpses,
//    containers_chests,
//
//    misc_ruins,
//
//    critters,
//    runes,
//    vfx_fire,
//    vfx_smoke

    toggle_set;
    String weightMap;

    public Operation.LE_OPERATION getOperation() {
         switch(this){

             case wall:
                 break;
             case alt_wall:
                 break;
             case none:
                 break;
             case toggle_set:
                 return Operation.LE_OPERATION.VOID_SET;
             case toggle_void:
                 return Operation.LE_OPERATION.VOID_TOGGLE;
         }
        return null;
    }
}
