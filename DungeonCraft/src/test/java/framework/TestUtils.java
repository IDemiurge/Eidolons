package framework;

import main.content.enums.GenericEnums;
import org.apache.commons.lang3.tuple.Pair;

public class TestUtils {

    public static void resultsAsTable(TestResultTable table) {
        StringBuilder builder = new StringBuilder(table.getName());
        int n = 0;
        for (Pair resultRow : table.getResultRows()) {
            StringBuilder rowBuilder = new StringBuilder();
            rowBuilder.append("|").append(n++).append(" Result >> ").append(resultRow.getKey())
                    .append(": ").append(resultRow.getValue())
                    .append("\n");
            // maxLength = Math.max(maxLength, rowBuilder.length());
            builder.append(rowBuilder.toString()).append("\n");
        }
    }

}
