// Clase que implementa la interfaz remota Seed.
// Actua como un servidor que ofrece un metodo remoto para leer los bloques
// del fichero que se esta descargando.
// Proporciona un metodo estatico (init) para instanciarla.
// LA FUNCIONALIDAD SE COMPLETA EN LAS 4 FASES TODO 1, TODO 2, TODO 3 y TODO 4
// En las fases 3 y 4 se convertira en un objeto remoto

package peers;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;

import interfaces.Seed;
import interfaces.Leech;
import interfaces.Tracker;
import interfaces.FileInfo;
import java.io.IOException;
import java.io.RandomAccessFile;

// Un Leech guarda en esta clase auxiliar su conocimiento sobre cual es el
// ultimo bloque descargado por otro Leech.
class LeechInfo {
    Leech leech;    // de que Leech se trata
    int lastBlock;  // ultimo bloque que sabemos que se ha descargado
    public LeechInfo (Leech l, int nBl) {
        leech = l;
        lastBlock = nBl;
    }
    Leech getLeech() {
        return leech;
    }
    int getLastBlock() {
        return lastBlock;
    }
    void setLastBlock(int nBl) {
        lastBlock = nBl;
    }
};
// Esta clase actua solo de cliente en las dos primeras fases, pero
// en las dos ultimas ejerce tambien como servidor convirtiendose en
// un objeto remoto.
public class DownloaderImpl { 
    String name; // nombre del nodo (solo para depurar)
    String file;
    String path;
    int blockSize;
    int numBlocks;
    int lastBlock = -1; // ultimo bloque descargado por este Leech
    Seed seed;
    FileInfo fInfo;
    RandomAccessFile raf;

    public DownloaderImpl(String n, String f, FileInfo finf) throws RemoteException, IOException {
        name = n;
        file = f;
        path = name + "/" + file;
        blockSize = finf.getBlockSize();
        numBlocks = finf.getNumBlocks();
        seed = finf.getSeed();
        fInfo = finf;

        // TODO 2: abre el fichero para escritura
        //raf = new RandomAccessFile(file, "w");

        // TODO 3: obtiene el numero del ultimo bloque descargado por leeches
	    // anteriores (contenidos en FileInfo) usando getLastBlockNumber

        // TODO 4: solicita a esos leeches anteriores usando newLeech
        // que le notifiquen cuando progrese su descarga
    }

    /* metodos locales */
    public int getNumBlocks() {
        return numBlocks;
    }

    public FileInfo getFileInfo() {
        return fInfo;
    }

    // realiza la descarga de un bloque y lo almacena en un fichero local
    public boolean downloadBlock(int numBl) throws RemoteException {
        // TODO 2: Lee bloque del seed y lo escribe en el fichero
        byte[] buf = seed.read(numBl);
        if (buf != null) {
            try {
                raf.write(buf);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new RemoteException("Error al escribir en el fichero", e);
            }
            return true;
        }
	    // TODO 3: Alterna leer bloques del seed y de otros leeches
	    // TODO 4: Notifica a los leeches posteriores (notifyBlock)
        return false;
    }

    /* metodos remotos que solo se usaran cuando se convierta en
       un objeto remoto en la fase 3 */
 
    // solo para depurar
    public String getName() throws RemoteException {
        return name;
    }
    // practicamente igual que read del Seed
    public byte [] read(int numBl) throws RemoteException {
        byte [] buf = null;
        System.out.println("downloader read " + numBl);
        // TODO 3: realiza lectura solicitada devolviendo lo leido en buf 
        // Cuidado con ultimo bloque que probablemente no estara completo
        return buf;
    } 
    // obtiene cual es el ultimo bloque descargado por este Leech
    public int getLastBlockNumber() throws RemoteException{
        return lastBlock;
    }
    /* metodos remotos solo para la ultima fase */
    // leech solicitante sera notificado del progreso de la descarga
    public void newLeech(Leech requester) throws RemoteException {
        // TODO 4: anhade ese leech a la lista de leeches posteriores
	// que deben ser notificados
    }
    // Informa del progreso de la descarga
    public void notifyBlock(Leech l, int nBl) throws RemoteException {
        // TODO 4: actualizamos la informacion sobre el ultimo bloque
	// descargado por ese leech
    }

    // metodo estatico que obtiene del registry una referencia al tracker y
    // obtiene mediante lookupFile la informacion del fichero especificado
    // creando una instancia de esta clase
    static public DownloaderImpl init(String host, int port, String name, String file) throws RemoteException {
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

        DownloaderImpl down = null;
        try {
            // TODO 1: localiza el registry en el host y puerto indicado y obtiene la referencia remota al tracker asignandola a esta variable:
            Registry registry = LocateRegistry.getRegistry(host, port);
            // obtiene una referencia remota el servicio
            Tracker trck = (Tracker) registry.lookup("BitCascade");

            // comprobamos si ha obtenido bien la referencia:
            System.out.println("el nombre del nodo del tracker es: " + trck.getName());
            
            // TODO 1: obtiene la informacion del fichero mediante el metodo lookupFile del Tracker.
            FileInfo finf = trck.lookupFile(file);

            if (finf==null) { // comprueba resultado
                // si null: no se ha publicado ese fichero
                System.err.println("Fichero no publicado");
                System.exit(1);
            }
            // TODO 1: crea un objeto de la clase DownloaderImpl
            down = new DownloaderImpl(name, file, finf);

            // TODO 3: usa el metodo addLeech del tracker para anhadirse
        }
        catch (Exception e) {
            System.err.println("Downloader exception:");
            e.printStackTrace();
            System.exit(1);
        }
        return down;
    }
}
