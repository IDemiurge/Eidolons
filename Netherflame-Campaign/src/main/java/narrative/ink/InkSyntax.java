package narrative.ink;

/**
 * Created by JustMe on 11/20/2018.
 * LIST DoctorsInSurgery = (Adams), Bernard, (Cartwright), Denver, Eamonn

 {LIST_COUNT(DoctorsInSurgery)} 	//  "2"
 {LIST_MIN(DoctorsInSurgery)} 		//  "Adams"
 {LIST_MAX(DoctorsInSurgery)} 		//  "Cartwright"
 */
public class InkSyntax {
    public static final String VAR_OPERATION = "~";
    public static final String SHUFFLE = "~";
    public static final String CYCLE = "&";
    public static final String ONCE = "!";
    public static final String TAG = "#";

    public static final String KNOT = "==";
    public static final String STITCH = "=";
    public static final String LABEL_OPEN = "(";
    public static final String LABEL_CLOSE = ")";

    public static final String SEQUENCE_VAR_OPEN = "{";
    public static final String SEQUENCE_VAR_CLOSE = "{";
    public static final String SILENCE_OPEN = "[";
    public static final String SILENCE_CLOSE = "]";
    public static final String INCLUDE= "INCLUDE";
    public static final String VAR= "VAR";
    public static final String LIST= "LIST";


    public static final String ACTOR_SEPARATOR = "::";
    public static final String SCRIPT_REF_SEPARATOR = "__";
    public static final String VALUE_MOD_SEPARATOR = "[[";
    public static final String REF_SEPARATOR = "[[[";


}
