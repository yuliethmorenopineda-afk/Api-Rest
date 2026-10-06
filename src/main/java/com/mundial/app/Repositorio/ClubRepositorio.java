package com.mundial.app.Repositorio;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.mundial.app.Entidades.Club;

/**
 * Repositorio de Club: acceso a la colección en MongoDB.
 *
 * Al extender MongoRepository<Club, String> (String = tipo del id), Spring crea
 * la implementación SOLA con save, findAll, findById, existsById, deleteById...
 * Por eso no escribimos una capa Service ni consultas SQL.
 */
public interface ClubRepositorio extends MongoRepository<Club, String> {

    // @Query: consulta propia escrita en JSON de MongoDB. Busca documentos cuyo campo
    // guarde ese id; con exists = true el resultado es solo true/false.
    @Query(value = "{ 'estadio': ?0 }", exists = true)
    boolean existeConEstadio(String estadioId);

    // @Query: consulta propia escrita en JSON de MongoDB. Busca documentos cuyo campo
    // guarde ese id; con exists = true el resultado es solo true/false.
    @Query(value = "{ 'entrenador': ?0 }", exists = true)
    boolean existeConEntrenador(String entrenadorId);

    // @Query: consulta propia escrita en JSON de MongoDB. Busca documentos cuyo campo
    // guarde ese id; con exists = true el resultado es solo true/false.
    @Query(value = "{ 'asociacion': ?0 }", exists = true)
    boolean existeConAsociacion(String asociacionId);

    // Para validar la relación uno a uno ignorando al propio club
    // @Query: consulta propia escrita en JSON de MongoDB. Busca documentos cuyo campo
    // guarde ese id; con exists = true el resultado es solo true/false.
    @Query(value = "{ 'estadio': ?0, '_id': { $ne: ?1 } }", exists = true)
    boolean estadioUsadoPorOtro(String estadioId, String clubId);

    // @Query: consulta propia escrita en JSON de MongoDB. Busca documentos cuyo campo
    // guarde ese id; con exists = true el resultado es solo true/false.
    @Query(value = "{ 'entrenador': ?0, '_id': { $ne: ?1 } }", exists = true)
    boolean entrenadorUsadoPorOtro(String entrenadorId, String clubId);
}
