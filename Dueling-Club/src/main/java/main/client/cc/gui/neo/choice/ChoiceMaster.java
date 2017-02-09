package main.client.cc.gui.neo.choice;

import main.client.dc.SequenceManager;
import main.content.MACRO_OBJ_TYPES;
import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.List;

public class ChoiceMaster {

    public static ObjType chooseType(MACRO_OBJ_TYPES TYPE, List<ObjType> types, final String info,
                                     final VALUE filterValue, final PROPERTY PROP) {
        ChoiceSequence cs = new ChoiceSequence();
        EntityChoiceView typeChoiceView = new EntityChoiceView(cs, null, types) {

            public String getInfo() {
                return info;
            }

            protected OBJ_TYPE getTYPE() {
                return TYPE;
            }

            protected PROPERTY getPROP() {
                return PROP;
            }

            protected VALUE getFilterValue() {
                return filterValue;
            }
        };
        cs.addView(typeChoiceView);
        cs.setManager(getTypeChoiceMaster(cs, typeChoiceView));
        cs.start();
        String name = (String) WaitMaster.waitForInput(WAIT_OPERATIONS.CUSTOM_SELECT);
        if (name == null) {
            return null;
        }
        return DataManager.getType(name, TYPE);
    }

    public static void chooseTypeNewThread(final MACRO_OBJ_TYPES TYPE, final List<ObjType> types,
                                           final String info, final VALUE filterValue, final PROPERTY PROP) {
        new Thread(new Runnable() {
            public void run() {
                WaitMaster.receiveInput(WAIT_OPERATIONS.SELECTION, chooseType(TYPE, types, info,
                        filterValue, PROP));
            }
        }, " thread").start();

    }

    private static SequenceManager getTypeChoiceMaster(final ChoiceSequence cs,
                                                       EntityChoiceView typeChoiceView) {
        return new SequenceManager() {

            public void doneSelection() {
                WaitMaster.receiveInput(WAIT_OPERATIONS.CUSTOM_SELECT, cs.getValue());
            }

            public void cancelSelection() {
                WaitMaster.receiveInput(WAIT_OPERATIONS.CUSTOM_SELECT, null);

            }
        };
    }

}
