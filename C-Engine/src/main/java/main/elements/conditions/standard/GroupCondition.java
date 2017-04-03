package main.elements.conditions.standard;

import main.data.ability.OmittedConstructor;
import main.elements.conditions.ConditionImpl;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.group.GroupImpl;

public class GroupCondition extends ConditionImpl {

    boolean event;
    private String OBJ_REF;
    private GroupImpl group;

    @OmittedConstructor
    public GroupCondition(KEYS OBJ_REF, GroupImpl group) {
        this.group = group;
        this.OBJ_REF = OBJ_REF.toString();
    }

    @OmittedConstructor
    public GroupCondition(String OBJ_REF, GroupImpl group) {
        this.group = group;
        this.OBJ_REF = OBJ_REF;
    }

    public GroupCondition(String OBJ_REF, Boolean event) {
        this.OBJ_REF = OBJ_REF;
        this.event = event;
    }

    public GroupCondition(String OBJ_REF) {
        this.OBJ_REF = OBJ_REF;
    }

    @Override
    public boolean check(Ref ref) {
        Ref REF = ref;
        if (event) {
            REF = ref.getEvent().getRef();
        }
        if (group == null) {
            group = REF.getGroup();
        }
        if (group == null) {
            return false;
        }
        return group.getObjectIds().contains(REF.getId(OBJ_REF));
    }

}
