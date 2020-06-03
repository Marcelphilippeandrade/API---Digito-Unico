package br.com.bancointer.digitounico.api.controllers;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.bancointer.digitounico.api.dtos.BuscaDigitoUnicoDto;
import br.com.bancointer.digitounico.api.dtos.CadastroDigitoUnicoDto;
import br.com.bancointer.digitounico.api.entidades.DigitoUnico;
import br.com.bancointer.digitounico.api.entidades.Usuario;
import br.com.bancointer.digitounico.api.exception.ExcecaoNumeroInteiroComLetras;
import br.com.bancointer.digitounico.api.exception.ExcecaoNumeroInteiroEFatorNaoPermitido;
import br.com.bancointer.digitounico.api.response.Response;
import br.com.bancointer.digitounico.api.services.DigitoUnicoService;
import br.com.bancointer.digitounico.api.services.UsuarioService;

@RestController
@RequestMapping("/api/digito-unico")
@CrossOrigin(origins = "*")
public class DigitoUnicoController {

	private static final Logger log = LoggerFactory.getLogger(DigitoUnicoController.class);

	@Autowired
	private DigitoUnicoService digitoUnicoService;

	@Autowired
	private UsuarioService usuarioService;

	@Value("${paginacao.qtd_por_pagina}")
	private int qtdPorPagina;

	public DigitoUnicoController() {
	}

