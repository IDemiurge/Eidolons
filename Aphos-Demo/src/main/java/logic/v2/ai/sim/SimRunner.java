package logic.v2.ai.sim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexander on 1/21/2023
 *  This class should allow us to run a battle any number of times quickly and without side-effects, with
 *  very clears logs and parametrization.
 */
public class SimRunner {

    public static void main(String[] args) {
        //from args!
        SimParams parameters = extractParams(args);
        int runsRequired = 1; //parameters.
        boolean maxThreads= false;
        //use streams? run in parallel?
        for (int i = 0; i < runsRequired; i++) {
            new Simulation(parameters).run();

        }
    }

    private static SimParams extractParams(String[] args) {
         List<String> list = new ArrayList<>();
        Map<String, String> namedArgs = new HashMap<>();
        for (int j = 0; j < args.length; j += 2) {
            //rather split by :?
            namedArgs.put(args[j].toString().toLowerCase(), args[j + 1]);
        }
        return new SimParams(namedArgs) ;
    }

}
