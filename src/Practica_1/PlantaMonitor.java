package Practica_1;

// PlantaMonitor.java
// programa principal de la planta de fabricacion de automoviles
// (memoria compartida)
import es.upm.babel.cclib.ConcIO;

class PlantaMonitor {
	public static final void main(final String[] args) throws InterruptedException{
		
		RoboFab cr = new RoboFabMonitor();
		Thread cc = new Thread(new ControlCinta(cr));

		Thread[] cg = new Thread[Robots.NUM_ROBOTS];


		for (int i = 0; i < Robots.NUM_ROBOTS; i++) {
			
			try {
				cg[i] = new Thread(new ControlRobot(i, cr));
			} catch (Exception x) {}
		}

		cc.start(); 
		for (Thread t : cg) {
			t.start();
		}
	}

	// Clase para los procesos que controlan los contenedores 
	static class ControlCinta extends Thread {
		private RoboFab cr;

		protected ControlCinta () {
		}

		public ControlCinta (RoboFab cr) {
			this.cr = cr;
		}

		public void run () {
			while (true) {
				ConcIO.printfnl("Solicitar avanzar contenedor del recurso");
				cr.solicitarAvance();
				ConcIO.printfnl("Permiso de avanzar contenedor obtenido");
				ConcIO.printfnl("Empieza a sustituir contenedor");
				Cinta.avance();
				ConcIO.printfnl("Contenedor sustuituido");
				ConcIO.printfnl("Informando el recurso sobre sustitucion del contenedor");
				cr.contenedorNuevo();
				ConcIO.printfnl("Fin de sustitucion");

				// Retardo para provocar potenciales entrelazados
				try {
					Thread.sleep(100);
				} catch (InterruptedException x) {}
			}
		}
	}


	// Clase para los procesos que controlan las robots 
	static class ControlRobot extends Thread {
		private RoboFab cr;
		private int indice;

		protected ControlRobot (){
		}

		public ControlRobot (int indice, RoboFab cr) throws Exception {
			this.cr = cr;
			if (indice >= 0 && indice <= Robots.NUM_ROBOTS) { 
				this.indice = indice;
			} else {
				throw new IllegalArgumentException("Indice de robot fuera de rango");
			}
		}

		public void run () {
			int peso;
			while (true) {
				ConcIO.printfnl ("Robot " + indice + " inicio recoger.");
				peso = Robots.recoger(indice);
				ConcIO.printfnl ("Robot " + indice + " recogio " + peso);
				ConcIO.printfnl ("Robot " + indice + " empieza a notificar peso "+peso);
				cr.notificarPeso(indice,peso);
				ConcIO.printfnl ("Robot " + indice + " notifia peso "+peso);
				ConcIO.printfnl ("Robot " + indice + " pide permiso de soltar");
				cr.permisoSoltar(indice);
				ConcIO.printfnl ("Robot " + indice + " obtuvo permiso de soltar");
				ConcIO.printfnl ("Robot " + indice + " inicia soltar.");
				Robots.soltar(indice);
				ConcIO.printfnl ("Robot " + indice + " solto " + peso);
			}
		}
	}
}
