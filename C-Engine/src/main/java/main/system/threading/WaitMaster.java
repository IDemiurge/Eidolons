package main.system.threading;

import main.system.auxiliary.log.Err;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.LogMaster.LOG_CHANNEL;
import main.system.datatypes.DequeImpl;

import java.util.HashMap;
import java.util.Map;

public class WaitMaster {
    // Sync map?
    private static Map<WAIT_OPERATIONS, Waiter> waiters = new HashMap<>();
    // private static List<Waiter> activeWaiters = new ArrayList<>() ;
    private static DequeImpl<WAIT_OPERATIONS> completeOperations;

    public static void unmarkAsComplete(WAIT_OPERATIONS operation) {
        getCompleteOperations().remove(operation);
        LogMaster.log(LogMaster.WAIT_DEBUG, "Unmarked As Complete: " + operation);
    }

    public static boolean isComplete(WAIT_OPERATIONS operation) {
        return
         getCompleteOperations().contains(operation);
    }
        public static void markAsComplete(WAIT_OPERATIONS operation) {
        getCompleteOperations().add(operation);
        LogMaster.log(LogMaster.WAIT_DEBUG, "Marked As Complete: " + operation);
        // waiters.get(operation).
    }

    public static Object waitForInputIfWaiting(WAIT_OPERATIONS operation) {
        if (getWaiters().get(operation) == null) {
            return false;
        }
        return waitForInput(operation);
    }

    public static Object waitForInput(WAIT_OPERATIONS animationFinished) {
        return waitForInput(animationFinished, null);
    }

    public static Object waitForInput(WAIT_OPERATIONS operation,
                                      Integer maxTime) {
        if (getCompleteOperations().contains(operation)) {
            return true;
        }
        LogMaster.log(LOG_CHANNEL.WAIT_DEBUG,
                " waiting for " + operation.toString());
        Waiter waiter = waiters.get(operation);
        boolean remove = true;
        if (waiter == null) {
            remove = false;
            waiter = new Waiter(operation);
            waiters.put(operation, waiter);
        }

        Object result = waiter.startWaiting(maxTime == null ? null : (long) maxTime);

        LogMaster.log(LogMaster.WAIT_DEBUG, "INPUT RETURNED: " + result);

        if (remove) {
            waiters.remove(waiter);
        } else {
            LogMaster.log(LogMaster.WAIT_DEBUG, "WAITER RETAINED: "
                    + operation.toString());
        }

        return result;
    }

    public static boolean receiveInput(WAIT_OPERATIONS operation, Object input) {
        return receiveInput(operation, input, true);
    }

    public static boolean receiveInput(WAIT_OPERATIONS operation, Object input, boolean removeWaiter) {
        LogMaster.log(LOG_CHANNEL.WAIT_DEBUG, " received input for "
                + operation.toString() + ": " + input);
        Waiter waiter = waiters.get(operation);
        if (waiter == null) {
            waiter = new Waiter(operation);
            waiters.put(operation, waiter);
        } else if (removeWaiter) {
            waiters.remove(waiter);
        }
        waiter.setInput(input);

        return true;
    }

    public static void interrupt(WAIT_OPERATIONS waiter) {
        if (waiters.get(waiter) == null) {
            Err.error("No such operation in process!" + waiter.name());
            return;
        }
        waiters.get(waiter).interrupt();
    }

