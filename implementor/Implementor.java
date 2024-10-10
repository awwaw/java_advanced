package info.kgeorgiy.ja.podkorytov.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;

/**
 * Making an implementation of some interface (or extension for abstract class) and packaging it to JAR
 * <p>
 * Class implements {@link Impler} and {@link JarImpler} interfaces, thus it can take some abstract class
 * or interface and generate class which will be an implementation (or extension) of given one. Then it can
 * compile to JAR file
 * </p>
 */
public class Implementor implements Impler, JarImpler {
    /**
     * Line separator used for generating .java file, depending on OS
     */
    private final static String LINE_SEPARATOR = System.lineSeparator();

    /**
     * Character used for indentation in .java file
     */
    private final static String INDENT = "    ";

    /**
     * String that will be printed if there are any errors in usage of main method
     */
    private final static String USAGE = """
            Usage: java Implementor <class-to-be-implemented> <output-path>
            OR (to create a JAR file)
            java Implementor -jar <class-to-be-implemented> <output-path>
            """;

    /**
     * Constant used to check privacy modifiers on methods
     */
    // :NOTE: стоит давать более точно название константам, MODIFIERS не понятно что это
    private final static int MODIFIERS = ~Modifier.ABSTRACT
            & ~Modifier.TRANSIENT
            & ~Modifier.STRICT
            & ~Modifier.NATIVE
            & ~Modifier.VOLATILE;

    /**
     * Method generating name of result class (implementation/extension class)
     *
     * @param clazz - type-token of class to implement
     * @return name of an implementation class
     */
    private String getResultClassName(Class<?> clazz) {
        return clazz.getSimpleName().concat("Impl");
    }

    /**
     * Creating first lines of .java file (e.g. package .... and public class ...)
     * <p>
     * This method generates package declaration, then checks whether result class is implementation or extension
     * and creates a list of some sort of "tokens"
     * </p>
     *
     * @param claZZ - type-token of class to implement
     * @return - {@link java.util.List<String>} of "tokens" in order as follows: package, "public class", classname,
     * "implements" or "extends", given class name
     */
    private List<String> generateClassDeclaration(Class<?> claZZ) {
//        StringBuilder sb = new StringBuilder();
        List<String> result = new ArrayList<>();

        if (!claZZ.getPackageName().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("package ")
                    .append(claZZ.getPackage().getName())
                    .append(";").append(LINE_SEPARATOR).append(LINE_SEPARATOR);
            result.add(sb.toString());
        }

        String extendsOrImplements = claZZ.isInterface() ? "implements " : "extends ";
        result.add("public class");
        result.add(getResultClassName(claZZ));
        result.add(extendsOrImplements);
        result.add(claZZ.getCanonicalName());
        return result;
    }

    /**
     * Get array of constructors for class
     *
     * @param token - class whose constructors we need
     * @return Array of non-private {@link Constructor} of given class
     */
    private Constructor<?>[] getConstructors(Class<?> token) {
        return Arrays.stream(token.getDeclaredConstructors())
                .filter(constructor -> !Modifier.isPrivate(constructor.getModifiers()))
                .toArray(Constructor<?>[]::new);
    }

    /*
        Generating methods (including constructors)
     */

    /**
     * Gets default value for some classes (more precisely - {@link String} and primitive classes)
     *
     * @param token - typetoken of which type's default value will be returned
     * @return {@link String} value of default value of given type
     */
    private String getDefaultValue(Class<?> token) {
        if (token.equals(boolean.class)) {
            return " false";
        }
        // :NOTE: лучше один раз создать этот лист, иначе очень не эффективно
        // fixed
        List<Class<?>> primitiveIntClasses = Arrays.stream(
                new Class<?>[]{
                        short.class,
                        char.class,
                        int.class,
                        long.class,
                        byte.class
                }).toList();
        if (primitiveIntClasses.contains(token)) {
            return " 0";
        }
        if (token.equals(double.class)) { // :NOTE: в целом, 0 из секции вые подойдет; и для float тоже
            return " 0.0";
        }
        if (token.equals(float.class)) {
            return " 0.0F";
        }
        if (token.equals(void.class)) {
            return "";
        }
        return " null";
    }

