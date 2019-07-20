package main.elements.conditions.standard;

import main.elements.conditions.MicroCondition;
import main.entity.Ref;

import java.util.function.Predicate;

public class LambdaCondition extends MicroCondition {
    Predicate<Ref> predicate;

    public LambdaCondition(Predicate<Ref> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean check(Ref ref) {
        return predicate.test(ref);
    }
}
