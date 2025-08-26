package clashroyale;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.sound.sampled.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.Timer;

public class Ventana extends JFrame {

    private final static int ANCHO = 1067;
    private final static int ALTO = 600;
    private Pantalla tablero;
    private Clip clip;
    private JLabel fondo;
    private String escenario;
    private boolean info;

    public Ventana() {
        setSize(ANCHO, ALTO);
        setTitle("Clash Royale");
        setLocationRelativeTo(null);
        setResizable(false);

        pantallaCarga();
    }

    private Font cargarFuente(String ruta, float tamaño) {
        Font font = null;
        try {
            InputStream is = getClass().getResourceAsStream(ruta);
            font = Font.createFont(Font.TRUETYPE_FONT, is);
            font = font.deriveFont(tamaño);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
        return font;
    }

    public void reproducirMusica(String ruta) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource(ruta));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            // clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    public void detenerMusica() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }

    private void pantallaCarga() {
        // Mostrar pantalla de carga
        fondo = new JLabel();
        ImageIcon imagenCarga = new ImageIcon("src/imagenes/Carga.png"); // Ruta de la imagen de la pantalla de carga
        fondo.setIcon(imagenCarga);
        fondo.setBounds(0, 0, imagenCarga.getIconWidth(), imagenCarga.getIconHeight());
        add(fondo);
        setVisible(true);

        reproducirMusica("/Sonidos/Intro.wav");

        // Esperar 5 segundos (5000 milisegundos) antes de continuar
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        detenerMusica();

        // Eliminar pantalla de carga y mostrar el resto del contenido
        remove(fondo);
        pantallaMenu();
    }

