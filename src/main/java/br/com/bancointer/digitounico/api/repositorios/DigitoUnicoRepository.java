package br.com.bancointer.digitounico.api.repositorios;

import java.util.List;

import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import br.com.bancointer.digitounico.api.entidades.DigitoUnico;

@NamedQueries({
		@NamedQuery(name = "DigitoUnicoRepository.findByUsuarioId", query = "SELECT dig FROM DigitoUnico dig WHERE dig.usuario.id = :usuarioId") })
public interface DigitoUnicoRepository extends CrudRepository<DigitoUnico, Long> {

	List<DigitoUnico> findByUsuarioId(@Param("usuarioId") Long usuarioId);

	Page<DigitoUnico> findByUsuarioId(@Param("usuarioId") Long usuarioId, Pageable pageable);
}
