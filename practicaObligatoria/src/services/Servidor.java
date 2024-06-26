package services;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("servidor")
@Singleton
public class Servidor {

	int estado; // LIBERADA=0, BUSCADA=1, TOMADA=2
	int ci, ti;
	int id;
	int numEsperando = 0;
	Object cola = new Object();
	Object supervisor = new Object();
	Object flag = new Object();

	@Path("pedirTiempo")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String pedirTiempo() {
		double t1 = System.currentTimeMillis();
		try {
			Thread.sleep((long) (1 + (Math.random() * 2)));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		double t2 = System.currentTimeMillis();
		return t1 + "\t" + t2;
	}

	@Path("esperarSupervisor")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String esperarSupervisor() {
		synchronized (supervisor) {
			numEsperando++;
			if (numEsperando < 6) {
				try {
					System.out.println("Num esperando: " + numEsperando);
					supervisor.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else {
				System.out.println("Estan todos");
				supervisor.notifyAll();
			}
		}
		return " ";
	}

	@Path("inicio")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String inicio(@QueryParam(value = "num") int num) {
		estado = 0;
		ci = 0;
		ti = 0;
		id = num;
		return "inicio " + id;
	}

	@Path("busco")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String busco() {
		estado = 1;
		ti = ci;
		return "" + ti;
	}

	@Path("peticion")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String peticion(@QueryParam(value = "tj") int tj, @QueryParam(value = "num") int num) {
		synchronized (cola) {
			ci = Math.max(ci, tj) + 1;
			if (estado == 2 || (estado == 1 && (ti < tj || (ti == tj && id < num)))) {
				try {
					cola.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return "peticion resuelta";
	}

	@Path("tomo")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String tomo() {
		estado = 2;
		ci = ci + 1;
		return "" + estado;
	}

	@Path("salgo")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String salgo() {
		estado = 0;
		synchronized (cola) {
			cola.notifyAll();
		}
		return "" + estado;
	}

}