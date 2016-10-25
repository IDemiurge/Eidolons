package main.entity.type;

import main.elements.costs.Costs;
import main.entity.obj.Active;
import main.entity.obj.Obj;

public abstract class ActiveObjType extends ObjType implements Active {

    protected Costs costs;
    protected Obj ownerObj;

    public ActiveObjType() {

    }

    public ActiveObjType(ObjType type) {
        super(type);
    }

    public Costs getCosts() {
        return costs;
    }

    public void setCosts(Costs costs) {
        this.costs = costs;
    }

    public Obj getOwnerObj() {
        return ownerObj;
    }

    public void setOwnerObj(Obj ownerObj) {
        this.ownerObj = ownerObj;
    }

}
