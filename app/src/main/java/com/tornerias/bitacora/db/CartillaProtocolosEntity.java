package com.tornerias.bitacora.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;
import java.util.UUID;

@Entity(tableName = "cartilla_protocolos")
public class CartillaProtocolosEntity {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    public UUID idRegistro; // UUID como clave primaria

    public Integer registroNumero;   // único excel: 5AD
    public Integer elementoNumero;   // único excel: I8

    @TypeConverters({Converters.class})
    public Date fechaRegistro;

    public String pinturaEsquema;    // "Verde" o "Rojo" excel: Q9
    public Integer diametro;         // >= 0 excel: J14
    public char esquemaPilote;       // 'A'(excel: C16), 'B'(excel:D21), 'D'(excel:G16)
    public String pinturaBase;       // "Verde" o "Rojo" excel: R14

    public short rollos;             // máximo 255 (tinyint → short en Java) excel:Y24

    public String buzoAplicador1;    // NombreA..NombreF excel:I44
    public String buzoAplicador2;    // NombreA..NombreF excel:I44
    public String buzoSupervisor;    // excel: AA44
    public String hidrodinamicaSupervisor; // excel: N51

    @TypeConverters({Converters.class})
    public Date fechaSupervision;    // excel: N52
    // Constructor vacío
    public CartillaProtocolosEntity() {}
}
