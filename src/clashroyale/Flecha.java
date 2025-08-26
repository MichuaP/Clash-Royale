package clashroyale;

import java.awt.geom.Rectangle2D;
import javax.swing.ImageIcon;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Flecha {

    public String direccion;
    private float x, y;
    private float vx, vy;
    private float posicionInicialX;
    private float posicionInicialY;
    public int daño;
    boolean choca;
    private static final int ANCHO = 20;
    private static final int ALTO = 10;
    public Rectangle2D centro;
    private Timer timer;
    private static final int INTERVALO_REINICIO = 2000;

    public Flecha(String direccion, int x, int y) {
        this.direccion = direccion;
        this.x = x;
        this.y = y;
        this.vx = 5;
        this.vy = 5;
        this.posicionInicialX = x;
        this.posicionInicialY = y;
        this.daño = 25;
        this.choca = false;
        this.centro = new Rectangle2D.Double(x, y, ANCHO, ALTO);

        timer = new Timer(INTERVALO_REINICIO, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reiniciarFlecha();
            }
        });
        timer.setRepeats(true); // Configurar el temporizador para repetirse
        timer.start(); // Iniciar el temporizador
    }

    public void fisica(float dt) {
        if (direccion.equals("Abajo")) {
            y -= vy * dt;
        } else if (direccion.equals("Arriba")) {
            y += vy * dt;
        }
        if (y > Pantalla.ALTO || y + ALTO < 0) {
            choca = false; // Restablecer choca a false
        }
        centro.setRect(x, y, ANCHO, ALTO);
    }

    public void reiniciarFlecha() {
        x = posicionInicialX;
        y = posicionInicialY;
    }

    public void removerFlecha() {
        x = -100; // Establecer la posición x fuera de la pantalla
        y = -100; // Establecer la posición y fuera de la pantalla
    }

    public ImageIcon getFlecha() {
        String rutaImagen;

        if (direccion.equals("Arriba")) {
            rutaImagen = "/imagenes/flechaArriba.png";
        } else if (direccion.equals("Abajo")) {
            rutaImagen = "/imagenes/flechaAbajo.png";
        } else {
            return null;
        }

        ImageIcon flecha = new ImageIcon(getClass().getResource(rutaImagen));
        return flecha;
    }

    public Rectangle2D getFlechaInt() {
        return centro;
    }

    public int getDaño() {
        return daño;
    }
}
