package com.mundial.app.Entidades;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Asociación o federación. Relación MUCHOS A UNO con Club (muchos clubes pertenecen a una asociación). Es independiente.
 *
 * Es un POJO (clase simple con atributos, getters y setters). Spring Data lo
 * convierte a documento JSON/BSON de MongoDB y viceversa.
 */
// @Document: la clase se guarda como "documento" en la colección indicada
// (una colección equivale a una tabla en SQL, un documento a una fila).
@Document(collection = "asociacion")
public class Asociacion {
    // @Id: identificador único del documento. Usamos un UUID (texto único)
    // que se genera solo al crear el objeto.
    @Id
    private String id = UUID.randomUUID().toString();
    private String nombre;
    private String siglas;
    private String pais;
    private String confederacion;

    // Constructor vacío: lo necesita Jackson/Spring para crear el objeto al leer JSON o la base de datos.
    public Asociacion() { }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getSiglas() { return siglas; }
    public void setSiglas(String siglas) { this.siglas = siglas; }
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
    public String getConfederacion() { return confederacion; }
    public void setConfederacion(String confederacion) { this.confederacion = confederacion; }
}
