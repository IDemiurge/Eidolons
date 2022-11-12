package content;

public class LOG {
    public static void log(Object... toLog) {
        StringBuilder builder = new StringBuilder();
        for (Object o : toLog) {
            builder.append(o.toString());
        }
        System.out.println(builder.toString());
    }
}
