package com.example.elderlycare.repository;

import com.example.elderlycare.entity.Elder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ElderRepository extends JpaRepository<Elder, Integer> {
    Optional<Elder> findByUserId(Integer userId);
}
