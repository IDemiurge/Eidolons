package main.data.ability;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/*
use to give names visible in Arcane Vault (AE)
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AE_ConstrArgs {
    String[] argNames();

}
