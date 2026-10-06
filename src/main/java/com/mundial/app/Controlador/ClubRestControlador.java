package com.mundial.app.Controlador;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mundial.app.Entidades.Club;
import com.mundial.app.Repositorio.AsociacionRepositorio;
import com.mundial.app.Repositorio.ClubRepositorio;
import com.mundial.app.Repositorio.CompeticionRepositorio;
import com.mundial.app.Repositorio.EntrenadorRepositorio;
import com.mundial.app.Repositorio.EstadioRepositorio;
import com.mundial.app.Repositorio.JugadorRepositorio;

/**
 * Controlador REST de Club: expone el CRUD como API que habla JSON.
 *
 *  - @RestController: lo que devuelve cada método se convierte en JSON (no en una página).
 *  - @RequestMapping("/api"): prefijo común de todas las rutas de esta clase.
 *  - ResponseEntity: permite elegir el código HTTP (200, 201, 204, 400, 404, 409...).
 *  - El repositorio llega por el constructor (inyección de dependencias): Spring lo entrega solo.
 */
@RestController
@RequestMapping("/api")
public class ClubRestControlador {

    private final ClubRepositorio repo;
    private final EstadioRepositorio estadios;
    private final EntrenadorRepositorio entrenadores;
    private final AsociacionRepositorio asociaciones;
    private final JugadorRepositorio jugadores;
    private final CompeticionRepositorio competiciones;

    public ClubRestControlador(ClubRepositorio repo, EstadioRepositorio estadios, EntrenadorRepositorio entrenadores,
            AsociacionRepositorio asociaciones, JugadorRepositorio jugadores, CompeticionRepositorio competiciones) {
        this.repo = repo;
        this.estadios = estadios;
        this.entrenadores = entrenadores;
        this.asociaciones = asociaciones;
        this.jugadores = jugadores;
        this.competiciones = competiciones;
    }

    /** Reglas de dependencia: el club no funciona si no existen su estadio, entrenador y asociación. */
    private String validar(Club c, String idActual) {
        if (c.getEstadio() == null || c.getEstadio().getId() == null || !estadios.existsById(c.getEstadio().getId())) {
            return "El club necesita un estadio existente. Registra primero el estadio.";
        }
        if (c.getEntrenador() == null || c.getEntrenador().getId() == null
                || !entrenadores.existsById(c.getEntrenador().getId())) {
            return "El club necesita un entrenador existente. Registra primero el entrenador.";
        }
        if (c.getAsociacion() == null || c.getAsociacion().getId() == null
                || !asociaciones.existsById(c.getAsociacion().getId())) {
            return "El club necesita una asociación existente. Registra primero la asociación.";
        }
        if (repo.estadioUsadoPorOtro(c.getEstadio().getId(), idActual)) {
            return "Ese estadio ya pertenece a otro club (relación uno a uno).";
        }
        if (repo.entrenadorUsadoPorOtro(c.getEntrenador().getId(), idActual)) {
            return "Ese entrenador ya dirige a otro club (relación uno a uno).";
        }
        return null;
    }

    // GET /api/clubes: READ (leer todos) -> responde 200 con la lista en JSON
    @GetMapping("/clubes")
    public List<Club> listar() {
        return repo.findAll();
    }

    // GET /api/clubes/{id}: READ (leer uno) -> 200 con el objeto, o 404 si no existe
    @GetMapping("/clubes/{id}")
    public ResponseEntity<Club> obtener(@PathVariable String id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // POST /api/clubes: CREATE (crear) -> @RequestBody convierte el JSON recibido en objeto; 201 si se guardó (400 si falta una dependencia)
    @PostMapping("/clubes")
    public ResponseEntity<?> crear(@RequestBody Club nuevo) {
        String error = validar(nuevo, nuevo.getId());
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("error", error));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(nuevo));
    }

    // PUT /api/clubes/{id}: UPDATE (actualizar) -> reemplaza el registro con ese id; 200 si se guardó, 404 si no existe
    @PutMapping("/clubes/{id}")
    public ResponseEntity<?> actualizar(@PathVariable String id, @RequestBody Club datos) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        datos.setId(id);
        String error = validar(datos, id);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("error", error));
        }
        return ResponseEntity.ok(repo.save(datos));
    }

    // DELETE /api/clubes/{id}: DELETE (eliminar) -> 204 sin contenido; 404 si no existe; 409 si otros registros dependen de él
    @DeleteMapping("/clubes/{id}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if (jugadores.existeConClub(id) || competiciones.existeConClub(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "No se puede eliminar: hay jugadores o competiciones que dependen de este club."));
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
