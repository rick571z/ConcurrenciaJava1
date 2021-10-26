package Practica_1;

import es.upm.babel.cclib.Monitor;

public class RoboFabMonitor implements RoboFab {

	/* 
	  DECLARACION DE LOS ATRIBUTOS DE CLASE 
       Dominio:
	   Tipo:
	 
	   Peso = {MIN P PIEZA .. MAX P PIEZA}
	   idRobot = {0 .. NUM ROBOTS - 1}
	 */

	// Creacion del mutex     
	private Monitor mutex;
	// Variable que contiene el peso del contenedor actual
	private int peso;
	// Array que contiene el peso que esta cargando cada robot en un momento dado
	private int pendientes[];
	// Array de conditions para permitir que los robots suelten las piezas en el contenedor
	private Monitor.Cond[] soltar = new Monitor.Cond[Robots.NUM_ROBOTS];
	// Condition para avanzar la cinta
	private Monitor.Cond avanzar;
	

	// CONSTRUCTOR DE LA CLASE
	public RoboFabMonitor() {

		// Inicializacion del mutex
		mutex = new Monitor();
		// Inicializacion del peso del contenedor actual a 0
		this.peso = 0;   
		// Inicializacion del tama del array pendientes al numero maximo de robots
		pendientes = new int[Robots.NUM_ROBOTS];   
		// Inicializacion de cada posicion del array pendientes a 0
		for (int i = 0; i < pendientes.length; i++) {
			pendientes[i] = 0;
		}
		// Inicializacion del de conditions soltar
		for (int i = 0; i < soltar.length; i++) {
			soltar[i] = mutex.newCond();
		}
		// Inicializacion de la condition avanzar
		avanzar = mutex.newCond();
	}

	@Override
	public void notificarPeso(int i, int p) {
		mutex.enter();
		/* CPRE:Cierto (No hay que poner nada)
		
		   Se actualiza el valor del robot en la posicion i al peso p
		 */
		pendientes[i] = p;
		this.desbloqueo();
		mutex.leave();
	}

	@Override
	public void permisoSoltar(int i) {
		mutex.enter();
		/* 
		   CPRE:self.peso + SELF.pendientes(i) <= MAX_P_CONTENEDOR
		   El peso del contenedor actual sumado al peso que carga el robot en ese instante
		   tiene que ser menor al maximo peso que puede cargar contenedor
		 */
		//es >= 
		if (this.peso + this.pendientes[i] > Cinta.MAX_P_CONTENEDOR) {//if(!CPRE)
			// Hago que las conditions de soltar, esperen hasta que se cumpla la CPRE
			soltar[i].await();
		}
		/*
		  
		   Si se cumple la CPRE el peso del contenedor se actualizara al peso que carga,
		   sumado al peso que cargaba el robot en ese instante, y el peso que carga el robot pasa a ser 0
		 */		 
		this.peso = this.peso + pendientes[i];
		pendientes[i] = 0;
		this.desbloqueo();
		mutex.leave();
	}

	@Override
	public void solicitarAvance() {
		mutex.enter();
		/*
		   CPRE:self=(p,pends) /\  V i (= idRobot.pends(i) + p > MAX_P_CONTENEDOR
		   Existe un robot que no puede descargar su pieza en el contenedor
		   porque supera el peso maximo de este
		 */ 
		boolean cpre = true;
		for (int i = 0; i < pendientes.length && cpre; i++) {
			if ((pendientes[i] + this.peso) <= Cinta.MAX_P_CONTENEDOR) {//if(!CPRE)
				//Hago que la condition avanzar, espere hasta que se cumpla la cpre
				//avanzar.await();
				cpre = false;
			}
		}
		if(!cpre){
			avanzar.await();
		}
		// POST:SELF = SELFpre (No se hace nada)
		this.desbloqueo();
		mutex.leave();
	}

	@Override
	public void contenedorNuevo() {
		mutex.enter();
		/*
		   CPRE: true (no se hace nada)
		
		   el peso del contenedor actual se actualiza a 0
		 */
		this.peso = 0;
		//tener cuidado con avanzar.signal();
		//avanzar.signal();
		this.desbloqueo();
		mutex.leave();
	}


	private void desbloqueo() {
		/*
		   booleano que sirve como "grifo" que se cierra automaticamente
		   cuando una condition logra pasar por el desbloqueo para que solamente se deje 
		   pasar uno a la vez
		 */
		
		boolean signaled = false;

		//Caso para que el metodo desbloquee a un robot para que pueda depositar su pieza en el contenedor:
		// se revisa si algun robot esta esperando para soltar su pieza y si el peso que carga
		//sumado al peso del contenedor es menor o igual que el peso maximo que el contenedor puede llevar
		//si todo lo anterior se cumple para almenos un robot; solamente el primero de estos que se encuentre
		//sera el que sera desbloqueado.
		for (int i = 0; i < Robots.NUM_ROBOTS && !signaled; i++) {
			if ((soltar[i].waiting() > 0) && ((this.peso + this.pendientes[i]) <= Cinta.MAX_P_CONTENEDOR)) {

				soltar[i].signal();
				signaled = true;
			}
			
		}

		//Caso para que el meodo desbloquee a la cinta para que pueda avanzar, y generarse un contenedor nuevo:
		//se revisa si la cinta esta esperando a ser desbloqueada, y si todos y cada uno de los robots esta llevando un peso
		//mayor al que puede cargar el contenedor actual, si todo lo anterior se cumple entonces la cinta va a avanzar.
		if ((avanzar.waiting() > 0) && !signaled) { 
			boolean cpreAvanzar = true;
			for (int i = 0; i < pendientes.length && cpreAvanzar ; i++) {
				if ((pendientes[i] + this.peso) <= Cinta.MAX_P_CONTENEDOR) {
					cpreAvanzar = false;
				}
			}
			if (cpreAvanzar){
				avanzar.signal();
			}
		}
	}
	
	//fin clase
}
