package com.edu.unimag.Gestion_de_activos__Modulo_1;

import org.springframework.boot.SpringApplication;

public class TestGestionDeActivosModulo1Application {

	public static void main(String[] args) {
		SpringApplication.from(GestionDeActivosModulo1Application::main).with(TestcontainersConfiguration.class).run(args);
	}

}
