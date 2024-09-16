package com.backend.utils;

import java.lang.reflect.Field;
import java.util.function.Consumer;

public class Helpers {

	public static <T> void updateFieldEntityIfChanged(T newValue, T currentValue, Consumer<T> setter) {
		if (newValue != null && !newValue.equals(currentValue)) {
			setter.accept(newValue);
		}
	}

	public static <T> void updateEntityFields(Object request, Object entity) {
		Field[] requestFields = request.getClass().getDeclaredFields();
		Field[] entityFields = entity.getClass().getDeclaredFields();

		for (Field requestField : requestFields) {
			try {
				requestField.setAccessible(true);
				Object newValue = requestField.get(request);

				for (Field entityField : entityFields) {
					entityField.setAccessible(true);
					if (requestField.getName().equals(entityField.getName())) {
						Object currentValue = entityField.get(entity);

						Consumer<Object> setter = newValueToSet -> {
							try {
								entityField.set(entity, newValueToSet);
							} catch (IllegalAccessException e) {
								throw new RuntimeException(e);
							}
						};

						// Update only if the value has changed
						updateFieldEntityIfChanged(newValue, currentValue, setter);
						break;
					}
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

}
