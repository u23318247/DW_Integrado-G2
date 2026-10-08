package com.proyectomdweb.proyectomdweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProyectomdwebApplication {
	/* //* Main para que corra todo el servidor 
	//* darle al boton de run o click derecho y "run java". 
	//* Asegurarse que este activo el contenedor */

	public static void main(String[] args) {
		SpringApplication.run(ProyectomdwebApplication.class, args);	
	}

}
