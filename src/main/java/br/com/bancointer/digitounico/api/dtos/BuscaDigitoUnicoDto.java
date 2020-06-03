package br.com.bancointer.digitounico.api.dtos;

public class BuscaDigitoUnicoDto {

	private Long id;
	private String numeroInteiro;
	private int fatorConcatenacao;
	private int resultado;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumeroInteiro() {
		return numeroInteiro;
	}

	public void setNumeroInteiro(String numeroInteiro) {
		this.numeroInteiro = numeroInteiro;
	}

	public int getFatorConcatenacao() {
		return fatorConcatenacao;
	}

	public void setFatorConcatenacao(int fatorConcatenacao) {
		this.fatorConcatenacao = fatorConcatenacao;
	}

	public int getResultado() {
		return resultado;
	}

	public void setResultado(int resultado) {
		this.resultado = resultado;
	}

	@Override
	public String toString() {
		return "BuscaDigitoUnicoDto [id=" + id + ", numeroInteiro=" + numeroInteiro + ", fatorConcatenacao="
				+ fatorConcatenacao + ", resultado=" + resultado + "]";
	}
}
