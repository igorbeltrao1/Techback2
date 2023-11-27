package br.com.tecnologia.iesp.tecback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.tecnologia.iesp.tecback.entities.Genero;

@Repository
public interface GeneroRepository extends JpaRepository<Genero, Long>{

}
