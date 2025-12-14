package com.generic.server;

import org.junit.jupiter.api.Test;

class ServerApplicationTest {

    @Test
    void test_main() {
        ServerApplication.main(new String[]{"--spring.main.web-application-type=none"});
    }

}