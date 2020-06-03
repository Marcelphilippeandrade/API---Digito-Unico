package br.com.bancointer.digitounico.api.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.collections4.IteratorUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.bancointer.digitounico.api.dtos.BuscaUsuarioDto;
import br.com.bancointer.digitounico.api.dtos.CadastroUsuarioDto;
import br.com.bancointer.digitounico.api.entidades.Usuario;
import br.com.bancointer.digitounico.api.services.UsuarioService;
import br.com.bancointer.digitounico.api.utils.CriptografiaUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UsuarioControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private UsuarioService usuarioService;

	private static final Long ID_USUARIO = 1L;
	private static final String URL_BASE = "/api/usuario";
	private static final String NOME = "Marcel Philippe Abreu Andrade";
	private static final String EMAIL = "marcelpaa@hotmail.com";
	private static final String NOME_ATUALIZA = "Marcel Philippe";
	private static final String EMAIL_ATUALIZA = "marcelpaa@gmail.com";
	private static final String CHAVE_PUBLICA = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmQQRGUIlg84uMImjw3pP47LWW4s9b2TkrZ/o3S8GPQn0qzfMNIoD/S87wY/5BZVpDOFR4/kgmYNasBC8gP1l8aFAm9D+sIlXPdZkyR/fzdgjas8/bFlVq8iEh6lzpvM8oCwznciEGXghnDollqwJM0wrIOcRM+4Z0qRU0+gpnBBsK64sIgTlDpn5RMZ6bP1/8ZHfGGqrnkKh6GWd3wecYbYBlzTB06YBFDGpD8cJfXoduwvJoVP17pjKSaVdWtkCzXVNXL1ru469HnWalXDD718o5ouTNfCXxv0aaLfHMjBsdPJWtVWjgQcy8MVYkHpXIrCinbmlcEZWWHPdBRSJ4QIDAQAB";
	private static final String CHAVE_PRIVADA = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCZBBEZQiWDzi4wiaPDek/jstZbiz1vZOStn+jdLwY9CfSrN8w0igP9LzvBj/kFlWkM4VHj+SCZg1qwELyA/WXxoUCb0P6wiVc91mTJH9/N2CNqzz9sWVWryISHqXOm8zygLDOdyIQZeCGcOiWWrAkzTCsg5xEz7hnSpFTT6CmcEGwrriwiBOUOmflExnps/X/xkd8YaqueQqHoZZ3fB5xhtgGXNMHTpgEUMakPxwl9eh27C8mhU/XumMpJpV1a2QLNdU1cvWu7jr0edZqVcMPvXyjmi5M18JfG/Rpot8cyMGx08la1VaOBBzLwxViQelcisKKduaVwRlZYc90FFInhAgMBAAECggEAZT8PoUrnT8NJRMyOE3YHDn7y3zfOurpjpY69ojzPP+wqfHA7Kjh4UzrBq138Q/mMvP0KFnJtY6ZKh11LnX4NykgMXA15uI3nr/8ASSRSDf4J0U64hJTH3xaaurkg0UL4xnL45FodLk0S0DCZVo0WuV6Y2viJpXbSl1Is3torTht0cw0dzzutmbpKq2r+6rTP3N3w6/2gV1idxpsv+N653ZxeltKj4FlRznrvDVxw69SNpe30T+MhtXG9CtjL/7LwaWxAVXGpwjd8fIk/oRrSiF+N67hXI9OkICrwOkBE3i7wWRLw/G1+8SshtWStu9UvZUCyCbpU5ADGPYKMvjH4kQKBgQDyKZWTQgPZsUv7UQltggWNzul49sE+WETdjE3sWyfvUTtEyRFnb9lxheeQ1PGcBcqll21fVq31SEZVpkOo/e9kNVL21Ia5BLPwrbyM/m8Zm5mOqq78txcyEsDKWBECxLaPdGGqFqv/bqvZIYv2YOxX/b+vMX4VPNz4HbukB56GDwKBgQChwmxf8IBj3JXGwYZw48SR8gabxW8TFehLlBNnb/+WCebLVSfxH9Sr2L18zOO/dDSYvEk53zUwUrU9/eg3j0miNICkZreWnT/r8PvzY1MQrOEHSa+oNH7okwQh8JSjC+dk2pMGBr0gUCqMFKi0knr34WF/6YDIxC33KgUStRFhDwKBgQDWf2MP9s92SgclathQ+XR18ar4DImK8aC+JQL4sp2i6272NKuH1ZjjZ1p//T6tlquzFXg5lIut0gEK6KTR0Wv0dQ8xt3pF9BZ2v01eDhjWs+7GYgVxr7OKFPZTxMH8k8WpN8syX7amIJ9zSrWw3JU8M3VQdyRZJ3oLBDsqxdzynQKBgGj5JR91kbw8kC41tKtaBFy59bPtAlIea2twos8DjZeuwUm+73a5M2h59S4iQMIkBWYA+nxF79x2MAwU1DgKErzi2YDW79kcHzlcYATotiUiK75xAT9lId6IWaw01iChPv7iIXtNsDpiC9pwJbNZQ9fNOVqrC9o+BZ5adIRZYPfdAoGBAOeqMWCwz6RHZMDbf+ARgtT0WW+qppCL7U2LXZf7CN6e0n2hOeAQiPOcnyhXoaDxafm2XizDujX0WcOngCmDHge+0FRFFywRDnBdKJNYUcr2PyUg7yruLdqXwG/uKP8AqAE0iaq639Xy6D1y+N6E2F3VY+69hWQ5g1a+AoyzhOVm";

	@Test
	@WithMockUser
	public void testCadastroUsuario() throws Exception {
		Usuario usuario = obterDadosUsuario();

		BDDMockito.given(this.usuarioService.buscarPorId(Mockito.anyLong())).willReturn(Optional.of(usuario));
		BDDMockito.given(this.usuarioService.persistirUsuario(Mockito.any(Usuario.class))).willReturn(usuario);
		
		mvc.perform(MockMvcRequestBuilders.post(URL_BASE + "/cadastrar").content(this.obterJsonRequisicaoPostCadastro())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.nome").isNotEmpty())
				.andExpect(jsonPath("$.data.email").isNotEmpty())
				.andExpect(jsonPath("$.erros").isEmpty());
	}

	@Test
	@WithMockUser
	public void testBuscarUsuarioPorNome() throws Exception {
		Usuario usuario = obterDadosUsuarioBusca();
		Iterable<Usuario> IterableUsuario = new ArrayList<Usuario>();
		List<Usuario> listaUsuario = new ArrayList<Usuario>();
		listaUsuario.add(usuario);

		IterableUsuario = IteratorUtils.asMultipleUseIterable(listaUsuario.iterator());

		BDDMockito.given(this.usuarioService.persistirUsuario(Mockito.any(Usuario.class))).willReturn(usuario);
		BDDMockito.given(this.usuarioService.findAll()).willReturn(IterableUsuario);

		mvc.perform(MockMvcRequestBuilders.post(URL_BASE + "/buscar/nome")
				.content(this.obterJsonRequisacaoPostBusca())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(ID_USUARIO))
				.andExpect(jsonPath("$.data.nome").value(NOME))
				.andExpect(jsonPath("$.data.email").value(EMAIL))
				.andExpect(jsonPath("$.erros").isEmpty());
	}

	@Test
	@WithMockUser
	public void testBuscarUsuarioPorEmail() throws JsonProcessingException, Exception {
		Usuario usuario = obterDadosUsuarioBusca();
		
		Iterable<Usuario> IterableUsuario = new ArrayList<Usuario>();
		List<Usuario> listaUsuario = new ArrayList<Usuario>();
		listaUsuario.add(usuario);
		
		IterableUsuario = IteratorUtils.asMultipleUseIterable(listaUsuario.iterator());

		BDDMockito.given(this.usuarioService.persistirUsuario(Mockito.any(Usuario.class))).willReturn(usuario);
		BDDMockito.given(this.usuarioService.findAll()).willReturn(IterableUsuario);

		mvc.perform(MockMvcRequestBuilders.post(URL_BASE + "/buscar/email")
				.content(this.obterJsonRequisacaoPostBusca())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(ID_USUARIO))
				.andExpect(jsonPath("$.data.nome").value(NOME))
				.andExpect(jsonPath("$.data.email").value(EMAIL))
				.andExpect(jsonPath("$.erros").isEmpty());
	}

	@Test
	@WithMockUser
	public void testAtualizarDadosUsuario() throws Exception {
		Usuario usuario = obterDadosUsuarioAtualizado();
		CadastroUsuarioDto cadastroUsuarioDto = null;

		BDDMockito.given(this.usuarioService.persistirUsuario(Mockito.any(Usuario.class))).willReturn(usuario);
		BDDMockito.given(this.usuarioService.buscarPorId(Mockito.anyLong())).willReturn(Optional.of(usuario));
		BDDMockito.given(this.usuarioService.persistirUsuario(Mockito.any(Usuario.class))).willReturn(usuario);

		mvc.perform(MockMvcRequestBuilders.put("/api/usuario/atualizar/" + usuario.getId())
				.content(this.obterJsonRequisicaoPut(usuario.getId(), cadastroUsuarioDto))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(ID_USUARIO))
				.andExpect(jsonPath("$.data.nome").value(usuario.getNome()))
				.andExpect(jsonPath("$.data.email").value(usuario.getEmail()))
				.andExpect(jsonPath("$.erros").isEmpty());
	}

	@Test
	@WithMockUser
	public void testDeletarUsuario() throws Exception {
		Usuario usuario = obterDadosUsuario();

		BDDMockito.given(this.usuarioService.persistirUsuario(Mockito.any(Usuario.class))).willReturn(usuario);
		BDDMockito.given(this.usuarioService.buscarPorId(Mockito.anyLong())).willReturn(Optional.of(usuario));

		mvc.perform(MockMvcRequestBuilders.delete(URL_BASE + "/deletar/" + usuario.getId())
				.content(this.obterJsonRequisicaoGet(usuario.getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(ID_USUARIO))
				.andExpect(jsonPath("$.data.nome").value(NOME))
				.andExpect(jsonPath("$.data.email").value(EMAIL))
				.andExpect(jsonPath("$.erros").isEmpty());

	}

	private String obterJsonRequisicaoGet(Long id) throws JsonProcessingException {
		CadastroUsuarioDto cadastroUsuarioDto = new CadastroUsuarioDto();
		cadastroUsuarioDto.setId(ID_USUARIO);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(cadastroUsuarioDto);
	}

	private String obterJsonRequisicaoPut(Long id, CadastroUsuarioDto cadastroUsuarioDto) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, IOException {
		cadastroUsuarioDto = new CadastroUsuarioDto();
		
		cadastroUsuarioDto.setId(id);
		cadastroUsuarioDto.setNome(NOME_ATUALIZA);
		cadastroUsuarioDto.setEmail(EMAIL_ATUALIZA);
		cadastroUsuarioDto.setChavePublica(CHAVE_PUBLICA);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(cadastroUsuarioDto);
	}
	
	private String obterJsonRequisacaoPostBusca() throws JsonProcessingException {
		BuscaUsuarioDto buscaUsuarioDto = new BuscaUsuarioDto();
		
		buscaUsuarioDto.setId(ID_USUARIO);
		buscaUsuarioDto.setNome(NOME);
		buscaUsuarioDto.setEmail(EMAIL);
		buscaUsuarioDto.setChavePrivada(CHAVE_PRIVADA);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(buscaUsuarioDto);
	}

	private String obterJsonRequisicaoPostCadastro() throws JsonProcessingException {
		CadastroUsuarioDto cadastroUsuarioDto = new CadastroUsuarioDto();
		cadastroUsuarioDto.setId(ID_USUARIO);
		cadastroUsuarioDto.setNome(NOME);
		cadastroUsuarioDto.setEmail(EMAIL);
		cadastroUsuarioDto.setChavePublica(CHAVE_PUBLICA);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(cadastroUsuarioDto);
	}
	
	private Usuario obterDadosUsuarioAtualizado() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, IOException {
		Usuario usuario = new Usuario();
		usuario.setId(ID_USUARIO);
		usuario.setNome(NOME_ATUALIZA);
		usuario.setEmail(EMAIL_ATUALIZA);
		return usuario;
	}
	
	private Usuario obterDadosUsuarioBusca() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, IOException {
		Usuario usuario = new Usuario();
		CriptografiaUtil criptografiaUtil = new CriptografiaUtil();
		
		usuario.setId(ID_USUARIO);
		usuario.setNome(criptografiaUtil.criptografar(NOME, criptografiaUtil.getPublicKey(CHAVE_PUBLICA)));
		usuario.setEmail(criptografiaUtil.criptografar(EMAIL, criptografiaUtil.getPublicKey(CHAVE_PUBLICA)));
		return usuario;
	}

	private Usuario obterDadosUsuario() {
		Usuario usuario = new Usuario();
		usuario.setId(ID_USUARIO);
		usuario.setNome(NOME);
		usuario.setEmail(EMAIL);
		return usuario;
	}
}
