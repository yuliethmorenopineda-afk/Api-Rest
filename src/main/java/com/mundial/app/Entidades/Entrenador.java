package com.mundial.app.Entidades;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entrenador de un club. Relación UNO A UNO con Club (un entrenador dirige un solo club). Es independiente.
 *
 * Es un POJO (clase simple con atributos, getters y setters). Spring Data lo
 * convierte a documento JSON/BSON de MongoDB y viceversa.
 */
// @Document: la clase se guarda como "documento" en la colección indicada
// (una colección equivale a una tabla en SQL, un documento a una fila).
@Document(collection = "entrenador")
public class Entrenador {
    // @Id: identificador único del documento. Usamos un UUID (texto único)
    // que se genera solo al crear el objeto.
    @Id
    private String id = UUID.randomUUID().toString();
    private String nombre;
    private String nacionalidad;
    private int experienciaAnios;

    // Constructor vacío: lo necesita Jackson/Spring para crear el objeto al leer JSON o la base de datos.
    public Entrenador() { }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }
    public int getExperienciaAnios() { return experienciaAnios; }
    public void setExperienciaAnios(int experienciaAnios) { this.experienciaAnios = experienciaAnios; }
}
