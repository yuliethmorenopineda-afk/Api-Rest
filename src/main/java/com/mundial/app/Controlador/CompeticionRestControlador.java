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
import com.mundial.app.Entidades.Competicion;
import com.mundial.app.Repositorio.ClubRepositorio;
import com.mundial.app.Repositorio.CompeticionRepositorio;

/**
 * Controlador REST de Competicion: expone el CRUD como API que habla JSON.
 *
 *  - @RestController: lo que devuelve cada método se convierte en JSON (no en una página).
 *  - @RequestMapping("/api"): prefijo común de todas las rutas de esta clase.
 *  - ResponseEntity: permite elegir el código HTTP (200, 201, 204, 400, 404, 409...).
 *  - El repositorio llega por el constructor (inyección de dependencias): Spring lo entrega solo.
 */
@RestController
@RequestMapping("/api")
public class CompeticionRestControlador {

    private final CompeticionRepositorio repo;
    private final ClubRepositorio clubes;

    public CompeticionRestControlador(CompeticionRepositorio repo, ClubRepositorio clubes) {
        this.repo = repo;
        this.clubes = clubes;
    }

    /** La competición no funciona sin clubes: al menos dos, y todos deben existir. */
    private String validar(Competicion c) {
        if (c.getClubes() == null || c.getClubes().size() < 2) {
            return "La competición necesita al menos dos clubes. Registra primero los clubes.";
        }
        for (Club club : c.getClubes()) {
            if (club == null || club.getId() == null || !clubes.existsById(club.getId())) {
                return "Uno de los clubes elegidos no existe.";
            }
        }
        return null;
    }

    // GET /api/competiciones: READ (leer todos) -> responde 200 con la lista en JSON
    @GetMapping("/competiciones")
    public List<Competicion> listar() {
        return repo.findAll();
    }

    // GET /api/competiciones/{id}: READ (leer uno) -> 200 con el objeto, o 404 si no existe
    @GetMapping("/competiciones/{id}")
    public ResponseEntity<Competicion> obtener(@PathVariable String id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // POST /api/competiciones: CREATE (crear) -> @RequestBody convierte el JSON recibido en objeto; 201 si se guardó (400 si falta una dependencia)
    @PostMapping("/competiciones")
    public ResponseEntity<?> crear(@RequestBody Competicion nueva) {
        String error = validar(nueva);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("error", error));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(nueva));
    }

    // PUT /api/competiciones/{id}: UPDATE (actualizar) -> reemplaza el registro con ese id; 200 si se guardó, 404 si no existe
    @PutMapping("/competiciones/{id}")
    public ResponseEntity<?> actualizar(@PathVariable String id, @RequestBody Competicion datos) {
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

    // DELETE /api/competiciones/{id}: DELETE (eliminar) -> 204 sin contenido; 404 si no existe; 409 si otros registros dependen de él
    @DeleteMapping("/competiciones/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
