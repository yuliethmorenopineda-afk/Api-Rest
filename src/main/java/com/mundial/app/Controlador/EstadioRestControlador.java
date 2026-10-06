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

import com.mundial.app.Entidades.Estadio;
import com.mundial.app.Repositorio.ClubRepositorio;
import com.mundial.app.Repositorio.EstadioRepositorio;

/**
 * Controlador REST de Estadio: expone el CRUD como API que habla JSON.
 *
 *  - @RestController: lo que devuelve cada método se convierte en JSON (no en una página).
 *  - @RequestMapping("/api"): prefijo común de todas las rutas de esta clase.
 *  - ResponseEntity: permite elegir el código HTTP (200, 201, 204, 400, 404, 409...).
 *  - El repositorio llega por el constructor (inyección de dependencias): Spring lo entrega solo.
 */
@RestController
@RequestMapping("/api")
public class EstadioRestControlador {

    private final EstadioRepositorio repo;
    private final ClubRepositorio clubes;

    public EstadioRestControlador(EstadioRepositorio repo, ClubRepositorio clubes) {
        this.repo = repo;
        this.clubes = clubes;
    }

    // GET /api/estadios: READ (leer todos) -> responde 200 con la lista en JSON
    @GetMapping("/estadios")
    public List<Estadio> listar() {
        return repo.findAll();
    }

    // GET /api/estadios/{id}: READ (leer uno) -> 200 con el objeto, o 404 si no existe
    @GetMapping("/estadios/{id}")
    public ResponseEntity<Estadio> obtener(@PathVariable String id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // POST /api/estadios: CREATE (crear) -> @RequestBody convierte el JSON recibido en objeto; 201 si se guardó (400 si falta una dependencia)
    @PostMapping("/estadios")
    public ResponseEntity<Estadio> crear(@RequestBody Estadio nuevo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(nuevo));
    }

    // PUT /api/estadios/{id}: UPDATE (actualizar) -> reemplaza el registro con ese id; 200 si se guardó, 404 si no existe
    @PutMapping("/estadios/{id}")
    public ResponseEntity<Estadio> actualizar(@PathVariable String id, @RequestBody Estadio datos) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        datos.setId(id);
        return ResponseEntity.ok(repo.save(datos));
    }

    // DELETE /api/estadios/{id}: DELETE (eliminar) -> 204 sin contenido; 404 si no existe; 409 si otros registros dependen de él
    @DeleteMapping("/estadios/{id}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        // Un club depende de este registro: no se puede borrar mientras exista
        if (clubes.existeConEstadio(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "No se puede eliminar: hay clubes que dependen de este registro."));
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
