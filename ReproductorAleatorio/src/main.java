
import java.io.BufferedInputStream;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class main {

    public static String dirStr = "D:\\Musica CD";
    public static File dir;
    public static final Random r = new Random();
    public static Set<Integer> completos = new HashSet<>();
    public static int MAX = -1;

    public static String[] devolverRaiz() {
        String[] lista = dir.list();
        if (lista == null) {
            MAX = -1;
        } else {
            MAX = lista.length;
        }
        return lista;
    }

    public static int getRandomNumber() {
        int i = -1;
        if (MAX > 0) {
            while (i < 0) {
                i = r.nextInt() % MAX;
                if (completos.contains(i)) {
                    i = -1;
                }
                if (completos.size() == MAX) {
                    i = -1;
                    break;
                }
            }
        }
        return i;
    }

    public static int getRandomNumberMp3(int size) {
        int i = -1;
        while (i < 0) {
            i = r.nextInt() % size;
        }
        return i;
    }

    public static boolean devolverAleatorio() {
        boolean ret_ = false;
        String[] raiz = main.devolverRaiz();
        int pos = main.getRandomNumber();
        if (pos >= 0) {
            System.err.println(dirStr + File.separator + raiz[pos]);
            List<String> listado = main.listarSubDirectorioCompleto(dirStr + File.separator + raiz[pos]);
            if (listado.isEmpty()) {
                completos.add(pos);
            } else {
                // Si solo devuelve una canción, es candidato a estar vacío, así que al saco :D
                if (listado.size() == 1) {
                    main.completos.add(pos);
                }
                // Ahora selecciono una canción al azar
                main.cantar(listado.get(main.getRandomNumberMp3(listado.size())));
            }
            ret_ = true;
        }
        return ret_;
    }

    public static void cantar(String ruta) {
        System.out.println(ruta);
        try {
            FileInputStream fis;
            Player player;
            fis = new FileInputStream(ruta);
            BufferedInputStream bis = new BufferedInputStream(fis);
            PausablePlayer pp = new PausablePlayer(bis);
            pp.play();
            Console console = System.console();
            String con;
            boolean cortadoUsuario = false;
            while (!pp.isComplete()) {
                con = console.readLine();
                if (con.equals("c")) {
                    pp.close();
                    cortadoUsuario = true;
                    break;
                }
            }
            if (cortadoUsuario) {
                System.out.println("Majo, has cortado el audio eh");
            }
        } catch (FileNotFoundException e) {
        } catch (JavaLayerException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        cambiarRuta(ruta);
    }

    public static void cambiarRuta(String old) {
        try {
            File afile = new File(old);

            //Sacamos última posición de "/"
            int lastPost = old.lastIndexOf(File.separator);
            String nuevaRuta = old.substring(0, lastPost);

            // Pido si SI o si NO
            Scanner teclado = new Scanner(System.in);
            System.out.println("¿Te ha gustado e.e?");
            String response = teclado.nextLine();
            String resp = "NO";
            if (response.compareToIgnoreCase("S") == 0) {
                resp = "SI";
            }

            nuevaRuta += File.separator + resp + File.separator;
            File folder = new File(nuevaRuta);    // Compruebo si existe la ruta, y si no existe, la creo
            if (!folder.exists()) {
                folder.mkdir();
            }

            if (afile.renameTo(new File(nuevaRuta + afile.getName()))) {
                System.out.println("Fichero movido correctamente");
            } else {
                System.out.println("No se ha podido mover el fichero");
            }

        } catch (Exception e) {
        }
    }

    public static List<String> listarSubDirectorioCompleto(String raiz) {
        List<String> listar = new LinkedList<>();
        main.listarAux(raiz, listar);
        return listar;
    }

    public static void listarAux(String raiz, List<String> listar) {
        File raizFile = new File(raiz);
        File[] list = raizFile.listFiles();
        for (File f : list) {
            if (f.isDirectory()) {
                listarAux(f.getAbsolutePath(), listar);
            } else {
                if (f.getAbsolutePath().contains("mp3")) {
                    String parent = f.getParent();    // Saco los dos últimos caracteres del padre
                    parent = parent.substring(parent.length() - 2, parent.length());
                    if (!(parent.compareToIgnoreCase("SI") == 0 || parent.compareToIgnoreCase("NO") == 0)) {
                        listar.add(f.getAbsolutePath());
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        if(args.length == 1) {
            dirStr = args[0];
        }
        dir = new File(dirStr);
        while (true) {
            if (!main.devolverAleatorio()) {
                System.out.println("Ya no hay más canciones u.u");
                break;
            }
        }
        System.out.println("FIN DEL PROGRAMA :(");
    }

}
