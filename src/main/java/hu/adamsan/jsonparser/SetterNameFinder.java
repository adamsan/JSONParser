package hu.adamsan.jsonparser;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 *  Finds fields annotated with @JsonPropertyName, makes a mapping between JSON property names and Java Field names.
 */
public class SetterNameFinder {
    private final Map<String, String> jsonToFieldNameMap = new HashMap<>();
    public <T> SetterNameFinder(Class<T> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            var customization = field.getAnnotation(JsonPropertyName.class);
            var name = customization != null ? customization.name() : field.getName();
            jsonToFieldNameMap.put(name, field.getName());
        }
    }

    public String getFieldName(String jsonPropertyName) {
        return jsonToFieldNameMap.get(jsonPropertyName);
    }
}
