package com.tornerias.bitacora.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;
import java.util.UUID;

@Dao
public interface CartillaProtocolosDao {

    @Insert
    long insert(CartillaProtocolosEntity entity);

    @Query("SELECT * FROM cartilla_protocolos WHERE idRegistro = :id LIMIT 1")
    CartillaProtocolosEntity getById(UUID id);

    @Query("SELECT * FROM cartilla_protocolos ORDER BY registroNumero DESC")
    List<CartillaProtocolosEntity> getAll();

    @Query("SELECT * FROM cartilla_protocolos ORDER BY registroNumero DESC LIMIT 1")
    CartillaProtocolosEntity getLast();

    // En CartillaProtocolosDao.java
    @Query("SELECT MAX(registroNumero) FROM cartilla_protocolos")
    Integer getMaxRegistroNumero();
}
