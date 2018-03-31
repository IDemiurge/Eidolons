package eidolons.game.battlecraft.ai.elements;

public class UnitCommand {
    private COMMAND_TYPE commandType;
    private Object[] args;

    public UnitCommand(COMMAND_TYPE commandType, Object... args) {
        // communicator related...
        this.setCommandType(commandType);
        this.setArgs(args);

    }

    public COMMAND_TYPE getCommandType() {
        return commandType;
    }

    public void setCommandType(COMMAND_TYPE commandType) {
        this.commandType = commandType;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public enum COMMAND_TYPE {
        ACTIVATE

    }
}
