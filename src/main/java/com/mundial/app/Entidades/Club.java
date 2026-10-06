package com.mundial.app.Entidades;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

/**
 * Club de fútbol: es el centro de las relaciones. NO funciona sin estadio, entrenador y asociación (la API lo valida).
 *
 * Es un POJO (clase simple con atributos, getters y setters). Spring Data lo
 * convierte a documento JSON/BSON de MongoDB y viceversa.
 */
// @Document: la clase se guarda como "documento" en la colección indicada
// (una colección equivale a una tabla en SQL, un documento a una fila).
@Document(collection = "club")
public class Club {
    // @Id: identificador único del documento. Usamos un UUID (texto único)
    // que se genera solo al crear el objeto.
    @Id
    private String id = UUID.randomUUID().toString();
    private String nombre;
    private int anioFundacion;

    // UNO A UNO: cada club tiene su propio estadio
    // @DocumentReference: no copia el documento relacionado, solo guarda su _id
    // y Spring lo "rellena" al leer (como una llave foránea).
    @DocumentReference
    private Estadio estadio;

    // UNO A UNO: cada club tiene su propio entrenador
    @DocumentReference
    private Entrenador entrenador;

    // MUCHOS A UNO: muchos clubes pertenecen a una asociación
    @DocumentReference
    private Asociacion asociacion;

    // Constructor vacío: lo necesita Jackson/Spring para crear el objeto al leer JSON o la base de datos.
    public Club() { }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getAnioFundacion() { return anioFundacion; }
    public void setAnioFundacion(int anioFundacion) { this.anioFundacion = anioFundacion; }
    public Estadio getEstadio() { return estadio; }
    public void setEstadio(Estadio estadio) { this.estadio = estadio; }
    public Entrenador getEntrenador() { return entrenador; }
    public void setEntrenador(Entrenador entrenador) { this.entrenador = entrenador; }
    public Asociacion getAsociacion() { return asociacion; }
    public void setAsociacion(Asociacion asociacion) { this.asociacion = asociacion; }
}
