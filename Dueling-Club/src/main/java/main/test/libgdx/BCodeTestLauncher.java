package main.test.libgdx;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Date: 04.11.2016
 * Time: 20:34
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class BCodeTestLauncher {
    public static void main(String[] args) {
        final List<Integer> a = new ArrayList<>();
        a.add(1);
        add(a);
        List<Integer> b = new ArrayList<>();
        b.clear();
    }

    private static int add(final List<Integer> a) {
        return a.size() + 20;
    }
}
