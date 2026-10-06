package com.mundial.app.Entidades;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

/**
 * Jugador. Relación MUCHOS A UNO con Club: muchos jugadores pertenecen a un club. NO funciona sin club.
 *
 * Es un POJO (clase simple con atributos, getters y setters). Spring Data lo
 * convierte a documento JSON/BSON de MongoDB y viceversa.
 */
// @Document: la clase se guarda como "documento" en la colección indicada
// (una colección equivale a una tabla en SQL, un documento a una fila).
@Document(collection = "jugador")
public class Jugador {
    // @Id: identificador único del documento. Usamos un UUID (texto único)
    // que se genera solo al crear el objeto.
    @Id
    private String id = UUID.randomUUID().toString();
    private String nombre;
    private String posicion;
    private int dorsal;
    private int edad;

    // MUCHOS A UNO: muchos jugadores pertenecen a un club
    // @DocumentReference: no copia el documento relacionado, solo guarda su _id
    // y Spring lo "rellena" al leer (como una llave foránea).
    @DocumentReference
    private Club club;

    // Constructor vacío: lo necesita Jackson/Spring para crear el objeto al leer JSON o la base de datos.
    public Jugador() { }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getPosicion() { return posicion; }
    public void setPosicion(String posicion) { this.posicion = posicion; }
    public int getDorsal() { return dorsal; }
    public void setDorsal(int dorsal) { this.dorsal = dorsal; }
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
    public Club getClub() { return club; }
    public void setClub(Club club) { this.club = club; }
}
