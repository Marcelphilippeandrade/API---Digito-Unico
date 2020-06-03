package br.com.bancointer.digitounico.api.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

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

import br.com.bancointer.digitounico.api.dtos.CadastroDigitoUnicoDto;
import br.com.bancointer.digitounico.api.entidades.DigitoUnico;
import br.com.bancointer.digitounico.api.entidades.Usuario;
import br.com.bancointer.digitounico.api.exception.ExcecaoNumeroInteiroComLetras;
import br.com.bancointer.digitounico.api.exception.ExcecaoNumeroInteiroEFatorNaoPermitido;
import br.com.bancointer.digitounico.api.services.DigitoUnicoService;
import br.com.bancointer.digitounico.api.services.UsuarioService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DigitoUnicoControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private DigitoUnicoService digitoUnicoService;

	@MockBean
	private UsuarioService usuarioService;

	private static final String URL_BASE_CADASTRO = "/api/digito-unico/cadastrar";
	private static final Long ID_USUARIO = 1L;
	private static final Long ID_DIGITOUNICO = 1L;
	private static final String NUMERO_INTEIRO = "9875";
	private static final Integer FATOR_CONCATENACAO = 2;

	@Test
	@WithMockUser
	public void testCadastroDigitoUnico() throws Exception {
		DigitoUnico digitoUnico = obterDadosDigitoUnico();
		BDDMockito.given(this.usuarioService.buscarPorId(Mockito.anyLong())).willReturn(Optional.of(new Usuario()));
		BDDMockito.given(this.digitoUnicoService.persistir(Mockito.any(DigitoUnico.class))).willReturn(digitoUnico);

		mvc.perform(MockMvcRequestBuilders.post(URL_BASE_CADASTRO).content(this.obterJsonRequisicaoPostDigitoUnico())
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.numeroInteiro").value(NUMERO_INTEIRO))
				.andExpect(jsonPath("$.data.fatorConcatenacao").value(FATOR_CONCATENACAO))
				.andExpect(jsonPath("$.erros").isEmpty());
	}

	@Test
	@WithMockUser
	public void testAssociacaoDigitoUnicoUsuario() throws Exception {
		DigitoUnico digitoUnico = obterDadosDigitoUnicoAssociaoDigitoUnicoUsuario();
		Usuario usuario = obterDadosUsuario();
		BDDMockito.given(this.digitoUnicoService.persistir(Mockito.any(DigitoUnico.class))).willReturn(digitoUnico);
		BDDMockito.given(this.usuarioService.persistirUsuario(Mockito.any(Usuario.class))).willReturn(usuario);
		BDDMockito.given(this.usuarioService.buscarPorId(Mockito.anyLong())).willReturn(Optional.of(usuario));

		mvc.perform(MockMvcRequestBuilders.post(URL_BASE_CADASTRO)
				.content(this.obterJsonRequiscaoAssociacaoDigitoUnicoUsuario()).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.numeroInteiro").value(NUMERO_INTEIRO))
				.andExpect(jsonPath("$.data.fatorConcatenacao").value(FATOR_CONCATENACAO))
				.andExpect(jsonPath("$.data.usuarioId").value(ID_USUARIO)).andExpect(jsonPath("$.erros").isEmpty());
	}

	@Test
	@WithMockUser
	public void testDigitoUnicoJaCadastrado() throws JsonProcessingException, Exception {
		DigitoUnico digitoUnico = obterDadosDigitoJaCadastrado();
		Usuario usuario = obterDadosUsuarioComDigitoJaCadastrado();
		BDDMockito.given(this.digitoUnicoService.persistir(Mockito.any(DigitoUnico.class))).willReturn(digitoUnico);
		BDDMockito.given(this.usuarioService.persistirUsuario(Mockito.any(Usuario.class))).willReturn(usuario);
		BDDMockito.given(this.usuarioService.buscarPorId(Mockito.anyLong())).willReturn(Optional.of(usuario));
		BDDMockito.given(this.digitoUnicoService.buscarUsuarioPorId((Mockito.anyLong())))
				.willReturn(usuario.getDigitoUnicos());

		mvc.perform(MockMvcRequestBuilders.post(URL_BASE_CADASTRO)
				.content(this.obterJsonRequiscaoAssociacaoDigitoUnicoUsuario()).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.erros").value("Digito único já cadastrado para o usuário."))
				.andExpect(jsonPath("$.data").isEmpty());
	}

	private DigitoUnico obterDadosDigitoJaCadastrado()
			throws ExcecaoNumeroInteiroComLetras, ExcecaoNumeroInteiroEFatorNaoPermitido {
		DigitoUnico digitoUnico = new DigitoUnico();
		digitoUnico.setId(ID_DIGITOUNICO);
		digitoUnico.setNumeroInteiro(NUMERO_INTEIRO);
		digitoUnico.setFatorConcatenacao(2);
		digitoUnico.setUsuario(this.obterDadosUsuarioComDigitoJaCadastrado());
		return digitoUnico;
	}

	private Usuario obterDadosUsuarioComDigitoJaCadastrado()
			throws ExcecaoNumeroInteiroComLetras, ExcecaoNumeroInteiroEFatorNaoPermitido {
		Usuario usuario = new Usuario();
		DigitoUnico digitoUnico = new DigitoUnico();

		int resultadoCalculo = digitoUnico.calculoDigitoUnico(new StringBuilder(NUMERO_INTEIRO), FATOR_CONCATENACAO);

		digitoUnico.setId(ID_DIGITOUNICO);
		digitoUnico.setNumeroInteiro(NUMERO_INTEIRO);
		digitoUnico.setFatorConcatenacao(2);
		digitoUnico.setUsuario(usuario);
		digitoUnico.setResultado(resultadoCalculo);

		usuario.setId(ID_USUARIO);
		usuario.setNome("Marcel Philippe Abreu Andrade");
		usuario.setEmail("marcelpaa@hotmail.com");
		usuario.getDigitoUnicos().add(digitoUnico);
		return usuario;
	}

	private String obterJsonRequiscaoAssociacaoDigitoUnicoUsuario() throws JsonProcessingException {
		CadastroDigitoUnicoDto digitoUnicoDto = new CadastroDigitoUnicoDto();
		digitoUnicoDto.setId(null);
		digitoUnicoDto.setNumeroInteiro(NUMERO_INTEIRO);
		digitoUnicoDto.setFatorConcatenacao(FATOR_CONCATENACAO);
		digitoUnicoDto.setUsuarioId(ID_USUARIO);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(digitoUnicoDto);
	}

	private DigitoUnico obterDadosDigitoUnicoAssociaoDigitoUnicoUsuario() {
		DigitoUnico digitoUnico = new DigitoUnico();
		digitoUnico.setId(ID_DIGITOUNICO);
		digitoUnico.setNumeroInteiro(NUMERO_INTEIRO);
		digitoUnico.setFatorConcatenacao(2);
		digitoUnico.setUsuario(null);
		return digitoUnico;
	}

	private String obterJsonRequisicaoPostDigitoUnico() throws JsonProcessingException {
		CadastroDigitoUnicoDto digitoUnicoDto = new CadastroDigitoUnicoDto();
		digitoUnicoDto.setId(null);
		digitoUnicoDto.setNumeroInteiro(NUMERO_INTEIRO);
		digitoUnicoDto.setFatorConcatenacao(FATOR_CONCATENACAO);
		digitoUnicoDto.setUsuarioId(null);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(digitoUnicoDto);
	}

	private DigitoUnico obterDadosDigitoUnico() {
		DigitoUnico digitoUnico = new DigitoUnico();
		digitoUnico.setId(ID_DIGITOUNICO);
		digitoUnico.setNumeroInteiro(NUMERO_INTEIRO);
		digitoUnico.setFatorConcatenacao(2);
		digitoUnico.setUsuario(new Usuario());
		digitoUnico.getUsuario().setId(ID_USUARIO);
		return digitoUnico;
	}

	private Usuario obterDadosUsuario() {
		Usuario usuario = new Usuario();
		usuario.setId(ID_USUARIO);
		usuario.setNome("Marcel Philippe Abreu Andrade");
		usuario.setEmail("marcelpaa@hotmail.com");
		return usuario;
	}
}