	/**
	 * Adiciona um novo digito único na base de dados e pode associar a um usuário
	 * 
	 * @param cadastroDigitoUnicoDto
	 * @param result
	 * @return ResponseEntity<Response<DigitoUnicoDto>>
	 * @throws NoSuchAlgorithmException
	 */
	@PostMapping(value = "/cadastrar")
	public ResponseEntity<Response<CadastroDigitoUnicoDto>> cadastrar(
			@Valid @RequestBody CadastroDigitoUnicoDto DigitoUnicoDto, BindingResult result)
			throws NoSuchAlgorithmException {

		log.info("Cadastrando o digito único: {}", DigitoUnicoDto.toString());
		Response<CadastroDigitoUnicoDto> response = new Response<CadastroDigitoUnicoDto>();

		try {
			Usuario usuario = validarUsuario(DigitoUnicoDto, result, response);
			DigitoUnico digitoUnico = converterDtoParaDigitoUnico(DigitoUnicoDto, result, response);

			if (result.hasErrors()) {
				return ResponseEntity.badRequest().body(response);
			}

			int resultadoCalculoDigitoUnico = digitoUnico.calculoDigitoUnico(
					new StringBuilder(DigitoUnicoDto.getNumeroInteiro().toString()),
					DigitoUnicoDto.getFatorConcatenacao());

			if (usuario != null) {
				List<DigitoUnico> digitoUnicoDoUsuario = digitoUnicoService.buscarUsuarioPorId(usuario.getId());

				if (!digitoUnicoDoUsuario.isEmpty()) {
					for (DigitoUnico digito : digitoUnicoDoUsuario) {
						if (digito.getNumeroInteiro().equals(DigitoUnicoDto.getNumeroInteiro()) && digito.getFatorConcatenacao() == DigitoUnicoDto.getFatorConcatenacao()) {
							result.addError(new ObjectError("digitoUnico", "Digito único já cadastrado para o usuário."));
							result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
							return ResponseEntity.badRequest().body(response);
						} else {

							digitoUnico.setUsuario(usuario);
							digitoUnico.setResultado(resultadoCalculoDigitoUnico);
							this.digitoUnicoService.persistir(digitoUnico);
							response.setData(this.converterDigitoUnicoDto(digitoUnico));
							return ResponseEntity.ok(response);
						}
					}
				} else {
					digitoUnico.setUsuario(usuario);
					digitoUnico.setResultado(resultadoCalculoDigitoUnico);
					this.digitoUnicoService.persistir(digitoUnico);
					response.setData(this.converterDigitoUnicoDto(digitoUnico));
				}
			} else {
				digitoUnico.setResultado(resultadoCalculoDigitoUnico);
				this.digitoUnicoService.persistir(digitoUnico);
				response.setData(this.converterDigitoUnicoDto(digitoUnico));
			}

		} catch (ExcecaoNumeroInteiroComLetras e) {
			result.addError(new ObjectError("numeroInteiro", "Número Inteiro não pode ter letras."));
			result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		} catch (ExcecaoNumeroInteiroEFatorNaoPermitido e) {
			result.addError(new ObjectError("fator", "Fator e númreo inteiro não pode ser 0."));
			result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		return ResponseEntity.ok(response);
	}

	/**
	 * Busca todos os digitos unico de um usuário dado um ID
	 * 
	 * @param id
	 * @param pag
	 * @param ord
	 * @param dir
	 * @return ResponseEntity<Response<Page<BuscaDigitoUnicoDto>>>
	 */
	@GetMapping(value = "/buscar/{id}")
	public ResponseEntity<Response<Page<BuscaDigitoUnicoDto>>> buscarPorId(@PathVariable("id") Long id,
			@RequestParam(value = "pag", defaultValue = "0") int pag,
			@RequestParam(value = "ord", defaultValue = "id") String ord,
			@RequestParam(value = "dir", defaultValue = "DESC") String dir) {

		log.info("Buscar digitos unico do usuário: {}, página: {} ", id, pag);
		Response<Page<BuscaDigitoUnicoDto>> response = new Response<Page<BuscaDigitoUnicoDto>>();

		PageRequest pageRequest = PageRequest.of(pag, this.qtdPorPagina, Direction.valueOf(dir), ord);
		Page<DigitoUnico> digitosUnico = this.digitoUnicoService.buscarUsuarioPorId(id, pageRequest);

		if (!digitosUnico.isEmpty()) {
			if (digitosUnico.get().findAny() != null) {
				DigitoUnico digitoUnico = digitosUnico.get().findAny().get();
				Usuario usuario = validarUsuario(digitoUnico, response);

				if (usuario == null) {
					return ResponseEntity.badRequest().body(response);
				}

				Page<BuscaDigitoUnicoDto> BuscaDigitoUnicoDto = digitosUnico
						.map(digito -> this.converterBuscaDigitoUnicoDto(digito));
				response.setData(BuscaDigitoUnicoDto);
			}
		} else {
			response.getErros().add("Usuário não possui digitos unico cadastrado. ID: {} " + id);
			return ResponseEntity.badRequest().body(response);
		}

		return ResponseEntity.ok(response);
	}

	/**
	 * Valida um usuário verificando se ele existe e é válido na base do sistema
	 * dado um digitoUnico
	 * 
	 * @param digitoUnico
	 * @param result
	 * @param response
	 * @return Usuario
	 */
	private Usuario validarUsuario(@Valid DigitoUnico digitoUnico, Response<Page<BuscaDigitoUnicoDto>> response) {

		Usuario usuario = null;

		if (digitoUnico.getUsuario() != null) {
			log.info("Validando usuário id: {} ", digitoUnico.getUsuario().getId());
			Optional<Usuario> usuarioBuscado = this.usuarioService.buscarPorId(digitoUnico.getUsuario().getId());

			if (usuarioBuscado.isPresent()) {
				usuario = usuarioBuscado.get();
				return usuario;
			} else {
				response.getErros()
						.add("Usuário não encontrado. ID inexistente: {} " + digitoUnico.getUsuario().getId());
			}
		}

		return usuario;
	}

	/**
	 * Converte um digitoUnico em um DTO
	 * 
	 * @param digitoUnico
	 * @return BuscaDigitoUnicoDto
	 */
	private BuscaDigitoUnicoDto converterBuscaDigitoUnicoDto(DigitoUnico digitoUnico) {
		BuscaDigitoUnicoDto BuscaDigitoUnicoDto = new BuscaDigitoUnicoDto();

		BuscaDigitoUnicoDto.setId(digitoUnico.getId());
		BuscaDigitoUnicoDto.setNumeroInteiro(digitoUnico.getNumeroInteiro());
		BuscaDigitoUnicoDto.setFatorConcatenacao(digitoUnico.getFatorConcatenacao());
		BuscaDigitoUnicoDto.setResultado(digitoUnico.getResultado());

		return BuscaDigitoUnicoDto;
	}

	/**
	 * Converte um digitoUnico em um DTO
	 * 
	 * @param digitoUnico
	 * @return DigitoUnicoDto
	 */
	private CadastroDigitoUnicoDto converterDigitoUnicoDto(DigitoUnico digitoUnico) {
		CadastroDigitoUnicoDto CadastroDigitoUnicoDto = new CadastroDigitoUnicoDto();
		CadastroDigitoUnicoDto.setId(digitoUnico.getId());
		CadastroDigitoUnicoDto.setNumeroInteiro(digitoUnico.getNumeroInteiro());
		CadastroDigitoUnicoDto.setFatorConcatenacao(digitoUnico.getFatorConcatenacao());

		if (digitoUnico.getUsuario() != null) {
			CadastroDigitoUnicoDto.setUsuarioId(digitoUnico.getUsuario().getId());
		}

		return CadastroDigitoUnicoDto;
	}

	/**
	 * Valida um usuário verificando se ele existe e é válido na base do sistema
	 * dado um digitoUnicoDto
	 * 
	 * @param digitoUnicoDto
	 * @param result
	 * @paran response
	 * @return Usuario
	 */
	private Usuario validarUsuario(@Valid CadastroDigitoUnicoDto digitoUnicoDto, BindingResult result,
			Response<CadastroDigitoUnicoDto> response) {

		Usuario usuario = null;

		if (digitoUnicoDto.getUsuarioId() != null) {
			log.info("Validando usuário id: {} ", digitoUnicoDto.getUsuarioId());
			Optional<Usuario> usuarioBuscado = this.usuarioService.buscarPorId(digitoUnicoDto.getUsuarioId());
			if (usuarioBuscado.isPresent()) {
				usuario = usuarioBuscado.get();
				return usuario;
			} else {
				result.addError(new ObjectError("usuario", "Usuario não encontrado. ID inexistente."));
				result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			}
		}

		return usuario;
	}

	/**
	 * Converte os dados de Dto para digito único
	 * 
	 * @param cadastroDigitoUnicoDto
	 * @param result
	 * @return DigitoUnico
	 * @throws ExcecaoNumeroInteiroEFatorNaoPermitido
	 * @throws ExcecaoNumeroInteiroComLetras
	 */
	private DigitoUnico converterDtoParaDigitoUnico(@Valid CadastroDigitoUnicoDto digitoUnicoDto, BindingResult result,
			Response<CadastroDigitoUnicoDto> response)
			throws ExcecaoNumeroInteiroComLetras, ExcecaoNumeroInteiroEFatorNaoPermitido {

		DigitoUnico digitoUnico = new DigitoUnico();

		if (result.hasErrors()) {

			return digitoUnico;
		}

		if (digitoUnicoDto.getUsuarioId() != null) {
			List<DigitoUnico> digitosUnico = this.digitoUnicoService.buscarUsuarioPorId(digitoUnicoDto.getUsuarioId());

			for (DigitoUnico digito : digitosUnico) {
				if (digito.getUsuario() != null) {
					if (digito.getNumeroInteiro().equals(digitoUnicoDto.getNumeroInteiro()) && digito.getFatorConcatenacao() == digitoUnicoDto.getFatorConcatenacao()) {
						result.addError(new ObjectError("digitoUnico", "Digito único já cadastrado para o usuário."));
						result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
					}
				}
			}
		}

		if (digitoUnicoDto.getId() != null) {
			Optional<DigitoUnico> digitoUnicoOptional = this.digitoUnicoService.buscarPorId(digitoUnicoDto.getId());

			if (digitoUnicoOptional.isPresent()) {
				digitoUnico = digitoUnicoOptional.get();
			} else {
				result.addError(new ObjectError("digitoUnico", "Digito único não encontrado."));
				result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
			}
		} else {
			if (digitoUnicoDto.getUsuarioId() != null) {
				digitoUnico.setUsuario(new Usuario());
				digitoUnico.getUsuario().setId(digitoUnicoDto.getUsuarioId());
			}
		}

		digitoUnico.setNumeroInteiro(digitoUnicoDto.getNumeroInteiro());
		digitoUnico.setFatorConcatenacao(digitoUnicoDto.getFatorConcatenacao());

		return digitoUnico;
	}
}
