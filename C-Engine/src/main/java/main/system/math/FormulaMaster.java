package main.system.math;

import main.entity.Ref;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 3/21/2017.
 */
public class FormulaMaster {
    private static List<String> failedFormulas = new ArrayList<>();

    public static List<String> getFailedFormulas() {
        return failedFormulas;
    }

    public static Integer getInt(String string, Ref ref) {
        return new Formula(string).getInt(ref);
    }
}
