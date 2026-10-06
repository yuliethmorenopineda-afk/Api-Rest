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

import com.mundial.app.Entidades.Jugador;
import com.mundial.app.Repositorio.ClubRepositorio;
import com.mundial.app.Repositorio.JugadorRepositorio;

/**
 * Controlador REST de Jugador: expone el CRUD como API que habla JSON.
 *
 *  - @RestController: lo que devuelve cada método se convierte en JSON (no en una página).
 *  - @RequestMapping("/api"): prefijo común de todas las rutas de esta clase.
 *  - ResponseEntity: permite elegir el código HTTP (200, 201, 204, 400, 404, 409...).
 *  - El repositorio llega por el constructor (inyección de dependencias): Spring lo entrega solo.
 */
@RestController
@RequestMapping("/api")
public class JugadorRestControlador {

    private final JugadorRepositorio repo;
    private final ClubRepositorio clubes;

    public JugadorRestControlador(JugadorRepositorio repo, ClubRepositorio clubes) {
        this.repo = repo;
        this.clubes = clubes;
    }

    /** El jugador no funciona si su club no existe. */
    private String validar(Jugador j) {
        if (j.getClub() == null || j.getClub().getId() == null || !clubes.existsById(j.getClub().getId())) {
            return "El jugador necesita un club existente. Registra primero el club.";
        }
        return null;
    }

    // GET /api/jugadores: READ (leer todos) -> responde 200 con la lista en JSON
    @GetMapping("/jugadores")
    public List<Jugador> listar() {
        return repo.findAll();
    }

    // GET /api/jugadores/{id}: READ (leer uno) -> 200 con el objeto, o 404 si no existe
    @GetMapping("/jugadores/{id}")
    public ResponseEntity<Jugador> obtener(@PathVariable String id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // POST /api/jugadores: CREATE (crear) -> @RequestBody convierte el JSON recibido en objeto; 201 si se guardó (400 si falta una dependencia)
    @PostMapping("/jugadores")
    public ResponseEntity<?> crear(@RequestBody Jugador nuevo) {
        String error = validar(nuevo);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("error", error));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(nuevo));
    }

    // PUT /api/jugadores/{id}: UPDATE (actualizar) -> reemplaza el registro con ese id; 200 si se guardó, 404 si no existe
    @PutMapping("/jugadores/{id}")
    public ResponseEntity<?> actualizar(@PathVariable String id, @RequestBody Jugador datos) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        datos.setId(id);
        String error = validar(datos);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("error", error));
        }
        return ResponseEntity.ok(repo.save(datos));
    }

    // DELETE /api/jugadores/{id}: DELETE (eliminar) -> 204 sin contenido; 404 si no existe; 409 si otros registros dependen de él
    @DeleteMapping("/jugadores/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
