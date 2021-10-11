package framework;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;

public class TestResultTable {

    List<Pair> resultRows =     new LinkedList<>() ;
    private String name;

    public TestResultTable(String name) {
        this.name = name;
    }

    public void add(String key, Object result) {
        resultRows.add(new ImmutablePair<>(key, result));
    }

    public List<Pair> getResultRows() {
        return resultRows;
    }

    public String getName() {
        return name;
    }

}
