package com.mundial.app.Entidades;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

/**
 * Competición. Relación MUCHOS A MUCHOS con Club: reúne varios clubes y un club puede jugar varias competiciones.
 *
 * Es un POJO (clase simple con atributos, getters y setters). Spring Data lo
 * convierte a documento JSON/BSON de MongoDB y viceversa.
 */
// @Document: la clase se guarda como "documento" en la colección indicada
// (una colección equivale a una tabla en SQL, un documento a una fila).
@Document(collection = "competicion")
public class Competicion {
    // @Id: identificador único del documento. Usamos un UUID (texto único)
    // que se genera solo al crear el objeto.
    @Id
    private String id = UUID.randomUUID().toString();
    private String nombre;
    private int anio;
    private String sede;

    // MUCHOS A MUCHOS: una competición reúne varios clubes y un club juega varias competiciones
    // @DocumentReference: no copia el documento relacionado, solo guarda su _id
    // y Spring lo "rellena" al leer (como una llave foránea).
    @DocumentReference
    private List<Club> clubes = new ArrayList<>();

    // Constructor vacío: lo necesita Jackson/Spring para crear el objeto al leer JSON o la base de datos.
    public Competicion() { }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }
    public String getSede() { return sede; }
    public void setSede(String sede) { this.sede = sede; }
    public List<Club> getClubes() { return clubes; }
    public void setClubes(List<Club> clubes) { this.clubes = clubes; }
}
