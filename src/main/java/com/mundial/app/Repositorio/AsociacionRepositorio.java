package com.mundial.app.Repositorio;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mundial.app.Entidades.Asociacion;

/**
 * Repositorio de Asociacion: acceso a la colección en MongoDB.
 *
 * Al extender MongoRepository<Asociacion, String> (String = tipo del id), Spring crea
 * la implementación SOLA con save, findAll, findById, existsById, deleteById...
 * Por eso no escribimos una capa Service ni consultas SQL.
 */
public interface AsociacionRepositorio extends MongoRepository<Asociacion, String> {
}
