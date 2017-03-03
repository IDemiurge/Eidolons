package main.client.net;

import main.client.net.GameConnector.HOST_CLIENT_CODES;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.Active;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlefield.Coordinates;
import main.game.core.game.DC_Game;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.net.Communicator;
import main.system.net.WaitingThread;
import main.test.debug.DebugMaster.DEBUG_FUNCTIONS;

public class DC_Communicator extends Communicator {

    public DC_Communicator(DC_Game game) {
        super(game, game.getConnection());
    }

    public void transmitDebugFunction(DEBUG_FUNCTIONS func, String data) {
        getConnectionHandler().send(COMMAND.DEBUG + StringMaster.NETCODE_SEPARATOR + data);

    }

    @Override
    public boolean transmitActivateCommand(Active active, Ref ref) {
        DC_ActiveObj activeObj = (DC_ActiveObj) active;
        String data = COMMAND.ACTIVATE.name() + CMD_SEPARATOR + ACTIVATED_OBJ
                + StringMaster.getPairSeparator() + activeObj.getId() + ARG_SEPARATOR
                + ref.getData();
        LogMaster.log(2, "command data sent: " + data);
        getConnectionHandler().send(COMMAND.ACTIVATE + StringMaster.NETCODE_SEPARATOR + data);
        // check response?
        return false;
    }

    @Override
    public void transmitCreateActionCommand(Integer id, Integer actionNumber) {
        String data = COMMAND.NEW_ACTION.name() + CMD_SEPARATOR + id + ARG_SEPARATOR + actionNumber;
        getConnectionHandler().send(COMMAND.NEW_ACTION + StringMaster.NETCODE_SEPARATOR + data);
        LogMaster.log(2, "new action command sent: " + data);

    }

    @Override
    public boolean transmitEndTurnCommand() {
        if (getConnectionHandler() == null) {
            return false;
        }
        getConnectionHandler().send(
                COMMAND.END_TURN + StringMaster.NETCODE_SEPARATOR + COMMAND.END_TURN);
        LogMaster.log(1, "end turn command sent!");
        return false;
    }

    @Override
    public boolean transmitMovementCommand(Integer id, String coordinates) {
        String data = COMMAND.MOVE.name() + CMD_SEPARATOR + id + ARG_SEPARATOR + coordinates;
        LogMaster.log(2, "move command sent: " + id + " to " + coordinates);
        getConnectionHandler().send(COMMAND.MOVE + StringMaster.NETCODE_SEPARATOR + data);
        return false;
    }

    public boolean executeCommand(COMMAND cmd, Object[] args) {

        switch (cmd) {
            case DEBUG:
                executeDebugFunction(args);
                break;
            case ACTIVATE:
                activate(args);
                break;
            case END_TURN:
                endTurn();
                break;
            case MOVE: // TODO DEPRECATED!!!
                move(args);
                break;
            case NEW_ACTION:
                newAction(args);
                break;
            default:
                break;
        }
        return false;

    }

    public DC_Game getGame() {
        return (DC_Game) game;
    }

    private void executeDebugFunction(Object[] args) {
//        for (Object arg : args) {
//            if (StringMaster.getPrefix(arg.toString()).equals(REF))
//                getGame().getDebugMaster().setRef(getRef(arg.toString()));
//            getGame().getDebugMaster().setData(arg.toString());
//            getGame().getDebugMaster().setInfoObj(getObj(arg.toString()));
//        }
//        getGame().getDebugMaster().executeDebugFunction(func);
    }

    @Override
    public void sendChoiceData(String input) {
        getConnectionHandler().send(HOST_CLIENT_CODES.CUSTOM_PICK, input);

    }

    @Override
    public String getChoiceData() {
        return WaitingThread.waitOrGetInput(HOST_CLIENT_CODES.CUSTOM_PICK);
    }

    private void newAction(Object[] args) {
        Unit sourceObj = (Unit) game.getObjectById(Integer.valueOf(args[0].toString()));
        int actionNumber = Integer.valueOf(args[1].toString());
        // ActionType actionType = (ActionType) game.getActionManager()
        // .getUnitActions(sourceObj).getOrCreate(actionNumber);

        // game.getActionManager().newAction(actionType, Ref.getCopy(sourceObj
        // .getRef()), sourceObj.getOwner(), game);

    }

    @Override
    public boolean transmitCreationCommand(Active activeObj, Ref ref) {
        // TODO Auto-generated method stub
        return false;
    }

    private void activate(Object[] args) {
        Active active = getActive(args[0].toString());
        if (active instanceof DC_ActiveObj) {
            DC_ActiveObj activeObj = (DC_ActiveObj) active;
            activeObj.construct();
        }
        activate(active, getRef((String) args[1]));
    }

    protected Active getActive(String idString) {
        Integer id;
        if (idString.contains(StringMaster.getPairSeparator())) {
            id = Integer.valueOf(idString.split(StringMaster.getPairSeparator())[1]);
        } else {
            id = Integer.valueOf(idString);
        }
        Active active = (Active) game.getObjectById(id);
        return active;
    }

    private void move(Object[] args) {
        Integer id = Integer.valueOf(args[0] + "");
        Obj obj = game.getObjectById(id);
        Coordinates c = new Coordinates(args[1].toString());
        c.invert();
        move(obj, c);
    }

    private void move(Obj obj, Coordinates c) {
        // game.getCellByCoordinates(c);
        // game.getManager().
        game.getMovementManager().move(obj, c);
    }

    private void endTurn() {
        game.getManager().endTurn();

    }

}
