package br.com.bancointer.digitounico.api.repositorios;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.bancointer.digitounico.api.entidades.Usuario;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

	Usuario findByNome(String nome);

	Usuario findByEmail(String email);

	Iterable<Usuario> findAll();

	Optional<Usuario> findById(Long id);

	void deleteById(Long id);

}
