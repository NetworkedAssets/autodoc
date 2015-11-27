package com.networkedassets.autodoc.transformer.util.javadoc;

import java.util.Collection;
import java.util.List;

/**
 * Created by mrobakowski on 11/27/2015.
 */
public class JavadocEntity {
    public String name;
    public String scope; // enum?
    public String comment;
    public String superclass;
    public String type; // enum?
    public List<Method> constructors;
    public List<Method> methods;
    public List<String> modifiers;
    public List<Field> fields;
    public List<String> constants; // for enum type
    public List<String> implementedInterfaces;


    public static class Method {
        public String name;
        public String scope;
        public boolean isVararg;
        public List<String> modifiers;
        public List<Field> parameters;
        public GenericType type;
        public List<String> exceptions;
        public List<Annotation> annotations;
        public List<Param> params;
    }

    public static class Field {
        public String name;
        public String scope;
        public List<String> modifiers;
        public String constantValue;
        public GenericType type;
        public List<Annotation> annotations;
    }

    public static class Annotation {

    }

    public static class GenericType {
        public String name;
        public List<Param> params;
    }

    public static class Param {
        public String name;
        public String boundType;
        public GenericType bound;
        public String comment;
    }
}

class Foo {
    public static <T, E extends Exception> List<T> foo(Collection<? extends T> x, E y) {
        return null;
    }
}
