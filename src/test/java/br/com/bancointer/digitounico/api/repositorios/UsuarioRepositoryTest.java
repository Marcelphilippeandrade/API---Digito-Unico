package br.com.bancointer.digitounico.api.repositorios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.bancointer.digitounico.api.entidades.Usuario;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UsuarioRepositoryTest {

	@Autowired
	private UsuarioRepository usuarioRepository;

	private static final String NOME = "Marcel Philippe Abreu Andrade";
	private static final String EMAIL = "marcelpaa@hotmail.com";

	@Before
	public void setUp() throws Exception {
		this.usuarioRepository.save(obterDadosUsuario());
	}

	@After
	public void tearDown() {
		this.usuarioRepository.deleteAll();
	}

	@Test
	public void testBuscarUsuarioPorEmail() {
		Usuario usuario = this.usuarioRepository.findByEmail(EMAIL);
		assertEquals(EMAIL, usuario.getEmail());
	}

	@Test
	public void testBuscarUsuarioPorNome() {
		Usuario usuario = this.usuarioRepository.findByNome(NOME);
		assertEquals(NOME, usuario.getNome());
	}

	@Test
	public void testBuscarUsuarioPorEmailIvalido() {
		Usuario usuario = this.usuarioRepository.findByEmail("marcel.andrade@hotmail.com");
		assertNull(usuario);
	}

	@Test
	public void testDeletarUsuarioPorId() {
		Usuario usuario = this.usuarioRepository.findByNome(NOME);
		this.usuarioRepository.deleteById(usuario.getId());
		Usuario usuarioDeletado = this.usuarioRepository.findByNome(NOME);
		assertNull(usuarioDeletado);

	}

	private Usuario obterDadosUsuario() {
		Usuario usuario = new Usuario();
		usuario.setNome(NOME);
		usuario.setEmail(EMAIL);
		usuario.setId(1L);

		return usuario;
	}
}
