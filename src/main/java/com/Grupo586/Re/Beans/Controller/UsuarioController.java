package com.Grupo586.Re.Beans.Controller;

import com.Grupo586.Re.Beans.Model.Cancion;
import com.Grupo586.Re.Beans.Model.Comentario;
import com.Grupo586.Re.Beans.Model.Playlist;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.Grupo586.Re.Beans.Model.Propietario.RolUsuario;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.Grupo586.Re.Beans.Model.Propietario;
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

    //UH de Perfil
    @GetMapping("/MostrarPerfil")
    public ResponseEntity<String> MostrarPerfil(@RequestParam("nombre") String nombre) throws IOException {
        try {

            String jsonData = new String(Files.readAllBytes(Paths.get("src/main/resources/usuarios.json")));


            Type listType = new TypeToken<List<Propietario>>() {
            }.getType();
            List<Propietario> perfiles = gson.fromJson(jsonData, listType);
            System.out.println("Usuarios cargados: " + perfiles.size());
            System.out.println("Buscando usuario con nombre: " + nombre);
            // Filtrar los perfiles según el nombre
            List<Propietario> filtrados = perfiles.stream()
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
    public ResponseEntity<String> CrearUsuario(@RequestParam("nombre") String nombre, @RequestParam("clave") String clave, @RequestParam("rol") String rolStr) {
        try {

            String jsonData = new String(Files.readAllBytes(Paths.get("src/main/resources/usuarios.json")));


            Type listType = new TypeToken<List<Propietario>>() {
            }.getType();
            List<Propietario> usuarios = gson.fromJson(jsonData, listType);


            int maxId = 0;
            for (Propietario usuario : usuarios) {
                if (usuario.getId() > maxId) {
                    maxId = usuario.getId();
                }
            }

            int nuevoId = maxId + 1;


            RolUsuario rol;
            try {
                rol = RolUsuario.valueOf(rolStr);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(400).body("{\"error\":\"Rol inválido\"}");
            }

            // Crear nuevo usuario
            Propietario nuevoUsuario = new Propietario(nombre, clave, nuevoId, rol, new ArrayList<>(), new ArrayList<>());
            usuarios.add(nuevoUsuario);

            Files.write(Paths.get("src/main/resources/usuarios.json"), gson.toJson(usuarios).getBytes());

            return ResponseEntity.ok("{\"mensaje\":\"Usuario registrado correctamente\"}");

        } catch (IOException e) {
            return ResponseEntity.status(500).body("{\"error\":\"Error al leer/escribir el JSON\"}");
        }
    }


    //UH de Cancion
    @PostMapping("/CrearCancion")
    public ResponseEntity<String> crearCancion(@RequestParam("idUsuario") Integer idUsuario,
                                               @RequestParam("titulo") String titulo,
                                               @RequestParam("autor") String autor,
                                               @RequestParam("genero") String genero,
                                               @RequestParam("fecha") String fecha,
                                               @RequestParam("imagen") String imagen) {
        try {

            String usuariosData = new String(Files.readAllBytes(Paths.get("src/main/resources/usuarios.json")));
            Type userListType = new TypeToken<List<Propietario>>() {
            }.getType();
            List<Propietario> usuarios = gson.fromJson(usuariosData, userListType);

            Propietario usuarioEncontrado = usuarios.stream()
                    .filter(u -> u.getId().equals(idUsuario))
                    .findFirst()
                    .orElse(null);


            if (usuarioEncontrado == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\":\"Usuario no registrado\"}");
            }
            if (usuarioEncontrado.getRol() != Propietario.RolUsuario.Admin) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\":\"Permiso denegado: Solo los administradores pueden crear canciones\"}");
            }


            String jsonData = new String(Files.readAllBytes(Paths.get("src/main/resources/canciones.json")));
            Type listType = new TypeToken<List<Cancion>>() {
            }.getType();
            List<Cancion> canciones = gson.fromJson(jsonData, listType);


            int nuevoIdCancion = canciones.stream()
                    .mapToInt(Cancion::getId)
                    .max()
                    .orElse(0) + 1;

            // Crear y guardar la nueva canción
            Cancion nuevaCancion = new Cancion(titulo, nuevoIdCancion, autor, fecha, imagen, new ArrayList<>(), new ArrayList<>());
            nuevaCancion.setId(nuevoIdCancion);

            canciones.add(nuevaCancion);
            Files.write(Paths.get("src/main/resources/canciones.json"), gson.toJson(canciones).getBytes());

            return ResponseEntity.ok("{\"mensaje\":\"Canción creada correctamente\"}");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al leer/escribir el archivo JSON\"}");
        }
    }

    @GetMapping("/CatalogoCanciones")
    public ResponseEntity<String> CatalogoCanciones() {
        try {

            String jsonData = new String(Files.readAllBytes(Paths.get("src/main/resources/canciones.json")));


            Type listType = new TypeToken<List<Cancion>>() {
            }.getType();
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
    public ResponseEntity<String> MostrarCancion(@RequestParam("idCancion") Integer idCancion) {
        try {

            String cancionesData = new String(Files.readAllBytes(Paths.get("src/main/resources/canciones.json")));
            Type cancionesListType = new TypeToken<List<Cancion>>() {
            }.getType();
            List<Cancion> canciones = gson.fromJson(cancionesData, cancionesListType);


            Cancion cancionEncontrada = canciones.stream()
                    .filter(c -> c.getId().equals(idCancion))
                    .findFirst()
                    .orElse(null);

            if (cancionEncontrada == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"Canción no encontrada\"}");
            }

            return ResponseEntity.ok(gson.toJson(cancionEncontrada));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al leer el archivo JSON\"}");
        }
    }

    //UH de Comentarios


    @PostMapping("/CrearComentario")
    public ResponseEntity<String> CrearComentario(@RequestParam("idCancion") Integer idCancion,
                                                  @RequestParam("idUsuario") Integer idUsuario,
                                                  @RequestParam("comentarioTexto") String comentarioTexto) {
        try {

            String usuariosData = new String(Files.readAllBytes(Paths.get("src/main/resources/usuarios.json")));
            Type userListType = new TypeToken<List<Propietario>>() {
            }.getType();
            List<Propietario> usuarios = gson.fromJson(usuariosData, userListType);


            Propietario usuarioEncontrado = usuarios.stream()
                    .filter(u -> u.getId().equals(idUsuario))
                    .findFirst()
                    .orElse(null);

            if (usuarioEncontrado == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\":\"Usuario no registrado\"}");
            }


            String cancionesData = new String(Files.readAllBytes(Paths.get("src/main/resources/canciones.json")));
            Type cancionesListType = new TypeToken<List<Cancion>>() {
            }.getType();
            List<Cancion> canciones = gson.fromJson(cancionesData, cancionesListType);


            Cancion cancionEncontrada = canciones.stream()
                    .filter(c -> c.getId().equals(idCancion))
                    .findFirst()
                    .orElse(null);

            if (cancionEncontrada == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"Canción no encontrada\"}");
            }


            String comentariosData = new String(Files.readAllBytes(Paths.get("src/main/resources/comentarios.json")));
            Type comentariosListType = new TypeToken<List<Comentario>>() {
            }.getType();
            List<Comentario> comentarios = gson.fromJson(comentariosData, comentariosListType);


            int nuevoIdComentario = comentarios.stream()
                    .mapToInt(Comentario::getId)
                    .max()
                    .orElse(0) + 1;


            Comentario nuevoComentario = new Comentario(nuevoIdComentario, comentarioTexto, idUsuario, usuarioEncontrado.getNombre());


            comentarios.add(nuevoComentario);
            Files.write(Paths.get("src/main/resources/comentarios.json"), gson.toJson(comentarios).getBytes());


            cancionEncontrada.getComentarios().add(nuevoIdComentario);
            Files.write(Paths.get("src/main/resources/canciones.json"), gson.toJson(canciones).getBytes());

            return ResponseEntity.ok("{\"mensaje\":\"Comentario agregado correctamente\"}");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al leer/escribir el archivo JSON\"}");
        }
    }

    @PostMapping("/CrearPlaylist")
    public ResponseEntity<String> CrearPlaylist(@RequestParam("idUsuario") Integer idUsuario,
                                                @RequestParam("descripcion") String descripcionPlaylist) {
        try {

            String usuariosData = new String(Files.readAllBytes(Paths.get("src/main/resources/usuarios.json")));
            Type userListType = new TypeToken<List<Propietario>>() {
            }.getType();
            List<Propietario> usuarios = gson.fromJson(usuariosData, userListType);


            Propietario usuarioEncontrado = usuarios.stream()
                    .filter(u -> u.getId().equals(idUsuario))
                    .findFirst()
                    .orElse(null);

            if (usuarioEncontrado == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\":\"Usuario no registrado\"}");
            }


            String playlistsData = new String(Files.readAllBytes(Paths.get("src/main/resources/playlist.json")));
            Type playlistListType = new TypeToken<List<Playlist>>() {
            }.getType();
            List<Playlist> playlists = gson.fromJson(playlistsData, playlistListType);


            int nuevoIdPlaylist = playlists.stream()
                    .mapToInt(Playlist::getId)
                    .max()
                    .orElse(0) + 1;


            Playlist nuevaPlaylist = new Playlist(descripcionPlaylist, nuevoIdPlaylist, new ArrayList<>(), idUsuario, usuarioEncontrado.getNombre());


            playlists.add(nuevaPlaylist);
            Files.write(Paths.get("src/main/resources/playlist.json"), gson.toJson(playlists).getBytes());


            usuarioEncontrado.getPlaylists().add(nuevoIdPlaylist);
            Files.write(Paths.get("src/main/resources/usuarios.json"), gson.toJson(usuarios).getBytes());

            return ResponseEntity.ok("{\"mensaje\":\"Playlist creada correctamente\"}");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al leer/escribir el archivo JSON\"}");
        }
    }

    @PostMapping("/AgregarCancionAPlaylist")
    public ResponseEntity<String> AgregarCancionAPlaylist(@RequestParam("idUsuario") Integer idUsuario,
                                                          @RequestParam("idPlaylist") Integer idPlaylist,
                                                          @RequestParam("idCancion") Integer idCancion) {
        try {

            String usuariosData = new String(Files.readAllBytes(Paths.get("src/main/resources/usuarios.json")));
            Type userListType = new TypeToken<List<Propietario>>() {
            }.getType();
            List<Propietario> usuarios = gson.fromJson(usuariosData, userListType);


            Propietario usuarioEncontrado = usuarios.stream()
                    .filter(u -> u.getId().equals(idUsuario))
                    .findFirst()
                    .orElse(null);

            if (usuarioEncontrado == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\":\"Usuario no registrado\"}");
            }


            String playlistsData = new String(Files.readAllBytes(Paths.get("src/main/resources/playlist.json")));
            Type playlistListType = new TypeToken<List<Playlist>>() {
            }.getType();
            List<Playlist> playlists = gson.fromJson(playlistsData, playlistListType);


            Playlist playlistEncontrada = playlists.stream()
                    .filter(p -> p.getId().equals(idPlaylist))
                    .findFirst()
                    .orElse(null);

            if (playlistEncontrada == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"Playlist no encontrada\"}");
            }


            if (!playlistEncontrada.getIdPropietario().equals(idUsuario)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\":\"No tienes permiso para modificar esta playlist\"}");
            }


            String cancionesData = new String(Files.readAllBytes(Paths.get("src/main/resources/canciones.json")));
            Type cancionesListType = new TypeToken<List<Cancion>>() {
            }.getType();
            List<Cancion> canciones = gson.fromJson(cancionesData, cancionesListType);


            Cancion cancionEncontrada = canciones.stream()
                    .filter(c -> c.getId().equals(idCancion))
                    .findFirst()
                    .orElse(null);

            if (cancionEncontrada == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"Canción no encontrada\"}");
            }


            if (!playlistEncontrada.getCanciones().contains(idCancion)) {
                playlistEncontrada.getCanciones().add(idCancion);
            }


            Files.write(Paths.get("src/main/resources/playlist.json"), gson.toJson(playlists).getBytes());

            return ResponseEntity.ok("{\"mensaje\":\"Canción agregada correctamente a la playlist\"}");

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
            Type userListType = new TypeToken<List<Propietario>>() {
            }.getType();
            List<Propietario> usuarios = gson.fromJson(usuariosData, userListType);


            Propietario usuarioEncontrado = null;
            for (Propietario u : usuarios) {
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
                return ResponseEntity.ok(gson.toJson(usuarioEncontrado));
            }

            System.out.print(usuarioEncontrado);
            return ResponseEntity.ok(gson.toJson(usuarioEncontrado));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al leer el archivo JSON\"}");
        }
    }

    @GetMapping("/MostrarPlaylists")
    public ResponseEntity<String> MostrarPlaylists(@RequestParam("idUsuario") Integer idUsuario) {
        try {
            // Leer el JSON de usuarios
            String usuariosData = new String(Files.readAllBytes(Paths.get("src/main/resources/usuarios.json")));
            Type usuariosListType = new TypeToken<List<Propietario>>() {
            }.getType();
            List<Propietario> usuarios = gson.fromJson(usuariosData, usuariosListType);

            // Buscar el usuario por ID
            Propietario usuarioEncontrado = usuarios.stream()
                    .filter(u -> u.getId().equals(idUsuario))
                    .findFirst()
                    .orElse(null);

            if (usuarioEncontrado == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"Usuario no encontrado\"}");
            }


            if (usuarioEncontrado.getPlaylists() == null || usuarioEncontrado.getPlaylists().isEmpty()) {
                return ResponseEntity.ok("{\"mensaje\":\"El usuario no tiene playlists\"}");
            }


            String playlistsData = new String(Files.readAllBytes(Paths.get("src/main/resources/playlist.json")));
            Type playlistsListType = new TypeToken<List<Playlist>>() {
            }.getType();
            List<Playlist> todasLasPlaylists = gson.fromJson(playlistsData, playlistsListType);


            List<Playlist> playlistsUsuario = todasLasPlaylists.stream()
                    .filter(pl -> usuarioEncontrado.getPlaylists().contains(pl.getId()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(gson.toJson(playlistsUsuario));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al leer el archivo JSON\"}");
        }
    }

    @GetMapping("/ConsultarComentarios")
    public ResponseEntity<String> ConsultarComentarios(@RequestParam("idCancion") Integer idCancion) {
        try {

            String cancionesData = new String(Files.readAllBytes(Paths.get("src/main/resources/canciones.json")));
            Type cancionesListType = new TypeToken<List<Cancion>>() {
            }.getType();
            List<Cancion> canciones = gson.fromJson(cancionesData, cancionesListType);


            Cancion cancionEncontrada = canciones.stream()
                    .filter(c -> c.getId().equals(idCancion))
                    .findFirst()
                    .orElse(null);

            if (cancionEncontrada == null || cancionEncontrada.getComentarios().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"No hay comentarios para esta canción\"}");
            }


            String comentariosData = new String(Files.readAllBytes(Paths.get("src/main/resources/comentarios.json")));
            Type comentariosListType = new TypeToken<List<Comentario>>() {
            }.getType();
            List<Comentario> comentarios = gson.fromJson(comentariosData, comentariosListType);


            List<Comentario> comentariosEncontrados = comentarios.stream()
                    .filter(com -> cancionEncontrada.getComentarios().contains(com.getId()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(gson.toJson(comentariosEncontrados));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al leer el archivo JSON\"}");
        }
    }

}















