package main.system;

import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Created by JustMe on 7/24/2018.
 */
public class PathUtils {
    private static final String PATH_SEPARATOR = System.getProperty("file.separator");
    public static final String PATH_SEPARATOR_UNIVERSAL = "/";

    public static String getPathSeparator() {
        return PATH_SEPARATOR;
    }

    public static List<String> splitPath(String path) {
        if (path.charAt(0) == '/') {
            path = path.substring(1);
        }
        List<String> segments = null;
        if (path.contains("/")) {
            path =   path.replace("/", PATH_SEPARATOR);
        }
        if (segments == null)
            segments =      Arrays.asList(path.split(Pattern.quote(PATH_SEPARATOR)));

        segments = new ArrayList<>(segments);
        segments.removeIf(s -> s.isEmpty());

        return segments;
    }

    public static List<String> getPathSegments(String path) {
        return splitPath(path);
    }

    public static String buildPath(String... strings) {
        String result = "";
        for (String s : strings) {
            result += s + PATH_SEPARATOR;
        }
        return result.substring(0, result.length() - 1);
    }

    public static String getSegment(int i, String str, String delimiter) {
        String[] array = str.split(delimiter);
        if (array.length < i) {
            return null;
        }
        return array[i];
    }

    public static String removePreviousPathSegments(String string, String path) {
        String p = string.toLowerCase();

        path = path.toLowerCase();
        String prefix = "";
        if (p.contains(path)) {
            prefix = path;
        } else {
            for (String sub : getPathSegments(path)) {
                if (p.contains(path)) {
                    prefix = path;
                    break;
                }

                path = StringMaster.cropFirstSegment(path, PATH_SEPARATOR);

            }
        }


//        for (String sub : getPathSegments(path)) {
//            if (portrait.startsWith(sub)) {
//                break;
//            }
//            prefix += sub + PATH_SEPARATOR;
//        }


//        if (string.startsWith(PATH_SEPARATOR)) {
//            string = string.substring(1);
//        }
//        final String portrait = string.toLowerCase();
//        path = path.toLowerCase();
//        final List<String> segments = getPathSegments(path);
//        String prefix = buildPartsIf(segments,
//         PATH_SEPARATOR, false, (String sub) ->
//          getPathSegments(portrait).indexOf(sub) < 0
//           ||
//           getPathSegments(portrait).indexOf(sub) < segments.indexOf(sub)
//         //TODO THIS IS NOT GUARANTEED TO WORK!!
//
//        );

        return p.replace(prefix, "");

    }

    public static String addMissingPathSegments(String string, String path) {
        final String p = string.toLowerCase();
        path = path.toLowerCase();
        String prefix = buildPartsIf(getPathSegments(path),
         PATH_SEPARATOR, true, (String sub) -> p.startsWith(sub));

        return prefix + p;
    }

    private static String buildPartsIf(List<String> segments,
                                       String separator,
                                       boolean breakOnFalse,
                                       Predicate<String> predicate) {
        StringBuilder builder = new StringBuilder(50);
        for (String sub : segments) {
            if (sub.isEmpty()) continue;
            if (predicate.test(sub)) {
                if (breakOnFalse) {
                    break;
                } else {
                    continue;
                }
            }
            builder.append(sub + separator);
        }
        return builder.toString();
    }

    public static String getUniversalPathSeparator() {
        return PATH_SEPARATOR_UNIVERSAL;
    }

    public static String getLastPathSegment(String path) {
        LinkedList<String> segments = new LinkedList<>(splitPath(path));
        return segments.getLast();
    }

    public static String cropLastPathSegment(String path, boolean cropPathSeparator) {
        String cropped = StringMaster.replaceLast(path, getLastPathSegment(path), "");
        if (!cropPathSeparator) {
            return cropped;
        }
        return StringMaster.replaceLast(cropped, "/", "");
    }

    public static String cropLastPathSegment(String path) {
        return StringMaster.replaceLast(path, getLastPathSegment(path), "");
    }
}
