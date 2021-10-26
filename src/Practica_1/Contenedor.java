package Practica_1;

import es.upm.babel.cclib.ConcIO;

public class Contenedor {
    // El generador números aleatorios y peso van a ser usados
    // simultáneamente
    private static java.util.concurrent.locks.Lock lock =
        new java.util.concurrent.locks.ReentrantLock();

    // Generador de números aleatorios
    private static java.util.Random random = new java.util.Random(0);

    // Peso en el contenedor
    private static int peso = 0;

    // El contenedor está preparado
    private static boolean preparado = true;

    // Se ha soltado una carga sin contenedor
    private static boolean atascado = false;

    // Clase sin objetos
    private Contenedor() {
    }

    // Sustituye el contenedor actual con otro vacio
    public static void sustituir() {
        if (peso > Cinta.MAX_P_CONTENEDOR) {
            while (true) {
                ConcIO.printfnl("ERROR: el contenedor no se puede mover, peso " + peso);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException x) {}
            }
        }
        // Sólo simulamos un retardo constante
        preparado = false;
        try {
            ConcIO.printfnl("Retirando contenedor con peso " + peso);
            lock.lock();
            int t = random.nextInt(peso / 10);
            peso = 0;
            lock.unlock();
            Thread.sleep(t);
        } catch (InterruptedException x) {}
        if (atascado) {
            while (true) {
                ConcIO.printfnl("ERROR: contenedor atascado por chatarra en carril.");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException x) {}
            }
        }
        preparado = true;
    }

    // Incrementar el peso real en el contenedor
    public static void incrementar(int p) {
        boolean sobrepeso;
        lock.lock();
        peso += p;
        sobrepeso = peso > Cinta.MAX_P_CONTENEDOR;
        lock.unlock();
        if (!preparado) {
            atascado = true;
        }
        if (sobrepeso) {
	    ConcIO.printfnl ("PESO LIMITE SOBREPASADO: " + peso + "!");
        }
    }
}
