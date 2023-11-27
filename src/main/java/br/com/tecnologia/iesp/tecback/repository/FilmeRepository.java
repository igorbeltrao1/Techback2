package br.com.tecnologia.iesp.tecback.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.tecnologia.iesp.tecback.entities.Filme;

@Repository
public interface FilmeRepository extends JpaRepository<Filme,Integer> {
}
