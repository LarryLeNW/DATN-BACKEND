package com.backend.utils;

import java.lang.reflect.Field;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;

public class Helpers {

	public static <T> void updateFieldEntityIfChanged(T newValue, T currentValue, Consumer<T> setter) {
		if (newValue != null && !newValue.equals(currentValue)) {
			setter.accept(newValue);
		}
	}

	public static String getCurrentTimeString(String format) {
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
		SimpleDateFormat fmt = new SimpleDateFormat(format);
		fmt.setCalendar(cal);
		return fmt.format(cal.getTimeInMillis());
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

	public static String handleRandom(int size) {
		Random random = new Random();
		StringBuilder randomNumber = new StringBuilder(5);
		for (int i = 0; i < size; i++) {
			int digit = random.nextInt(10);
			randomNumber.append(digit);
		}
		return randomNumber.toString();
	}

	public static String toSlug(String input) {
		String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
		String withoutDiacritics = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

		withoutDiacritics = withoutDiacritics.toLowerCase();

		String slug = withoutDiacritics.replaceAll("[^a-z0-9\\s]", "").replaceAll("\\s+", "-");

		slug = slug.replaceAll("^-|-$", "");

		return slug;
	}

}