    private void pantallaMenu() {
        // Mostrar pantalla de Menu
        JLabel btnJuegoNuevo = new JLabel("Juego Nuevo");
        Font font = cargarFuente("/Fuentes/ClashFont.ttf", 20); // Ruta de la fuente
        btnJuegoNuevo.setFont(font);
        btnJuegoNuevo.setBounds(454, 230, 200, 50);
        btnJuegoNuevo.setForeground(Color.WHITE);

        JLabel btnInfo = new JLabel("Informacion");
        btnInfo.setFont(font);
        btnInfo.setBounds(456, 375, 200, 50);
        btnInfo.setForeground(Color.WHITE);

        btnJuegoNuevo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { // Eventos del mouse para que funcione el boton
                remove(btnJuegoNuevo); // Eliminar el botón "Juego Nuevo"
                remove(btnInfo); // Eliminar el botón "Juego Nuevo"
                info = false;
                pantallaMenuArenas();

            }
        });
        add(btnJuegoNuevo);

        btnInfo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { // Eventos del mouse para que funcione el boton
                remove(btnInfo); // Eliminar el botón "Juego Nuevo"
                remove(btnJuegoNuevo); // Eliminar el botón "Juego Nuevo"
                info = true;
                pantallaInformacion();
            }
        });
        add(btnInfo);

        fondo = new JLabel();
        ImageIcon imagenMenu = new ImageIcon("src/imagenes/Menu.png"); // Ruta de la imagen de fondo
        fondo.setIcon(imagenMenu);
        fondo.setBounds(0, 0, imagenMenu.getIconWidth(), imagenMenu.getIconHeight());
        add(fondo);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        reproducirMusica("/Sonidos/Menu.wav"); // Ruta del archivo de música

        remove(fondo);
    }

    private void pantallaMenuArenas() {
        // Mostrar pantalla de Menu
        JLabel btnJuegoNuevo1 = new JLabel("Arena 1");
        JLabel btnJuegoNuevo2 = new JLabel("Arena 2");
        JLabel btnJuegoNuevo3 = new JLabel("Arena 3");

        Font font = cargarFuente("/Fuentes/ClashFont.ttf", 20); // Ruta de la fuente
        btnJuegoNuevo1.setFont(font);
        btnJuegoNuevo1.setBounds(140, 368, 200, 50);
        btnJuegoNuevo1.setForeground(Color.WHITE);

        btnJuegoNuevo2.setFont(font);
        btnJuegoNuevo2.setBounds(490, 363, 200, 50);
        btnJuegoNuevo2.setForeground(Color.WHITE);

        btnJuegoNuevo3.setFont(font);
        btnJuegoNuevo3.setBounds(825, 372, 200, 50);
        btnJuegoNuevo3.setForeground(Color.WHITE);

        if (info == false) {
            btnJuegoNuevo1.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) { // Eventos del mouse para que funcione el botón
                    remove(btnJuegoNuevo1); // Eliminar el botón "Arena 1"
                    remove(btnJuegoNuevo2); // Eliminar el botón "Arena 2"
                    remove(btnJuegoNuevo3); // Eliminar el botón "Arena 3"
                    escenario = "/imagenes/selvaSolo.png";
                    remove(fondo); // Remover la imagen de fondo
                    revalidate();
                    repaint();
                    pantallaDialogo();
                }
            });
            add(btnJuegoNuevo1);

            btnJuegoNuevo2.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) { // Eventos del mouse para que funcione el botón
                    remove(btnJuegoNuevo1); // Eliminar el botón "Arena 1"
                    remove(btnJuegoNuevo2); // Eliminar el botón "Arena 2"
                    remove(btnJuegoNuevo3); // Eliminar el botón "Arena 3"
                    escenario = "/imagenes/duendesSolo.png";
                    remove(fondo); // Remover la imagen de fondo
                    revalidate();
                    repaint();
                    pantallaDialogo();
                }
            });
            add(btnJuegoNuevo2);

            btnJuegoNuevo3.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) { // Eventos del mouse para que funcione el botón
                    remove(btnJuegoNuevo1); // Eliminar el botón "Arena 1"
                    remove(btnJuegoNuevo2); // Eliminar el botón "Arena 2"
                    remove(btnJuegoNuevo3); // Eliminar el botón "Arena 3"
                    remove(fondo); // Remover la imagen de fondo
                    escenario = "/imagenes/valleSolo.png";
                    revalidate();
                    repaint();
                    pantallaDialogo();
                }
            });
            add(btnJuegoNuevo3);
        } 

        fondo = new JLabel();
        ImageIcon imagenMenuArenas = new ImageIcon("src/imagenes/MenuArenas.png"); // Ruta de la imagen de fondo
        fondo.setIcon(imagenMenuArenas);
        fondo.setBounds(0, 0, imagenMenuArenas.getIconWidth(), imagenMenuArenas.getIconHeight());
        add(fondo);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void pantallaDialogo() {
        fondo = new JLabel();
        ImageIcon imagenDialogo = new ImageIcon("src/imagenes/Dialogo.png");
        fondo.setIcon(imagenDialogo);
        fondo.setBounds(0, 0, imagenDialogo.getIconWidth(), imagenDialogo.getIconHeight());
        add(fondo);
        revalidate();
        repaint();

        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                remove(fondo);
                revalidate();
                repaint();
                detenerMusica();
                iniciarNuevoJuego(escenario);
            }
        };

        Timer timer = new Timer(3000, taskPerformer);
        timer.setRepeats(false);
        timer.start();

        revalidate();
    }
    
    private void pantallaInformacion() {
        detenerMusica();
        reproducirMusica("/Sonidos/Info.wav");
        fondo = new JLabel();
        ImageIcon imagenDialogo = new ImageIcon("src/imagenes/Informacion.png");
        fondo.setIcon(imagenDialogo);
        fondo.setBounds(0, 0, imagenDialogo.getIconWidth(), imagenDialogo.getIconHeight());
        add(fondo);
        revalidate();
        repaint();
    }

    private void iniciarNuevoJuego(String escenario) {
        tablero = new Pantalla(escenario, cargarFuente("/Fuentes/ClashFont.ttf", 15), ANCHO, ALTO, this); // Pantalla donde estará la lógica del juego
        add(tablero); // Agregar el nuevo panel a la ventana
        revalidate(); // Actualizar la ventana
        reproducirMusica("/Sonidos/Start.wav");
        reproducirMusica("/Sonidos/Batalla.wav");
    }
}
