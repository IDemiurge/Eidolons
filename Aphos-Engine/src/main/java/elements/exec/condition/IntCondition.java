package elements.exec.condition;

import elements.exec.EntityRef;

/**
 * Created by Alexander on 8/25/2023
 */
public class IntCondition extends ConditionImpl {
    public static final int GREATER = 0;
    public static final int EQUAL = 1;
    public static final int LESS = -1;

    private int mode = GREATER;
    private boolean strict = true;

    @Override
    protected boolean checkThis(EntityRef ref) {
        final int compared = ref.getMatch().getInt(data.getS("key"));
        int to = data.getInt("value");
        boolean result = switch(mode){
            case GREATER -> strict? compared > to : compared >= to  ;
            case EQUAL -> compared == to;
            case LESS -> strict? compared < to : compared <= to  ;
            default -> true;
        };
                //
        return result;
    }

    public IntCondition equal(){
        mode = EQUAL;
        return this;
    }
    public IntCondition orEqual(){
        strict = false;
        return this;
    }
    public IntCondition less(){
        mode = LESS;
        return this;
    }

    @Override
    public String[] getArgs() {
        return new String[]{
                "value",
                "key"
        };
    }
}
