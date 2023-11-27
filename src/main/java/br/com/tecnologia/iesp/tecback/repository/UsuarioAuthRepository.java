package br.com.tecnologia.iesp.tecback.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.tecnologia.iesp.tecback.entities.Usuario;

@Repository
public interface UsuarioAuthRepository extends CrudRepository<Usuario, Long>{

	Optional<Usuario> findByLogin(String login);
}
