package br.com.tecnologia.iesp.tecback.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.tecnologia.iesp.tecback.entities.Email;

public interface EmailRepository extends JpaRepository<Email, UUID>{

}
