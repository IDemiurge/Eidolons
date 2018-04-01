package eidolons.libgdx;

import eidolons.libgdx.screens.ScreenData;

public class OnEngineFail {
    private boolean isCritical; //if true rollback not required
    private ScreenData rollbackTo;
    private String message;

    public OnEngineFail(boolean isCritical, ScreenData rollbackTo, String message) {
        this.isCritical = isCritical;
        this.rollbackTo = rollbackTo;
        this.message = message;
    }

    public OnEngineFail(ScreenData rollbackTo, String message) {
        this.rollbackTo = rollbackTo;
        this.message = message;
        isCritical = false;
    }

    public boolean isCritical() {
        return isCritical;
    }

    public ScreenData getRollbackTo() {
        return rollbackTo;
    }

    public String getMessage() {
        return message;
    }
}
