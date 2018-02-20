package net.sf.jkniv.whinstone.jpa2;

import java.io.InputStream;

class DefaultClassLoader
{
    /**
     * Return the default ClassLoader to use: typically the thread context
     * ClassLoader, if available; the ClassLoader that loaded the ClassUtils
     * class will be used as fallback.
     * <p>Call this method if you intend to use the thread context ClassLoader
     * in a scenario where you absolutely need a non-null ClassLoader reference:
     * for example, for class path resource loading (but not necessarily for
     * {@code Class.forName}, which accepts a {@code null} ClassLoader
     * reference as well).
     * @return the default ClassLoader (never {@code null})
     * @see Thread#getContextClassLoader()
     */
    public static ClassLoader getClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back to system class loader...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = DefaultClassLoader.class.getClassLoader();
        }
        return cl;
    }

    public static InputStream getResourceAsStream(String resource)
    {
        InputStream is = null;
        
        is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        if (is == null)
            is = ClassLoader.getSystemResourceAsStream(resource);
        if (is == null)
            is = DefaultClassLoader.class.getResourceAsStream(resource);
        return is;
    }
}
