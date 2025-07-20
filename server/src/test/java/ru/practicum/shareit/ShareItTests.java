package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ShareItTests {
    @Autowired
    private ApplicationContext context;

    @Value("${spring.main.banner-mode}")
    private String bannerMode;

    @Test
    void contextLoads() {
        assertNotNull(context);
    }

    @Test
    void verifyMainBeansAreAvailable() {
        assertNotNull(context.getBean(ShareItServer.class));
    }

    @Test
    void testCustomProperty() {
        assertEquals("off", bannerMode);
    }

}
