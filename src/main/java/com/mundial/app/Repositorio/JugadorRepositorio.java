package com.mundial.app.Repositorio;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.mundial.app.Entidades.Jugador;

/**
 * Repositorio de Jugador: acceso a la colección en MongoDB.
 *
 * Al extender MongoRepository<Jugador, String> (String = tipo del id), Spring crea
 * la implementación SOLA con save, findAll, findById, existsById, deleteById...
 * Por eso no escribimos una capa Service ni consultas SQL.
 */
public interface JugadorRepositorio extends MongoRepository<Jugador, String> {

    // @Query: consulta propia escrita en JSON de MongoDB. Busca documentos cuyo campo
    // guarde ese id; con exists = true el resultado es solo true/false.
    @Query(value = "{ 'club': ?0 }", exists = true)
    boolean existeConClub(String clubId);
}
