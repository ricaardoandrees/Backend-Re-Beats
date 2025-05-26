package com.Grupo586.Re.Beans.Controller;

import com.Grupo586.Re.Beans.Model.Cancion;
import com.Grupo586.Re.Beans.Model.Comentario;
import com.Grupo586.Re.Beans.Model.Playlist;
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
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    public UsuarioController() {
    }

    private final Gson gson = new Gson();
    //UH de Perfil
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
    //Esta es la que crea el perfil
    @PostMapping("/CrearUsuario")
    public ResponseEntity<String> CrearUsuario(@RequestParam("nombre") String nombre, @RequestParam("clave") String clave) {
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



    //UH de Cancion
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

    @GetMapping("/MostrarCancion")
    public ResponseEntity<String> MostrarCancion(@RequestParam("titulo") String titulo) {
        try {

            String jsonData = new String(Files.readAllBytes(Paths.get("src/main/resources/canciones.json")));


            Type listType = new TypeToken<List<Cancion>>() {}.getType();
            List<Cancion> canciones = gson.fromJson(jsonData, listType);


            List<Cancion> resultado = new ArrayList<>();
            for (Cancion cancion : canciones) {
                if (cancion.getTitulo().equalsIgnoreCase(titulo)) {
                    resultado.add(cancion);
                }
            }


            if (resultado.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"Canción no encontrada\"}");
            }

            return ResponseEntity.ok(gson.toJson(resultado));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al leer el archivo JSON\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error inesperado\"}");
        }
    }

    //UH de Comentarios
    //La UH de mostrar comentarios, ya esta hecha indirectamente en mostrar cancion, debido a que comentarios tiene un atributo en canciones que es una lista de comentarios y ya eso lo mostraria directamente en el front//

    @PostMapping("/CrearComentario")
    public ResponseEntity<String> CrearComentario(@RequestParam("titulo") String titulo,
                                                  @RequestParam("usuario") String usuarioNombre,
                                                  @RequestParam("comentario") String comentarioTexto) {
        try {
            //leo usuarios
            String usuariosData = new String(Files.readAllBytes(Paths.get("src/main/resources/usuarios.json")));
            Type userListType = new TypeToken<List<Usuario>>() {}.getType();
            List<Usuario> usuarios = gson.fromJson(usuariosData, userListType);


            Usuario usuarioEncontrado = null;

            for (Usuario u : usuarios) {
                if (u.getNombre().equalsIgnoreCase(usuarioNombre)) {
                    usuarioEncontrado = u;
                    break;
                }
            }

            if (usuarioEncontrado == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\":\"Usuario no registrado\"}");
            }

            // leo canciones
            String cancionesData = new String(Files.readAllBytes(Paths.get("src/main/resources/canciones.json")));
            Type songListType = new TypeToken<List<Cancion>>() {}.getType();
            List<Cancion> canciones = gson.fromJson(cancionesData, songListType);

            // busco cancion
            Cancion cancionEncontrada = null;
            for (Cancion cancion : canciones) {
                if (cancion.getTitulo().equalsIgnoreCase(titulo)) {
                    cancionEncontrada = cancion;
                    break;
                }
            }


            if (cancionEncontrada == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"Canción no encontrada\"}");
            }


            Comentario nuevoComentario = new Comentario(comentarioTexto, usuarioEncontrado, String.valueOf(java.time.LocalDateTime.now()));


            cancionEncontrada.getComentarios().add(nuevoComentario);


            Files.write(Paths.get("src/main/resources/canciones.json"), gson.toJson(canciones).getBytes());

            return ResponseEntity.ok("{\"mensaje\":\"Comentario agregado correctamente\"}");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al leer/escribir el archivo JSON\"}");
        }
    }
    @PostMapping("/CrearPlaylist")
    public ResponseEntity<String> CrearPlaylist(@RequestParam("usuario") String usuarioNombre,
                                                @RequestParam("descripcion") String descripcionPlaylist,
                                                @RequestParam("canciones") List<String> cancionesTitulos) {
        try {

            String usuariosData = new String(Files.readAllBytes(Paths.get("src/main/resources/usuarios.json")));
            Type userListType = new TypeToken<List<Usuario>>() {}.getType();
            List<Usuario> usuarios = gson.fromJson(usuariosData, userListType);


            Usuario usuarioEncontrado = null;
            for (Usuario u : usuarios) {
                if (u.getNombre().equalsIgnoreCase(usuarioNombre)) {
                    usuarioEncontrado = u;
                    break;
                }
            }


            if (usuarioEncontrado == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\":\"Usuario no registrado\"}");
            }


            String cancionesData = new String(Files.readAllBytes(Paths.get("src/main/resources/canciones.json")));
            Type songListType = new TypeToken<List<Cancion>>() {}.getType();
            List<Cancion> cancionesDisponibles = gson.fromJson(cancionesData, songListType);


            List<Cancion> cancionesValidas = new ArrayList<>();
            for (String titulo : cancionesTitulos) {
                for (Cancion c : cancionesDisponibles) {
                    if (c.getTitulo().equalsIgnoreCase(titulo)) {
                        cancionesValidas.add(c);
                        break;
                    }
                }
            }


            if (cancionesValidas.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\":\"Ninguna canción válida encontrada\"}");
            }


            Playlist nuevaPlaylist = new Playlist(descripcionPlaylist, usuarioEncontrado.getNombre(), new ArrayList<>(cancionesValidas));
            usuarioEncontrado.getPlaylists().add(nuevaPlaylist);


            Files.write(Paths.get("src/main/resources/usuarios.json"), gson.toJson(usuarios).getBytes());

            return ResponseEntity.ok("{\"mensaje\":\"Playlist creada correctamente\"}");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al leer/escribir el archivo JSON\"}");
        }
    }

    @GetMapping("/Login")
    public ResponseEntity<String> Login(@RequestParam("nombre") String nombre,
                                        @RequestParam("clave") String clave) {
        try {

            String usuariosData = new String(Files.readAllBytes(Paths.get("src/main/resources/usuarios.json")));
            Type userListType = new TypeToken<List<Usuario>>() {}.getType();
            List<Usuario> usuarios = gson.fromJson(usuariosData, userListType);


            Usuario usuarioEncontrado = null;
            for (Usuario u : usuarios) {
                if (u.getNombre().equalsIgnoreCase(nombre) && u.getClave().equals(clave)) {
                    usuarioEncontrado = u;
                    break;
                }
            }


            if (usuarioEncontrado == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\":\"Usuario o clave incorrectos\"}");
            }


            if ("admin".equalsIgnoreCase(usuarioEncontrado.getNombre())) {
                return ResponseEntity.ok("{\"mensaje\":\"Login exitoso\", \"perfil\":\"admin\"}");
            }


            return ResponseEntity.ok(gson.toJson(usuarioEncontrado));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al leer el archivo JSON\"}");
        }
    }

    @GetMapping("/MostrarPlaylists")
    public ResponseEntity<String> MostrarPlaylists(@RequestParam("nombre") String nombre) {
        try {

            String jsonData = new String(Files.readAllBytes(Paths.get("src/main/resources/usuarios.json")));
            Type listType = new TypeToken<List<Usuario>>() {}.getType();
            List<Usuario> usuarios = gson.fromJson(jsonData, listType);


            Usuario usuarioEncontrado = null;
            for (Usuario usuario : usuarios) {
                if (usuario.getNombre().equalsIgnoreCase(nombre)) {
                    usuarioEncontrado = usuario;
                    break;
                }
            }


            if (usuarioEncontrado == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"Usuario no encontrado\"}");
            }


            if (usuarioEncontrado.getPlaylists().isEmpty()) {
                return ResponseEntity.ok("{\"mensaje\":\"El usuario no tiene playlists\"}");
            }


            return ResponseEntity.ok(gson.toJson(usuarioEncontrado.getPlaylists()));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al leer el archivo JSON\"}");
        }
    }
    }
















