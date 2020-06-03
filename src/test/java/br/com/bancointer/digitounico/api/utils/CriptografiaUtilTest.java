package br.com.bancointer.digitounico.api.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Test;

public class CriptografiaUtilTest {

	public final String ALGORITHM = "RSA";
	private static final String CHAVE_PUBLICA = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmQQRGUIlg84uMImjw3pP47LWW4s9b2TkrZ/o3S8GPQn0qzfMNIoD/S87wY/5BZVpDOFR4/kgmYNasBC8gP1l8aFAm9D+sIlXPdZkyR/fzdgjas8/bFlVq8iEh6lzpvM8oCwznciEGXghnDollqwJM0wrIOcRM+4Z0qRU0+gpnBBsK64sIgTlDpn5RMZ6bP1/8ZHfGGqrnkKh6GWd3wecYbYBlzTB06YBFDGpD8cJfXoduwvJoVP17pjKSaVdWtkCzXVNXL1ru469HnWalXDD718o5ouTNfCXxv0aaLfHMjBsdPJWtVWjgQcy8MVYkHpXIrCinbmlcEZWWHPdBRSJ4QIDAQAB";
	private static final String CHAVE_PRIVADA = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCZBBEZQiWDzi4wiaPDek/jstZbiz1vZOStn+jdLwY9CfSrN8w0igP9LzvBj/kFlWkM4VHj+SCZg1qwELyA/WXxoUCb0P6wiVc91mTJH9/N2CNqzz9sWVWryISHqXOm8zygLDOdyIQZeCGcOiWWrAkzTCsg5xEz7hnSpFTT6CmcEGwrriwiBOUOmflExnps/X/xkd8YaqueQqHoZZ3fB5xhtgGXNMHTpgEUMakPxwl9eh27C8mhU/XumMpJpV1a2QLNdU1cvWu7jr0edZqVcMPvXyjmi5M18JfG/Rpot8cyMGx08la1VaOBBzLwxViQelcisKKduaVwRlZYc90FFInhAgMBAAECggEAZT8PoUrnT8NJRMyOE3YHDn7y3zfOurpjpY69ojzPP+wqfHA7Kjh4UzrBq138Q/mMvP0KFnJtY6ZKh11LnX4NykgMXA15uI3nr/8ASSRSDf4J0U64hJTH3xaaurkg0UL4xnL45FodLk0S0DCZVo0WuV6Y2viJpXbSl1Is3torTht0cw0dzzutmbpKq2r+6rTP3N3w6/2gV1idxpsv+N653ZxeltKj4FlRznrvDVxw69SNpe30T+MhtXG9CtjL/7LwaWxAVXGpwjd8fIk/oRrSiF+N67hXI9OkICrwOkBE3i7wWRLw/G1+8SshtWStu9UvZUCyCbpU5ADGPYKMvjH4kQKBgQDyKZWTQgPZsUv7UQltggWNzul49sE+WETdjE3sWyfvUTtEyRFnb9lxheeQ1PGcBcqll21fVq31SEZVpkOo/e9kNVL21Ia5BLPwrbyM/m8Zm5mOqq78txcyEsDKWBECxLaPdGGqFqv/bqvZIYv2YOxX/b+vMX4VPNz4HbukB56GDwKBgQChwmxf8IBj3JXGwYZw48SR8gabxW8TFehLlBNnb/+WCebLVSfxH9Sr2L18zOO/dDSYvEk53zUwUrU9/eg3j0miNICkZreWnT/r8PvzY1MQrOEHSa+oNH7okwQh8JSjC+dk2pMGBr0gUCqMFKi0knr34WF/6YDIxC33KgUStRFhDwKBgQDWf2MP9s92SgclathQ+XR18ar4DImK8aC+JQL4sp2i6272NKuH1ZjjZ1p//T6tlquzFXg5lIut0gEK6KTR0Wv0dQ8xt3pF9BZ2v01eDhjWs+7GYgVxr7OKFPZTxMH8k8WpN8syX7amIJ9zSrWw3JU8M3VQdyRZJ3oLBDsqxdzynQKBgGj5JR91kbw8kC41tKtaBFy59bPtAlIea2twos8DjZeuwUm+73a5M2h59S4iQMIkBWYA+nxF79x2MAwU1DgKErzi2YDW79kcHzlcYATotiUiK75xAT9lId6IWaw01iChPv7iIXtNsDpiC9pwJbNZQ9fNOVqrC9o+BZ5adIRZYPfdAoGBAOeqMWCwz6RHZMDbf+ARgtT0WW+qppCL7U2LXZf7CN6e0n2hOeAQiPOcnyhXoaDxafm2XizDujX0WcOngCmDHge+0FRFFywRDnBdKJNYUcr2PyUg7yruLdqXwG/uKP8AqAE0iaq639Xy6D1y+N6E2F3VY+69hWQ5g1a+AoyzhOVm";
	private static final String TEXTO = "Marcel Philippe Abreu Andrade";
	
