// Clase que implementa la interfaz remota Seed.
// Actua como un servidor que ofrece un metodo remoto para leer los bloques
// del fichero publicado.
// LA FUNCIONALIDAD DE LA CLASE SE COMPLETA EN FASE 1 (TODO 1) Y LA 2 (TODO 2)

package peers;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import interfaces.Seed;
import interfaces.Tracker;

// Se comporta como un objeto remoto: UnicastRemoteObject
public class Publisher extends UnicastRemoteObject implements Seed {
    public static final long serialVersionUID=1234567890L;
    String name; // nombre del nodo (solo para depurar)
    String filename;
    String path; // convenio: path = name + "/" + file
    int blockSize;
    int numBlocks;
    transient RandomAccessFile raf;

    public Publisher(String n, String f, int bSize) throws RemoteException, IOException {
        name = n; // nombre del nodo (solo para depurar)
        filename = f; // nombre del fichero especificado
        path = name + "/" + filename; // convenio: directorio = nombre del nodo
        blockSize = bSize; // tamanho de bloque especificado
	    // Calculo del nº bloques redondeado por exceso:
	    //     truco: ⌈x/y⌉ -> (x+y-1)/y
        File file=new File(path);
        numBlocks = (int) (file.length() + blockSize - 1)/blockSize;

        try {
            raf = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException e) {
            System.out.println("Fichero en ruta '" + path +"' no encontrado");
        }
    }

    public String getName() throws RemoteException {
        return name;
    }

    public byte [] read(int numBl) throws RemoteException {
        byte [] buf = null;
        System.out.println("publisher read " + numBl);

        long fileSizeBytes = new File(path).length();
	    // redondeo por exceso
        int fileSizeBlocks = (int)((fileSizeBytes + blockSize - 1)/blockSize);

        // se asegura que el bloque solicitado esta dentro del fichero
        if (numBl < fileSizeBlocks) {
            int bufSize = blockSize;

            if (numBl + 1 == fileSizeBlocks) { // ultimo bloque
                int fragmentSize = (int) (fileSizeBytes % blockSize);
                if (fragmentSize > 0) bufSize = fragmentSize;
            }
            buf = new byte[bufSize];
            
            try {
                raf.seek(numBl * blockSize);
                int n = raf.read(buf);
                System.out.println("Bytes leidos = " + n);
                System.out.write(buf);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RemoteException("Error interno", e);
            }
        }
        return buf;
    }

    public int getNumBlocks() { // no es metodo remoto
        return numBlocks;
    }

    // Obtiene del registry una referencia al tracker y publica mediante
    // announceFile el fichero especificado creando una instancia de esta clase
    static public void main(String args[]) throws RemoteException {
        if (args.length != 5) {
            System.err.println("Usage Publisher: registryHost registryPort name file blockSize");
            return;
        }
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

        try {

            Registry registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
            Tracker trck = (Tracker) registry.lookup("BitCascade");

            // comprobamos si ha obtenido bien la referencia:
            System.out.println("el nombre del nodo del tracker es: " + trck.getName());
            String publisherName = args[2];
            String filename = args[3];
            int blockSize = Integer.parseInt(args[4]);
            Publisher p = new Publisher(publisherName, filename, blockSize);

            // asigna resultado de announceFile
            boolean res = trck.announceFile(p, filename, blockSize, p.getNumBlocks());

            if (!res) { // comprueba resultado
                // si false: ya existe fichero publicado con ese nombre
                System.err.println("Fichero ya publicado");
                System.exit(1);
            }
            System.err.println("Dando servicio...");
            // no termina nunca (modo de operación de UnicastRemoteObject)
        } catch (Exception e) {
            System.err.println("Publisher exception:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
