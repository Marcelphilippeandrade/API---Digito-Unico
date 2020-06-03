package br.com.bancointer.digitounico.api.dtos;

public class CriptografiaDto {

	private String chavePublica;

	private String chavePrivada;

	public String getChavePublica() {
		return chavePublica;
	}

	public void setChavePublica(String chavePublica) {
		this.chavePublica = chavePublica;
	}

	public String getChavePrivada() {
		return chavePrivada;
	}

	public void setChavePrivada(String chavePrivada) {
		this.chavePrivada = chavePrivada;
	}

	@Override
	public String toString() {
		return "CriptografiaDto [chavePublica=" + chavePublica + ", chavePrivada=" + chavePrivada + "]";
	}
}
