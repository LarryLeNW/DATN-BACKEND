package com.backend.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.backend.dto.request.fqa.QuestionCreation;
import com.backend.dto.request.product.ProductCreationRequest.RentalPackageDTO;
import com.backend.dto.response.question.QuestionReactionResponse;
import com.backend.dto.response.question.QuestionReplyResponse;
import com.backend.dto.response.question.QuestionResponse;
import com.backend.entity.Question;
import com.backend.entity.QuestionReaction;
import com.backend.entity.QuestionReply;
import com.backend.entity.User;
import com.backend.entity.rental.RentalPackage;

@Mapper(componentModel = "spring")
public interface RentalPackageMapper {

	RentalPackage toRentalPackage(RentalPackageDTO rentalDTO);

	List<RentalPackage> toRentalPackages(List<RentalPackageDTO> rentalPackages);

}
