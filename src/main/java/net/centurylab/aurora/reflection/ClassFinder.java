package net.centurylab.aurora.reflection;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClassFinder
{
    private static final char PKG_SEPERATOR = '.';
    private static final char DIR_SEPERATOR = '/';

    private static final String CLASS_FILE_SUFFIX = ".class";

    private static final String BAD_PACKAGE_ERROR = "Unable to get resources from path '%s'. Are you sure the package '%s' exists?";

    public static List<Class<?>> find(String pkg) throws Exception
    {
        String path = pkg.replace(PKG_SEPERATOR, DIR_SEPERATOR);
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);

        if (url == null)
        {
            throw new IllegalArgumentException(String.format(BAD_PACKAGE_ERROR, path, pkg));
        }

        File dir = new File(url.getFile());
        List<Class<?>> classes = new ArrayList<>();

        for (File file : dir.listFiles())
        {
            classes.addAll(find(file, pkg));
        }

        return classes;
    }

    private static List<Class<?>> find(File file, String pkg)
    {
        List<Class<?>> classes = new ArrayList<>();

        String resource = pkg + PKG_SEPERATOR + file.getName();

        if (file.isDirectory())
        {
            for (File child : file.listFiles())
            {
                classes.addAll(find(child, resource));
            }
        }
        else if (resource.endsWith(CLASS_FILE_SUFFIX))
        {
            int endIndex = resource.length() - CLASS_FILE_SUFFIX.length();
            String className = resource.substring(0, endIndex);

            try
            {
                classes.add(Class.forName(className));
            }
            catch (ClassNotFoundException e)
            {
                System.out.format("Could not find package: '%s'", e.getMessage());
            }
        }

        return classes;
    }
}