    /**
     * Generates single parameter in format {@code ParamType[optional] paramName}
     *
     * @param parameter - parameter to generate for
     * @param calling   - specifies whether ParamTypes will be returned or not.
     *                  Calling = 1 can be used for example in cases of {@code super(p1, p2, ...)}
     * @return String value of parameter in format shown above
     */
    private String generateParameter(Parameter parameter, boolean calling) { // calling = 1 <=> no type names returned
        String parameterType = calling ? "" : parameter.getType().getCanonicalName();
        return parameterType + " " + parameter.getName();
    }

    /**
     * Method returning all parameters of executable in format {@code (PType1[optional] pName1, ... )}
     *
     * @param executable - executable whose params will be generated
     * @param calling    - specifies whether ParamTypes will be returned or not.
     *                   Calling = 1 can be used for example in cases of {@code super(p1, p2, ...)}
     * @return Basically this method returns String value of
     * anything in between brackets in method calls or declarations. For example for this method, it will return
     * {@code (Executable executable, boolean calling)} with calling = 0 and
     * {@code (executable, calling)} with calling = 1
     */
    private String generateParameters(Executable executable, boolean calling) {
        return Arrays.stream(executable.getParameters())
                .map(parameter -> generateParameter(parameter, calling))
                .collect(Collectors.joining(
                        ", ",
                        "(",
                        ")"
                ));
    }

    /**
     * Returns "last line" of method or constructor.
     *
     * @param executable - method or constructor to generate
     * @return If given {@link Executable} is method, returning value will be like {@code return DEFAULT_VALUE)
     * If given Executable is constructor, returning value will be like {@code super(params)} (without types)
     */
    private String getReturnOrSuperStatement(Executable executable) {
        if (executable instanceof Method method) {
            return "return" + getDefaultValue(method.getReturnType());
        } else {
            return "super" + generateParameters(executable, true);
        }
    }

    /**
     * Returns string value of privacy modifiers of given method
     *
     * @param executable of which we need to get modifiers
     * @return {@link String} containing modifiers of executable. For example {@code private final static}
     */
    private String getModifiers(Executable executable) {
        return Modifier.toString(
                executable.getModifiers() & MODIFIERS
        );
    }

    /**
     * Returns String containing all exceptions thrown by executable.
     *
     * @param executable to be checked for exceptions
     * @return If there are some exceptions, method will return {@code throws Ex1, Ex2, ...}.
     * Otherwise, empty string will be returned
     */
    private String generateExceptions(Executable executable) {
        Class<?>[] exceptionTypes = executable.getExceptionTypes();
        if (exceptionTypes.length != 0) {
            String exceptions = Arrays.stream(exceptionTypes)
                    .map(Class::getCanonicalName)
                    .collect(Collectors.joining(", "));
            return " throws " + exceptions;
        }
        return "";
    }

    /**
     * Generates all executable signature
     *
     * @param executable to generate declaration for
     * @param token
     * @return string like {@code private final String foo(Object bar)}
     */

    private String generateSignature(Executable executable, Class<?> token) {
        StringBuilder sb = new StringBuilder();

        // Modifiers
        sb.append(INDENT).append(getModifiers(executable)).append(" ");

        // Method name
        if (executable instanceof Method method) {
            Class<?> returnType = method.getReturnType();
            sb.append(returnType.getCanonicalName()).append(" ").append(method.getName());
        } else {
            Constructor<?> constructor = (Constructor<?>) executable;
            sb.append(getResultClassName(constructor.getDeclaringClass()));
        }

        // Method params
        sb.append(generateParameters(executable, false));

        // Exceptions
        sb.append(generateExceptions(executable));

        sb.append(" {").append(LINE_SEPARATOR);

        return sb.toString();
    }

    /**
     * Combines executable signature and return (or super) statement
     *
     * @param executable
     * @param token
     * @return executable declaration, for example
     * {@code
     * private final String foo(String bar) {
     * return null;
     * }
     * }
     */
    private String generateExecutableDeclaration(Executable executable, Class<?> token) {
        StringBuilder sb = new StringBuilder();

        sb.append(generateSignature(executable, token))
                .append(INDENT)
                .append(INDENT)
                .append(getReturnOrSuperStatement(executable))
                .append(";")
                .append(LINE_SEPARATOR)
                .append(INDENT)
                .append("}")
                .append(LINE_SEPARATOR);

        return sb.toString();
    }

    /**
     * Class to wrap methods, so I can change hashCode for methods
     *
     * @param method
     */
    private record MethodWrapper(Method method) {

