package com.generic.server;

import org.junit.jupiter.api.Test;

class ServerApplicationTest {

    @Test
    void main() {
        ServerApplication.main(new String[]{"--spring.main.web-application-type=none"});
    }

    @Test
    void constructor() {
        new ServerApplication();
    }
}