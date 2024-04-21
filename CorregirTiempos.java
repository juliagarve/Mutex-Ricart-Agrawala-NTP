import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.math.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CorregirTiempos {

	public static void main(String[] args) {
		
		File fichero = null;
		File ficheroEscribir = null;
	    FileReader fr = null;
	    BufferedReader br = null;
	    
	    FileWriter fw = null;
	    BufferedWriter bw = null;
	    
	    try {
	    	fichero = new File ("/home/i0961775/Descargas/"+args[0]+"/"+args[1]);
			fr = new FileReader (fichero);
			br = new BufferedReader(fr);
			
			ficheroEscribir = new File ("corregido"+args[1]);
			fw = new FileWriter (ficheroEscribir);
			bw = new BufferedWriter (fw);
			
			String lineaActual;
	        while((lineaActual=br.readLine())!=null) {
	        	String[] datos = lineaActual.split(" ");
				double offset = Double.parseDouble(args[2]);
	        	double tiempo = Double.parseDouble(datos[2]) - offset;
				DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
				otherSymbols.setDecimalSeparator('.');
				otherSymbols.setGroupingSeparator(','); 
				DecimalFormat df = new DecimalFormat("#", otherSymbols);
        		df.setMaximumFractionDigits(4);
	        	bw.write(datos[0] + " " + datos[1] + " " + df.format(tiempo) + "\n");
	        }
	        
	        if (bw != null)
				bw.close();
			if (fw != null)
				fw.close();
				
			if (fr != null)
				bw.close();
			if (br != null)
				fw.close();
					
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}

