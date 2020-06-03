package br.com.bancointer.digitounico.api.controllers;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Optional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.validation.Valid;

import org.apache.commons.collections4.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.bancointer.digitounico.api.dtos.BuscaUsuarioDto;
import br.com.bancointer.digitounico.api.dtos.CadastroUsuarioDto;
import br.com.bancointer.digitounico.api.entidades.Usuario;
import br.com.bancointer.digitounico.api.response.Response;
import br.com.bancointer.digitounico.api.services.UsuarioService;
import br.com.bancointer.digitounico.api.utils.CriptografiaUtil;

@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "*")
public class UsuarioController {

	private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

	@Autowired
	private UsuarioService usuarioService;

	@Value("${paginacao.qtd_por_pagina}")
	private int qtdPorPagina;

	public UsuarioController() {
	}

	/**
	 * Cadastra um usuário na base de dados
	 * 
	 * @param nome
	 * @return ResponseEntity<Response<UsuuarioDto>>
	 * 
	 */
	@PostMapping(value = "/cadastrar")
	public ResponseEntity<Response<CadastroUsuarioDto>> cadastrar(
			@Valid @RequestBody CadastroUsuarioDto cadastroUsuarioDto, BindingResult result)
			throws NoSuchAlgorithmException {

		log.info("Cadastrando usuário: {} ", cadastroUsuarioDto.toString());
		Response<CadastroUsuarioDto> response = new Response<CadastroUsuarioDto>();

		Usuario usuario = converterDtoParaUsuario(cadastroUsuarioDto, result);

		if (result.hasErrors()) {
			log.error("Erro validando dados de cadastro usuário: {} ", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		this.usuarioService.persistirUsuario(usuario);
		response.setData(this.converterCadastroUsuarioDto(usuario));

		return ResponseEntity.ok(response);
	}

	@PostMapping(value = "/buscar/nome")
	public ResponseEntity<Response<BuscaUsuarioDto>> buscarPorNome(@Valid @RequestBody BuscaUsuarioDto buscaUsuarioDto,
			BindingResult result) {
		log.info("Buscar usuário por nome: {} ", buscaUsuarioDto.getNome());
		Response<BuscaUsuarioDto> response = new Response<BuscaUsuarioDto>();
		Usuario usuario = new Usuario();

		Iterable<Usuario> usuarios = usuarioService.findAll();
		List<Usuario> listaUsuarios = IteratorUtils.toList(usuarios != null ? usuarios.iterator() : null);

		CriptografiaUtil criptografiaUtil = new CriptografiaUtil();
		PrivateKey chavePrivada = null;

		for (Usuario usuarioUsuario : listaUsuarios) {
			try {
				chavePrivada = criptografiaUtil.getPrivateKey(buscaUsuarioDto.getChavePrivada());
				String nomeDescriptografado = criptografiaUtil.descriptografar(usuarioUsuario.getNome(), chavePrivada);

				if (!buscaUsuarioDto.getNome().equals(nomeDescriptografado)) {
					log.info("Usuário não cadastrado para o nome: {} ", buscaUsuarioDto.getNome());
					response.getErros().add("Usuário não encontrado para o nome: {} " + buscaUsuarioDto.getNome());
					return ResponseEntity.badRequest().body(response);
				}

				usuario = usuarioUsuario;

				response.setData(this.converterBuscaUsuarioDto(usuario, buscaUsuarioDto.getChavePrivada(), result, response));

				if (result.hasErrors()) {
					return ResponseEntity.badRequest().body(response);
				}

			} catch (NoSuchAlgorithmException e) {
				log.info("Algorítimo criptografico indisponível." + chavePrivada.getAlgorithm());
				result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			} catch (InvalidKeySpecException e) {
				log.info("Chave privada inválida." + buscaUsuarioDto.getChavePrivada());
				result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			} catch (InvalidKeyException e) {
				log.info("Codificação de chave privada inválida." + buscaUsuarioDto.getChavePrivada());
				result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			} catch (NoSuchPaddingException e) {
				log.info("Mecanismo de preenchimento de chave privada inválido.");
				result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			} catch (IllegalBlockSizeException e) {
				log.info("Os dados da criptografia não foram preenchidos corretamente.");
				result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			} catch (BadPaddingException e) {
				log.info("Comprimento de blocos da cifra está incorreto.");
				result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			} catch (IOException e) {
				log.info("Falha na entrada ou saida de dados.");
				result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			}
		}

		return ResponseEntity.ok(response);
	}

	@PostMapping(value = "/buscar/email")
	public ResponseEntity<Response<BuscaUsuarioDto>> buscarPorEmail(@Valid @RequestBody BuscaUsuarioDto buscaUsuarioDto,
			BindingResult result) {

		log.info("Buscar usuário por email: {} ", buscaUsuarioDto.getEmail());
		Response<BuscaUsuarioDto> response = new Response<BuscaUsuarioDto>();
		Usuario usuario = new Usuario();

		Iterable<Usuario> usuarios = usuarioService.findAll();
		List<Usuario> listaUsuarios = IteratorUtils.toList(usuarios.iterator());

		CriptografiaUtil criptografiaUtil = new CriptografiaUtil();
		PrivateKey chavePrivada = null;

		for (Usuario usuarioUsuario : listaUsuarios) {

			try {

				chavePrivada = criptografiaUtil.getPrivateKey(buscaUsuarioDto.getChavePrivada());
				String emailDescriptografado = criptografiaUtil.descriptografar(usuarioUsuario.getEmail(),
						chavePrivada);

				if (!buscaUsuarioDto.getEmail().equals(emailDescriptografado)) {
					log.info("Usuário não cadastrado para o email: {} ", buscaUsuarioDto.getEmail());
					response.getErros().add("Usuário não encontrado para o email: {} " + buscaUsuarioDto.getEmail());
					return ResponseEntity.badRequest().body(response);
				}

				usuario = usuarioUsuario;

				response.setData(this.converterBuscaUsuarioDto(usuario, buscaUsuarioDto.getChavePrivada(), result, response));

				if (result.hasErrors()) {
					return ResponseEntity.badRequest().body(response);
				}

			} catch (NoSuchAlgorithmException e) {
				log.info("Algorítimo criptografico indisponível." + chavePrivada.getAlgorithm());
				result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			} catch (InvalidKeyException e) {
				log.info("Codificação de chave privada inválida." + buscaUsuarioDto.getChavePrivada());
				result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			} catch (NoSuchPaddingException e) {
				log.info("Mecanismo de preenchimento de chave privada inválido.");
				result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			} catch (IllegalBlockSizeException e) {
				log.info("Os dados da criptografia não foram preenchidos corretamente.");
				result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			} catch (BadPaddingException e) {
				log.info("Comprimento de blocos da cifra está incorreto.");
				result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			} catch (IOException e) {
				log.info("Falha na entrada ou saida de dados.");
				result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			} catch (InvalidKeySpecException e) {
				log.info("Chave privada inválida." + buscaUsuarioDto.getChavePrivada());
				result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			}

		}

		return ResponseEntity.ok(response);
	}

	/**
	 * Atualiza os dados de um usuário
	 * 
	 * @param id
	 * @param usuarioDto
	 * @param result
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	@PutMapping(value = "/atualizar/{id}")
	public ResponseEntity<Response<CadastroUsuarioDto>> atualizar(@PathVariable("id") Long id,
			@Valid @RequestBody CadastroUsuarioDto cadastroUsuarioDto, BindingResult result)
			throws NoSuchAlgorithmException {

		log.info("Atualizando usuário: {} ", cadastroUsuarioDto.toString());
		Response<CadastroUsuarioDto> response = new Response<CadastroUsuarioDto>();

		Optional<Usuario> usuario = this.usuarioService.buscarPorId(id);

		if (!usuario.isPresent()) {
			result.addError(new ObjectError("Usuario", "Usuário não encontrado. "));
			result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		this.atualizarDadosUsuario(usuario.get(), cadastroUsuarioDto, result);

		if (result.hasErrors()) {
			log.error("Erro validando Usuario: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		this.usuarioService.persistirUsuario(usuario.get());
		response.setData(converterCadastroUsuarioDto(usuario.get()));

		return ResponseEntity.ok(response);
	}

	/**
	 * Deleta um usuário
	 * 
	 * @param id
	 * @return ResponseEntity<Response<UsuarioDto>>
	 */
	@DeleteMapping(value = "/deletar/{id}")
	public ResponseEntity<Response<CadastroUsuarioDto>> deletarPorId(@PathVariable("id") Long id) {
		log.info("Deletar usuario por id: {} ", id);
		Response<CadastroUsuarioDto> response = new Response<CadastroUsuarioDto>();

		Optional<Usuario> usuario = usuarioService.buscarPorId(id);

		if (!usuario.isPresent()) {
			log.info("Usuário não localizado para o id: {} ", id);
			response.getErros().add("Usuário não localizado para o id: {} " + id);
			return ResponseEntity.badRequest().body(response);
		}

		usuarioService.deleteById(id);

		response.setData(this.converterCadastroUsuarioDto(usuario.get()));
		return ResponseEntity.ok(response);
	}

	/**
	 * Converte um usuário em um DTO durante o cadastro
	 * 
	 * @param usuario
	 * @return UsuarioDto
	 */
	private CadastroUsuarioDto converterCadastroUsuarioDto(Usuario usuario) {
		CadastroUsuarioDto cadastroUsuarioDto = new CadastroUsuarioDto();
		cadastroUsuarioDto.setId(usuario.getId());
		cadastroUsuarioDto.setNome(usuario.getNome());
		cadastroUsuarioDto.setEmail(usuario.getEmail());

		return cadastroUsuarioDto;
	}

	/**
	 * Converte um usuário em um DTO durante a busca
	 * 
	 * @param usuario
	 * @return BuscaUsuarioDto
	 */
	private BuscaUsuarioDto converterBuscaUsuarioDto(Usuario usuario, String chavePrivada, BindingResult result,
			Response<BuscaUsuarioDto> response) {
		BuscaUsuarioDto buscaUsuarioDto = new BuscaUsuarioDto();
		CriptografiaUtil criptografia = new CriptografiaUtil();
		PrivateKey chavePrivadaChave = null;

		try {
			chavePrivadaChave = criptografia.getPrivateKey(chavePrivada);

			if (usuario != null) {
				String nomeDescriptografado = criptografia.descriptografar(usuario.getNome(), chavePrivadaChave);
				String emailDescriptografado = criptografia.descriptografar(usuario.getEmail(), chavePrivadaChave);

				buscaUsuarioDto.setEmail(emailDescriptografado);
				buscaUsuarioDto.setNome(nomeDescriptografado);
				buscaUsuarioDto.setId(usuario.getId());
			}

		} catch (InvalidKeyException e) {
			log.info("Codificação de chave privada inválida." + buscaUsuarioDto.getChavePrivada());
			result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
		} catch (InvalidKeySpecException e) {
			log.info("Chave privada inválida." + buscaUsuarioDto.getChavePrivada());
			result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
		} catch (NoSuchAlgorithmException e) {
			log.info("Algorítimo criptografico indisponível." + chavePrivadaChave.getAlgorithm());
			result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
		} catch (NoSuchPaddingException e) {
			log.info("Mecanismo de preenchimento de chave privada inválido.");
			result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
		} catch (IllegalBlockSizeException e) {
			log.info("Os dados da criptografia não foram preenchidos corretamente.");
			result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
		} catch (BadPaddingException e) {
			log.info("Comprimento de blocos da cifra está incorreto.");
			result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
		} catch (IOException e) {
			log.info("Falha na entrada ou saida de dados.");
			result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
		}

		return buscaUsuarioDto;
	}

	/**
	 * Converte os dados de DTO para usuário
	 * 
	 * @param cadastroUsuarioDto
	 * @param result
	 * @return Usuario
	 * @throws NoSuchAlgorithmException
	 */
	private Usuario converterDtoParaUsuario(@Valid CadastroUsuarioDto cadastroUsuarioDto, BindingResult result) {
		CriptografiaUtil criptografiaUtil = new CriptografiaUtil();
		Usuario usuario = new Usuario();
		PublicKey chavePublica = null;
		try {
			chavePublica = criptografiaUtil.getPublicKey(cadastroUsuarioDto.getChavePublica());
			String nomeCriptografado = criptografiaUtil.criptografar(cadastroUsuarioDto.getNome(), chavePublica);
			String emailCriptografado = criptografiaUtil.criptografar(cadastroUsuarioDto.getEmail(), chavePublica);
			
			usuario.setNome(nomeCriptografado);
			usuario.setEmail(emailCriptografado);
		} catch (NoSuchAlgorithmException e) {
			log.info("Algorítimo criptografico indisponível." + chavePublica.getAlgorithm());
			result.addError(new ObjectError("algorítimo", "Algorítimo criptografico indisponível."));
		} catch (InvalidKeySpecException e) {
			log.info("Chave publica inválida." + cadastroUsuarioDto.getChavePublica());
			result.addError(new ObjectError("chavePublica", "Chave publica inválida."));
		} catch (InvalidKeyException e) {
			log.info("Codificação de chave publica inválida." + cadastroUsuarioDto.getChavePublica());
			result.addError(new ObjectError("codificacaoChavePublica", "Codificação de chave publica inválida."));
		} catch (NoSuchPaddingException e) {
			log.info("Mecanismo de preenchimento de chave publica inválido.");
			result.addError(new ObjectError("preenchimentoChavePublica",
					"Mecanismo de preenchimento de chave publica inválido."));
		} catch (IllegalBlockSizeException e) {
			log.info("Os dados da criptografia não foram preenchidos corretamente.");
			result.addError(
					new ObjectError("dadosIncorretos", "Os dados da criptografia não foram preenchidos corretamente."));
		} catch (BadPaddingException e) {
			log.info("Comprimento de blocos da cifra está incorreto.");
			result.addError(new ObjectError("comprimentoBlocoCifra", "Comprimento de blocos da cifra está incorreto."));
		} catch (IOException e) {
			log.info("Falha na entrada ou saida de dados.");
			result.addError(new ObjectError("entradaSaida", "Falha na entrada ou saida de dados."));
		}

		return usuario;
	}

	/**
	 * Transforma os dados da requisição do usuárioDto em objeto usuário
	 * 
	 * @param usuario
	 * @param usuarioDto
	 * @param result
	 */
	private void atualizarDadosUsuario(Usuario usuario, @Valid CadastroUsuarioDto cadastroUsuarioDto,
			BindingResult result) throws NoSuchAlgorithmException {
		CriptografiaUtil criptografiaUtil = new CriptografiaUtil();
		try {
			PublicKey chavePublica = criptografiaUtil.getPublicKey(cadastroUsuarioDto.getChavePublica());
			String nomeCriptografado = criptografiaUtil.criptografar(cadastroUsuarioDto.getNome(), chavePublica);
			String emailCriptografado = criptografiaUtil.criptografar(cadastroUsuarioDto.getEmail(), chavePublica);

			usuario.setNome(nomeCriptografado);
			usuario.setEmail(emailCriptografado);

		} catch (InvalidKeySpecException e) {
			log.info("Chave publica inválida." + cadastroUsuarioDto.getChavePublica());
			result.addError(new ObjectError("chavePublica", "Chave publica inválida."));
		} catch (InvalidKeyException e) {
			log.info("Codificação de chave publica inválida." + cadastroUsuarioDto.getChavePublica());
			result.addError(new ObjectError("codificacaoChavePublica", "Codificação de chave publica inválida."));
		} catch (NoSuchPaddingException e) {
			log.info("Mecanismo de preenchimento de chave publica inválido.");
			result.addError(new ObjectError("preenchimentoChavePublica",
					"Mecanismo de preenchimento de chave publica inválido."));
		} catch (IllegalBlockSizeException e) {
			log.info("Comprimento de blocos da cifra está incorreto.");
			result.addError(
					new ObjectError("comprimentoChavePublica", "Comprimento de blocos da cifra está incorreto."));
		} catch (BadPaddingException e) {
			log.info("Comprimento de blocos da cifra está incorreto.");
			result.addError(new ObjectError("comprimentoBlocoCifra", "Comprimento de blocos da cifra está incorreto."));
		} catch (IOException e) {
			log.info("Falha na entrada ou saida de dados.");
			result.addError(new ObjectError("entradaSaida", "Falha na entrada ou saida de dados."));
		}
	}
}
