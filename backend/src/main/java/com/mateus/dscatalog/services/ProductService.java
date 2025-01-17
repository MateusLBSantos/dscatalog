package com.mateus.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mateus.dscatalog.dto.CategoryDTO;
import com.mateus.dscatalog.dto.ProductDTO;
import com.mateus.dscatalog.entities.Category;
import com.mateus.dscatalog.entities.Product;
import com.mateus.dscatalog.repositories.CategoryRepository;
import com.mateus.dscatalog.repositories.ProductRepository;
import com.mateus.dscatalog.services.exceptions.DataBaseException;
import com.mateus.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private CategoryRepository categoryRepository;

	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged	(PageRequest pageRequest) {
		Page<Product> list = repository.findAll(pageRequest);

		return list.map(x -> new ProductDTO(x));

	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Optional<Product> obj = repository.findById(id);
		Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Objeto Não existente"));
		return new ProductDTO(entity,entity.getCategories());
	}

	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);

		return new ProductDTO(entity);
	}


	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		try {	
			Product entity = repository.getOne(id);
			copyDtoToEntity(dto, entity);
			entity = repository.save(entity);

			return new ProductDTO(entity);
		} 
		catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}

	}

	public void delete(Long id) {
		try {
		repository.deleteById(id);
		}
		catch (EmptyResultDataAccessException e){
			throw new ResourceNotFoundException("Id not found " + id);
		}
		catch (DataIntegrityViolationException e) {
			throw new DataBaseException("Integrity violation");
		}
	}

	private void copyDtoToEntity(ProductDTO dto, Product entity) {
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setDate(dto.getDate());
		entity.setPrice(dto.getPrice());
		entity.setImgUrl(dto.getImgUrl());
		
		entity.getCategories().clear();
		for (CategoryDTO catDto : dto.getCategories()) {
			Category category = categoryRepository.getOne(catDto.getId());
			entity.getCategories().add(category);
		}
	}
}
