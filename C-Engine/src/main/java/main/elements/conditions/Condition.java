package main.elements.conditions;

import main.entity.Entity;
import main.entity.Ref;

import java.io.Serializable;

public interface Condition extends Serializable {

    boolean preCheck(Ref ref);

    boolean check(Entity source, Entity match);

    boolean check(Ref ref);

    String getTooltip();

    boolean check(Entity match);

    Condition join(Condition condition);

    boolean isTrue();

    void setXml(String xml);

    String toXml();
}
