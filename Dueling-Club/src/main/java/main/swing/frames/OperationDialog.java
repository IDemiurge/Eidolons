package main.swing.frames;

import main.entity.obj.unit.Unit;

/**
 * Created by JustMe on 3/25/2017.
 */
public interface OperationDialog {
    String getPoolTooltip();

    void refresh();

    void open();

    void done();

    String getOperationsData();

    void cancel();

    String getPoolText();

    Unit getHero();

    void setHero(Unit hero);

    int getNumberOfOperations();

    void setNumberOfOperations(int nOfOperations);
}
