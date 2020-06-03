package br.com.bancointer.digitounico.api.services.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.bancointer.digitounico.api.entidades.Usuario;
import br.com.bancointer.digitounico.api.repositorios.UsuarioRepository;
import br.com.bancointer.digitounico.api.services.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	private static final Logger log = LoggerFactory.getLogger(UsuarioServiceImpl.class);

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public Optional<Usuario> buscarPorNome(String nome) {
		log.info("Buscando um usuário pelo nome: {}", nome);
		return Optional.ofNullable(this.usuarioRepository.findByNome(nome));
	}

	@Override
	public Optional<Usuario> buscarPorEmail(String email) {
		log.info("Buscando um usuário pelo email: {}", email);
		return Optional.ofNullable(this.usuarioRepository.findByEmail(email));
	}

	@Override
	public Optional<Usuario> buscarPorId(Long id) {
		log.info("Buscando um usuário pelo id: {}", id);
		return this.usuarioRepository.findById(id);
	}

	@Override
	public Iterable<Usuario> findAll() {
		log.info("Buscando por usuários");
		return this.usuarioRepository.findAll();
	}

	@Override
	public void deleteById(Long id) {
		log.info("Deletar um usuário pelo id: {}", id);
		this.usuarioRepository.deleteById(id);
	}

	@Override
	public Usuario persistirUsuario(Usuario usuario) {
		log.info("Persistindo usuario: {}", usuario);
		return this.usuarioRepository.save(usuario);
	}

}
