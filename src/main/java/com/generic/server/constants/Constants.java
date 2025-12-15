package com.generic.server.constants;

public class Constants {

    public static final String[] SWAGGER_PATHS = {
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    public static final String[] ACTUATOR_CSRF_EXCLUDED_PATHS = {
            "/actuator/hawtio/**",
            "/actuator/jolokia/**",
            "/actuator/shutdown"
    };

}
