package br.com.bancointer.digitounico.api.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.bancointer.digitounico.api.entidades.Usuario;
import br.com.bancointer.digitounico.api.repositorios.UsuarioRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@MockBean
	private UsuarioRepository usuarioRepository;

	@Autowired
	private UsuarioService usuarioService;

	@Before
	public void setUp() {
		BDDMockito.given(this.usuarioRepository.save(Mockito.any(Usuario.class))).willReturn(new Usuario());
		BDDMockito.given(this.usuarioRepository.findById(Mockito.anyLong()))
				.willReturn(Optional.ofNullable(new Usuario()));
		BDDMockito.given(this.usuarioRepository.findByEmail(Mockito.anyString())).willReturn(new Usuario());
		BDDMockito.given(this.usuarioRepository.findByNome(Mockito.anyString())).willReturn(new Usuario());
	}

	@Test
	public void testPersisteUsuario() {
		Usuario usuario = this.usuarioService.persistirUsuario(new Usuario());
		assertNotNull(usuario);
	}

	@Test
	public void testBuscarUsuarioPorId() {
		Optional<Usuario> usuario = this.usuarioService.buscarPorId(1L);
		assertTrue(usuario.isPresent());
	}

	@Test
	public void testBuscarUsuarioPorEmail() {
		Optional<Usuario> usuario = this.usuarioService.buscarPorEmail("marcelpaa@hotmail.com");
		assertTrue(usuario.isPresent());
	}

	@Test
	public void testBuscarUsuarioPorNome() {
		Optional<Usuario> usuario = this.usuarioService.buscarPorNome("Marcel Philippe Abreu Andrade");
		assertNotNull(usuario.get());
	}

	@Test
	public void testDeletarUsuarioPorId() {
		when(this.usuarioRepository.findById((Mockito.anyLong()))).thenReturn(Optional.ofNullable(new Usuario()));
		usuarioService.deleteById(Mockito.anyLong());
		verify(this.usuarioRepository).deleteById(Mockito.anyLong());
		when(this.usuarioRepository.findById((Mockito.anyLong()))).thenReturn(null);
		verifyNoMoreInteractions(usuarioRepository);
	}
}
