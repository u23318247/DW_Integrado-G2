package com.utp.demo.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CircleServiceTest {

    @Test
    void testCalculateArea() {
        CircleService service = new CircleService();

        double radius = 5.0;
        double expected = Math.PI * radius * radius;

        double result = service.calculateArea(radius);

        assertEquals(expected, result, 0.001);
    }

    @Test
    void testCalculateAreaWithNegativeRadius() {
        CircleService service = new CircleService();

        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateArea(-5.0);
        });
    }
}
