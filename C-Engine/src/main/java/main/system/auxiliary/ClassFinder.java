package main.system.auxiliary;

import main.data.filesys.PathFinder;
import main.system.launch.CoreEngine;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassFinder {

    private static String path;

    private static Class<?> filterClass;

    private static String[] ignoredpaths;

    // public static Class<?>[] getClasses(String packageName, Class c)
    // throws ClassNotFoundException, IOException {
    // if (path == null)
    // path = PathFinder.getENGINE_PATH();
    // filterClass = c;
    // Class[] classes = getClasses(packageName);
    // filterClass = null;
    // return classes;
    // }
    //
    // private static List<Class> findClasses(File directory, String
    // packageName)
    // throws ClassNotFoundException {
    // if (path == null)
    // path = PathFinder.getENGINE_PATH();
    // List<Class> classes = new ArrayList<Class>();
    // if (!directory.exists()) {
    // return classes;
    // }
    // File[] files = directory.listFiles();
    // for (File file : files) {
    // if (file.isDirectory()) {
    // assert !file.getName().contains(".");
    // classes.addAll(findClasses(file, packageName + "."
    // + file.getName()));
    // } else if (file.getName().endsWith(".class")) {
    //
    // try {
    // Class c = Class.forName(packageName
    // + '.'
    // + file.getName().substring(0, file.getName()
    // .length() - 6));
    // if (filterClass != null) {
    // if (c.getSuperclass() == filterClass
    // || Arrays.asList(c.getInterfaces())
    // .contains(filterClass)) {
    // classes.add(c);
    // }
    // } else
    // classes.add(c);
    // } catch (ClassNotFoundException e) {
    // // logger.info("Class not found or faulty");
    // continue;
    // }
    // }
    // }
    // return classes;
    // }
    //
    // public static Class[] getClasses(String packageName)
    // throws ClassNotFoundException, IOException {
    // return getClasses(PathFinder.getENGINE_PATH() + "bin\\", packageName);
    // }
    //
    // public static Class[] getClasses(String root, String packageName)
    // throws ClassNotFoundException, IOException {
    //
    // ClassLoader classLoader = Thread.currentThread()
    // .getContextClassLoader();
    // assert classLoader != null;
    // String path = root +
    //
    // packageName.replace('.', '/');
    // // logger.info("Searching for classes in: " + path);
    // Enumeration<URL> resources = classLoader.getResources(path);
    // assert resources != null;
    // List<File> dirs = new ArrayList<File>();
    // while (resources.hasMoreElements()) {
    // URL resource = resources.nextElement();
    // // logger.info("Element: " + resource.toString());
    //
    // dirs.add(new File(resource.getFile()));
    // }
    // ArrayList<Class> classes = new ArrayList<Class>();
    // for (File directory : dirs) {
    // classes.addAll(findClasses(directory, packageName));
    // }
    // return classes.toArray(new Class[classes.size()]);
    // }

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
        if (CoreEngine.isJar()) {
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
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
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
//                    System.out.println("Jar entry passed: " +je.getName());
                    continue;
                }

                if (!je.getName().startsWith("main")) {
                    continue;
                }
                String  className = je.getName().replace('/', '.');
                if (!className.contains(packageName))
                {
                    continue;
                }
                // -6 because of .class
                 className = className.substring(0, je.getName().length() - 6);

                Class c = cl.loadClass(className);
//                System.out.println(packageName+ "- Class found: " +c.getName());
                classes.add(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

//        System.out.println(classes.size()+ " classes found: " +classes);
        return classes.toArray(new Class[classes.size()]);
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
