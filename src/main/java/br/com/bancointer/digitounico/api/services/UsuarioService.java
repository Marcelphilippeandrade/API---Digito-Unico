package br.com.bancointer.digitounico.api.services;

import java.util.Optional;

import br.com.bancointer.digitounico.api.entidades.Usuario;

public interface UsuarioService {

	/**
	 * Retorna um usuário dado um nome
	 * 
	 * @param nome
	 * @return Optional<Usuario>
	 */
	Optional<Usuario> buscarPorNome(String nome);

	/**
	 * Retorna um usuário dado um e-mail
	 * 
	 * @param email
	 * @return Optional<Usuario>
	 */
	Optional<Usuario> buscarPorEmail(String email);

	/**
	 * Retorna um usuário dado um ID
	 * 
	 * @param id
	 * @returnOptional<Usuario>
	 */
	Optional<Usuario> buscarPorId(Long id);

	/**
	 * Retorna um Iterable de usuário
	 * 
	 * @return Iterable<Usuario>
	 */
	Iterable<Usuario> findAll();

	/**
	 * Deleta um usuário dado um ID
	 * 
	 * @param id
	 */
	void deleteById(Long id);

	/**
	 * Cadastra um novo usuário na base de dados
	 * 
	 * @param usuario
	 * @return Usuario
	 */
	Usuario persistirUsuario(Usuario usuario);
}
