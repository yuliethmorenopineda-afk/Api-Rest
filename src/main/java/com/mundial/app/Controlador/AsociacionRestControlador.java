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

import com.mundial.app.Entidades.Asociacion;
import com.mundial.app.Repositorio.ClubRepositorio;
import com.mundial.app.Repositorio.AsociacionRepositorio;

/**
 * Controlador REST de Asociacion: expone el CRUD como API que habla JSON.
 *
 *  - @RestController: lo que devuelve cada método se convierte en JSON (no en una página).
 *  - @RequestMapping("/api"): prefijo común de todas las rutas de esta clase.
 *  - ResponseEntity: permite elegir el código HTTP (200, 201, 204, 400, 404, 409...).
 *  - El repositorio llega por el constructor (inyección de dependencias): Spring lo entrega solo.
 */
@RestController
@RequestMapping("/api")
public class AsociacionRestControlador {

    private final AsociacionRepositorio repo;
    private final ClubRepositorio clubes;

    public AsociacionRestControlador(AsociacionRepositorio repo, ClubRepositorio clubes) {
        this.repo = repo;
        this.clubes = clubes;
    }

    // GET /api/asociaciones: READ (leer todos) -> responde 200 con la lista en JSON
    @GetMapping("/asociaciones")
    public List<Asociacion> listar() {
        return repo.findAll();
    }

    // GET /api/asociaciones/{id}: READ (leer uno) -> 200 con el objeto, o 404 si no existe
    @GetMapping("/asociaciones/{id}")
    public ResponseEntity<Asociacion> obtener(@PathVariable String id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // POST /api/asociaciones: CREATE (crear) -> @RequestBody convierte el JSON recibido en objeto; 201 si se guardó (400 si falta una dependencia)
    @PostMapping("/asociaciones")
    public ResponseEntity<Asociacion> crear(@RequestBody Asociacion nuevo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(nuevo));
    }

    // PUT /api/asociaciones/{id}: UPDATE (actualizar) -> reemplaza el registro con ese id; 200 si se guardó, 404 si no existe
    @PutMapping("/asociaciones/{id}")
    public ResponseEntity<Asociacion> actualizar(@PathVariable String id, @RequestBody Asociacion datos) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        datos.setId(id);
        return ResponseEntity.ok(repo.save(datos));
    }

    // DELETE /api/asociaciones/{id}: DELETE (eliminar) -> 204 sin contenido; 404 si no existe; 409 si otros registros dependen de él
    @DeleteMapping("/asociaciones/{id}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        // Un club depende de este registro: no se puede borrar mientras exista
        if (clubes.existeConAsociacion(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "No se puede eliminar: hay clubes que dependen de este registro."));
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
