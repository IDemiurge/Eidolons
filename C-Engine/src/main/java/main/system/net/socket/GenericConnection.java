package main.system.net.socket;

import main.system.auxiliary.StringMaster;
import main.system.net.WaitingThread;
import main.system.net.socket.ServerConnector.NetCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class GenericConnection implements Runnable {

    private static final int MAX_CODE_POOL_SIZE = 20;
    public CONNECTION_STATUS status;
    public Socket socket;
    public Thread thread;
    public PrintWriter out;
    public BufferedReader in;
    public NetCode lastSentCode;
    public boolean pinged = false;
    public int unresponded = 0;
    public Class<?> codestype;
    private NetCode lastReceivedCode;
    private List<NetCode> THIS_CODES;

    public GenericConnection(Socket socket1, Class<?> codestype) {

        status = CONNECTION_STATUS.CONNECTED;
        socket = socket1;
        thread = new Thread(this, "ConnectionThread");
        setCodeType(codestype);
        initIO();
        thread.start();
    }

    public GenericConnection() {

    }

    public void setCodeType(Class<?> codestype) {
        this.codestype = codestype;
    }

    private boolean checkMonoString(String input) {
        if (!input.contains(StringMaster.NETCODE_SEPARATOR)) {
            return false;
        }
        main.system.auxiliary.LogMaster.log(1, "monostring: " + input);

        NetCode code = appropriateCode(input.split(StringMaster.NETCODE_SEPARATOR)[0]);

        if (code == null) {
            main.system.auxiliary.LogMaster.log(4, "NO NETCODE FOUND!"
                    + input.split(StringMaster.NETCODE_SEPARATOR)[0]);
            return false;
        }

        String INPUT = input.split(StringMaster.NETCODE_SEPARATOR)[1];

        handleInputCodeConcurrently(code);
        setLastReceivedCode(code);
        main.system.auxiliary.LogMaster.log(4, code + " - input for code: "
                + input.split(StringMaster.NETCODE_SEPARATOR)[1]);
        INPUT(INPUT, code); // waiting thread fail

        return true;
    }

    public boolean loop() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            if (in == null) {
                return false;
            }
            if (in.ready()) {
                try {
                    String input = in.readLine();
                    if (checkMonoString(input)) {
                        return true;
                    }
                    NetCode code = null;
                    try {
                        code = appropriateCode(input);
                    } catch (Exception e) {
                        handleInputConcurrently(input);
                    }
                    if (code == null) {
                        handleInputConcurrently(input);
                    } else {
                        handleInputConcurrently(code);
                        if (!code.isInputIrrelevant()) {
                            setLastReceivedCode(code);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void run() {
        while (status == CONNECTION_STATUS.CONNECTED) {
            if (!loop()) {
                break;
            }
        }
        try {
            kill();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public NetCode appropriateCode(String input) {
        if (getTHIS_CODES() == null) {
            setCODES();
        }
        for (NetCode c : getTHIS_CODES()) {
            if (c.name().equals(input)) {
                return c;
            }
        }

        return null;
    }

    public void setCODES() {
        setTHIS_CODES(new LinkedList<NetCode>((List<NetCode>) Arrays.asList(codestype
                .getEnumConstants())));
    }

    public void handleInputConcurrently(final NetCode code) {
        new Thread(new Runnable() {
            public void run() {
                handleInputCode(code);
            }
        }, " handleInputCode thread").start();
    }

    public void handleInputConcurrently(final String input) {
        new Thread(new Runnable() {
            public void run() {
                handleInput(input);
            }
        }, " handleInput thread").start();
    }

    public abstract void handleInput(String input);

    private void handleInputCodeConcurrently(final NetCode code) {
        new Thread(new Runnable() {
            public void run() {
                handleInputCode(code);
            }
        }, " handleInputCode thread").start();
    }

    public void CONCURRENT_INPUT_FOR_CODE(final String input, final NetCode code) {
        new Thread(new Runnable() {
            public void run() {
                INPUT(input, code);
            }
        }, " INPUT thread").start();
    }

    public void CONCURRENT_INPUT(final String input) {
        new Thread(new Runnable() {
            public void run() {
                INPUT(input);
            }
        }, " INPUT thread").start();
    }

    public void INPUT(String input) {
        INPUT(input, getLastReceivedCode());
    }

    public void INPUT(String input, NetCode code) {
        int i = 0;
        boolean result = false;
        while (i < 10) {
            if (WaitingThread.setINPUT(input, code)) {
                result = true;
                break;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
        if (!result) {
            main.system.auxiliary.LogMaster.log(0, "FAILED TO SET INPUT: " + input + " for "
                    + getLastReceivedCode().name());

            // create that waiting thread and go?!
        }

    }

    public void sendReply() {
        send(getLastReceivedCode());
    }

    public abstract void handleInputCode(NetCode codes);

    public void send(NetCode code) {
        send(code.toString());
    }

    public void send(NetCode code, String data) {
        if (data == null) {
            send(code.toString());
        } else {
            send(code.toString() + StringMaster.NETCODE_SEPARATOR + data);
        }

    }

    public synchronized void send(Object o) {
        if (out != null && o != null) {

            out.print(String.valueOf(o) + "\n");
            out.flush();
        }
    }

    public void initIO() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public PrintWriter getOut() {
        return out;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }

    public BufferedReader getIn() {
        return in;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }

    public CONNECTION_STATUS getStatus() {
        return status;
    }

    public void setStatus(CONNECTION_STATUS status) {
        this.status = status;
    }

    public void kill() throws IOException {
        Socket socket = getSocket();
        BufferedReader in = getIn();
        PrintWriter out = getOut();
        if (socket != null) {
            socket.close();
            socket = null;
        }

        if (in != null) {
            in.close();
            in = null;
        }

        if (out != null) {
            out.close();
            out = null;
        }
    }

    public NetCode getLastReceivedCode() {
        return lastReceivedCode;
    }

    public void setLastReceivedCode(NetCode lastReceivedCode) {
        this.lastReceivedCode = lastReceivedCode;

    }

    public void registerCode() {
        // if (!WaitingThread.getThreads().isEmpty())
        // if (!(WaitingThread.getRegisteredCodes().size() <
        // MAX_CODE_POOL_SIZE))
        WaitingThread.registerCode(lastReceivedCode);
    }

    public List<? extends NetCode> getTHIS_CODES() {
        return THIS_CODES;
    }

    public void setTHIS_CODES(List<NetCode> tHIS_CODES) {
        THIS_CODES = tHIS_CODES;
    }

    // setTHIS_CODES(new LinkedList<NetCode>(
    // (Collection<? extends NetCode>) EnumMaster
    // .getEnumConstants(NetCode.class)
    //
    // ));
    public void addCodes(List<? extends NetCode> newCodes) {

        List<NetCode> list = new LinkedList<NetCode>();
        for (NetCode code : getTHIS_CODES()) {
            list.add(code);
        }
        for (NetCode code : newCodes) {
            list.add(code);
        }

        setTHIS_CODES(list);
    }

    public enum CONNECTION_STATUS {
        CONNECTED, DISCONNECTED,
    }

}
