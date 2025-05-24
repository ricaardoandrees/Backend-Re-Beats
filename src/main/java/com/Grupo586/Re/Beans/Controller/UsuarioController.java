package com.Grupo586.Re.Beans.Controller;

import com.Grupo586.Re.Beans.Model.Usuario;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequestMapping("/user")
@RestController
public class UsuarioController {

    public UsuarioController() {}

    // Usemos funciones nombradas con get y post. ej: PostPerfil
    // En este caso estas creando el perfil no? Entonces seria crearPerfil
    @GetMapping("/MostrarPerfil")
    public ResponseEntity<String> MostrarPerfil(@RequestParam("nombre") String nombre) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            List<Usuario> perfiles = mapper.readValue(
                    new ClassPathResource("usuarios.json").getInputStream(),
                    new TypeReference<List<Usuario>>() {}
            );



            Optional<Usuario> perfilEncontrado = perfiles.stream()
                    .filter(p -> p.getNombre().equalsIgnoreCase(nombre)).findFirst();

            if (perfilEncontrado.isPresent()) {
                String jsonResultado = mapper.writeValueAsString(perfilEncontrado.get());
                return ResponseEntity.ok(jsonResultado);
            } else {
                return ResponseEntity.status(404).body("Perfil no encontrado");
            }

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al encontrar el perfil");
        }
    }





}

