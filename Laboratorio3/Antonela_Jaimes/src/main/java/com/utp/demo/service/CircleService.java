package com.utp.demo.service;

import org.springframework.stereotype.Service;

@Service
public class CircleService {

    public double calculateArea(double radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("El radio no puede ser negativo");
        }
        return Math.PI * Math.pow(radius, 2);
    }
}
