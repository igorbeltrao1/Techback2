package br.com.tecnologia.iesp.tecback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.tecnologia.iesp.tecback.entities.Cartao;

@Repository
public interface CartaoRepository extends JpaRepository<Cartao, Long>{

    @Query(value = "SELECT * FROM tb_cartao WHERE nr_Cpf = :cpf", nativeQuery = true)
    Cartao verificarCpf(@Param("cpf") String cpf);

}
