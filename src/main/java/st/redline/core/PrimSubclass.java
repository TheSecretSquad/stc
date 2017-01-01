package st.redline.core;

import st.redline.classloader.SmalltalkClassLoader;

public class PrimSubclass extends PrimObject {

    public static final PrimObject PRIM_SUBCLASS = new PrimSubclass();

    protected PrimObject invoke(PrimObject receiver, PrimContext primContext) {
        System.out.println("PrimSubclass invoke: " + String.valueOf(primContext.argumentJavaValueAt(0)));
        assert receiver.equals(primContext.receiver());

        String subclassName = String.valueOf(primContext.argumentJavaValueAt(0));
        PrimObject superclass = primContext.receiver();
        PrimClass newClass;
        PrimClass newMeta;
        boolean bootstrapping = isBootstrapping();

        if (bootstrapping) {
            newClass = (PrimClass) superclass.resolveObject(subclassName);
            if (newClass == null)
                throw new RuntimeException("New class is unexpectedly null.");
        } else {
            newClass = new PrimClass(subclassName);
            newMeta = new PrimClass(subclassName, true);
            newClass.selfClass(newMeta);
            newClass.superclass(superclass);
            newMeta.superclass(superclass.selfClass());
        }

        // TODO - Add other definitions to appropriate objects.
        System.out.println("TODO - Add other definitions to appropriate objects.");

        if (!bootstrapping) {
            SmalltalkClassLoader classLoader = classLoader();
            String fullName = classLoader.findPackage(subclassName);
            classLoader.cacheObject(fullName, newClass);
        }

        return newClass;
    }

    private String subclassName(PrimContext context) {
        return String.valueOf(context.argumentJavaValueAt(0));
    }
}
