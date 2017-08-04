package main.test.frontend;

import main.swing.generic.services.dialog.DialogMaster;

/**
 * Created by JustMe on 8/2/2017.
 */
public class Showcase {


    public static void main(String[] args) {
        String[] missions = {
         "1",
        };
      int index=  DialogMaster.optionChoice(missions, "Choose mission to launch" );
        String[] args1 = {
         null, index + ""
        };
        ScenarioLauncher.main(args1);
    }
}
