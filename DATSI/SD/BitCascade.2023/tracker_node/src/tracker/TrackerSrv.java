// Clase que implementa la interfaz remota Tracker
// Actua como un servidor que gestiona la informacion de los ficheros publicados
// TODA LA FUNCIONALIDAD DE ESTA CLASE SE COMPLETA EN LA FASE 1 (TODO 1)
// EXCEPTO AnhADIR LEECHES QUE SE INCLUYE EN LA FASE 3 (TODO 3)
//

package tracker;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import interfaces.Tracker;
import interfaces.Seed;
import interfaces.Leech;
import interfaces.FileInfo;

// guarda la informacion de los ficheros publicados asociando el nombre
// del fichero con un objeto de la clase FileInfo que contiene esa informacion
// usando para ello algun tipo de mapa (p.e. HashMap)
class TrackerSrv extends UnicastRemoteObject implements Tracker  {
    public static final long serialVersionUID=1234567890L;
    String name;
    // TODO 1: anhadir los campos que se requieran
    Map<String,FileInfo> files;
    
    public TrackerSrv(String n) throws RemoteException {
        name = n;
        files = new HashMap<>();
    }

    // NO MODIFICAR: solo para depurar
    public String getName() throws RemoteException {
        return name;
    }

    // se publica fichero: debe ser sincronizado para asegurar exclusion mutua;
    // devuelve falso si ya estaba publicado un fichero con el mismo nombre
    public synchronized boolean announceFile(Seed publisher, String fileName, int blockSize, int numBlocks) throws RemoteException {
        if (files.containsKey(fileName)) {
            System.out.println(publisher.getName() + " ha publicado " + fileName);
            return false;
        }
        files.put(fileName, new FileInfo(publisher, blockSize, numBlocks));
        return true;
    }

    public synchronized FileInfo lookupFile(String fileName) throws RemoteException {
        return files.get(fileName);
    }

    // TODO 3: se añade un nuevo leech a ese fichero (tercera fase)
    public boolean addLeech(Leech leech, String fileName) throws RemoteException {
        return false;
    }

    static public void main (String args[])  {
        if (args.length!=2) {
            System.err.println("Usage: TrackerSrv registryPortNumber name");
            return;
        }
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());
        try {
            TrackerSrv srv = new TrackerSrv(args[1]);
            // localiza el registry en el puerto especificado de esta maquina
            Registry registry = LocateRegistry.getRegistry(Integer.parseInt(args[0]));
            // da de alta el servicio
            registry.rebind("BitCascade", srv);
        }
        catch (Exception e) {
            System.err.println("Tracker exception:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
