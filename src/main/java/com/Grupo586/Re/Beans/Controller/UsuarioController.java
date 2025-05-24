package com.Grupo586.Re.Beans.Controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.Grupo586.Re.Beans.Model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/user")
@RestController
public class UsuarioController {

    public UsuarioController() {
    }

    private final Gson gson = new Gson();

    @GetMapping("/MostrarPerfil")
    public ResponseEntity<String> MostrarPerfil(@RequestParam("nombre") String nombre) throws IOException {
        try {

            String jsonData = new String(Files.readAllBytes(Paths.get("src/main/resources/usuarios.json")));


            Type listType = new TypeToken<List<Usuario>>() {
            }.getType();
            List<Usuario> perfiles = gson.fromJson(jsonData, listType);

            // Filtrar los perfiles según el nombre
            List<Usuario> filtrados = perfiles.stream()
                    .filter(perfil -> perfil.getNombre().equalsIgnoreCase(nombre))
                    .collect(Collectors.toList());


            if (filtrados.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"Usuario no encontrado\"}");
            }

            return ResponseEntity.ok(gson.toJson(filtrados));

        } catch (IOException e) {
            // si hay error leyendo el jsom
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al leer el archivo JSON\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error inesperado\"}");
        }
    }

    @PostMapping("/RegistrarUsuario")
    public ResponseEntity<String> RegistrarUsuario(@RequestParam("nombre") String nombre, @RequestParam("clave") String clave) {
        try {

            String jsonData = new String(Files.readAllBytes(Paths.get("src/main/resources/usuarios.json")));


            Type listType = new TypeToken<List<Usuario>>() {}.getType();
            List<Usuario> usuarios = gson.fromJson(jsonData, listType);


            int maxId = 0;
            for (Usuario usuario : usuarios) {
                if (usuario.getID() > maxId) {
                    maxId = usuario.getID();
                }
            }

            int nuevoId = maxId + 1;


            Usuario nuevoUsuario = new Usuario( nombre, clave, new ArrayList<>(), new ArrayList<>(),nuevoId);
            usuarios.add(nuevoUsuario);


            Files.write(Paths.get("src/main/resources/usuarios.json"), gson.toJson(usuarios).getBytes());

            return ResponseEntity.ok("{\"mensaje\":\"Usuario registrado correctamente\"}");

        } catch (IOException e) {
            return ResponseEntity.status(500).body("{\"error\":\"Error al leer/escribir el JSON\"}");
        }
    }


}











