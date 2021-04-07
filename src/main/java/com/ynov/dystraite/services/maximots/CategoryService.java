package com.ynov.dystraite.services.maximots;

import com.ynov.dystraite.entities.maximots.Category;
import com.ynov.dystraite.repositories.maximots.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;
    
    public Category getById(long id) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        return categoryOpt.orElse(null);
    }

    public List<Category> getByIdIn(List<Long> ids) {
        return categoryRepository.findByIdIn(ids);
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category delete(long id) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if(categoryOpt.isPresent()) {
            categoryRepository.deleteById(id);
            return categoryOpt.get();
        }else {
            return null;
        }
    }

    public Category update(long id, Category newCategory) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if(categoryOpt.isPresent()) {

            //libelle
            if (newCategory.getLibelle() != null && newCategory.getLibelle().trim().length() > 0){
                categoryOpt.get().setLibelle(newCategory.getLibelle());
            }

            return categoryRepository.save(categoryOpt.get());
        }
        return null;
    }

    public Category create(Category newCategory) {
        if (newCategory.getLibelle() != null && newCategory.getLibelle().trim().length() > 0) {
            return categoryRepository.save(newCategory);
        }
        return null;
    }
}

