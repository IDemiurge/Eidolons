package logic.v2.ai.sim;

import gdx.dto.LaneDto;
import logic.v2.ai.adapter.GameAdapter;
import logic.v2.ai.single.SingleAi;

/**
 * Created by Alexander on 1/21/2023
 *
 */
public class Simulation {
    private SimParams parameters;
    private GameAdapter adapter;

    public Simulation(SimParams parameters) {
        this.parameters = parameters;
    }

    public void run() {
       adapter = new GameAdapter();
        // new SimFileLogger();
        // on what level does MOCK come in? Game?
        //in real time - so it inevitably starts a new thread
        // wait for result to be set into a variable?
        // create logger - for file, console and maybe something else
    }

    public SimParams getParameters() {
        return parameters;
    }

    public GameAdapter getAdapter() {
        return adapter;
    }

    public void addAi(SingleAi singleAi) {

    }
}
