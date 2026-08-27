package com.bdt.bancotalentosbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * CORS de la API.
     *
     * <p>
     * La lista es DELIBERADAMENTE la misma en los dos backends (BDT y AutFMI):
     * los frontends se llaman en cruz — el front de BDT pega al backend de FMI
     * (`axiosInstanceFMI`: requerimientos, postulantes, entrevistas) y el front de
     * FMI pega al backend de BDT (`axiosInstanceBDT`) — asi que cada backend tiene
     * que admitir el origen de AMBOS frontends en cada entorno. Si se añade un
     * entorno, hay que añadirlo en los dos ficheros o ese entorno se cae con un
     * fallo de preflight.
     *
     * <p>
     * OJO: esto NO tiene nada que ver con la subida de archivos a S3. El PUT
     * pre-firmado va del navegador a S3 directamente, sin pasar por este backend:
     * ese lo gobierna la politica CORS del bucket, no esta configuracion.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        // Front-ends
                        "https://autfmi.fractal.com.pe",
                        "https://bancodetalentos.fractal.com.pe",
                        // Back-ends
                        "https://api-autfmi.fractal.com.pe")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "Accept")
                .exposedHeaders("Authorization")
                .allowCredentials(true);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        return scheduler;
    }

}
