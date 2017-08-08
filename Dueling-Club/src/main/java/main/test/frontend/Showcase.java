package main.test.frontend;

import main.swing.generic.services.dialog.DialogMaster;

/**
 * Created by JustMe on 8/2/2017.
 */
public class Showcase {

    public static final String[] missions = {
     "Road Ambush",
     "A Walk among Tombstones",
     "The Ravenguard",
     "In Spider's Den",
     "The Tunnel",
         "Bone Temple",
    };

    public static final String[] launch_options = {
     "Road Ambush",
    };

    public static void main(String[] args) {
      int index=  DialogMaster.optionChoice(missions, "Choose mission to launch" );
        String[] args1 = {
         null, index + ""
        };
        ScenarioLauncher.main(args1);
    }
}
