package com.proyectomdweb.proyectomdweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuración de Asincronismo y Auditoría JPA
 * Permite procesar correos y notificaciones en segundo plano sin bloquear el checkout
 * Alumna: Antonela Jaimes
 */
@Configuration
@EnableAsync
@EnableJpaAuditing
public class AsyncAndAuditingConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("MDWeb-Async-");
        executor.initialize();
        return executor;
    }
}
