package com.Grupo586.Re.Beans.Controller;

import com.Grupo586.Re.Beans.Model.Cancion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/admin")
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    public AdminController() {}

    private final Gson gson = new Gson();
    @PostMapping("/CrearCancion")
    public ResponseEntity<String> crearCancion(@RequestParam("titulo") String titulo, @RequestParam("autor") String autor, @RequestParam("genero") String genero,
                                               @RequestParam("fecha") String fecha,
                                               @RequestParam("imagen") String imagen) {
        try {

            String jsonData = new String(Files.readAllBytes(Paths.get("src/main/resources/canciones.json")));

            Type listType = new TypeToken<List<Cancion>>() {}.getType();
            List<Cancion> canciones = gson.fromJson(jsonData, listType);

           //creo la cancion para guardarla en el json
            Cancion nuevaCancion = new Cancion(new ArrayList<>(), imagen, fecha, titulo, autor, genero, new ArrayList<>());


            canciones.add(nuevaCancion);

            Files.write(Paths.get("src/main/resources/canciones.json"), gson.toJson(canciones).getBytes());

            return ResponseEntity.ok("{\"mensaje\":\"Canción creada correctamente\"}");

        } catch (IOException e) {
            return ResponseEntity.status(500).body("{\"error\":\"Error al leer/escribir el JSON\"}");
        }
    }
    @GetMapping("/ConsultarCanciones")
    public ResponseEntity<String> ConsultarCanciones() {
        try {

            String jsonData = new String(Files.readAllBytes(Paths.get("src/main/resources/canciones.json")));


            Type listType = new TypeToken<List<Cancion>>() {}.getType();
            List<Cancion> canciones = gson.fromJson(jsonData, listType);


            if (canciones.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"No hay canciones disponibles\"}");
            }

            return ResponseEntity.ok(gson.toJson(canciones));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al leer el archivo JSON\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error inesperado\"}");
        }
    }
}


