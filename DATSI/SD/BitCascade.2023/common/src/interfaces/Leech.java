// Interfaz remota Leech
// NO MODIFICAR

package interfaces;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Leech extends Remote {
    // solo para depurar
    public String getName() throws RemoteException;

    // peticion de lectura del bloque indicado
    public byte [] read(int numBl) throws RemoteException;

    // obtiene cual es el ultimo bloque descargado por este Leech
    public int getLastBlockNumber() throws RemoteException;

    /* metodos remotos solo para la ultima fase */

    // leech solicitante sera notificado del progreso de la descarga
    public void newLeech(Leech requester) throws RemoteException;

    // Informa del progreso de la descarga
    public void notifyBlock(Leech l, int nBl) throws RemoteException;
}
