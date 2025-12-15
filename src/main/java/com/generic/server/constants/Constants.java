package com.generic.server.constants;

public final class Constants {

    private Constants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

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
