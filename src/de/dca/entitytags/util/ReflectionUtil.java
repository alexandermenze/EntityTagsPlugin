package de.dca.entitytags.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectionUtil {

    private static final Field fieldModifiersField;
    private static final Unsafe unsafeInstance;

    static {
        try{
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafeInstance = (Unsafe) unsafeField.get(null);

            fieldModifiersField = Field.class.getDeclaredField("modifiers");
            fieldModifiersField.setAccessible(true);
        }catch (Throwable t){
            throw new RuntimeException(t); //Should never occur
        }
    }

    public static void setPrivateFinalField(Field f, Object object, Object value) throws IllegalAccessException {
        f.setAccessible(true);
        fieldModifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
        f.set(object, value);
    }

    public static <T> T allocateObject(Class<T> clazz) throws InstantiationException {
        return (T) unsafeInstance.allocateInstance(clazz);
    }

    public static <T> T copyObject(T src) throws InstantiationException, IllegalAccessException {
        return copyObject(src, src.getClass());
    }

    public static <T> T copyObject(T src, Class<?> typeToCopy) throws InstantiationException, IllegalAccessException {
        T out = (T) unsafeInstance.allocateInstance(typeToCopy);
        copyObjectInto(src, out);
        return out;
    }

    public static <T> void copyObjectInto(T from, T to) throws IllegalAccessException {
        copyObjectInto(from, to, from.getClass());
    }

    public static <T> void copyObjectInto(T from, T to, Class<?> typeToCopy) throws IllegalAccessException {
        for(Field f : typeToCopy.getDeclaredFields()){
            if((f.getModifiers() & Modifier.STATIC) == Modifier.STATIC){
                continue;
            }
            f.setAccessible(true);
            ReflectionUtil.setPrivateFinalField(f, to, f.get(from));
        }
    }
}
