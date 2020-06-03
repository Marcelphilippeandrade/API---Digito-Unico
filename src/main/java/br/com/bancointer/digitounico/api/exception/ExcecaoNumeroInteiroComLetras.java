package br.com.bancointer.digitounico.api.exception;

import java.io.Serializable;


public class ExcecaoNumeroInteiroComLetras extends Exception implements Serializable{

	private static final long serialVersionUID = 1L;

	public ExcecaoNumeroInteiroComLetras() {
		super("Valor do número inteiro não pode ter letras!");
	}
}
