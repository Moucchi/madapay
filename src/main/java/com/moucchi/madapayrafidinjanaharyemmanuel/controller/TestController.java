package com.moucchi.madapayrafidinjanaharyemmanuel.controller;

import com.moucchi.madapayrafidinjanaharyemmanuel.businessObject.Compte;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {
    @Resource(name = "loadComptes")
    private final List<Compte> comptes;

    public TestController(List<Compte> comptes) {
        this.comptes = comptes;
    }

    @GetMapping
    public ResponseEntity<List<Compte>> getComptes() {
        try {
            return ResponseEntity.ok().body(comptes);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
