package br.com.bancointer.digitounico.api.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CriptografiaUtil {

	private static final Logger log = LoggerFactory.getLogger(CriptografiaUtil.class);

	public final String ALGORITHM = "RSA";

	public CriptografiaUtil() {
	}

	/**
	 * Retorna o gerador de chaves RSA.
	 * 
	 * @return KeyPair
	 * @throws NoSuchAlgorithmException
	 */
	public KeyPair geraChaves() throws NoSuchAlgorithmException {
		final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
		keyGen.initialize(2048, new SecureRandom());
		final KeyPair key = keyGen.generateKeyPair();
		log.info("Gerando o par de chaves RSA: " + key);
		return key;
	}

	/**
	 * Retorna a chave publica em forma de String
	 * 
	 * @param chavePublica
	 * @return String
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public String getPublicKey(PublicKey chavePublica) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFectory = KeyFactory.getInstance(ALGORITHM);
		X509EncodedKeySpec x509EncodedKeySpec = keyFectory.getKeySpec(chavePublica, X509EncodedKeySpec.class);
		String chavePublicaString = null;
		if (x509EncodedKeySpec != null) {
			chavePublicaString = Base64.getEncoder().encodeToString(x509EncodedKeySpec.getEncoded());
			log.info("Transformando a chave publica em String: " + chavePublicaString);
		}
		return chavePublicaString;
	}

	/**
	 * Retorna a chave publica em forma de String
	 * 
	 * @param chavePrivada
	 * @return String
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public String getPrivateKey(PrivateKey chavePrivada) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFectory = KeyFactory.getInstance(ALGORITHM);
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = keyFectory.getKeySpec(chavePrivada, PKCS8EncodedKeySpec.class);
		String chavePrivadaString = null;

		if (pkcs8EncodedKeySpec != null) {
			chavePrivadaString = Base64.getEncoder().encodeToString(pkcs8EncodedKeySpec.getEncoded());
			log.info("Transformando a chave privada em String: " + chavePrivadaString);

		}
		return chavePrivadaString;
	}

	/**
	 * Retorna a chave publica em forma de objeto PublicKey
	 * 
	 * @param chavePublica
	 * @return PublicKey
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public PublicKey getPublicKey(String chavePublica) throws NoSuchAlgorithmException, InvalidKeySpecException {
		PublicKey publicKey = null;
		KeyFactory keyFectory = KeyFactory.getInstance(ALGORITHM);

		if (chavePublica != null) {
			X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(chavePublica.getBytes()));
			publicKey = keyFectory.generatePublic(x509EncodedKeySpec);
			log.info("Transformando a chave publica: " + chavePublica + " de String para a chave publica: " + publicKey);
		}
		return publicKey;
	}

	/**
	 * Retorna a chave publica em forma de objeto PrivateKey
	 * 
	 * @param chavePrivada
	 * @return PrivateKey
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public PrivateKey getPrivateKey(String chavePrivada) throws NoSuchAlgorithmException, InvalidKeySpecException {
		PrivateKey privateKey = null;
		KeyFactory keyFectory = KeyFactory.getInstance(ALGORITHM);

		if (chavePrivada != null) {
			PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(chavePrivada.getBytes()));
			privateKey = keyFectory.generatePrivate(pkcs8EncodedKeySpec);
			log.info("Transformando a chave privada: " + chavePrivada + " de String para a chave privada: " + privateKey);
		}
		return privateKey;
	}

	/**
	 * Retorna a criptografia do texto
	 * 
	 * @param texto
	 * @param chavePublica
	 * @return String
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws IOException
	 */
	public String criptografar(String texto, PublicKey chavePublica) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.PUBLIC_KEY, chavePublica);
		String textoCriptografado = null;

		log.info("Criptografando o texto: " + texto + " com a chave publica: " + chavePublica);

		if (texto != null) {
			byte[] bytesTexto = texto.getBytes();
			int inputLen = bytesTexto.length;
			log.info("Tamanho de bytes do texto a ser criptografado: " + inputLen);

			int inputOffLen = 0;
			int i = 0;

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			while (inputLen - inputOffLen > 0) {
				byte[] cache;
				if (inputLen - inputOffLen > 117) {
					cache = cipher.doFinal(bytesTexto, inputOffLen, 117);
					log.info("Tamanho de bytes criptografado: " + cache.length);
				} else {
					cache = cipher.doFinal(bytesTexto, inputOffLen, inputLen - inputOffLen);
					log.info("Tamanho de bytes criptografado: " + cache.length);
				}

				byteArrayOutputStream.write(cache);
				i++;
				inputOffLen = 117 * i;
			}

			byteArrayOutputStream.close();
			byte[] encryptedTexto = byteArrayOutputStream.toByteArray();
			textoCriptografado = Base64.getEncoder().encodeToString(encryptedTexto);
			log.info("Resultado da criptografia do texto: " + texto + " é: " + textoCriptografado);
		}

		return textoCriptografado;
	}

	/**
	 * Retorna a descriptografia do texto
	 * 
	 * @param textoCriptografado
	 * @param chavePrivada
	 * @return String
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws IOException
	 */
	public String descriptografar(String textoCriptografado, PrivateKey chavePrivada) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.PRIVATE_KEY, chavePrivada);

		log.info("Descriptografando o texto: " + textoCriptografado);

		byte[] bytesTextoCriptografado = Base64.getDecoder().decode(textoCriptografado);
		int inputLen = bytesTextoCriptografado.length;

		log.info("Tamanho de bytes do texto a ser descriptografado: " + inputLen);

		int inputOffLen = 0;
		int i = 0;

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		while (inputLen - inputOffLen > 0) {
			byte[] cache;

			if (inputLen - inputOffLen > 256) {
				cache = cipher.doFinal(bytesTextoCriptografado, inputOffLen, 256);
				log.info("Tamanho de bytes descriptografado: " + cache.length);
			} else {
				cache = cipher.doFinal(bytesTextoCriptografado, inputOffLen, inputLen - inputOffLen);
				log.info("Tamanho de bytes descriptografado: " + cache.length);
			}

			byteArrayOutputStream.write(cache);
			i++;
			inputOffLen = 256 * i;
		}

		byteArrayOutputStream.close();
		byte[] byteArrayTextoDescriptografado = byteArrayOutputStream.toByteArray();
		String textoDescriptografado = new String(byteArrayTextoDescriptografado);
		log.info("Resultado da descriptografia do texto: " + textoCriptografado + "\n é: " + textoDescriptografado);
		return textoDescriptografado;
	}
}
