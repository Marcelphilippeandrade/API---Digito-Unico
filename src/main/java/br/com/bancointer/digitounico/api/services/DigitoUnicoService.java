package br.com.bancointer.digitounico.api.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import br.com.bancointer.digitounico.api.entidades.DigitoUnico;

public interface DigitoUnicoService {

	/**
	 * Retorna uma lista paginada de digitos unicos de um determinado usuário
	 * 
	 * @param usuarioId
	 * @param pageRequest
	 * @return Page<DigitoUnico>
	 */
	Page<DigitoUnico> buscarUsuarioPorId(Long usuarioId, PageRequest pageRequest);

	/**
	 * Retorna uma lista de digitos unicos de um determinado usuário
	 * 
	 * @param usuarioId
	 * @return List<DigitoUnico>
	 */
	List<DigitoUnico> buscarUsuarioPorId(Long usuarioId);

	/**
	 * Retorna um digito unico por ID
	 * 
	 * @param id
	 * @return Optional<DigitoUnico>
	 */
	Optional<DigitoUnico> buscarPorId(Long id);

	/**
	 * Persiste um digito unico na base de dados
	 * 
	 * @param digitoUnico
	 * @return DigitoUnico
	 */
	DigitoUnico persistir(DigitoUnico digitoUnico);

	/**
	 * Remove um digito unico na base de dados
	 * 
	 * @param id
	 */
	void remover(Long id);

}