        @Override
        public int hashCode() {
            return Objects.hash(
                    method.getName(),
                    Arrays.hashCode(
                            method.getParameterTypes()
                    )
            );
        }

        @Override
        public boolean equals(Object other) {
            if (other == null) {
                return false;
            }
            if (other.getClass() != getClass()) {
                return false;
            }

            Method otherMethod = ((MethodWrapper) other).method();
            return method.getName().equals(otherMethod.getName())
                    && Arrays.equals(method.getParameterTypes(), otherMethod.getParameterTypes());
        }
    }

    /**
     * Method that will "filter" methods that we need to implement
     * <p>
     * We need to implement only abstract and non-final methods, so here we check for these modifiers.
     * Also we check for methods with same signature and if there are many similar, we leave one with
     * "smallest" return type and correct modifiers
     * </p>
     *
     * @param methods    to be filtered
     * @param methodsMap result map, technically we only need {@link Map::values()} of this map
     */
    private void putMethods(Method[] methods, Map<MethodWrapper, Method> methodsMap) {
        for (Method method : methods) {
            MethodWrapper wrapper = new MethodWrapper(method);
            if (methodsMap.containsKey(wrapper)) {
                Method other = methodsMap.get(wrapper);
                if (other.getReturnType().equals(method.getReturnType())
                        && Modifier.isAbstract(other.getModifiers())) {
                    continue;
                }
                if (!Modifier.isFinal(other.getModifiers())
                        && other.getReturnType().isAssignableFrom(method.getReturnType())) {
                    methodsMap.put(wrapper, method);
                }
            } else {
                methodsMap.put(wrapper, method);
            }
        }
    }

    /**
     * Returns a list of methods we need to implement
     * <p>
     * To get ALL methods, we go up the hierarchy and check methods of superclasses too
     * </p>
     *
     * @param token class which methods we will get
     * @return {@link java.util.List} of methods
     */
    private List<Method> generateMethods(Class<?> token) {
        Map<MethodWrapper, Method> uniqueMethods = new HashMap<>();

        putMethods(token.getMethods(), uniqueMethods);
        for (Class<?> cur = token; cur != null; cur = cur.getSuperclass()) {
            putMethods(cur.getDeclaredMethods(), uniqueMethods);
        }

        return uniqueMethods.values()
                .stream()
                .filter(method -> !Modifier.isFinal(method.getModifiers())
                        && Modifier.isAbstract(method.getModifiers()))
                .toList();
    }

    /**
     * Check if there are any private classes in returnType of method, or it's params
     *
     * @param method to check
     * @return {@code true} if there are any private classes, {@code false} otherwise
     */
    private boolean checkMethodSignatureForPrivateClasses(Method method) {
        Class<?>[] types = method.getParameterTypes();
        boolean res = Modifier.isPrivate(method.getReturnType().getModifiers());
        return res | Arrays.stream(types)
                .anyMatch(type -> Modifier.isPrivate(type.getModifiers()));
    }

    /**
     * Writes a given string converted to unicode characters, with given writer
     *
     * @param string
     * @param writer
     * @throws IOException - if an error occurs while trying to write
     */
    private void writeUnicodeString(final String string, BufferedWriter writer) throws IOException {
        final char[] characters = string.toCharArray();
        for (final char c : characters) {
            writer.write(String.format("\\u%04x", (int) c));
        }
    }

    /**
     * Returns {@link Path} from {@code rootPath} to {@code token}, adding specified suffix (.java or .class)
     *
     * @param token "endpoint" of path
     * @param rootPath "starting point" of path
     * @param suffix suffix of file specifying its type
     * @return as above
     */
    private Path getCorrectPath(Class<?> token, Path rootPath, String suffix) {
        return rootPath
                .resolve(token.getPackageName().replace('.', File.separatorChar))
                .resolve(getResultClassName(token) + suffix);
    }