	@Test
	public void testeGeraChaves() throws NoSuchAlgorithmException {
		CriptografiaUtil criptografiaUtil = new CriptografiaUtil();
		KeyPair parDeChaves = criptografiaUtil.geraChaves();

		assertNotNull(parDeChaves.getPublic());
		assertNotNull(parDeChaves.getPrivate());
	}

	@Test
	public void testTransformarChavePublicaEmString() throws NoSuchAlgorithmException, InvalidKeySpecException {
		CriptografiaUtil criptografiaUtil = new CriptografiaUtil();
		KeyPair parDeChaves = criptografiaUtil.geraChaves();
		PublicKey chavePublica = parDeChaves.getPublic();
		String chavePublicaString = criptografiaUtil.getPublicKey(chavePublica);

		assertNotNull(chavePublicaString);
	}
	
	@Test
	public void testTransformarChavePrivadaString() throws NoSuchAlgorithmException, InvalidKeySpecException {
		CriptografiaUtil criptografiaUtil = new CriptografiaUtil();
		KeyPair parDeChaves = criptografiaUtil.geraChaves();
		PrivateKey chavePrivada = parDeChaves.getPrivate();
		String chavePrivadaString = criptografiaUtil.getPrivateKey(chavePrivada);
		
		assertNotNull(chavePrivadaString);
	}
	
	@Test
	public void testTransformarStringEmChavePublica() throws NoSuchAlgorithmException, InvalidKeySpecException {
		CriptografiaUtil criptografiaUtil = new CriptografiaUtil();
		PublicKey chavePublica = criptografiaUtil.getPublicKey(CHAVE_PUBLICA);
		
		assertNotNull(chavePublica);
	}
	
	@Test
	public void testTransformarStringEmChavePrivada() throws NoSuchAlgorithmException, InvalidKeySpecException {
		CriptografiaUtil criptografiaUtil = new CriptografiaUtil();
		PrivateKey chavePrivada = criptografiaUtil.getPrivateKey(CHAVE_PRIVADA);
		
		assertNotNull(chavePrivada);
	}
	
	@Test
	public void testCriptografarTexto() throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		CriptografiaUtil criptografiaUtil = new CriptografiaUtil();
		PublicKey chavePublica = criptografiaUtil.getPublicKey(CHAVE_PUBLICA);
		String textoCriptografado = criptografiaUtil.criptografar(TEXTO, chavePublica);
		
		assertNotNull(textoCriptografado);
		
	}
	
	@Test
	public void testDescriptografarTexto() throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		CriptografiaUtil criptografiaUtil = new CriptografiaUtil();
		PublicKey chavePublica = criptografiaUtil.getPublicKey(CHAVE_PUBLICA);
		String textoCriptografado = criptografiaUtil.criptografar(TEXTO, chavePublica);
		PrivateKey chavePrivada = criptografiaUtil.getPrivateKey(CHAVE_PRIVADA);
		String textoDescriptografado = criptografiaUtil.descriptografar(textoCriptografado, chavePrivada);
		
		assertNotNull(textoDescriptografado);
		assertEquals(TEXTO, textoDescriptografado);
	}
}
