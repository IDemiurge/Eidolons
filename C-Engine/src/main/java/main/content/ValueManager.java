package main.content;

import main.content.values.parameters.PARAMETER;

public interface ValueManager {

    boolean checkValueGroup(String sparam);

    PARAMETER[] getValueGroupParams(String sparam);

    PARAMETER[] getParamsFromContainer(String sparam);

}
