package main.system.auxiliary;

import main.system.PathUtils;

import java.util.List;

/**
 * Created by JustMe on 8/18/2017.
 */
public class StrPathBuilder {
    StringBuilder builder;

    public StrPathBuilder(String... parts) {
        builder = new StringBuilder();
        if (parts.length > 0)
            build_(parts);
    }

    public static String build(Object... strings) {
        return _build(true, strings);

    }

    public static String _build(boolean cropLast, Object... strings) {
        return new StrPathBuilder().build_(cropLast, strings);
    }

    public static String build(List<String> list) {
        return build(list.toArray(new String[list.size()]));
    }

    public StringBuilder append(String str) {
        if (str == null) return builder;
        return builder.append(str + PathUtils.getPathSeparator());
    }

    public String build_(Object... strings) {
        return build_(true, strings);
    }

    public String build_(boolean cropLast, Object... strings) {
        for (Object o : strings) {
            String s = o.toString();
            if (s.endsWith(PathUtils.getPathSeparator()))
                builder.append(s);
            else
                builder.append(s + PathUtils.getPathSeparator());
        }
        String result = builder.toString();
        if (cropLast)
            result = result.substring(0, result.length() - 1);
        return result;
    }

    public StringBuilder getBuilder() {
        return builder;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
