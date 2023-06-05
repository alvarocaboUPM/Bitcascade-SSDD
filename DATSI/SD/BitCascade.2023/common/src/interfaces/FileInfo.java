// Clase que contiene la informacion de un fichero
// NO MODIFICAR

package interfaces;
import java.util.LinkedList;
import java.io.Serializable;
import interfaces.Seed;
import interfaces.Leech;

// solo por print, que usa los metodos remotos getName
import java.rmi.RemoteException;

public class FileInfo implements Serializable {
    public static final long serialVersionUID=1234567890L;
    int blockSize;
    int numBlocks;
    Seed seed;
    LinkedList<Leech> leechs = null;

    // constructor
    public FileInfo(Seed pub, int blSz, int nBls) {
        seed = pub;
        blockSize = blSz;
        numBlocks = nBls;
        leechs = new LinkedList<Leech>();
    }
    // anhade un nuevo leech
    public void newLeech(Leech l) {
        leechs.add(l);
    }
    // getters
    public Seed getSeed() {
        return seed;
    }
    public int getBlockSize() {
        return blockSize;
    }
    public int getNumBlocks() {
        return numBlocks;
    }
    public LinkedList<Leech> getLeechList() {
        return leechs;
    }
    // solo para depurar
    public void print() throws RemoteException {
        System.out.println("\ttamanho de bloque: " + blockSize);
        System.out.println("\tnumero de bloques: " + numBlocks);
        System.out.println("\tSeed: " + seed.getName());
	// llama a metodo remoto getName requiere RemoteException
	for (Leech leech : leechs)
	    // llama a metodo remoto getName requiere RemoteException
            System.out.println("\tLeech: " + leech.getName());
    }
}
