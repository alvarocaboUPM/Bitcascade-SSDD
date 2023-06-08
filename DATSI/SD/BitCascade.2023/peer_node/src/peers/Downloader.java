// Clase que realiza el proceso de descarga de un fichero.
// Instancia un objeto de la clase DownloaderImpl que implementa
// la interfaz Leech.
// NO MODIFICAR

package peers;
import java.io.File;
public class Downloader {
    static public void main(String args[]) {
        if (args.length!=4) {
            System.err.println("Usage: Downloader registryHost registryPort name file");
            return;
        }
        try {
            // crea subdirectorio en "bin" con el nombre del nodo
            // donde se dejara la copia del fichero
            new File(args[2]).mkdir();

            // Instancia la clase que implementa la interfaz Leech
            DownloaderImpl d = DownloaderImpl.init(args[0], Integer.parseInt(args[1]), args[2], args[3]);

            // Imprime la informacion del fichero lo que permite verificar
            // si ha funcionado el metodo lookupFile.
            d.getFileInfo().print();

            // Bucle que descarga los bloques del fichero
            boolean EOF = false;
            for (int i = 0; !EOF; i++) {
                System.out.print("Pulse return para leer el siguiente bloque ");
                System.in.read();
                EOF = !d.downloadBlock(i);
            }  
            System.out.print("Sigo en servicio a otros peers...");
        }
        catch (Exception e) {
            System.err.println("Downloader exception:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
