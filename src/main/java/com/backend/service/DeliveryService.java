package com.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.entity.Delivery;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.repository.DeliveryRepository;
import com.backend.utils.Helpers;

@Service
public class DeliveryService {
	
    @Autowired
	DeliveryRepository deliveryRepository;

	public Delivery createDelivery(Delivery delivery) {
		return deliveryRepository.save(delivery);
	}
	public Delivery updateDelivery(Delivery delivery, Integer id) {
	    Delivery deliveryItem = deliveryRepository.findById(id)
	        .orElseThrow(() -> new AppException(ErrorCode.DELIVERY_NOT_EXISTED));

	    Helpers.updateFieldEntityIfChanged(delivery.getUsername(), deliveryItem.getUsername(), deliveryItem::setUsername);
	    Helpers.updateFieldEntityIfChanged(delivery.getAddress(), deliveryItem.getAddress(), deliveryItem::setAddress);
	    Helpers.updateFieldEntityIfChanged(delivery.getEmail(), deliveryItem.getEmail(), deliveryItem::setEmail);
	    Helpers.updateFieldEntityIfChanged(delivery.getNumberPhone(), deliveryItem.getNumberPhone(), deliveryItem::setNumberPhone);
	    Helpers.updateFieldEntityIfChanged(delivery.getNote(), deliveryItem.getNote(), deliveryItem::setNote);

	    return deliveryRepository.save(deliveryItem);
	}
	public Delivery getDeliveryById(Integer id) {
	    return deliveryRepository.findById(id)
	        .orElseThrow(() -> new AppException(ErrorCode.DELIVERY_NOT_EXISTED));
	}
	public void deleteDelivery(Integer id) {
		deliveryRepository.deleteById(id);
	}


}
