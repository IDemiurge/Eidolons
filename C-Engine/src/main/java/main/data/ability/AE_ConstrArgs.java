package main.data.ability;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AE_ConstrArgs {
    String[] argNames();

}
