package com.adirsa.gestionproyectos.config;

import com.adirsa.gestionproyectos.entity.Rol;
import com.adirsa.gestionproyectos.repository.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initRoles(RolRepository rolRepository) {
        return args -> {

            if (rolRepository.count() == 0) {
                rolRepository.save(new Rol("GERENTE_GENERAL"));
                rolRepository.save(new Rol("GERENTE_PROYECTOS"));
                rolRepository.save(new Rol("CONTADOR"));
                rolRepository.save(new Rol("INGENIERO"));

                System.out.println("Roles insertados correctamente");
            }

        };
    }
}