package logic.v2.ai.ipc;

import logic.v2.ai.generic.AiConsts;
import logic.v2.ai.generic.AiHandler;
import logic.v2.ai.generic.AiMaster;
import logic.v2.ai.single.SingleAi;

/**
 * Created by Alexander on 1/21/2023
 */
public class IpcHandler extends AiHandler {

    public IpcHandler(AiMaster master) {
        super(master);
    }

    public AiConsts.AiMode getIpcMode(SingleAi ai) {
      master.getMath().get("");
        ai.getMoods();
        ai.getRoleModel();
        /*
        compare some separate numbers?
        or delegate fully to MATH?
         */
    }
}
