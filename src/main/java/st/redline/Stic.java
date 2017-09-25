/* Redline Smalltalk, Copyright (c) James C. Ladd. All rights reserved. See LICENSE in the root of this distribution. */
package st.redline;

import st.redline.classloader.*;
import st.redline.kernel.Smalltalk;

import java.io.*;

public class Stic {

    private final String[] args;

    public static void main(String[] args) throws Exception {
        new Stic(args).run();
    }

    public Stic(String[] args) {
        this.args = args;
    }

    private void run() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        run(loadScript(scriptName()));
    }

    private void run(Class cls) throws IllegalAccessException, InstantiationException {
        // Instantiate Class that contains the message sends embodied in the Smalltalk class.
        Script script = (Script) cls.newInstance();
        // Call 'sendMessages' with a Smalltalk instance to invoke all the message sends
        // embodied in the Smalltalk class.
        System.out.println(script.sendMessages(smalltalk()));
    }

    private Smalltalk smalltalk() {
        return new RedlineSmalltalk();
    }

    private Class loadScript(String name) throws ClassNotFoundException {
        return classLoader().loadScript(name);
    }

    private SmalltalkClassLoader classLoader() {
        return new SmalltalkClassLoader(currentClassLoader(), sourceFinder());
    }

    private SourceFinder sourceFinder() {
        return new SmalltalkSourceFinder(sourceFactory(), classPaths());
    }

    private SourceFactory sourceFactory() {
        return new SourceFactory();
    }

    public String[] classPaths() {
        return classPath().split(File.pathSeparator);
    }

    private String classPath() {
        return System.getProperty("java.class.path");
    }

    private ClassLoader currentClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private String scriptName() {
        return hasArguments() ? firstArgument() : defaultScriptName();
    }

    private String defaultScriptName() {
        return "st.redline.script.NoArguments";
    }

    private String firstArgument() {
        return args[0];
    }

    private boolean hasArguments() {
        return args.length > 0;
    }
}
