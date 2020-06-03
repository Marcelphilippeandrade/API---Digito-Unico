package br.com.bancointer.digitounico.api.dtos;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class CadastroDigitoUnicoDto {

	private Long id;

	@NotEmpty(message = "Número Inteiro não pode ser vazio.")
	private String numeroInteiro;

	@NotNull(message = "Fator de Concatenação não pode ser vazio.")
	private int fatorConcatenacao;

	private Long usuarioId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Long usuarioId) {
		this.usuarioId = usuarioId;
	}

	public int getFatorConcatenacao() {
		return fatorConcatenacao;
	}

	public void setFatorConcatenacao(int fatorConcatenacao) {
		this.fatorConcatenacao = fatorConcatenacao;
	}

	public String getNumeroInteiro() {
		return numeroInteiro;
	}

	public void setNumeroInteiro(String numeroInteiro) {
		this.numeroInteiro = numeroInteiro;
	}

	@Override
	public String toString() {
		return "DigitoUnicoDto [id=" + id + ", numeroInteiro=" + numeroInteiro + ", fatorConcatenacao="
				+ fatorConcatenacao + ", usuario=" + usuarioId + "]";
	}
}
