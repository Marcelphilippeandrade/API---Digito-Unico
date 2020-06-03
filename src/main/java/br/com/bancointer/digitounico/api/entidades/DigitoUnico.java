package br.com.bancointer.digitounico.api.entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.bancointer.digitounico.api.exception.ExcecaoNumeroInteiroComLetras;
import br.com.bancointer.digitounico.api.exception.ExcecaoNumeroInteiroEFatorNaoPermitido;

@Entity
@Table(name = "digitounico")
public class DigitoUnico implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "numeroInteiro", nullable = false)
	private String numeroInteiro;

	@Column(name = "fatorConcatenacao", nullable = false)
	private int fatorConcatenacao;

	@Column(name = "resultado", nullable = false)
	private int resultado;

	@ManyToOne(fetch = FetchType.EAGER)
	private Usuario usuario;

	public DigitoUnico() {
	}

	private static final Logger log = LoggerFactory.getLogger(DigitoUnico.class);

	private static List<DigitoUnico> cacheCalculoDigitoUnico = new ArrayList<DigitoUnico>(10);

	/**
	 * Calcula o digito único dado um número inteiro e um fator de concatenação. Faz
	 * a implementação do cache caso o número já exista na lista
	 * 
	 * @param numeroInteiro
	 * @param fatorConcatenacao
	 * @return int
	 * @throws ExcecaoNumeroInteiroComLetras
	 * @throws ExcecaoNumeroInteiroEFatorNaoPermitido
	 */
	public int calculoDigitoUnico(StringBuilder numeroInteiro, int fatorConcatenacao)
			throws ExcecaoNumeroInteiroComLetras, ExcecaoNumeroInteiroEFatorNaoPermitido {

		DigitoUnico digitoUnico = new DigitoUnico();
		digitoUnico.setNumeroInteiro(numeroInteiro.toString());
		digitoUnico.setFatorConcatenacao(fatorConcatenacao);

		if (!cacheCalculoDigitoUnico.isEmpty()) {
			if (cacheCalculoDigitoUnico.contains(digitoUnico)) {
				cacheCalculoDigitoUnico.stream().filter(filter -> filter.equals(digitoUnico)).forEach(digito -> digitoUnico.setResultado(digito.getResultado()));
				return digitoUnico.getResultado();
			}
		}

		if (verificaSeNumeroInteroTemLetras(numeroInteiro)) {
			throw new ExcecaoNumeroInteiroComLetras();
		}

		if (numeroInteiro.toString().equals("0") || fatorConcatenacao == 0) {
			throw new ExcecaoNumeroInteiroEFatorNaoPermitido();
		}

		if (numeroInteiro.toString().toCharArray().length == 1 && fatorConcatenacao == 1) {
			int numeroInteiroUmCaracter = Integer.parseInt(numeroInteiro.toString());
			log.info("Digito único do número: " + numeroInteiro + " é: " + numeroInteiro);

			digitoUnico.setResultado(numeroInteiroUmCaracter);

			cacheCalculoDigitoUnico.add(digitoUnico);

			return numeroInteiroUmCaracter;
		}

		cacheCalculoDigitoUnico.add(digitoUnico);

		StringBuilder novoNumeroInteiro = new StringBuilder();

		for (int i = 0; i < fatorConcatenacao; i++) {
			novoNumeroInteiro.append(numeroInteiro);
		}

		log.info("Número Inteiro concatenado: {}", novoNumeroInteiro);

		int somaDosDigitos = 0;
		StringBuilder stringSomaDosDigitos = new StringBuilder("");

		while (stringSomaDosDigitos.toString().toCharArray().length != 1) {

			somaDosDigitos = 0;
			int digito = 0;

			for (char caracterDoNumeroInteiro : novoNumeroInteiro.toString().toCharArray()) {
				digito = Character.getNumericValue(caracterDoNumeroInteiro);
				somaDosDigitos += digito;
			}

			log.info("Digito único do número: " + novoNumeroInteiro + " é: " + somaDosDigitos);

			stringSomaDosDigitos = new StringBuilder(Integer.toString(somaDosDigitos));
			novoNumeroInteiro = stringSomaDosDigitos;
		}

		digitoUnico.setResultado(somaDosDigitos);
		return somaDosDigitos;
	}

	private boolean verificaSeNumeroInteroTemLetras(StringBuilder numeroInteiro) {

		boolean numeroInteiroPossuiLetra = false;

		for (int i = 0; i < numeroInteiro.length(); i++) {
			if (Character.isLetter(numeroInteiro.charAt(i)) == true) {
				log.info("Valor do número inteiro: {}", numeroInteiro);
				numeroInteiroPossuiLetra = true;
			}
		}
		return numeroInteiroPossuiLetra;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
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

	public String getNumeroInteiro() {
		return numeroInteiro;
	}

	public void setNumeroInteiro(String numeroInteiro) {
		this.numeroInteiro = numeroInteiro;
	}

	@Override
	public String toString() {
		return "DigitoUnico [id=" + id + ", numeroInteiro=" + numeroInteiro + ", fatorConcatenacao=" + fatorConcatenacao
				+ ", resultado=" + resultado + ", usuario=" + usuario + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + fatorConcatenacao;
		result = prime * result + ((numeroInteiro == null) ? 0 : numeroInteiro.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DigitoUnico other = (DigitoUnico) obj;

		if (this.numeroInteiro == other.numeroInteiro && this.fatorConcatenacao == other.fatorConcatenacao)
			return true;
		if (this.numeroInteiro != other.numeroInteiro && this.fatorConcatenacao != other.fatorConcatenacao)
			return false;

		if (fatorConcatenacao != other.fatorConcatenacao)
			return false;
		if (numeroInteiro == null) {
			if (other.numeroInteiro != null)
				return false;
		} else if (!numeroInteiro.equals(other.numeroInteiro))
			return false;
		return true;
	}
}
