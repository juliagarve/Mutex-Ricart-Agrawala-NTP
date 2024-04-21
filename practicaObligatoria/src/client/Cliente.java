package client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

public class Cliente {

	/**
	 * Primer argumento id del proceso (1,2,..) Posteriores argumentos ip y el
	 * puerto de las máquinas Ej: localhost:8080, localhost:8081, 192.160.1.60:8080 192.160.1.60:8081 ...
	 * Tienen que ir primero las de la propia maquina y luego las de las otras maquinas yendo primeras las
	 * de la maquina con procesos id 1 y 2
	 * Tambien mencionar primero al puerto 8080 y luego 8081
	 * @param args
	 */

	public static void main(String args[]) {

		String dir;
		int id;
		String tempTi;
		int ti;
		Client client;
		URI uri;
		WebTarget target;

		double t0, t1, t2, t3, o, d;
		String tiempos;
		String[] temp = new String[2];
		ParNTP par, mejorPar=null;
		ArrayList<ParNTP> mejoresPares1 = new ArrayList<>();

		id = Integer.parseInt(args[0]);
		dir = args[1];

		if (id != 1) {
			client = ClientBuilder.newClient();
			if (id == 2)
				uri = UriBuilder.fromUri("http://localhost:8080/practicaObligatoria").build();
			else {
				uri = UriBuilder.fromUri("http://"+args[3]+"/practicaObligatoria").build();
			}
			
			target = client.target(uri);
			//System.out.println("Soy el cliente "+ id + " y espero al resto de clientes");
			System.out.println(target.path("rest").path("servidor").path("esperarSupervisor").request(MediaType.TEXT_PLAIN)
					.get(String.class));
		} else {

			//NTP
			
			for (int z = 1; z < args.length; z = z + 2) {
				
				mejorPar = new ParNTP(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

				client = ClientBuilder.newClient();
				uri = UriBuilder.fromUri("http://" + args[z] + "/practicaObligatoria").build();
				target = client.target(uri);

				//System.out.printf("\n\nMáquina %s\n", args[z]);
				//System.out.printf("---------------------------\n");

				for (int j = 0; j < 10; j++) {
					t0 = System.currentTimeMillis();
					tiempos = target.path("rest").path("servidor").path("pedirTiempo").request(MediaType.TEXT_PLAIN)
							.get(String.class);
					temp = tiempos.split("\t");
					t1 = Double.parseDouble(temp[0]);
					t2 = Double.parseDouble(temp[1]);
					t3 = System.currentTimeMillis();
					o = (t1 - t0 + t2 - t3) / 2;
					d = t1 - t0 + t3 - t2;
					
					par = new ParNTP(o, d);
					if (par.getD() < mejorPar.getD())
						mejorPar = par;
					
					//System.out.printf("Offset: %f\tDelay: %f\n", o, d);
				}

				//System.out.printf("Mejor par: offset: %f\t delay: %f\n", mejorPar.getO(), mejorPar.getD());
				mejoresPares1.add(mejorPar);
			
			}

			client = ClientBuilder.newClient();
			uri = UriBuilder.fromUri("http://" + dir + "/practicaObligatoria").build();
			target = client.target(uri);
			
			// System.out.println("Soy el cliente "+ id + " y espero al resto de clientes");
			target.path("rest").path("servidor").path("esperarSupervisor").request(MediaType.TEXT_PLAIN)
			.get(String.class);
		}

		BufferedWriter bw = null;
		FileWriter fw = null;
		File file = null;
		try {
			file = new File("log.txt");
			if (!file.exists()) {
				file.createNewFile();
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		client = ClientBuilder.newClient();
		uri = UriBuilder.fromUri("http://" + dir + "/practicaObligatoria").build();
		target = client.target(uri);

		target.path("rest").path("servidor").path("inicio").queryParam("num", "" + id).request(MediaType.TEXT_PLAIN)
				.get(String.class);

		for (int j = 0; j < 100; j++) {

			// REALIZO EL CÁLCULO
			long t = (long) ((0.3 + (Math.random() * 0.2)) * 1000);
			try {
				Thread.sleep(t);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// ENTRAR EN SECCIÓN CRÍTICA
			tempTi = target.path("rest").path("servidor").path("busco").request(MediaType.TEXT_PLAIN).get(String.class);
			ti = Integer.parseInt(tempTi);

			for (int z = 2; z < args.length; z++) {

				Client client2 = ClientBuilder.newClient();
				URI uri2 = UriBuilder.fromUri("http://" + args[z] + "/practicaObligatoria").build();
				WebTarget target2 = client2.target(uri2);

				target2.path("rest").path("servidor").path("peticion").queryParam("tj", "" + ti)
						.queryParam("num", "" + id).request(MediaType.TEXT_PLAIN).get(String.class);
			}

			target.path("rest").path("servidor").path("tomo").request(MediaType.TEXT_PLAIN).get(String.class);

			//System.out.println("P" + id + " E " + System.currentTimeMillis());
			try {

				fw = new FileWriter(file, true);
				bw = new BufferedWriter(fw);

				bw.write("P" + id + " E " + System.currentTimeMillis() + "\n");

				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();

			} catch (IOException e1) {
				e1.printStackTrace();
			}

			t = (long) ((0.1 + (Math.random() * 0.2)) * 1000);
			try {
				Thread.sleep(t);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			//System.out.println("P" + id + " S " + System.currentTimeMillis());
			try {

				fw = new FileWriter(file, true);
				bw = new BufferedWriter(fw);

				bw.write("P" + id + " S " + System.currentTimeMillis() + "\n");

				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();

			} catch (IOException e1) {
				e1.printStackTrace();
			}

			target.path("rest").path("servidor").path("salgo").request(MediaType.TEXT_PLAIN).get(String.class);
		}

		if (id == 1) {
			
			//NTP
			
			int maquina=0;
			for (int z = 1; z < args.length; z = z + 2) {
				
				mejorPar = new ParNTP(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
				
				client = ClientBuilder.newClient();
				uri = UriBuilder.fromUri("http://" + args[z] + "/practicaObligatoria").build();
				target = client.target(uri);

				System.out.printf("\n\nMáquina %s\n", args[z]);
				System.out.printf("---------------------------\n");

				for (int j = 0; j < 10; j++) {
					t0 = System.currentTimeMillis();
					tiempos = target.path("rest").path("servidor").path("pedirTiempo").request(MediaType.TEXT_PLAIN)
							.get(String.class);
					temp = tiempos.split("\t");
					t1 = Double.parseDouble(temp[0]);
					t2 = Double.parseDouble(temp[1]);
					t3 = System.currentTimeMillis();
					o = (t1 - t0 + t2 - t3) / 2;
					d = t1 - t0 + t3 - t2;
					
					par = new ParNTP(o, d);
					if (par.getD() < mejorPar.getD())
						mejorPar = par;
					
					//System.out.printf("Offset: %f\tDelay: %f\n", o, d);
				}
				
				//System.out.printf("Mejor par: offset: %f\t delay: %f\n", mejorPar.getO(), mejorPar.getD());
				double om;
				double dm;
				om = (mejoresPares1.get(maquina).getO()+mejorPar.getO())/2;
				dm = (mejoresPares1.get(maquina).getD()+mejorPar.getD())/2;
				System.out.printf("MEDIA MEJOR PAR: offset: %f\t delay: %f\n",om,dm);
								
				maquina++;
				
			}
			
			
		}

	}
}
