package main.elements.conditions;

import main.content.CONTENT_CONSTS.PRINCIPLES;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

public class PrinciplesCondition extends StringComparison {
    private Boolean exclusive;

    public PrinciplesCondition(String str1, String str2, Boolean exclusive) {
        super(str1, str2, true);
        this.exclusive = exclusive;
    }

    @Override
    public boolean check() {
        super.check();

        for (String s : StringMaster.openContainer(val1)) {
            for (String s2 : StringMaster.openContainer(val2)) {
                PRINCIPLES p1 = new EnumMaster<PRINCIPLES>().retrieveEnumConst(
                        PRINCIPLES.class, s);
                PRINCIPLES p2 = new EnumMaster<PRINCIPLES>().retrieveEnumConst(
                        PRINCIPLES.class, s2);
                if (exclusive) {
                    if (p1.getOpposite() == p2) {
                        return false;
                    }
                } else if (p1 == p2) {
                    return true;
                }
            }
        }

        return true;
    }

}
