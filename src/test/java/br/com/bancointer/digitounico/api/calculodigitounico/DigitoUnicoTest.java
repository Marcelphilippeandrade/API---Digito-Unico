package br.com.bancointer.digitounico.api.calculodigitounico;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import br.com.bancointer.digitounico.api.entidades.DigitoUnico;
import br.com.bancointer.digitounico.api.exception.ExcecaoNumeroInteiroComLetras;
import br.com.bancointer.digitounico.api.exception.ExcecaoNumeroInteiroEFatorNaoPermitido;

public class DigitoUnicoTest {
	
	@Test
	public void testeCalculoDigitoUnicoComLetras() throws ExcecaoNumeroInteiroEFatorNaoPermitido {

		StringBuilder numeroInteiro = new StringBuilder("1000a");

		try {
			new DigitoUnico().calculoDigitoUnico(numeroInteiro, 2);
			fail();
		} catch (ExcecaoNumeroInteiroComLetras e) {
			assertEquals("Valor do número inteiro não pode ter letras!", e.getMessage());
		}
	}

	@Test
	public void testeDigitoUnicoInteiroEFatocNaoPermitido() throws ExcecaoNumeroInteiroComLetras {

		StringBuilder numeroInteiro = new StringBuilder("0");

		try {
			new DigitoUnico().calculoDigitoUnico(numeroInteiro, 0);
		} catch (ExcecaoNumeroInteiroEFatorNaoPermitido e) {
			assertEquals("Valor ou fator do número inteiro não podem ser 0!", e.getMessage());
		}
	}

	@Test
	public void testeDigitoUnicoComUmCaracterEUmFator() {
		StringBuilder numeroInteiro = new StringBuilder("5");

		try {
			assertEquals(5, new DigitoUnico().calculoDigitoUnico(numeroInteiro, 1));
		} catch (ExcecaoNumeroInteiroComLetras | ExcecaoNumeroInteiroEFatorNaoPermitido e) {
		}
	}

	@Test
	public void testeDigitoUnicoComVariosCaracteresEUmFator() {
		StringBuilder numeroInteiro = new StringBuilder("9875");

		try {
			assertEquals(2, new DigitoUnico().calculoDigitoUnico(numeroInteiro, 1));
		} catch (ExcecaoNumeroInteiroComLetras | ExcecaoNumeroInteiroEFatorNaoPermitido e) {
		}
	}

	@Test
	public void testeDigitoUnicoComVariosCaracteresEFatorMaiorQueUm() {
		StringBuilder numeroInteiro = new StringBuilder("9875");

		try {
			assertEquals(8, new DigitoUnico().calculoDigitoUnico(numeroInteiro, 4));
		} catch (ExcecaoNumeroInteiroComLetras | ExcecaoNumeroInteiroEFatorNaoPermitido e) {
		}
	}
}