    /**
     * Generates implementation of given class and puts it to given path.
     * <p>
     * Also it checks whether class can be implemented at all.
     * </p>
     *
     * @param token type token to create implementation for.
     * @param root  root directory.
     * @throws ImplerException - if we can't implement given class for some reason
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        if (token.isArray()
                || token == Record.class
                || token.isPrimitive()
                || Modifier.isFinal(token.getModifiers())
                || token == Enum.class
                || Modifier.isPrivate(token.getModifiers())) {
            throw new ImplerException("Can't generate given class");
        }

        Path classPath = getCorrectPath(token, root, ".java");

        if (classPath.getParent() != null) {
            try {
                Files.createDirectories(classPath.getParent());
            } catch (IOException e) {
                throw new ImplerException("Can't create parent directories for output file");
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(classPath)) {
            List<String> declaration = generateClassDeclaration(token);
            for (String piece : declaration) {
                writeUnicodeString(piece, writer);
                writer.write(String.format("\\u%04x", (int) ' '));
            }
            writer.write(String.format("\\u%04x", (int) '{'));
            writer.write(String.format("\\u%04x", (int) LINE_SEPARATOR.charAt(0)));

            Constructor<?>[] constructors = getConstructors(token);
            if (constructors.length == 0 && !token.isInterface()) {
                throw new ImplerException("Can't implement utility class");
            }
            for (Constructor<?> constructor : constructors) {
                writeUnicodeString(generateExecutableDeclaration(constructor, token), writer);
            }

            List<Method> methods = generateMethods(token);
            for (Method method : methods) {
                if (checkMethodSignatureForPrivateClasses(method)) {
                    throw new ImplerException("Can't implement method with parameters of private class type");
                }
                writeUnicodeString(generateExecutableDeclaration(method, token), writer);
            }

            writer.write(String.format("\\u%04x", (int) '}'));
            writer.write(String.format("\\u%04x", (int) LINE_SEPARATOR.charAt(0)));

        } catch (IOException exc) {
            // :NOTE: лучше прикрпеплять исключение, которое порадило это исключение (fixed)
            throw new ImplerException("Can't write class implementation to output file: " + exc.getMessage(), exc);
        }
    }

    /**
     * Generates implementation of class and compiles it to JAR file
     *
     * @param token   type token to create implementation for.
     * @param jarFile target <var>.jar</var> file.
     * @throws ImplerException if we can't create implementation
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        Path rootPath = Paths.get("");
        Path sourcePath = getCorrectPath(token, rootPath, ".java");

        implement(token, rootPath);

        String classpath;
        try {
            classpath = Path.of(token.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        } catch (URISyntaxException | SecurityException e) {
            throw new ImplerException("Can't convert URL to URI", e);
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new ImplerException("Couldn't get java compiler");
        }

        final int exitCode = compiler.run(
                null,
                null,
                null,
                "-cp",
                classpath,
                sourcePath.toString(),
                "-encoding",
                StandardCharsets.UTF_8.name()
        );

        if (exitCode != 0) {
            throw new ImplerException("Compilation ended with non-zero exit code: " + exitCode);
        }

        try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(jarFile))) {
            Path filePath = getCorrectPath(token, rootPath, ".class");
            JarEntry entry = new JarEntry(
                    token.getPackageName().replace('.', '/')
                            + "/"
                            + token.getSimpleName() + "Impl.class"
            );
            output.putNextEntry(entry);
            Files.copy(filePath, output);
        } catch (IOException e) {
            throw new ImplerException("Couldn't write JAR file: ", e);
        }
    }

    /**
     * Check if there is valid amount of args
     *
     * @param args
     * @return {@code true} if args are invalid
     */
    private static boolean validateArgsLength(String[] args) {
        if (args.length < 2) {
            System.err.println("Invalid amount of arguments");
            System.err.println(USAGE);
            return true;
        }
        return false;
    }

    /**
     * Implements (or also compiles to JAR) given in args class
     *
     * @param args: -jar[optional] - if need to compile to jar
     *              classname - name of class to implement
     *              path - where to put result
     */
    public static void main(String[] args) {
        if (validateArgsLength(args)) {
            return;
        }

        Implementor implementor = new Implementor();
        try {
            if (args[0].equals("-jar")) {
                if (args.length != 3) {
                    System.err.println("Invalid amount of arguments for JAR creation");
                    System.err.println(USAGE);
                    return;
                }
                implementor.implementJar(
                        Class.forName(args[1]),
                        Path.of(args[2])
                );
            } else {
                if (validateArgsLength(args)) {
                    return;
                }
                implementor.implement(
                        Class.forName(args[0]),
                        Path.of(args[1])
                );
            }
        } catch (ImplerException | ClassNotFoundException e) {
            System.err.println("An error occurred while trying to implement class: " + e.getMessage());
        }
    }
}
