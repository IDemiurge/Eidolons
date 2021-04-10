package eidolons.game.netherflame.dungeons.model.assembly;

import eidolons.game.netherflame.dungeons.QD_Enums;
import eidolons.game.netherflame.dungeons.model.QD_Module;
import main.system.data.DataUnit;

import java.util.Comparator;
import java.util.Random;
import java.util.Set;

public class QD_Picker implements Comparator<QD_Module> {
    protected int i, max, attempt;
    protected DataUnit<QD_Enums.FloorProperty> data;
    protected Set<String> permittedTypes, permittedElevation,
            permittedLocations, permittedEntrances, permittedSizes;
    QD_Module previous, next;

    public QD_Picker(Random random, int i, int max, int attempt, DataUnit<QD_Enums.FloorProperty> data, QD_Module previous, QD_Module next) {
        this.i = i;
        this.max = max;
        this.attempt = attempt;
        this.data = data;
        this.previous = previous;
        this.next = next;

        QD_ReqsFetcher fetcher = new QD_ReqsFetcher(this);
        permittedSizes = fetcher.getPermittedSizes();
        permittedElevation = fetcher.getPermittedElevation();
        permittedEntrances = fetcher.getPermittedEntrances();
        permittedLocations = fetcher.getPermittedLocations();
        permittedTypes = fetcher.getPermittedType();
    }

    private int getValue(QD_Module o1) {
        return 0;
    }

    public boolean check(QD_Module module) {
        if (checkContainer(module, permittedSizes, QD_Enums.ModuleProperty.size))
            if (checkContainer(module, permittedTypes, QD_Enums.ModuleProperty.type))
                if (checkContainer(module, permittedElevation, QD_Enums.ModuleProperty.elevation))
                    if (checkContainer(module, permittedEntrances, QD_Enums.ModuleProperty.dimension))
                        return checkContainer(module, permittedLocations, QD_Enums.ModuleProperty.location);
        return false;
    }

    private boolean checkContainer(QD_Module module, Set<String> permittedSizes, QD_Enums.ModuleProperty size) {
        return permittedSizes.contains(module.getData().getValue(size));
    }

    @Override
    public int compare(QD_Module o1, QD_Module o2) {
        if (getValue(o1) > getValue(o2)) {
            return 1;
        }
        return -1;
    }

}
