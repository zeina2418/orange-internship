package com.example.demo.repository;

import com.example.demo.entity.Utilisateur;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilisateurRepo extends JpaRepository<Utilisateur, Long> {
    Utilisateur findByTel(Long tel);

    @Transactional
    boolean existsByTel(Long tel);

    @Transactional
    @Modifying
    @Query("DELETE FROM Utilisateur u WHERE u.tel = :tel")
    void removeUtilisateurByTel(@Param("tel") Long tel);

    long countByTel(long tel);
}

