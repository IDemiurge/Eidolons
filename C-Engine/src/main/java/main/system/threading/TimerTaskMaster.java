package main.system.threading;

import main.system.auxiliary.log.LogMaster;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

public class TimerTaskMaster {

    public static Timer newTimer(TimerTask task, long period) {
        Timer timer = new Timer();
        timer.schedule(task, period, period);
        return timer;
    }
        public static Timer newTimer(Object object, final String methodName, Class<?>[] params,
        final Object[] args, long period) {

        Timer timer = new Timer();
        try {
            final Method method = object.getClass().getMethod(methodName, params);
            if (method == null) {
                LogMaster.log(1, "*** Timer cannot find method: "
                 + methodName);
                return null;
            }
            LogMaster.log(1, "Timer started for " + methodName);

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    try {
//                        LogMaster.log(0, "Invoking " + methodName);
                        if (args == null) {
                            method.invoke(null);
                        } else {
                            method.invoke(args);
                        }
                    } catch (IllegalAccessException | IllegalArgumentException
                     | InvocationTargetException e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
                }

            };

            newTimer(task, period);

        } catch (NoSuchMethodException | SecurityException e) {
            main.system.ExceptionMaster.printStackTrace(e);
            return timer;
        }
        return timer;
    }

}
