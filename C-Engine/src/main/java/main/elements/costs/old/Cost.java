package main.elements.costs.old;

import main.entity.Ref;

/**
 * Created with IntelliJ IDEA.
 * Date: 19.10.2016
 * Time: 19:28
 * To change this template use File | Settings | File Templates.
 */
public interface Cost {
    boolean isPaid();

    boolean pay(SoEObj payee, Ref ref);

    boolean canBePaid(Ref ref);

    Ref getRef();

    void setRef(Ref ref);
}
