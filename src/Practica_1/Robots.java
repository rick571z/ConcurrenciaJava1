package Practica_1;

// Interfaz de las robots en Java
// Robots.java
import java.util.Random;

class Robots {
    // Número de robots en el sistema
    // public static final int NUM_ROBOTS = 5;
    public static final int NUM_ROBOTS = 5;

    // Peso mínimo que cargará una robot
    public static final int MIN_P_PIEZA = 1000;

    // Peso máximo que cargará una robot
    static final int MAX_P_PIEZA = 5000;

    // ----------------------------------------------------------------------


    // Abajo codigo para implementar los ordenes de tomar y soltar
    // piezas para un robot -- para uso en una simulacion

    // Tiempo mínimo para la descarga
    private static final int TIEMPO_MIN_SOLTAR_MS = MIN_P_PIEZA / 10;

    // Tiempo máximo para la descarga
    private static final int TIEMPO_MAX_SOLTAR_MS = MAX_P_PIEZA / 10;

    // El generador números aleatorios y las cargas van a ser usados
    // simultáneamente
    private static java.util.concurrent.locks.Lock lock =
        new java.util.concurrent.locks.ReentrantLock();

    // Generador de números aleatorios
    private static java.util.Random random = new java.util.Random(0);

    // Cargas de cada robot
    private static int[] cargas = new int[NUM_ROBOTS];

    // Clase sin objetos
    private Robots() {
        for (int idRobot = 0; idRobot < NUM_ROBOTS; idRobot++) {
            cargas[idRobot] = 0;
        }
    }

    // Recoge metal e informa de su peso 
    // 1 <= idRobot <= NUM_ROBOTS
    // MIN_P_PIEZA <= resultado <= MAX_P_PIEZA
    public static int recoger(int idRobot) {
        lock.lock();
        int carga = MIN_P_PIEZA + random.nextInt(MAX_P_PIEZA - MIN_P_PIEZA);
        cargas[idRobot] = carga;
        lock.unlock();
	try {
	    Thread.sleep(2 * carga);
	} catch (InterruptedException x) {}
	return carga;
    }

    // Mueve la robot hasta el punto de descarga y
    // desactiva el electroiman
    public static void soltar(int idRobot) {
	try {
            lock.lock();
            int t = random.nextInt(TIEMPO_MAX_SOLTAR_MS - TIEMPO_MIN_SOLTAR_MS);
            Contenedor.incrementar(cargas[idRobot]);
            cargas[idRobot] = 0;
            lock.unlock();
	    Thread.sleep(TIEMPO_MIN_SOLTAR_MS + t);
	} catch (InterruptedException x) {}
    }
}
