package com.mundial.app.Repositorio;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.mundial.app.Entidades.Competicion;

/**
 * Repositorio de Competicion: acceso a la colección en MongoDB.
 *
 * Al extender MongoRepository<Competicion, String> (String = tipo del id), Spring crea
 * la implementación SOLA con save, findAll, findById, existsById, deleteById...
 * Por eso no escribimos una capa Service ni consultas SQL.
 */
public interface CompeticionRepositorio extends MongoRepository<Competicion, String> {

    // @Query: consulta propia escrita en JSON de MongoDB. Busca documentos cuyo campo
    // guarde ese id; con exists = true el resultado es solo true/false.
    @Query(value = "{ 'clubes': ?0 }", exists = true)
    boolean existeConClub(String clubId);
}
