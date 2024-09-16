package com.backend.dto.request.category;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryCreationRequest {

	@Valid
	@NotNull(message = "Id cannot be empty")
	String id;
	
	@NotNull(message = "name cannot be empty")
	String name;
	
	String description;

}
