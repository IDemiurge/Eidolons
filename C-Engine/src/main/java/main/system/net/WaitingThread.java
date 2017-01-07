package main.system.net;

import main.data.XLinkedMap;
import main.system.auxiliary.Chronos;
import main.system.net.socket.ServerConnector;
import main.system.net.socket.ServerConnector.NetCode;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class WaitingThread implements Runnable {

    private static final long MAX_WAITING_TIME = 30000;
    private static final long WAITING_PERIOD = 1500;
    static int i = 0;
    private static List<WaitingThread> threads = new LinkedList<WaitingThread>();
    private static Viewer VIEWER;
    private static Set<NetCode> registeredCodes = new HashSet<NetCode>();
    private static XLinkedMap<NetCode, String> inputMap = new XLinkedMap<>();
    Boolean result = null;
    private boolean input;
    private String INPUT;
    private NetCode code;
    private Thread t;
    private long waitingTime;

    public WaitingThread(NetCode code) {
        threads.add(this);
        this.setCode(code);
        waitingTime = MAX_WAITING_TIME;
    }

    public WaitingThread(NetCode code, long time) {
        this(code);
        this.waitingTime = time;
    }

    public static String waitForInput(NetCode code) {
        if (!new WaitingThread(code).waitForInput())
            return null;
        return getINPUT(code);

    }

    public static String getINPUT(NetCode code) {
        WaitingThread w = getThread(code);
        if (w == null)
            return null;

        String s = w.getINPUT();
        if (s != null)
            threads.remove(w);
        return s;
    }

    private static WaitingThread getThread(NetCode code) {
        for (WaitingThread wt : threads)
            if (wt.getCode() == code || wt.getCode().equals(code)) {
                return wt;
            }
        main.system.auxiliary.LogMaster.log(0, "WAITING THREAD NOT FOUND " + code);

        return null;
    }

    // TODO additional identifier?
    public static boolean setINPUT(String INPUT, NetCode lastReceivedCode) {
        inputMap.put(lastReceivedCode, INPUT);
        try {
            getThread(lastReceivedCode).setINPUT(INPUT);
        } catch (java.lang.NullPointerException e) {
            main.system.auxiliary.LogMaster.log(0, INPUT + " - waiting thread not found: "
                    + lastReceivedCode.name());

            return false;
        }
        main.system.auxiliary.LogMaster.log(1, lastReceivedCode.name()
                + " - waiting thread input set: " + INPUT);

        return true;
    }

    public static void interruptAll() {
        for (WaitingThread t : threads) {
            t.getT().interrupt();
        }
    }

    public static void setViewer(Viewer viewer) {
        VIEWER = viewer;

    }

    public static void registerCode(NetCode code) {
        // String time = Chronos.getTime();
        // registeredCodes.put(time, code);
        getRegisteredCodes().add(code);
    }

    public static Set<NetCode> getRegisteredCodes() {
        return registeredCodes;
    }

    public static void setRegisteredCodes(Set<NetCode> registeredCodes) {
        WaitingThread.registeredCodes = registeredCodes;
    }

    public static String waitOrGetInput(NetCode code) {
        String input = inputMap.get(code);
        if (input != null)
            return input;
        else
            return waitForInput(code);
    }

    public static XLinkedMap<NetCode, String> getInputMap() {
        return inputMap;
    }

    public boolean Wait() {
        input = false;
        try {
            return WAIT();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean waitForInput() {
        input = true;

        return WAIT();
    }

    private boolean WAIT() {

        getRegisteredCodes().clear();
        t = new Thread(this, "waiting thread - " + getCode().name());
        t.start();
        long time_elapsed = 0;
        while (result == null
            // && ServerConnector.getHandler().getStatus() ==
            // CONNECTION_STATUS.CONNECTED

                ) {
            try {
                synchronized (this) {
                    wait(WAITING_PERIOD);
                    time_elapsed += WAITING_PERIOD;
                    if (time_elapsed > waitingTime) {
                        main.system.auxiliary.LogMaster.log("Waiting time expired! - " + getCode());
                        if (isBreakOnExpired())
                            break;
                        else
                            time_elapsed = 0;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }
        if (result == null)
            result = false;
        Boolean b = result;
        result = null;
        return b;

		/*
         * while (true) { try { Thread.sleep(100); } catch (InterruptedException
		 * e) { e.printStackTrace(); } if (result != null) { boolean b = result;
		 * result = null; return b; } }
		 */

		/*
         * Waiter waiter = new Waiter(); boolean b = false; waiter.execute();
		 * try { b = waiter.get(); } catch (InterruptedException e) {
		 * e.printStackTrace(); } catch (ExecutionException e) {
		 * e.printStackTrace(); } Err.warn("WAITING: " + b); return b;
		 */
    }

    private boolean isBreakOnExpired() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void run() {

        if (VIEWER != null)
            VIEWER.info("WAITING THREAD STARTED FOR " + getCode().name() + "; input: " + input);
        Chronos.mark("WAITING" + getCode().name());
        while (true)
        // (ServerConnector.getHandler().getStatus() ==
        // CONNECTION_STATUS.CONNECTED)
        {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("WAITING INTERRUPTED");
                return;
            }
            if (input) {
                if (INPUT != null) {
                    result = true;
                    break;
                }
                continue;
            } else {
                if (getRegisteredCodes().contains(getCode())) {
                    main.system.auxiliary.LogMaster.log(2, "Code found! " + getCode());
                    result = true;
                    getRegisteredCodes().remove(getCode());
                    break;
                }
            }
            // ??
            if (ServerConnector.isFailure(getCode())) {
                ServerConnector.setFailure(false, getCode());
                result = false;
                break;
            }
            if (ServerConnector.isSuccess(getCode())) {
                ServerConnector.setSuccess(false, getCode());
                result = true;
                break;
            }
            if (Chronos.getTimeElapsedForMark("WAITING" + getCode().name()) > (waitingTime)) {
                if (VIEWER != null)
                    VIEWER.info("WAITING THREAD " + getCode().name() + " TIMED OUT");

                ServerConnector.setFailure(true, getCode());
            }

        }
        if (!input) {
            threads.remove(this);
        }
        if (VIEWER != null)
            VIEWER.info("WAITING THREAD FINISHED FOR " + getCode().name() + "; input: " + input
                    + " " + INPUT);
        main.system.auxiliary.LogMaster.log(1, "WAITING THREAD FINISHED FOR " + getCode().name()
                + "; input: " + input + " " + INPUT);

        i++;
        synchronized (this) {
            notifyAll();
        }
    }

    public String getInput() {
        String s = getINPUT();
        if (s != null)
            threads.remove(code);
        return s;
    }

    private String getINPUT() {
        String INPUT1 = INPUT;
        setINPUT(null);
        return INPUT1;
    }

    public void setINPUT(String iNPUT) {
        INPUT = iNPUT;
    }

    public Thread getT() {
        return t;
    }

    public void setT(Thread t) {
        this.t = t;
    }

    public NetCode getCode() {
        return code;
    }

    public void setCode(NetCode code) {
        this.code = code;
    }

	/*
	 * public class Waiter extends SwingWorker<Boolean, Void> {
	 * 
	 * private Thread t;
	 * 
	 * public Waiter(Thread t) { this.t = t; }
	 * 
	 * public Waiter() { // TODO Auto-generated constructor stub }
	 * 
	 * @Override protected Boolean doInBackground() throws Exception { int i =
	 * 0; if(VIEWER!=null) VIEWER.info( "WAITING THREAD STARTED; input:" +
	 * input); Chronos.mark("WAITING"); while (true) { try { Thread.sleep(5); }
	 * catch (InterruptedException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } System.out.println(i); i++; if (input) { if (INPUT
	 * != null) { result = true; break; } continue; } if
	 * (ServerConnector.isFailure()) { ServerConnector.setFailure(false); result
	 * = false; break; } if (ServerConnector.isSuccess()) {
	 * ServerConnector.setSuccess(false); result = true; break; } if
	 * (Chronos.getTimeElapsedForMark("WAITING") > (MAX_WAITING_TIME)) {
	 * if(VIEWER!=null) VIEWER.info( "WAITING THREAD TIMED OUT");
	 * 
	 * }
	 * 
	 * } return result; } }
	 */
}
