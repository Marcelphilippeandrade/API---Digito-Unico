package br.com.bancointer.digitounico.api.exception;

import java.io.Serializable;

public class ExcecaoNumeroInteiroEFatorNaoPermitido extends Exception implements Serializable{

	private static final long serialVersionUID = 1L;

	public ExcecaoNumeroInteiroEFatorNaoPermitido() {
		super("Valor ou fator do número inteiro não podem ser 0!");
	}
}
