package com.shivani.scribeapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;

/**
 * Configuration class for OpenAPI (Swagger UI) documentation. Configures global
 * security schemes, server profiles, and metadata branding.
 */
@Configuration
public class OpenApiConfig {

	private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

	@Bean
	public OpenAPI customOpenAPI() {
		Server localServer = new Server().url("http://localhost:8080").description("Local Sandbox (Development)");

		String enhancedDescription = "### Production-Ready Blogging Engine Backend\n"
				+ "An enterprise-grade, high-performance RESTful content management API built using Spring Boot 3, Java 23, and Spring Security.\n\n"
				+ "#### Core Architectural Implementations Highlighted:\n"
				+ "* **Stateless JWT Security Filter Matrix**: Fine-grained role guards (ROLE_ADMIN / ROLE_NORMAL) keeping internal memory overhead flat.\n"
				+ "* **N+1 Database Query Mitigation**: Custom repository layers utilizing JOIN FETCH operations to fetch relational dependencies in single transaction steps.\n"
				+ "* **Isolated Filesystem Media Tier**: Local disk path protection structures parsing image uploads securely away from the source code via randomized UUID identifiers.\n"
				+ "* **Global Exception Envelopes**: @RestControllerAdvice interceptors formatting data validations smoothly into structured JSON tracking models.\n\n"
				+ "--- \n"
				+ "* **Developer Profile**: Shivani Dubey | [LinkedIn](www.linkedin.com/in/shivanis5) | [Email](mailto:work.shivanidubey@gmail.com)\n\n"
				+ "*To unlock and execute secure endpoints, click the Authorize button on the right and input a valid JWT Bearer token.*";

		return new OpenAPI().servers(List.of(localServer))
				.info(new Info().title("Scribe API – Enterprise Blogging & Content Management Engine by Shivani Dubey")
						.description(enhancedDescription).version("1.0.0")
						.license(new License().name("MIT Open Source License").url("https://opensource.org")))
				// Enables global authorization lock icons on protected endpoints
				.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
				// Registers the JWT security modal definitions into the context components
				.components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
						.name(SECURITY_SCHEME_NAME).type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
						.in(SecurityScheme.In.HEADER).description(
								"Provide a valid JSON Web Token (JWT) to authorize requests against secure paths.")));
	}
}
