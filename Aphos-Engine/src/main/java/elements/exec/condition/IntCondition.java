package elements.exec.condition;

import elements.exec.EntityRef;

/**
 * Created by Alexander on 8/25/2023
 */
public class IntCondition extends ConditionImpl {
    @Override
    protected boolean checkThis(EntityRef ref) {
        // data.getB("equal")
        // data.getB("equal")
        int value = data.getInt("value");
        boolean result = ref.getMatch().getInt(data.getS("key")) > value;
        return result;
    }

    @Override
    public String[] getArgs() {
        return new String[]{
                "value",
                "key"
        };
    }

    // public Entity getMatch(){
//         return context.get();
// }
//when checking without preset ref? yeah, where is that condition used?!
}
