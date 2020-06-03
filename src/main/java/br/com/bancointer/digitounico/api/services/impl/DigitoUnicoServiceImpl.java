package br.com.bancointer.digitounico.api.services.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import br.com.bancointer.digitounico.api.entidades.DigitoUnico;
import br.com.bancointer.digitounico.api.repositorios.DigitoUnicoRepository;
import br.com.bancointer.digitounico.api.services.DigitoUnicoService;

@Service
public class DigitoUnicoServiceImpl implements DigitoUnicoService {

	private static final Logger log = LoggerFactory.getLogger(DigitoUnicoServiceImpl.class);

	@Autowired
	private DigitoUnicoRepository digitoUnicoRepository;

	@Override
	public Page<DigitoUnico> buscarUsuarioPorId(Long usuarioId, PageRequest pageRequest) {
		log.info("Buscando um digito unico para o usuário: {}", usuarioId);
		return this.digitoUnicoRepository.findByUsuarioId(usuarioId, pageRequest);
	}

	@Override
	public List<DigitoUnico> buscarUsuarioPorId(Long usuarioId) {
		log.info("Buscando um digito unico para o usuário: {}", usuarioId);
		return this.digitoUnicoRepository.findByUsuarioId(usuarioId);
	}

	@Override
	public Optional<DigitoUnico> buscarPorId(Long id) {
		log.info("Buscando um digito unico pelo ID: {}", id);
		return this.digitoUnicoRepository.findById(id);
	}

	@Override
	public DigitoUnico persistir(DigitoUnico digitoUnico) {
		log.info("Persistir o digito unico: {}", digitoUnico);
		return this.digitoUnicoRepository.save(digitoUnico);
	}

	@Override
	public void remover(Long id) {
		log.info("Removendo o digito unico ID {}", id);
		this.digitoUnicoRepository.deleteById(id);
	}
}
