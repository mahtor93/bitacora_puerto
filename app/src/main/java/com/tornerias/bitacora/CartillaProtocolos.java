package com.tornerias.bitacora;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.tornerias.bitacora.db.Converters;

import java.util.Date;
import java.util.UUID;

@Entity(tableName = "cartilla_protocolos")
public class CartillaProtocolos {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    public UUID idRegistro; // UUID como clave primaria

    public Integer registroNumero;   // único excel: 5AD
    public Integer elementoNumero;   // único excel: I8

    @TypeConverters({Converters.class})
    public Date fechaRegistro;       // excel: AD6

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
    public CartillaProtocolos() {}

    // Constructor con todos los campos
    public CartillaProtocolos(@NonNull UUID idRegistro, Integer registroNumero,
                                    Integer elementoNumero, Date fechaRegistro,
                                    String pinturaEsquema, Integer diametro,
                                    char esquemaPilote, String pinturaBase,
                                    short rollos, String buzoAplicador1,
                                    String buzoAplicador2, String buzoSupervisor,
                                    String hidrodinamicaSupervisor, Date fechaSupervision) {
        this.idRegistro = idRegistro;
        this.registroNumero = registroNumero;
        this.elementoNumero = elementoNumero;
        this.fechaRegistro = fechaRegistro;
        this.pinturaEsquema = pinturaEsquema;
        this.diametro = diametro;
        this.esquemaPilote = esquemaPilote;
        this.pinturaBase = pinturaBase;
        this.rollos = rollos;
        this.buzoAplicador1 = buzoAplicador1;
        this.buzoAplicador2 = buzoAplicador2;
        this.buzoSupervisor = buzoSupervisor;
        this.hidrodinamicaSupervisor = hidrodinamicaSupervisor;
        this.fechaSupervision = fechaSupervision;
    }

    // Getters y Setters
    @NonNull
    public UUID getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(@NonNull UUID idRegistro) {
        this.idRegistro = idRegistro;
    }

    public Integer getRegistroNumero() {
        return registroNumero;
    }

    public void setRegistroNumero(Integer registroNumero) {
        this.registroNumero = registroNumero;
    }

    public Integer getElementoNumero() {
        return elementoNumero;
    }

    public void setElementoNumero(Integer elementoNumero) {
        this.elementoNumero = elementoNumero;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getPinturaEsquema() {
        return pinturaEsquema;
    }

    public void setPinturaEsquema(String pinturaEsquema) {
        this.pinturaEsquema = pinturaEsquema;
    }

    public Integer getDiametro() {
        return diametro;
    }

    public void setDiametro(Integer diametro) {
        this.diametro = diametro;
    }

    public char getEsquemaPilote() {
        return esquemaPilote;
    }

    public void setEsquemaPilote(char esquemaPilote) {
        this.esquemaPilote = esquemaPilote;
    }

    public String getPinturaBase() {
        return pinturaBase;
    }

    public void setPinturaBase(String pinturaBase) {
        this.pinturaBase = pinturaBase;
    }

    public short getRollos() {
        return rollos;
    }

    public void setRollos(short rollos) {
        this.rollos = rollos;
    }

    public String getBuzoAplicador1() {
        return buzoAplicador1;
    }

    public void setBuzoAplicador1(String buzoAplicador1) {
        this.buzoAplicador1 = buzoAplicador1;
    }

    public String getBuzoAplicador2() {
        return buzoAplicador2;
    }

    public void setBuzoAplicador2(String buzoAplicador2) {
        this.buzoAplicador2 = buzoAplicador2;
    }

    public String getBuzoSupervisor() {
        return buzoSupervisor;
    }

    public void setBuzoSupervisor(String buzoSupervisor) {
        this.buzoSupervisor = buzoSupervisor;
    }

    public String getHidrodinamicaSupervisor() {
        return hidrodinamicaSupervisor;
    }

    public void setHidrodinamicaSupervisor(String hidrodinamicaSupervisor) {
        this.hidrodinamicaSupervisor = hidrodinamicaSupervisor;
    }

    public Date getFechaSupervision() {
        return fechaSupervision;
    }

    public void setFechaSupervision(Date fechaSupervision) {
        this.fechaSupervision = fechaSupervision;
    }
}