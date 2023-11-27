package br.com.tecnologia.iesp.tecback.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_cartao")
public class Cartao {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "nrCartao", length = 16)
	private String numCartao;
	
	@Column(name = "validadeCartao")
	private String validadeCartao;
	
	@Column(name = "codSeguranca")
	private String codSeguranca;
	
	@Column(name = "titularNome")
	private String titularNome;
	
	@Column(name = "nrCpf", length = 11)
	private String cpf;
}
