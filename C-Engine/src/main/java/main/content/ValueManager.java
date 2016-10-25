package main.content;

import main.content.parameters.PARAMETER;

public interface ValueManager {

    boolean checkValueGroup(String sparam);

    PARAMETER[] getValueGroupParams(String sparam);

    PARAMETER[] getParamsFromContainer(String sparam);

    boolean isRolledRoundind(PARAMETER valueToPay);
}
