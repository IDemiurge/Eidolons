package main.elements.targeting;

import main.data.ability.AE_ConstrArgs;
import main.entity.Ref;
import main.entity.Ref.KEYS;

public class FixedTargeting extends TargetingImpl {
    private String keyword;
    private boolean group;

    public FixedTargeting() {
        this(KEYS.SOURCE);
    }
    public FixedTargeting(KEYS keyword) {
        this.keyword = keyword.name();
    }

    public FixedTargeting(String keyString) {
        this.keyword = keyString;
    }

    @AE_ConstrArgs(argNames = {"group?"})
    public FixedTargeting(Boolean group) {
        this.group = group;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public boolean select(Ref ref) {

        if (group) {
            // ref.setGroupTargeting(true);
            return true;
        }
        if (ref.getId(keyword) != null) {
            if (ref.getId(keyword) instanceof Integer) {
                ref.setTarget(ref.getId(keyword));
                return true;
            } else {
                main.system.auxiliary.LogMaster.log(1,
                        "keyword returned non-Integer: " + keyword);

                return false;
            }

        } else {
            main.system.auxiliary.LogMaster.log(1, "keyword returned null: "
                    + keyword);
            return false;
        }

    }
}