    public static void WAIT(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public static DequeImpl<WAIT_OPERATIONS> getCompleteOperations() {
        if (completeOperations == null) {
            completeOperations = new DequeImpl<>();
        }
        return completeOperations;
    }

    public static Map<WAIT_OPERATIONS, Waiter> getWaiters() {
        return waiters;
    }


    // additional identifying for batch operations?
    public enum WAIT_OPERATIONS {
        SELECT_BF_OBJ,
        PRECOMBAT,
        TEST_MODE,
        TEST_GAME_STARTED,
        CUSTOM_SELECT,
        ACTION_COMPLETE,
        UNIT_OBJ_INIT,
        READING_DONE,
        GUI_READY,
        SELECTION,
        ANIMATION_FINISHED,
        OPTION_DIALOG,
        BATTLE_FINISHED,
        SELECT_MAP_OBJ,
        MAP_CLICK,
        DIALOGUE_DONE,
        AUTO_TEST_INPUT,
        ACTIVE_UNIT_SELECTED, ACTION_INPUT, ANIMATION_QUEUE_FINISHED,
        GAME_LOOP_PAUSE_DONE, GAME_FINISHED, AI_TRAINING_FINISHED, WAIT_COMPLETE
    }

}
// TODO NEW VERSION HAS A BUG: receiveInput doesn't register and so XML_Reader
// gets stuck on wait()
// package main.system.threading;
//
// import main.system.auxiliary.Err;
// import main.system.auxiliary.LogMaster;
// import main.system.auxiliary.LogMaster.LOG_CHANNELS;
// import main.system.datatypes.DequeImpl;
//
// import java.util.HashMap;
// import java.util.Map;
//
// public class WaitMaster {
// // Sync map?
// private static Map<WAIT_OPERATIONS, Waiter> waiters = new
// HashMap<WAIT_OPERATIONS, Waiter>();
// // private static List<Waiter> activeWaiters = new ArrayList<>() ;
// private static DequeImpl<WAIT_OPERATIONS> completeOperations;
//
// // additional identifying for batch operations?
// public enum WAIT_OPERATIONS {
// SELECT_BF_OBJ,
// PRECOMBAT,
// TEST_MODE,
// TEST_GAME_STARTED,
// CUSTOM_SELECT,
// ACTION_COMPLETE,
// UNIT_OBJ_INIT,
// READING_DONE,
// GUI_READY,
// SELECTION,
// ANIMATION_FINISHED,
// OPTION_DIALOG,
// BATTLE_FINISHED,
// SELECT_MAP_OBJ,
// MAP_CLICK,
// DIALOGUE_DONE {
// public boolean isOneShot() {
// return true;
// }
// },
// ;
//
// public boolean isOneShot() {
// return false;
// }
// }
//
// public static void unmarkAsComplete(WAIT_OPERATIONS operation) {
// getCompleteOperations().remove(operation);
// }
//
// public static void markAsComplete(WAIT_OPERATIONS operation) {
// getCompleteOperations().add(operation);
// // waiters.get(operation).
// }
//
// public static Object waitForInputIfWaiting(WAIT_OPERATIONS operation) {
// if (getWaiters().get(operation) == null)
// return false;
// return waitForInput(operation);
// }
//
// public static Object waitForInput(WAIT_OPERATIONS operation) {
// if (getCompleteOperations().contains(operation))
// return true;
// main.system.auxiliary.LogMaster.log(LOG_CHANNELS.WAIT_DEBUG, " waiting for "
// + operation.toString());
// Waiter waiter = waiters.get(operation);
// boolean remove = true;
// Object result = null;
// if (waiter == null) {
// if (!operation.isOneShot())
// remove = false;
// waiter = new Waiter(operation);
// waiters.put(operation, waiter);
// } else {
// result = waiter.getInput();
// }
// if (result == null)
// result = waiter.startWaiting();
//
// LogMaster.log(LogMaster.WAIT_DEBUG, "INPUT RETURNED: " + result);
//
// if (remove) {
// waiters.remove(waiter);
// } else
// main.system.auxiliary.LogMaster.log(LogMaster.WAIT_DEBUG, "WAITER RETAINED: "
// + operation.toString());
//
// return result;
// }
//
// public static boolean receiveInput(WAIT_OPERATIONS operation, Object input) {
// return receiveInput(operation, input, false);
// }
//
// public static boolean receiveInput(WAIT_OPERATIONS operation, Object input,
// boolean removeWaiter) {
// main.system.auxiliary.LogMaster.log(LOG_CHANNELS.WAIT_DEBUG,
// " received input for "
// + operation.toString() + ": " + input);
// Waiter waiter = waiters.get(operation);
// if (waiter == null) {
// waiter = new Waiter(operation);
// waiters.put(operation, waiter);
// } else if (removeWaiter)
// waiters.remove(waiter);
// waiter.setInput(input);
//
// return true;
// }
//
// public static void interrupt(WAIT_OPERATIONS waiter) {
// if (waiters.get(waiter) == null) {
// Err.error("No such operation in process!" + waiter.name());
// return;
// }
// waiters.get(waiter).interrupt();
// }
//
// public static void WAIT(int i) {
// try {
// Thread.sleep(i);
// } catch (InterruptedException ex) {
// Thread.currentThread().interrupt();
// }
// }
//
// public static DequeImpl<WAIT_OPERATIONS> getCompleteOperations() {
// if (completeOperations == null)
// completeOperations = new DequeImpl<WaitMaster.WAIT_OPERATIONS>();
// return completeOperations;
// }
//
// public static Map<WAIT_OPERATIONS, Waiter> getWaiters() {
// return waiters;
// }
//
// }
