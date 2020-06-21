package main.system.auxiliary;

import main.content.values.properties.G_PROPS;
import main.entity.Entity;
import main.system.auxiliary.data.ListMaster;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 7/24/2018.
 */
public class ContainerUtils {
    public static boolean checkContainer(String container, String string) {
        return checkContainer(container, string, false);
    }

    public static boolean checkContainer(String container, String string, boolean strict) {
        for (String s1 : open(container)) {
            if (StringMaster.compareByChar(s1, string, strict)) {
                return true;
            }
        }
        return false;

    }

    public static List<String> split(String subString, String delimiter) {
        return split(subString, delimiter, true);

    }

    public static List<String> split(String containerString, String delimiter, boolean strict) {
        if (StringMaster.isEmpty(containerString)) {
            return Collections.emptyList();
        }
        if (!containerString.contains(delimiter)) {
            if (strict) {
                ArrayList<String> ArrayList = new ArrayList<>();
                ArrayList.add(containerString);
                return ArrayList;
            } else {
                delimiter = delimiter.toUpperCase();
                if (!containerString.contains(delimiter)) {
                    delimiter = delimiter.toLowerCase();
                }
                if (!containerString.contains(delimiter)) {
                    delimiter = StringMaster.format(delimiter);
                }
            }
        }
        List<String> list = new ArrayList<>(Arrays
                .asList(containerString.split(Pattern.quote(delimiter))));
        list.removeIf(s -> StringMaster.isEmpty(s));
        return list;
    }

    public static List<String> openContainer(String containerString, String separator) {
        return split(containerString, separator);
    }

    public static List<String> openFormattedContainer(String containerString) {
        return split(containerString, getFormattedContainerSeparator());
    }

    public static String[] open(String containerString) {
        return open(containerString, StringMaster.getSeparator());
    }

    public static String[] open(String containerString, String separator) {
        if (containerString == null)
            return new String[]{""};

        String[] array = containerString.split(Pattern.quote(separator));
        int n = 0;
        for (String sub : array) {
            if (!sub.isEmpty())
                n++;
        }
        String[] result = new String[n];
        n = 0;
        for (String sub : array) {
            if (!sub.isEmpty()) {
                result[n] = sub;
                n++;
            }
        }

        return result;
    }

    public static List<String> openContainer(String containerString) {
        return openContainer(containerString, StringMaster.CONTAINER_SEPARATOR);
    }

    public static String getContainerSeparator() {
        return StringMaster.getSeparator();
    }

    public static void formatList(List<String> listData) {
        int i = 0;
        for (String item : listData) {

            listData.set(i, StringMaster.format(item));
            i++;
        }
    }

    public static void formatResList(List<String> listData) {
        int i = 0;
        for (String item : listData) {
            item = item.substring(item.indexOf("/", item.length()));
            listData.set(i, item);
            i++;
        }
    }

    public static String joinStringList(Collection<String> list, String divider, boolean cropLastDivider) {
        if (list == null) {
            return "";
        }
        if (list.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();

        for (String str : list) {
            if (str != null)
                builder.append(str).append(divider);
        }
        String result = builder.toString();
        return (cropLastDivider) ? result.substring(0, result.lastIndexOf(divider)) : result;
    }

    public static String joinArray(String s, Object[] args) {
        return joinStringList(ListMaster.toStringList(args), s, true);
    }

    public static String join(String s, Collection c) {
        return c.stream().map(Object::toString).collect(Collectors.joining(s)).toString();
    }
    public static String join(String s, String... parts) {
        return joinStringList(new ArrayList<>(Arrays.asList(parts)), s, true);
    }

    public static String joinList(List list) {
        return joinList(list, StringMaster.SEPARATOR);
    }

    public static String joinList(List list, String divider) {
        return joinStringList(convertToStringList(list), divider);
    }

    public static String joinStringList(Collection<String> list, String divider) {
        return joinStringList(list, divider, true);
    }

    public static String constructContainer(Collection<String> list) {
        return joinStringList(list, getContainerSeparator(), false);
    }

    public static String construct(String separator, String... parts) {
        return constructStringContainer(Arrays.asList(parts), separator);

    }

    public static String constructStringContainer(Collection<?> list) {
        return constructStringContainer(list, getContainerSeparator());
    }

    public static String toStringContainer(Collection<?> list) {
        return toStringContainer(list, getContainerSeparator());
    }

    public static String toStringContainer(Collection<?> list, String divider) {
        return joinStringList(list.stream().map(obj -> obj.toString()).collect(Collectors.toList())
                , divider);
    }

    public static String constructStringContainer(Collection<?> list, String separator) {
        return joinStringList(ListMaster.toStringList(list.toArray()), separator, false);
    }

    public static String constructStringContainer(boolean cropLast, Collection<?> list, String separator) {
        return joinStringList(ListMaster.toStringList(list.toArray()), separator, cropLast);
    }
    public static String constructEntityNameContainer(Collection<? extends Entity> list

    ) {
        return joinStringList(ListMaster.toStringList(true, list.toArray()),
                getContainerSeparator(), false);
    }

    public static List<String> convertToStringList(Collection<?> values) {
        List<String> list = new ArrayList<>();
        for (Object object : values) {
            if (object != null) {
                list.add(object.toString());
            }
        }

        return list;
    }

    public static List<String> convertToIdList(Collection<?> list) {

        List<String> idList = new ArrayList<>();
        if (list != null) {
            for (Object object : list) {
                if (object != null) {
                    idList.add(((Entity) object).getId() + "");
                }
            }
        }

        return idList;
    }

    public static List<String> toNameList(List<? extends Entity> list) {
        return toNameList(false, list);
    }

    public static List<String> toNameList(boolean base, List<? extends Entity> list) {
        List<String> nameList = new ArrayList<>();
        if (list != null) {
            for (Entity object : list) {
                if (object != null) {
                    nameList.add(base ? object.getProperty(G_PROPS.NAME, true) : object.getName());
                }
            }
        }
        return nameList;
    }

    public static Collection<Integer> convertToIdIntList(Collection<? extends Entity> list) {
        List<Integer> idList = new ArrayList<>();
        for (Entity object : list) {
            if (object != null) {
                idList.add(object.getId());
            }
        }

        return idList;
    }

    public static String getFormattedContainerString(String container) {
        if (container.endsWith(getContainerSeparator())) {
            container = container.substring(0, container.length() - 1);
        }
        return container.replace(getContainerSeparator(),
                getFormattedContainerSeparator());
    }

    public static String getFormattedContainerSeparator() {
        return ", ";
    }

    public static String build(Object... strings) {
        return build(false, strings);
    }

    public static String build(List<String> list) {
        return build(list.toArray(new String[0]));
    }

    public static String build(boolean whitespaces, Object... strings) {
        StringBuilder builder = new StringBuilder();
        Arrays.stream(strings).forEach(s -> {
            builder.append(s);
            if (whitespaces)
                builder.append(" ");
        });
        return builder.toString();
    }

    public static String toIdContainer(Collection<? extends Entity> list) {
        return constructContainer(convertToIdList(list));
    }
}
