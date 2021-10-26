package Practica_1;

class Cinta {
    // Max peso en un contenedor
    static final int MAX_P_CONTENEDOR = 20000;

    // ----------------------------------------------------------------------


    // Abajo codigo para implementar el orden de avanzar la cinta
    // -- para uso en una simulacion

    // Avanza la cinta hasta que se dispone de un contenedor vacio
    static void avance() {
	Contenedor.sustituir();
    }
}
