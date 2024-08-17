    package com.yp.puppy.api.config;

    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.web.servlet.config.annotation.CorsRegistry;
    import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
    import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
    import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

    @Configuration
    public class WebConfig {

        @Bean
        public WebMvcConfigurer corsConfigurer() {
            return new WebMvcConfigurer() {
                @Override
                public void addCorsMappings(CorsRegistry registry) {
                    registry.addMapping("/**")
                            .allowedOrigins("http://localhost:3000")
                            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                            .allowedHeaders("*")
                            .allowCredentials(true);
                }

                @Override
                public void addInterceptors(InterceptorRegistry registry) {
                    registry.addInterceptor(new LogInterceptor());
                }
            };
        }
    }

    /*
    public class WebConfig implements WebMvcConfigurer {
        @Value("${file.upload-dir}")
        private String uploadDir;

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOrigins("http://localhost:3000")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true);
        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/resources/**", "/img/**", "/images/**")
                    .addResourceLocations(uploadDir);
        }
    }
    */