package main.system.threading;

import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

public class WaitMaster {
    // Sync map?
    private static Map<WAIT_OPERATIONS, Waiter> waiters = new HashMap<>();
    // private static List<Waiter> activeWaiters = new ArrayList<>() ;
    private static DequeImpl<WAIT_OPERATIONS> completeOperations;

    private static Map<Object, Lock> locks = new HashMap<>();
    private static Map<Lock, Condition> conditions = new HashMap<>();
    private static Set<Object> unlocked = new LinkedHashSet<>();
    private static int conditionCounter = 0;

    public static void unlock(Object o) {
        unlocked.add(o);
        Lock lock = locks.remove(o);
        if (lock == null) {
            main.system.auxiliary.log.LogMaster.important("NO LOCK " + o);
            return;
        }
        main.system.auxiliary.log.LogMaster.important( "UNLOCKING " + o);
        lock.lock();
         conditions.remove(lock).signal();
        lock.newCondition().signal();
        lock.unlock();
//        main.system.auxiliary.log.LogMaster.dev("UNLOCKED " + o);
    }

    public static void waitLock(Object o) {
        waitLock(o, 0);
    }

    public static void waitLock(Object o, int maxMillis) {
        if (unlocked.contains(o)) {
            main.system.auxiliary.log.LogMaster.important("ALREADY UNLOCKED " + o);
            return;
        }
        Lock lock = new ReentrantLock();
        if (locks.containsKey(o)) {
            main.system.auxiliary.log.LogMaster.important("******ALREADY LOCKED " + o);
            return;
        }
        locks.put(o, lock);
        Condition waiting = lock.newCondition();
        conditions.put(lock, waiting);
        main.system.auxiliary.log.LogMaster.important("LOCKED with key: " + o);
        lock.lock();
        try {
            if (maxMillis > 0) {
                waiting.await(maxMillis, TimeUnit.MILLISECONDS);
            } else
                waiting.await();
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally{
            lock.unlock();
        }
    }


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

    public static Object waitForInputIfNotWaiting(WAIT_OPERATIONS operation) {
        if (getWaiters().get(operation) != null) {
            return false;
        }
        return waitForInput(operation);
    }

    public static Object waitForInput(WAIT_OPERATIONS operation) {
        return waitForInput(operation, 0);
    }

    public static Object waitForInputAnew(WAIT_OPERATIONS operation) {
        WaitMaster.getWaiters().remove(operation);
        return waitForInput(operation, 0);
    }

    public static Object waitForInput(WAIT_OPERATIONS operation,
                                      int maxTime) {
        if (getCompleteOperations().contains(operation)) {
            return true;
        }
        LogMaster.log(LOG_CHANNEL.WAIT_DEBUG,
                " waiting for " + operation.toString());
        Waiter waiter = waiters.get(operation);
        if (waiter == null) {
            waiter = new Waiter(operation);
            waiters.put(operation, waiter);
        } else
            waiter.setInterrupted(false);
        if (waiter.getInput() != null) {
            waiters.remove(operation);
            return waiter.getInput();
        }

        Object result = waiter.startWaiting(maxTime == 0 ? null : (long) maxTime);
        LogMaster.log(LogMaster.WAIT_DEBUG, "INPUT RETURNED: " + result);
        waiters.remove(operation);
        return result;
    }

    public static boolean receiveInput(WAIT_OPERATIONS operation, Object input) {
        return receiveInput(operation, input, true);
    }

    public static boolean receiveInputIfWaiting(WAIT_OPERATIONS operation, Object input) {
        return receiveInputIfWaiting(operation, input, true);
    }

    public static boolean receiveInputIfWaiting(WAIT_OPERATIONS operation, Object input, boolean removeWaiter) {
        if (getWaiters().get(operation) == null) {
            return false;
        }
        return receiveInput(operation, input, removeWaiter);
    }

    public static boolean receiveInput(WAIT_OPERATIONS operation, Object input, boolean removeWaiter) {
        if (isLogged(operation))
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

    private static boolean isLogged(WAIT_OPERATIONS operation) {
        switch (operation) {
            case TOWN_DONE:
                return true;
        }
        return false;
    }

    public static void interrupt(WAIT_OPERATIONS waiter) {
        if (waiters.get(waiter) == null) {
            main.system.auxiliary.log.LogMaster.log(1, "No such operation in process!" + waiter.name());
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

    public static void doAfterWait(int i, Runnable o) {
        new Thread(() -> {
            WAIT(i);
            o.run();
        }, "do after wait").start();
    }

    public static WAIT_OPERATIONS getOperation(String value) {
        return new EnumMaster<WAIT_OPERATIONS>().retrieveEnumConst(WAIT_OPERATIONS.class, value);
    }

    public static void waitForCondition(Predicate<Float> p, int max) {
        Object o = conditionCounter++;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (p.test(0f)) {
                    unlock(o);
                }
            }
        };

        Timer timer = TimerTaskMaster.newTimer(task, 1000, 100);
        waitLock(o, max);
        timer.cancel();
    }


    // additional identifying for batch operations?
    public enum WAIT_OPERATIONS {
        SELECT_BF_OBJ,
        PRECOMBAT,
        TEST_MODE,
        TEST_GAME_STARTED,
        CUSTOM_SELECT,
        HERO_SELECTION,
        ACTION_COMPLETE,
        UNIT_OBJ_INIT,
        READING_DONE,
        GUI_READY, DUNGEON_SCREEN_PRELOADED,
        SELECTION,
        ANIMATION_FINISHED,
        OPTION_DIALOG,
        BATTLE_FINISHED,
        SELECT_MAP_OBJ,
        MAP_CLICK,
        DIALOGUE_DONE,
        AUTO_TEST_INPUT,
        ACTIVE_UNIT_SELECTED, ACTION_INPUT, ANIMATION_QUEUE_FINISHED,
        GAME_RESUMED, GAME_FINISHED, AI_TRAINING_FINISHED, GDX_READY, TEXT_INPUT, DUNGEON_SCREEN_READY,
        GAME_LOOP_STARTED, XML_READY, CONFIRM, HC_DONE, TOWN_DONE, PLAYER_ACTION_FINISHED, BRIEFING_COMPLETE, MESSAGE_RESPONSE, MESSAGE_RESPONSE_DEATH,
        FULLSCREEN_DONE, COMMENT_DONE, INPUT, FILE_SELECTION, DIALOG_SELECTION, DIALOG_SELECTION_CELL_SCRIPT_VALUE, DIALOG_SELECTION_ENUM, WAIT_COMPLETE
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
