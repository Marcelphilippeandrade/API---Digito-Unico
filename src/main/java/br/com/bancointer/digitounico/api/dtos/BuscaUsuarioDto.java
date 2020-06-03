package br.com.bancointer.digitounico.api.dtos;

import javax.validation.constraints.NotEmpty;

public class BuscaUsuarioDto {

	private Long id;

	private String nome;

	private String email;

	@NotEmpty(message = "Chave Privada não pode ser vazia.")
	private String chavePrivada;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getChavePrivada() {
		return chavePrivada;
	}

	public void setChavePrivada(String chavePrivada) {
		this.chavePrivada = chavePrivada;
	}

	@Override
	public String toString() {
		return "BuscaUsuarioDto [id=" + id + ", nome=" + nome + ", email=" + email + ", chavePublica" + chavePrivada + "]";
	}
}
