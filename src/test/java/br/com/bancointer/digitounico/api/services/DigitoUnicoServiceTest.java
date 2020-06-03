package br.com.bancointer.digitounico.api.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.bancointer.digitounico.api.entidades.DigitoUnico;
import br.com.bancointer.digitounico.api.repositorios.DigitoUnicoRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class DigitoUnicoServiceTest {

	@MockBean
	private DigitoUnicoRepository digitoUnicoRepository;

	@Autowired
	private DigitoUnicoService digitoUnicoService;

	@Before
	public void setUp() throws Exception {
		BDDMockito.given(this.digitoUnicoRepository.findByUsuarioId(Mockito.anyLong(), Mockito.any(PageRequest.class)))
				.willReturn(new PageImpl<DigitoUnico>(new ArrayList<DigitoUnico>()));
		BDDMockito.given(this.digitoUnicoRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(new DigitoUnico()));
		BDDMockito.given(this.digitoUnicoRepository.save(Mockito.any(DigitoUnico.class))).willReturn(new DigitoUnico());
	}
	
	@Test
	public void testBuscarDigitoUnicoPorUsuarioIdPaginado() {
		Page<DigitoUnico> digitoUnico = this.digitoUnicoService.buscarUsuarioPorId(1L, PageRequest.of(0, 10));
		assertNotNull(digitoUnico);
	}
	
	@Test
	public void testBuscarDigitoUnicoPorUsuarioId(){
		List<DigitoUnico> digitoUnico = this.digitoUnicoService.buscarUsuarioPorId(1L);
		assertNotNull(digitoUnico);
	}
	
	@Test
	public void testBuscarDigitoUnicoPorId() {
		Optional<DigitoUnico> digitoUnico = this.digitoUnicoService.buscarPorId(1L);
		assertTrue(digitoUnico.isPresent());
	}
	
	@Test
	public void testPersistirDigitoUnico() {
		DigitoUnico digitoUnico = this.digitoUnicoService.persistir(new DigitoUnico());
		assertNotNull(digitoUnico);
	}
}
