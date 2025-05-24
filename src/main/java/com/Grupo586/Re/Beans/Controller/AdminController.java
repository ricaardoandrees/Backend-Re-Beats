package com.Grupo586.Re.Beans.Controller;

import com.Grupo586.Re.Beans.Model.Cancion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
public class AdminController {

    public AdminController() {}

    private final Gson gson = new Gson();
    @PostMapping("/CrearCancion")
    public ResponseEntity<String> crearCancion(@RequestParam("titulo") String titulo,
                                               @RequestParam("autor") String autor,
                                               @RequestParam("genero") String genero,
                                               @RequestParam("fecha") String fecha,
                                               @RequestParam("imagen") String imagen) {
        try {
            // Leer el JSON actual de canciones desde el archivo
            String jsonData = new String(Files.readAllBytes(Paths.get("src/main/resources/canciones.json")));

            // Convertir JSON a lista de canciones
            Type listType = new TypeToken<List<Cancion>>() {}.getType();
            List<Cancion> canciones = gson.fromJson(jsonData, listType);

            // Crear nueva canción con listas vacías
            Cancion nuevaCancion = new Cancion(new ArrayList<>(), imagen, fecha, titulo, autor, genero, new ArrayList<>());

            // Agregar la nueva canción a la lista
            canciones.add(nuevaCancion);

            // Guardar la lista actualizada en el JSON
            Files.write(Paths.get("src/main/resources/canciones.json"), gson.toJson(canciones).getBytes());

            return ResponseEntity.ok("{\"mensaje\":\"Canción creada correctamente\"}");

        } catch (IOException e) {
            return ResponseEntity.status(500).body("{\"error\":\"Error al leer/escribir el JSON\"}");
        }
    }
}


