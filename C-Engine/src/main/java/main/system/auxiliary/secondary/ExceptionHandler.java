package main.system.auxiliary.secondary;

import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 8/7/2017.
 */
public class ExceptionHandler {

    public static  void handle(Exception e){
        if (CoreEngine.isExceptionTraceLogged())
        e.printStackTrace();
    }
}
