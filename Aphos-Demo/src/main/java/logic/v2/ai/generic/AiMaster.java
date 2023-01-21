package logic.v2.ai.generic;

import logic.v2.ai.adapter.GameAdapter;
import logic.v2.ai.ipc.IpcHandler;
import logic.v2.ai.math.AiMath;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 1/21/2023
 */
public class AiMaster {
    private final List<AiHandler> list = new ArrayList<>();
    private final GameAdapter adapter;
    private final IpcHandler ipc;
    private AiMath math;

    public AiMaster(GameAdapter adapter) {
        this.adapter = adapter;
        list.add(ipc=new IpcHandler(this));
    }

    public IpcHandler getIpcHandler() {
        return ipc;
    }

    public AiMath getMath() {
        return math;
    }

}
