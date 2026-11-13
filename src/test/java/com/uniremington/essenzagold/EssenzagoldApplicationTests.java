package com.uniremington.essenzagold;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EssenzagoldApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void rendersDashboardAndEveryModuleView() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("El pulso de tu negocio")));
		mockMvc.perform(get("/usuarios")).andExpect(status().isOk());
		mockMvc.perform(get("/productos")).andExpect(status().isOk());
		mockMvc.perform(get("/proveedores")).andExpect(status().isOk());
		mockMvc.perform(get("/compras")).andExpect(status().isOk());
		mockMvc.perform(get("/ventas")).andExpect(status().isOk());
		mockMvc.perform(get("/devoluciones")).andExpect(status().isOk());
		mockMvc.perform(get("/css/estilos.css")).andExpect(status().isOk());
		mockMvc.perform(get("/js/app.js")).andExpect(status().isOk());
		mockMvc.perform(get("/img/essenza-hero.svg")).andExpect(status().isOk());
	}

	@Test
	void userApiDoesNotExposePassword() throws Exception {
		mockMvc.perform(post("/api/usuarios")
						.contentType(APPLICATION_JSON)
						.content("""
								{
								  "nombre": "Prueba",
								  "apellido": "Essenza",
								  "documento": "TEST-001",
								  "correo": "prueba@example.test",
								  "contrasena": "clave-segura-123",
								  "rol": "Vendedor",
								  "estado": "Activo"
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(content().string(not(containsString("contrasena"))));
	}
}
