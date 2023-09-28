package me.googas.lazy.jsongo.query;

import com.google.gson.annotations.SerializedName;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@RequiredArgsConstructor
public class ReflectiveElementIdSupplier implements ElementIdSupplier {

    @NonNull
    private final Field field;

    @NonNull
    public static ReflectiveElementIdSupplier from(@NonNull Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
                continue;
            }
            if (field.isAnnotationPresent(SerializedName.class)) {
                SerializedName serializedName = field.getAnnotation(SerializedName.class);
                if (serializedName.value().equals("_id")) {
                    field.setAccessible(true);
                    return new ReflectiveElementIdSupplier(field);
                }
            }
            if (field.getName().equals("id")) {
                field.setAccessible(true);
                return new ReflectiveElementIdSupplier(field);
            }
        }
        throw new IllegalStateException("Could not find a field annotated with @SerializedName(\"_id\") or named \"id\" in " + clazz);
    }

    @NonNull
    public static ReflectiveElementIdSupplier from(@NonNull Object element) {
        return ReflectiveElementIdSupplier.from(element.getClass());
    }

    @Override
    public @NonNull Object getId(@NonNull Object element) {
        try {
            return field.get(element);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Could not get id from " + element + " using " + field, e);
        }
    }
}
