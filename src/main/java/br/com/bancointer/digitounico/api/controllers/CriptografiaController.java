package br.com.bancointer.digitounico.api.controllers;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.bancointer.digitounico.api.dtos.CriptografiaDto;
import br.com.bancointer.digitounico.api.response.Response;
import br.com.bancointer.digitounico.api.utils.CriptografiaUtil;

@RestController
@RequestMapping("/api/digito-unico/criptografia")
@CrossOrigin(origins = "*")
public class CriptografiaController {

	private static final Logger log = LoggerFactory.getLogger(CriptografiaController.class);

	public CriptografiaController() {
	}

	@GetMapping(value = "gerar/chaves")
	public ResponseEntity<Response<CriptografiaDto>> gerarParChaves() {
		Response<CriptografiaDto> response = new Response<CriptografiaDto>();
		CriptografiaUtil criptografiaUtil = new CriptografiaUtil();
		String chavePublicaString = null;
		String chavePrivadaString = null;

		try {
			KeyPair parDeChaves = criptografiaUtil.geraChaves();
			PublicKey chavePublica = parDeChaves.getPublic();
			chavePublicaString = criptografiaUtil.getPublicKey(chavePublica);

			PrivateKey chavePrivada = parDeChaves.getPrivate();
			chavePrivadaString = criptografiaUtil.getPrivateKey(chavePrivada);

		} catch (NoSuchAlgorithmException e) {
			log.info("Criptografia não suporta o algorítimo especificado.");
			response.getErros().add("Criptografia não suporta o algorítimo especificado.");
			return ResponseEntity.badRequest().body(response);
		} catch (InvalidKeySpecException e) {
			log.info("Chave especificada não pode ser processada.");
			response.getErros().add("Chave especificada não pode ser processada.");
			return ResponseEntity.badRequest().body(response);
		}

		response.setData(this.converterCriptografiaDto(chavePublicaString, chavePrivadaString));
		return ResponseEntity.ok(response);
	}

	private CriptografiaDto converterCriptografiaDto(String chavePublica, String chavePrivada) {
		CriptografiaDto criptografiaDto = new CriptografiaDto();
		criptografiaDto.setChavePublica(chavePublica);
		criptografiaDto.setChavePrivada(chavePrivada);
		return criptografiaDto;
	}
}
