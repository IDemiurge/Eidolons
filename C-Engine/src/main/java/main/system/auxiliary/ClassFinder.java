package main.system.auxiliary;

import main.data.filesys.PathFinder;
import main.system.auxiliary.data.FileManager;
import main.system.launch.Flags;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassFinder {
    private static String[] ignoredpaths;

    /**
     * Recursive method used to find all classes in a given directory and
     * subdirs.
     *
     * @param directory
     *            The base directory
     * @param packageName
     *            The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */

    /**
     * Scans all classes accessible from the context class loader which belong
     * to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static Class[] getClasses(String packageName)
     throws ClassNotFoundException, IOException {
        if (Flags.isJar()) {
            return getClassesFromJar(packageName);
        }
        ClassLoader classLoader = Thread.currentThread()
         .getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(FileManager.getFile(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[0]);
    }

    private static Class[] getClassesFromJar(String packageName) {
        String pathToJar = PathFinder.getJarPath();

        List<Class> classes = new ArrayList<>();
        try {
            JarFile jarFile = new JarFile(pathToJar);
            Enumeration<JarEntry> e = jarFile.entries();

            URL[] urls = {new URL("jar:file:" + pathToJar + "!/")};
            URLClassLoader cl = URLClassLoader.newInstance(urls);

            while (e.hasMoreElements()) {
                JarEntry je = e.nextElement();
                if (je.isDirectory() || !je.getName().endsWith(".class")) {
                    //EA check - we could do it offline or something... feels hacky!
                    continue;
                }

                if (!je.getName().startsWith("main")
                 && !je.getName().startsWith("eidolons")) {
                    continue;
                }
                String className = je.getName().replace('/', '.');
                if (!className.contains(packageName)) {
                    continue;
                }
                // -6 because of .class
                className = className.substring(0, je.getName().length() - 6);

                Class c = cl.loadClass(className);
                System.out.println(packageName+ "- Class found: " +c.getName());
                classes.add(c);
            }
        } catch (IOException e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } catch (ClassNotFoundException e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

        System.out.println(classes.size()+ " classes found: " +classes);
        return classes.toArray(new Class[0]);
    }

    /**
     * Recursive method used to find all classes in a given directory and
     * subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class> findClasses(File directory, String packageName)
     throws ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (ignoredpaths != null)
            if (Arrays.asList(ignoredpaths).contains(file.getPath())) {
                continue;
            }
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "."
                 + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName
                 + '.'
                 + file.getName()
                 .substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    public static void setIgnoredPaths(String[] ignoredpathz) {
        ignoredpaths = ignoredpathz;
    }
}
