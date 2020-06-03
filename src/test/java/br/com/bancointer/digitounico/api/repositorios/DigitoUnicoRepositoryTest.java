package br.com.bancointer.digitounico.api.repositorios;

import static org.junit.Assert.assertEquals;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.bancointer.digitounico.api.entidades.DigitoUnico;
import br.com.bancointer.digitounico.api.entidades.Usuario;
import br.com.bancointer.digitounico.api.exception.ExcecaoNumeroInteiroComLetras;
import br.com.bancointer.digitounico.api.exception.ExcecaoNumeroInteiroEFatorNaoPermitido;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class DigitoUnicoRepositoryTest {

	@Autowired
	private DigitoUnicoRepository digitoUnicoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	private Long usuarioId;

	@Before
	public void setUp() throws Exception {

		Usuario usuario = this.usuarioRepository.save(ObterDadosUsuario());
		this.usuarioId = usuario.getId();

		this.digitoUnicoRepository.save(ObterDadosDigitoUnico(usuario));
		this.digitoUnicoRepository.save(ObterDadosDigitoUnico(usuario));
	}

	@After
	public void tearDown() throws Exception {
		this.usuarioRepository.deleteAll();
	}

	@Test
	public void testBuscarDigitosUnicoPorUsuarioId() {
		List<DigitoUnico> digitoUnico = this.digitoUnicoRepository.findByUsuarioId(usuarioId);

		assertEquals(2, digitoUnico.size());
	}

	@Test
	public void testBuscarDigitosUnicoPorUsuarioIdPaginado() {
		PageRequest page = PageRequest.of(0, 10);
		Page<DigitoUnico> digitosUnico = this.digitoUnicoRepository.findByUsuarioId(usuarioId, page);

		assertEquals(2, digitosUnico.getTotalElements());
	}

	private Usuario ObterDadosUsuario() throws NoSuchAlgorithmException {
		Usuario usuario = new Usuario();
		usuario.setNome("Marcel Philippe Abreu Andrade");
		usuario.setEmail("marcelpaa@hotmail.com");

		return usuario;
	}

	private DigitoUnico ObterDadosDigitoUnico(Usuario usuario)
			throws ExcecaoNumeroInteiroComLetras, ExcecaoNumeroInteiroEFatorNaoPermitido {
		DigitoUnico digitoUnico = new DigitoUnico();
		digitoUnico.setNumeroInteiro("9875");
		digitoUnico.setFatorConcatenacao(1);
		digitoUnico.setResultado(
				digitoUnico.calculoDigitoUnico(new StringBuilder(digitoUnico.getNumeroInteiro().toString()), digitoUnico.getFatorConcatenacao()));
		digitoUnico.setUsuario(usuario);

		return digitoUnico;
	}
}
