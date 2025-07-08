package com.Grupo586.Re.Beans.Controller;

import com.Grupo586.Re.Beans.Model.Cancion;
import com.Grupo586.Re.Beans.Model.Comentario;
import com.Grupo586.Re.Beans.Model.Playlist;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.Grupo586.Re.Beans.Model.Usuario.RolUsuario;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.Grupo586.Re.Beans.Model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/user")
@RestController

public class UsuarioController {

    public UsuarioController() {
    }

    private final Gson gson = new Gson();

    //UH de Perfil
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/MostrarUsuario")
    public ResponseEntity<String> MostrarUsuario(@RequestParam("id") Integer id) throws IOException {
        try {

            // Leer el JSON de usuarios
            String jsonData = new String(
                    Files.readAllBytes(Paths.get("src/main/resources/Almacenamiento/JSON/usuarios.json")));

            Type listType = new TypeToken<List<Usuario>>() {}.getType();
            List<Usuario> perfiles = gson.fromJson(jsonData, listType);

            System.out.println("Usuarios cargados: " + perfiles.size());
            System.out.println("Buscando usuario con id: " + id);

            // Filtrar los perfiles según el id
            List<Usuario> filtrados = perfiles.stream()
                    .filter(perfil -> perfil.getId().equals(id))
                    .collect(Collectors.toList());

            if (filtrados.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"Usuario no encontrado\"}");
            }

            return ResponseEntity.ok(gson.toJson(filtrados));

        } catch (IOException e) {
            // si hay error leyendo el JSON
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al leer el archivo JSON\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error inesperado\"}");
        }
    }
    //Esta es la que crea el perfil
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/CrearUsuario")
    public ResponseEntity<String> CrearUsuario(@RequestParam("nombre") String nombre,
                                               @RequestParam("clave") String clave) {
        try {
            String jsonData = new String(Files.readAllBytes(Paths.get("src/main/resources/Almacenamiento/JSON/usuarios.json")));

            Type listType = new TypeToken<List<Usuario>>() {}.getType();
            List<Usuario> usuarios = gson.fromJson(jsonData, listType);

            boolean nombreExiste = usuarios.stream()
                    .anyMatch(usuario -> usuario.getNombre().equalsIgnoreCase(nombre));
            if (nombreExiste) {
                return ResponseEntity.status(409).body("{\"error\":\"Ya existe un usuario con ese nombre\"}");
            }

            int maxId = usuarios.stream()
                    .mapToInt(Usuario::getId)
                    .max()
                    .orElse(0);

            int nuevoId = maxId + 1;


            RolUsuario rol = RolUsuario.Usuario;

            Usuario nuevoUsuario = new Usuario(nombre, clave, nuevoId, rol, new ArrayList<>(), new ArrayList<>());
            usuarios.add(nuevoUsuario);

            Files.write(Paths.get("src/main/resources/Almacenamiento/JSON/usuarios.json"),
                    gson.toJson(usuarios).getBytes());

            return ResponseEntity.ok("{\"mensaje\":\"Usuario registrado correctamente\"}");

        } catch (IOException e) {
            return ResponseEntity.status(500).body("{\"error\":\"Error en el JSON al crear el perfil.\"}");
        }
    }
    //UH de Cancion
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/CrearCancion")
    public ResponseEntity<String> crearCancion(@RequestParam("idUsuario") Integer idUsuario,
                                               @RequestParam("titulo") String titulo,
                                               @RequestParam("autor") String autor,
                                               @RequestParam("fecha") String fecha,
                                               @RequestParam("imagen") String imagen) {
        try {

            String usuariosData = new String(Files.readAllBytes(Paths.get("src/main/resources/Almacenamiento/JSON/usuarios.json")));
            Type userListType = new TypeToken<List<Usuario>>() {
            }.getType();
            List<Usuario> usuarios = gson.fromJson(usuariosData, userListType);

            Usuario usuarioEncontrado = usuarios.stream()
                    .filter(u -> u.getId().equals(idUsuario))
                    .findFirst()
                    .orElse(null);


            if (usuarioEncontrado == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\":\"Usuario no registrado\"}");
            }
            if (usuarioEncontrado.getRol() != Usuario.RolUsuario.Admin) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\":\"Permiso denegado: Solo los administradores pueden crear canciones\"}");
            }


            String jsonData = new String(Files.readAllBytes(Paths.get("src/main/resources/Almacenamiento/JSON/canciones.json")));
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
            Files.write(Paths.get("src/main/resources/Almacenamiento/JSON/canciones.json"), gson.toJson(canciones).getBytes());

            return ResponseEntity.ok("{\"mensaje\":\"Canción creada correctamente\"}");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al leer/escribir el archivo JSON\"}");
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/CatalogoCanciones")
    public ResponseEntity<String> CatalogoCanciones() {
        try {

            String jsonData = new String(Files.readAllBytes(Paths.get("src/main/resources/Almacenamiento/JSON/canciones.json")));


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

    @CrossOrigin(origins = {"http://localhost:8080","http://localhost:4200"})
    @GetMapping("/MostrarCancion")
    public ResponseEntity<String> MostrarCancion(@RequestParam("idCancion") Integer idCancion) {
        try {

            String cancionesData = new String(Files.readAllBytes(Paths.get("src/main/resources/Almacenamiento/JSON/canciones.json")));
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


    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/CrearComentario")
    public ResponseEntity<String> CrearComentario(@RequestParam("idCancion") Integer idCancion,
                                                  @RequestParam("idUsuario") Integer idUsuario,
                                                  @RequestParam("comentarioTexto") String comentarioTexto) {
        try {

            String usuariosData = new String(Files.readAllBytes(Paths.get("src/main/resources/Almacenamiento/JSON/usuarios.json")));
            Type userListType = new TypeToken<List<Usuario>>() {
            }.getType();
            List<Usuario> usuarios = gson.fromJson(usuariosData, userListType);


            Usuario usuarioEncontrado = usuarios.stream()
                    .filter(u -> u.getId().equals(idUsuario))
                    .findFirst()
                    .orElse(null);

            if (usuarioEncontrado == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\":\"Usuario no registrado\"}");
            }


            String cancionesData = new String(Files.readAllBytes(Paths.get("src/main/resources/Almacenamiento/JSON/canciones.json")));
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


            String comentariosData = new String(Files.readAllBytes(Paths.get("src/main/resources/Almacenamiento/JSON/comentarios.json")));
            Type comentariosListType = new TypeToken<List<Comentario>>() {
            }.getType();
            List<Comentario> comentarios = gson.fromJson(comentariosData, comentariosListType);


            int nuevoIdComentario = comentarios.stream()
                    .mapToInt(Comentario::getId)
                    .max()
                    .orElse(0) + 1;


            Comentario nuevoComentario = new Comentario(nuevoIdComentario, comentarioTexto, idUsuario, usuarioEncontrado.getNombre());


            comentarios.add(nuevoComentario);
            Files.write(Paths.get("src/main/resources/Almacenamiento/JSON/comentarios.json"), gson.toJson(comentarios).getBytes());


            cancionEncontrada.getComentarios().add(nuevoIdComentario);
            Files.write(Paths.get("src/main/resources/Almacenamiento/JSON/canciones.json"), gson.toJson(canciones).getBytes());

            return ResponseEntity.ok("{\"mensaje\":\"Comentario agregado correctamente\"}");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al leer/escribir el archivo JSON\"}");
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/CrearPlaylist")
    public ResponseEntity<String> CrearPlaylist(@RequestParam("idUsuario") Integer idUsuario,
                                                @RequestParam("descripcion") String descripcionPlaylist) {
        try {

            String usuariosData = new String(Files.readAllBytes(Paths.get("src/main/resources/Almacenamiento/JSON/usuarios.json")));
            Type userListType = new TypeToken<List<Usuario>>() {
            }.getType();
            List<Usuario> usuarios = gson.fromJson(usuariosData, userListType);


            Usuario usuarioEncontrado = usuarios.stream()
                    .filter(u -> u.getId().equals(idUsuario))
                    .findFirst()
                    .orElse(null);

            if (usuarioEncontrado == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\":\"Usuario no registrado\"}");
            }


            String playlistsData = new String(Files.readAllBytes(Paths.get("src/main/resources/Almacenamiento/JSON/playlist.json")));
            Type playlistListType = new TypeToken<List<Playlist>>() {
            }.getType();
            List<Playlist> playlists = gson.fromJson(playlistsData, playlistListType);


            int nuevoIdPlaylist = playlists.stream()
                    .mapToInt(Playlist::getId)
                    .max()
                    .orElse(0) + 1;


            Playlist nuevaPlaylist = new Playlist(descripcionPlaylist, nuevoIdPlaylist, new ArrayList<>(), idUsuario, usuarioEncontrado.getNombre());


            playlists.add(nuevaPlaylist);
            Files.write(Paths.get("src/main/resources/Almacenamiento/JSON/playlist.json"), gson.toJson(playlists).getBytes());


            usuarioEncontrado.getPlaylists().add(nuevoIdPlaylist);
            Files.write(Paths.get("src/main/resources/Almacenamiento/JSON/usuarios.json"), gson.toJson(usuarios).getBytes());

            return ResponseEntity.ok("{\"mensaje\":\"Playlist creada correctamente\"}");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al leer/escribir el archivo JSON\"}");
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/AgregarCancionAPlaylist")
    public ResponseEntity<String> AgregarCancionAPlaylist(@RequestParam("idUsuario") Integer idUsuario,
                                                          @RequestParam("idPlaylist") Integer idPlaylist,
                                                          @RequestParam("idCancion") Integer idCancion) {
        try {

            String usuariosData = new String(Files.readAllBytes(Paths.get("src/main/resources/Almacenamiento/JSON/usuarios.json")));
            Type userListType = new TypeToken<List<Usuario>>() {
            }.getType();
            List<Usuario> usuarios = gson.fromJson(usuariosData, userListType);


            Usuario usuarioEncontrado = usuarios.stream()
                    .filter(u -> u.getId().equals(idUsuario))
                    .findFirst()
                    .orElse(null);

            if (usuarioEncontrado == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\":\"Usuario no registrado\"}");
            }


            String playlistsData = new String(Files.readAllBytes(Paths.get("src/main/resources/Almacenamiento/JSON/playlist.json")));
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


            String cancionesData = new String(Files.readAllBytes(Paths.get("src/main/resources/Almacenamiento/JSON/canciones.json")));
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


            Files.write(Paths.get("src/main/resources/Almacenamiento/JSON/playlist.json"), gson.toJson(playlists).getBytes());

            return ResponseEntity.ok("{\"mensaje\":\"Canción agregada correctamente a la playlist\"}");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al leer/escribir el archivo JSON\"}");
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/Login")
    public ResponseEntity<String> Login(@RequestParam("nombre") String nombre,
                                        @RequestParam("clave") String clave) {
        try {

            String usuariosData = new String(Files.readAllBytes(Paths.get("src/main/resources/Almacenamiento/JSON/usuarios.json")));
            Type userListType = new TypeToken<List<Usuario>>() {
            }.getType();
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
                return ResponseEntity.ok(gson.toJson(usuarioEncontrado));
            }

            System.out.print(usuarioEncontrado);
            return ResponseEntity.ok(gson.toJson(usuarioEncontrado));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al leer el archivo JSON\"}");
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/MostrarPlaylists")
    public ResponseEntity<String> mostrarPlaylist(@RequestParam("idPlaylist") Integer idPlaylist) {
        try {
            // Leer el JSON de playlists
            String playlistsData = new String(Files.readAllBytes(
                    Paths.get("src/main/resources/Almacenamiento/JSON/playlist.json")));
            Type playlistsListType = new TypeToken<List<Playlist>>() {}.getType();
            List<Playlist> todasLasPlaylists = gson.fromJson(playlistsData, playlistsListType);

            // Buscar la playlist por ID
            Playlist playlistEncontrada = todasLasPlaylists.stream()
                    .filter(pl -> pl.getId().equals(idPlaylist))
                    .findFirst()
                    .orElse(null);

            if (playlistEncontrada == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"Error, la playlist no encontrada\"}");
            }

            return ResponseEntity.ok(gson.toJson(playlistEncontrada));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al leer el archivo JSON\"}");
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/ConsultarComentarios")
    public ResponseEntity<String> ConsultarComentarios(@RequestParam("idCancion") Integer idCancion) {
        try {

            String cancionesData = new String(Files.readAllBytes(Paths.get("src/main/resources/Almacenamiento/JSON/canciones.json")));
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


            String comentariosData = new String(Files.readAllBytes(Paths.get("src/main/resources/Almacenamiento/JSON/comentarios.json")));
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


    //SPRINt 2
//editar y eliminar comentarios
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/EditarComentario")
    public ResponseEntity<String> editarComentario(@RequestParam("idComentario") Integer idComentario,
                                                   @RequestParam("idUsuario") Integer idUsuario,
                                                   @RequestParam("nuevoComentario") String nuevoComentario) {
        try {

            String comentariosData = new String(Files.readAllBytes(
                    Paths.get("src/main/resources/Almacenamiento/JSON/comentarios.json")));
            Type listaComentariosType = new TypeToken<List<Comentario>>() {}.getType();
            List<Comentario> comentarios = gson.fromJson(comentariosData, listaComentariosType);


            Comentario comentarioEncontrado = comentarios.stream()
                    .filter(c -> c.getId().equals(idComentario))
                    .findFirst()
                    .orElse(null);

            if (comentarioEncontrado == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"Comentario no encontrado\"}");
            }

            // verificar usuario sea el propietario
            if (!comentarioEncontrado.getIdPropietario().equals(idUsuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"mensaje\":\"No tienes permiso para editar este comentario\"}");
            }


            comentarioEncontrado.setComentario(nuevoComentario);


            String jsonActualizado = gson.toJson(comentarios);
            Files.write(Paths.get("src/main/resources/Almacenamiento/JSON/comentarios.json"),
                    jsonActualizado.getBytes());

            return ResponseEntity.ok("{\"mensaje\":\"Comentario actualizado correctamente\"}");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al editar el comentario\"}");
        }
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/EliminarComentario")
    public ResponseEntity<String> eliminarComentario(@RequestParam("idComentario") Integer idComentario,
                                                     @RequestParam("idUsuario") Integer idUsuario) {
        try {
            // Leer usuarios
            String usuariosData = new String(Files.readAllBytes(
                    Paths.get("src/main/resources/Almacenamiento/JSON/usuarios.json")));
            Type usuariosListType = new TypeToken<List<Usuario>>() {}.getType();
            List<Usuario> usuarios = gson.fromJson(usuariosData, usuariosListType);

            Usuario usuario = usuarios.stream()
                    .filter(u -> u.getId().equals(idUsuario))
                    .findFirst()
                    .orElse(null);

            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"Error,usuario no encontrado\"}");
            }

            // ler comentarios
            String comentariosData = new String(Files.readAllBytes(
                    Paths.get("src/main/resources/Almacenamiento/JSON/comentarios.json")));
            Type listaComentariosType = new TypeToken<List<Comentario>>() {}.getType();
            List<Comentario> comentarios = gson.fromJson(comentariosData, listaComentariosType);

            Comentario comentario = comentarios.stream()
                    .filter(c -> c.getId().equals(idComentario))
                    .findFirst()
                    .orElse(null);

            if (comentario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"El comentario no fue encotnrado\"}");
            }

            boolean esAdmin = usuario.getRol() == Usuario.RolUsuario.Admin;
            boolean esAutor = usuario.getId().equals(comentario.getIdPropietario());

            if (!esAdmin && !esAutor) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"mensaje\":\"No tienes permiso para eliminar este comentario, lo sentimos\"}");
            }

            // elimiuna comentario del json comentarios
            comentarios.removeIf(c -> c.getId().equals(idComentario));
            Files.write(Paths.get("src/main/resources/Almacenamiento/JSON/comentarios.json"),
                    gson.toJson(comentarios).getBytes());

            // entra en canciones json y borra
            String cancionesData = new String(Files.readAllBytes(
                    Paths.get("src/main/resources/Almacenamiento/JSON/canciones.json")));
            Type listaCancionesType = new TypeToken<List<Cancion>>() {}.getType();
            List<Cancion> canciones = gson.fromJson(cancionesData, listaCancionesType);

            for (Cancion cancion : canciones) {
                if (cancion.getComentarios() != null) {
                    cancion.getComentarios().removeIf(id -> id.equals(idComentario));
                }
            }

            Files.write(Paths.get("src/main/resources/Almacenamiento/JSON/canciones.json"),
                    gson.toJson(canciones).getBytes());

            return ResponseEntity.ok("{\"mensaje\":\"Comentario eliminado correctamente\"}");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al procesar la eliminación\"}");
        }
    }

    //editar y eliminar perfil
   /* @PostMapping("/EliminarPerfil")
    public ResponseEntity<String> eliminarPerfil(@RequestParam("idUsuario") Integer idUsuarioActivo,
                                                 @RequestParam("idPerfil") Integer idPerfil) {
        try {
            if (!idUsuarioActivo.equals(idPerfil)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"mensaje\":\"Solo puedes eliminar tu propio perfil\"}");
            }

            // Leer usuarios
            String usuariosData = Files.readString(Paths.get("src/main/resources/Almacenamiento/JSON/usuarios.json"));
            Type usuariosType = new TypeToken<List<Usuario>>() {}.getType();
            List<Usuario> usuarios = gson.fromJson(usuariosData, usuariosType);

            Usuario usuarioAEliminar = usuarios.stream()
                    .filter(u -> u.getId().equals(idPerfil))
                    .findFirst()
                    .orElse(null);

            if (usuarioAEliminar == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"Usuario no encontrado\"}");
            }


            String comentariosData = Files.readString(Paths.get("src/main/resources/Almacenamiento/JSON/comentarios.json"));
            Type comentariosType = new TypeToken<List<Comentario>>() {}.getType();
            List<Comentario> comentarios = gson.fromJson(comentariosData, comentariosType);


            comentarios.removeIf(c -> c.getIdPropietario().equals(idPerfil));


            String playlistsData = Files.readString(Paths.get("src/main/resources/Almacenamiento/JSON/playlists.json"));
            Type playlistsType = new TypeToken<List<Playlist>>() {}.getType();
            List<Playlist> playlists = gson.fromJson(playlistsData, playlistsType);

            // -1 en playlists del usuario para desvincular
            for (Playlist playlist : playlists) {
                if (playlist.getIdPropietario().equals(idPerfil)) {
                    playlist.setIdPropietario(-1);
                }
            }

            // eliminar al usuario de la lista de amigos de otro
            for (Usuario u : usuarios) {
                if (!u.getId().equals(idPerfil)) {
                    u.getAmigos().removeIf(id -> id.equals(idPerfil));
                }
            }


            usuarios.removeIf(u -> u.getId().equals(idPerfil));


            Files.writeString(Paths.get("src/main/resources/Almacenamiento/JSON/usuarios.json"), gson.toJson(usuarios));
            Files.writeString(Paths.get("src/main/resources/Almacenamiento/JSON/comentarios.json"), gson.toJson(comentarios));
            Files.writeString(Paths.get("src/main/resources/Almacenamiento/JSON/playlists.json"), gson.toJson(playlists));

            return ResponseEntity.ok("{\"mensaje\":\"Perfil eliminado correctamente\"}");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al procesar la eliminación\"}");
        }
    }*/
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/EditarNombre")
    public ResponseEntity<String> editarNombre(@RequestParam("idUsuario") Integer idUsuario,
                                               @RequestParam("nuevoNombre") String nuevoNombre) {
        try {
            String usuariosData = Files.readString(Paths.get("src/main/resources/Almacenamiento/JSON/usuarios.json"));
            Type usuariosType = new TypeToken<List<Usuario>>() {}.getType();
            List<Usuario> usuarios = gson.fromJson(usuariosData, usuariosType);

            Usuario usuario = usuarios.stream()
                    .filter(u -> u.getId().equals(idUsuario))
                    .findFirst()
                    .orElse(null);

            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"Usuario no encontrado\"}");
            }

            boolean nombreYaExiste = usuarios.stream()
                    .anyMatch(u -> u.getNombre().equalsIgnoreCase(nuevoNombre) && !u.getId().equals(idUsuario));

            if (nombreYaExiste) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"mensaje\":\"Lo sentimos, nombre ya está en uso por otro usuario\"}");
            }

            usuario.setNombre(nuevoNombre);
            Files.writeString(Paths.get("src/main/resources/Almacenamiento/JSON/usuarios.json"), gson.toJson(usuarios));

            return ResponseEntity.ok("{\"mensaje\":\"El nombre ha sido actualizado correctamente\"}");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al procesar la edición de nombre\"}");
        }
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/EditarClave")
    public ResponseEntity<String> editarClave(@RequestParam("idUsuario") Integer idUsuario,
                                              @RequestParam("nuevaClave") String nuevaClave) {
        try {
            String usuariosData = Files.readString(Paths.get("src/main/resources/Almacenamiento/JSON/usuarios.json"));
            Type usuariosType = new TypeToken<List<Usuario>>() {}.getType();
            List<Usuario> usuarios = gson.fromJson(usuariosData, usuariosType);

            Usuario usuario = usuarios.stream()
                    .filter(u -> u.getId().equals(idUsuario))
                    .findFirst()
                    .orElse(null);

            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"Usuario no encontrado\"}");
            }

            if (usuario.getClave().equals(nuevaClave)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"mensaje\":\"La nueva clave no puede ser igual a la anterior\"}");
            }

            usuario.setClave(nuevaClave);
            Files.writeString(Paths.get("src/main/resources/Almacenamiento/JSON/usuarios.json"), gson.toJson(usuarios));

            return ResponseEntity.ok("{\"mensaje\":\"La clave ha sido actualizada correctamente\"}");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al procesar la edición de clave\"}");
        }
    }

    //eliminar cancion y eliminar cancion de playlist
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/EliminarCancion")
    public ResponseEntity<String> eliminarCancion(@RequestParam("idCancion") Integer idCancion,
                                                  @RequestParam("idUsuario") Integer idUsuario) {
        try {
            // Leer usuarios
            String usuariosData = Files.readString(Paths.get("src/main/resources/Almacenamiento/JSON/usuarios.json"));
            Type usuariosType = new TypeToken<List<Usuario>>() {}.getType();
            List<Usuario> usuarios = gson.fromJson(usuariosData, usuariosType);

            Usuario usuario = usuarios.stream()
                    .filter(u -> u.getId().equals(idUsuario))
                    .findFirst()
                    .orElse(null);

            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"Usuario no encontrado\"}");
            }

            if (usuario.getRol() != Usuario.RolUsuario.Admin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"mensaje\":\"Solo los administradores pueden eliminar canciones permanentemente\"}");
            }

            // Leer canciones
            String cancionesData = Files.readString(Paths.get("src/main/resources/Almacenamiento/JSON/canciones.json"));
            Type cancionesType = new TypeToken<List<Cancion>>() {}.getType();
            List<Cancion> canciones = gson.fromJson(cancionesData, cancionesType);

            Cancion cancionAEliminar = canciones.stream()
                    .filter(c -> c.getId().equals(idCancion))
                    .findFirst()
                    .orElse(null);

            if (cancionAEliminar == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"Canción no encontrada\"}");
            }


            canciones.removeIf(c -> c.getId().equals(idCancion));
            Files.writeString(Paths.get("src/main/resources/Almacenamiento/JSON/canciones.json"), gson.toJson(canciones));

            // Limpiar de playlists
            String playlistsData = Files.readString(Paths.get("src/main/resources/Almacenamiento/JSON/playlists.json"));
            Type playlistsType = new TypeToken<List<Playlist>>() {}.getType();
            List<Playlist> playlists = gson.fromJson(playlistsData, playlistsType);

            for (Playlist playlist : playlists) {
                playlist.getCanciones().removeIf(id -> id.equals(idCancion));
            }

            Files.writeString(Paths.get("src/main/resources/Almacenamiento/JSON/playlists.json"), gson.toJson(playlists));

            return ResponseEntity.ok("{\"mensaje\":\"Canción eliminada permanentemente por el administrador\"}");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al procesar la eliminación de la canción\"}");
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/EliminarCancionDePlaylist")
    public ResponseEntity<String> eliminarCancionDePlaylist(@RequestParam("idUsuario") Integer idUsuario,
                                                            @RequestParam("idPlaylist") Integer idPlaylist,
                                                            @RequestParam("idCancion") Integer idCancion) {
        try {
            // Leer usuarios
            String usuariosData = Files.readString(Paths.get("src/main/resources/Almacenamiento/JSON/usuarios.json"));
            Type usuariosType = new TypeToken<List<Usuario>>() {}.getType();
            List<Usuario> usuarios = gson.fromJson(usuariosData, usuariosType);

            Usuario usuario = usuarios.stream()
                    .filter(u -> u.getId().equals(idUsuario))
                    .findFirst()
                    .orElse(null);

            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"mensaje\":\"Usuario no registrado\"}");
            }

            // Leer playlists
            String playlistsData = Files.readString(Paths.get("src/main/resources/Almacenamiento/JSON/playlists.json"));
            Type playlistsType = new TypeToken<List<Playlist>>() {}.getType();
            List<Playlist> playlists = gson.fromJson(playlistsData, playlistsType);

            Playlist playlist = playlists.stream()
                    .filter(p -> p.getId().equals(idPlaylist))
                    .findFirst()
                    .orElse(null);

            if (playlist == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"Playlist no encontrada\"}");
            }

            if (!playlist.getIdPropietario().equals(idUsuario)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"mensaje\":\"No tienes permiso para modificar esta playlist\"}");
            }

            if (!playlist.getCanciones().contains(idCancion)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"mensaje\":\"La canción no está en la playlist\"}");
            }

            playlist.getCanciones().removeIf(id -> id.equals(idCancion));


            Files.writeString(Paths.get("src/main/resources/Almacenamiento/JSON/playlists.json"), gson.toJson(playlists));

            return ResponseEntity.ok("{\"mensaje\":\"Canción eliminada de la playlist correctamente\"}");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al procesar la eliminación\"}");
        }
    }
    //agregar y eliminar amigos
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/AgregarAmigo")
    public ResponseEntity<String> agregarAmigo(@RequestParam("idUsuario") Integer idUsuario,
                                               @RequestParam("idAmigo") Integer idAmigo) {
        try {
            String usuariosData = Files.readString(Paths.get("src/main/resources/Almacenamiento/JSON/usuarios.json"));
            Type usuariosType = new TypeToken<List<Usuario>>() {}.getType();
            List<Usuario> usuarios = gson.fromJson(usuariosData, usuariosType);

            Usuario usuario = usuarios.stream()
                    .filter(u -> u.getId().equals(idUsuario))
                    .findFirst()
                    .orElse(null);

            Usuario amigo = usuarios.stream()
                    .filter(u -> u.getId().equals(idAmigo))
                    .findFirst()
                    .orElse(null);

            if (usuario == null || amigo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"Usuario o amigo no encontrado\"}");
            }

            if (usuario.getAmigos().contains(idAmigo)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"mensaje\":\"El usuario ya tiene agregado a ese amigo\"}");
            }

            usuario.getAmigos().add(idAmigo);

            Files.writeString(Paths.get("src/main/resources/Almacenamiento/JSON/usuarios.json"), gson.toJson(usuarios));
            return ResponseEntity.ok("{\"mensaje\":\"Amigo agregado correctamente\"}");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al procesar la solicitud\"}");
        }
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/EliminarAmigo")
    public ResponseEntity<String> eliminarAmigo(@RequestParam("idUsuario") Integer idUsuario,
                                                @RequestParam("idAmigo") Integer idAmigo) {
        try {
            String usuariosData = Files.readString(Paths.get("src/main/resources/Almacenamiento/JSON/usuarios.json"));
            Type usuariosType = new TypeToken<List<Usuario>>() {}.getType();
            List<Usuario> usuarios = gson.fromJson(usuariosData, usuariosType);

            Usuario usuario = usuarios.stream()
                    .filter(u -> u.getId().equals(idUsuario))
                    .findFirst()
                    .orElse(null);

            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"Usuario no encontrado\"}");
            }

            if (!usuario.getAmigos().contains(idAmigo)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"mensaje\":\"Ese usuario no está en tu lista de amigos\"}");
            }

            usuario.getAmigos().removeIf(id -> id.equals(idAmigo));

            Files.writeString(Paths.get("src/main/resources/Almacenamiento/JSON/usuarios.json"), gson.toJson(usuarios));
            return ResponseEntity.ok("{\"mensaje\":\"Amigo eliminado correctamente\"}");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al procesar la solicitud\"}");
        }
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/EditarPlaylist")
    public ResponseEntity<String> editarNombrePlaylist(@RequestParam("idUsuario") Integer idUsuario,
                                                       @RequestParam("idPlaylist") Integer idPlaylist,
                                                       @RequestParam("nuevoTitulo") String nuevoTitulo) {
        try {
            String playlistsData = Files.readString(Paths.get("src/main/resources/Almacenamiento/JSON/playlists.json"));
            Type playlistsType = new TypeToken<List<Playlist>>() {}.getType();
            List<Playlist> playlists = gson.fromJson(playlistsData, playlistsType);

            Playlist playlist = playlists.stream()
                    .filter(p -> p.getId().equals(idPlaylist))
                    .findFirst()
                    .orElse(null);

            if (playlist == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"Playlist no encontrada\"}");
            }

            if (!playlist.getIdPropietario().equals(idUsuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"mensaje\":\"No tienes permiso para modificar esta playlist\"}");
            }

            playlist.setTitulo(nuevoTitulo);

            Files.writeString(Paths.get("src/main/resources/Almacenamiento/JSON/playlists.json"), gson.toJson(playlists));

            return ResponseEntity.ok("{\"mensaje\":\"Nombre de la playlist actualizado correctamente\"}");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al procesar la edición de la playlist\"}");
        }
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/EditarCancion")
    public ResponseEntity<String> editarCancion(@RequestParam("idUsuario") Integer idUsuario,
                                                @RequestParam("idCancion") Integer idCancion,
                                                @RequestParam("nuevoTitulo") String nuevoTitulo,
                                                @RequestParam("nuevoAutor") String nuevoAutor,
                                                @RequestParam("nuevaFecha") String nuevaFecha,
                                                @RequestParam("nuevaImagen") String nuevaImagen,
                                                @RequestParam("links") String links) {
        //para pdoer enviar varios links: sepáralos con comas (ej: link1,link2,link3)
        try {
            // Leer usuarios
            String usuariosData = Files.readString(Paths.get("src/main/resources/Almacenamiento/JSON/usuarios.json"));
            Type usuariosType = new TypeToken<List<Usuario>>() {}.getType();
            List<Usuario> usuarios = gson.fromJson(usuariosData, usuariosType);

            Usuario usuario = usuarios.stream()
                    .filter(u -> u.getId().equals(idUsuario))
                    .findFirst()
                    .orElse(null);

            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"Usuario no encontrado\"}");
            }

            if (usuario.getRol() != Usuario.RolUsuario.Admin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"mensaje\":\"Solo los administradores pueden editar canciones, lo sentimos\"}");
            }

            // Leer canciones
            String cancionesData = Files.readString(Paths.get("src/main/resources/Almacenamiento/JSON/canciones.json"));
            Type cancionesType = new TypeToken<List<Cancion>>() {}.getType();
            List<Cancion> canciones = gson.fromJson(cancionesData, cancionesType);

            Cancion cancion = canciones.stream()
                    .filter(c -> c.getId().equals(idCancion))
                    .findFirst()
                    .orElse(null);

            if (cancion == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"mensaje\":\"Canción no encontrada\"}");
            }

            // Procesar links separados por coma
            List<String> listaLinks = Arrays.stream(links.split(","))
                    .map(String::trim)
                    .filter(link -> !link.isEmpty())
                    .collect(Collectors.toList());

            // Actualizar campos
            cancion.setTitulo(nuevoTitulo);
            cancion.setAutor(nuevoAutor);
            cancion.setFecha(nuevaFecha);
            cancion.setImagen(nuevaImagen);
            cancion.setLinks(new ArrayList<>(listaLinks));

            // Guardar cambios
            Files.writeString(Paths.get("src/main/resources/Almacenamiento/JSON/canciones.json"), gson.toJson(canciones));

            return ResponseEntity.ok("{\"mensaje\":\"Canción editada correctamente\"}");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al procesar la edición\"}");
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/MostrarTodasPlaylists")
    public ResponseEntity<String> mostrarTodasPlaylists() {
        try {
            String playlistsData = Files.readString(Paths.get("src/main/resources/Almacenamiento/JSON/playlists.json"));
            Type playlistListType = new TypeToken<List<Playlist>>() {}.getType();
            List<Playlist> playlists = gson.fromJson(playlistsData, playlistListType);

            return ResponseEntity.ok(gson.toJson(playlists));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Error al leer el archivo de playlists\"}");
        }
    }


    //xd
}















