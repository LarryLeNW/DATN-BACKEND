package com.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.repository.user.ModuleRepository;

@Service
public class ModuleService {

	@Autowired
	private ModuleRepository moduleRepository;

	public List<com.backend.entity.Module> getAllModules() {
		return moduleRepository.findAll();
	}

	public Optional<com.backend.entity.Module> getModuleById(Long id) {
		return moduleRepository.findById(id);
	}

	public com.backend.entity.Module createModule(com.backend.entity.Module module) {
		return moduleRepository.save(module);
	}

	public com.backend.entity.Module updateModule(Long id, com.backend.entity.Module moduleDetails) {
		return moduleRepository.findById(id).map(module -> {
			module.setName(moduleDetails.getName());
			return moduleRepository.save(module);
		}).orElseThrow(() -> new RuntimeException("Module not found with id " + id));
	}

	public void deleteModule(Long id) {
		moduleRepository.deleteById(id);
	}
}
