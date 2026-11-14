package com.automatas.vista;

/**
 * Interfaz para los paneles que deben actualizar su contenido cada vez que
 * se muestran (por ejemplo, recargar las listas de AFD y gramáticas del
 * repositorio). La ventana principal la invoca al cambiar de pantalla.
 *
 * @author Jonatan Abdiel Alvararado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public interface Refrescable {

    /** Se ejecuta cada vez que el panel va a mostrarse. */
    void alMostrar();
}
