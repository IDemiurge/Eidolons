package main.elements.conditions;

import main.entity.Ref;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;

public class StringContainersComparison extends StringComparison {

    private boolean negative;
    private boolean strictContents;

    public StringContainersComparison(String str1, String str2, Boolean negative) {
        this(str1, str2);
        this.negative = negative;
    }

    public StringContainersComparison(String str1, String str2) {
        super(str1, str2, true);
    }

    public StringContainersComparison(Boolean strict, String str1, String str2,
                                      Boolean negative) {
        this(str1, str2, negative);
        this.strictContents = strict;
    }

    @Override
    public boolean check(Ref ref) {

        if (super.check(ref)) {
            return !negative;
        }
        if (StringMaster.isEmpty(val1)) {
            return negative;
        }
        if (StringMaster.isEmpty(val2)) {
            return !negative;
        }
        boolean result = !negative & !strictContents;
        // result = StringMaster.compareContainers(val1, val2, strictContents);

        for (String s1 : ContainerUtils.open(val1)) {
            for (String s : ContainerUtils.open(val2)) {
                result = negative;
                if (strictContents) {
                    if (StringMaster.compareByChar(s1, s, strictContents)) {
                        return !negative;
                    }
                }

                if (StringMaster.compare(s1, s, strictContents)) {
                    return !negative;
                }
            }
        }
        return result;

    }
}
