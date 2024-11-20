package com.backend.specification;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.dto.response.common.PagedResponse;

public class CommonSpecification {

    public static <T> void handleFields(T response, Set<String> fields, boolean include) {
        try {
            Field[] declaredFields = response.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                if (include && !fields.contains(field.getName())) {
                    field.set(response, null); // Remove non-required fields
                }
                if (!include && fields.contains(field.getName())) {
                    field.set(response, null); // Exclude specified fields
                }
            }
        } catch (IllegalAccessException e) {
            System.out.println("Error processing fields: " + e.getMessage());
        }
    }
}
