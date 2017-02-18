package main.system.net;

import main.entity.Ref;
import main.entity.obj.Active;
import main.game.core.game.MicroGame;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.net.socket.GenericConnection;
import main.system.net.socket.ServerConnector.NetCode;

import java.util.Arrays;

public abstract class Communicator {

    protected static final String ACTIVATED_OBJ = "ACTIVATED_OBJ";
    protected static final String CMD_SEPARATOR = StringMaster.getAltPairSeparator();
    protected static final String ARG_SEPARATOR = StringMaster.getAltSeparator();
    protected MicroGame game;
    protected CMD_ARGS ID_ARG = CMD_ARGS.ID;
    protected CMD_ARGS REF_ARG = CMD_ARGS.REF;
    protected COMMAND ACTIVATE_COMMAND = COMMAND.ACTIVATE;
    protected COMMAND END_TURN_COMMAND = COMMAND.END_TURN;
    private GenericConnection connectionHandler;

    public Communicator(MicroGame game, GenericConnection handlerThread) {
        this.setConnectionHandler(handlerThread);
        this.game = game;

        init();
    }

    protected void init() {
        ID_ARG.setGame(game);
        REF_ARG.setGame(game);
        ACTIVATE_COMMAND.setArgs(new CMD_ARGS[]{ID_ARG, REF_ARG});

    }

    public void awaitCommand(NetCode commandCode) {
        LogMaster.log(4, "awaiting input for command: " + commandCode);
        // if (commandCode.isInputIrrelevant())

        if (!new WaitingThread(commandCode).waitForInput()) {
            LogMaster.log("NO INPUT EVER CAME! - " + commandCode);
            // TODO request to resend data!!!
            return;
        }
        String string = WaitingThread.getINPUT(commandCode);
        executeCommand(string);

    }

    protected void executeCommand(String string) {
        LogMaster.log("COMMAND: " + string);
        COMMAND cmd = COMMAND.valueOf(string.split(CMD_SEPARATOR)[0]);
        if (string.split(CMD_SEPARATOR).length == 1) {
            executeCommand(cmd, null);
            return;
        }
        String args = string.split(CMD_SEPARATOR)[1];
        executeCommand(cmd, args.split(ARG_SEPARATOR));

    }

    public abstract boolean executeCommand(COMMAND cmd, Object[] args);

    public abstract boolean transmitEndTurnCommand();

    public abstract boolean transmitMovementCommand(Integer id, String coordinates);

    public abstract boolean transmitCreationCommand(Active activeObj, Ref ref);

    /**
     * Ref transmission selective: TARGET:<ID>,GROUP:<ID;ID;ID> =>
     * getRef().setTarget(id) ...
     */
    public abstract boolean transmitActivateCommand(Active activeObj, Ref ref);

    protected Ref getRef(String string) {
        Ref ref = new Ref(string);
        ref.setGame(game);
        return ref;
    }

    public boolean activate(Active active, Ref ref) {
        LogMaster.log("Activating: " + active + "\nREF: " + ref);
        active.setRef(ref);
        return active.activate(false);
    }

    protected COMMAND getCommand(COMMAND cmd) {
        switch (cmd) {
            case ACTIVATE:
                return ACTIVATE_COMMAND;
            case END_TURN:
                return END_TURN_COMMAND;
            default:
                break;
        }
        return null;
    }

    public abstract void sendChoiceData(String input);

    public abstract String getChoiceData();

    public abstract void transmitCreateActionCommand(Integer id, Integer actN);

    public GenericConnection getConnectionHandler() {
        return connectionHandler;
    }

    public void setConnectionHandler(GenericConnection connectionHandler) {
        if (connectionHandler != null) {
            connectionHandler.addCodes(Arrays.asList(COMMAND.values()));
        }
        this.connectionHandler = connectionHandler;
    }

    public enum CMD_ARGS {
        REF {
            @Override
            public Object construct(Object... args) {
                return new Ref();

            }
        },
        ID {
            @Override
            public Object construct(Object... args) {
                Integer id = Integer.valueOf(args[0].toString());
                return getGame().getObjectById(id);

            }
        },;

        protected MicroGame game;

        public Object construct(Object... args) {
            return null;
        }

        public MicroGame getGame() {
            return game;
        }

        public void setGame(MicroGame game) {
            this.game = game;
        }
    }

    public enum COMMAND implements NetCode {
        /**
         * ID;REF
         */
        ACTIVATE(), END_TURN, MOVE, NEW_ACTION, DEBUG,;
        protected CMD_ARGS[] args;
        protected Class<?> returnClass;

        COMMAND() {

        }

        COMMAND(Class<?> returnClass, CMD_ARGS... args) {
            this.setArgs(args);
            this.setReturnClass(returnClass);
        }

        public Object execute(Object[] values) {
            return null;
        }

        public CMD_ARGS[] getArgs() {
            return args;
        }

        public void setArgs(CMD_ARGS[] args) {
            this.args = args;
        }

        public Class<?> getReturnClass() {
            return returnClass;
        }

        public void setReturnClass(Class<?> returnClass) {
            this.returnClass = returnClass;
        }

        @Override
        public boolean isInputIrrelevant() {
            // TODO Auto-generated method stub
            return false;
        }

    }

}
