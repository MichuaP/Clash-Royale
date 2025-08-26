package clashroyale;

import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class Pantalla extends JComponent implements Runnable {

    private Ventana ventana;
    private ImageIcon fondo;
    private Font fuente;
    public static int ANCHO;
    public static int ALTO;
    public int i = 0;
    private final static int DIAMETRO = 20;
    private float x, y, yf, xf;
    private float xm, ym;
    private float vx, vy, vyf, vxf, vy2, vyf2, vxf2, yf2;
    private ArrayList<Cartas> cartas;
    private int contador = 0, contadorAzul=0, contadorRojo=0, trofeos, numero;
    private ScheduledExecutorService executorService;
    private Timer timer;
    private boolean win,lose;
    private BufferedImage ganar,perder;
    
    private Scanner scanner;
    private File f = new File("src/archivos/trofeos.txt");
    
    //Arreglos de las cartas
    private ArrayList<Cartas> cartasColocadas;
    private ArrayList<Cartas> enemigos;
    
    //Zona prohibida del rio
    private Rectangle recRio = new Rectangle(520,260, 50,30);
    
    //Arreglos de sprites
    private Sprite spritesArriba[] = new Sprite[30];
    private Sprite spritesAbajo[] = new Sprite[30];
    
    //Matriz del tablero
    private int[][] mat = new int[5][3]; // Declarar la matriz de enteros
    int numRows = mat.length; // Número de filas de la matriz
    int numCols = mat[0].length; // Número de columnas de la matriz
    int cellWidth = 120; // Ancho de cada celda
    int cellHeight = 85; // Altura de cada celda
    int matrixWidth = numCols * cellWidth; // Ancho total de la matriz
    int matrixHeight = numRows * cellHeight; // Altura total de la matriz

    Thread hilo; // hilo para ciclo del juego

    // Torres
    //2 bando rojo, 1 bando azul
    Torre torreReyRojo = new Torre(2, 482, 40, 800, "/imagenes/reyRojo.png");
    Torre torrePrincesaRoja1 = new Torre(2, 362, 102, 600, "/imagenes/princesaRoja.png");
    Torre torrePrincesaRoja2 = new Torre(2, 616, 102, 600, "/imagenes/princesaRoja.png");
    Torre torreReyAzul = new Torre(1, 490, 465, 800, "/imagenes/reyAzul.png");
    Torre torrePrincesaAzul1 = new Torre(1, 365, 400, 600, "/imagenes/princesaAzul.png");
    Torre torrePrincesaAzul2 = new Torre(1, 620, 400, 600, "/imagenes/princesaAzul.png");

    // Flechas (ataque princesa)
    Flecha flechaArriba1 = new Flecha("Arriba", 394, 140);
    Rectangle2D centroFlechaArriba1 = flechaArriba1.getFlechaInt();
    ImageIcon imagenFlechaArriba1 = flechaArriba1.getFlecha();

    Flecha flechaArriba2 = new Flecha("Arriba", 648, 140);
    Rectangle2D centroFlechaArriba2 = flechaArriba2.getFlechaInt();
    ImageIcon imagenFlechaArriba2 = flechaArriba2.getFlecha();

    Flecha flechaAbajo1 = new Flecha("Abajo", 394, 370);
    Rectangle2D centroFlechaAbajo1 = flechaAbajo1.getFlechaInt();
    ImageIcon imagenFlechaAbajo1 = flechaAbajo1.getFlecha();

    Flecha flechaAbajo2 = new Flecha("Abajo", 648, 370);
    Rectangle2D centroFlechaAbajo2 = flechaAbajo2.getFlechaInt();
    ImageIcon imagenFlechaAbajo2 = flechaAbajo2.getFlecha();

    // Balas (ataque rey)
    Bala balaArriba = new Bala("Arriba", 510, 100);
    Rectangle2D centroBalaArriba = balaArriba.getBalaInt();
    ImageIcon imagenBalaArriba = balaArriba.getBala();

    Bala balaAbajo = new Bala("Abajo", 510, 420);
    Rectangle2D centroBalaAbajo = balaAbajo.getBalaInt();
    ImageIcon imagenBalaAbajo = balaAbajo.getBala();

    public Pantalla(String escenario, Font fuente, int ANCHO, int ALTO, Ventana ventana) {
        this.fuente = fuente;
        this.ANCHO = ANCHO;
        this.ALTO = ALTO;
        this.ventana = ventana;

        hilo = new Thread(this, "hilo");

        fondo = new ImageIcon(getClass().getResource(escenario));
        Dimension dimension = new Dimension(fondo.getIconWidth(), fondo.getIconHeight());
        setPreferredSize(dimension);

        // Mensaje de Lucha
        JLabel mensajeLucha = new JLabel("¡A LUCHAR!");
        mensajeLucha.setFont(fuente.deriveFont(Font.BOLD, 30));
        mensajeLucha.setForeground(new Color(255, 236, 0));
        mensajeLucha.setHorizontalAlignment(SwingConstants.CENTER);
        mensajeLucha.setBounds(ANCHO / 2 - 150, ALTO / 2 - 50, 300, 100);
        mensajeLucha.setVisible(false);
        add(mensajeLucha);

        // Animación de aparición y aumento gradual del mensaje
        Timer timer = new Timer(12, new ActionListener() {
            private int count = 0;
            private int size = 0;
            private int width = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                count++;

                // Aparición gradual del mensaje desde atrás
                if (count <= 20) {
                    float scale = count / 20.0f;
                    size = (int) (30 * scale);
                    width = (int) (300 * scale);
                    mensajeLucha.setBounds(ANCHO / 2 - width / 2, ALTO / 2 - 50, width, 100);
                    mensajeLucha.setVisible(true);
                }

                // Aumento gradual del tamaño del mensaje
                if (count > 20 && size < 30) {
                    size++;
                    width = size * 10;
                    mensajeLucha.setBounds(ANCHO / 2 - width / 2, ALTO / 2 - 50, width, 100);
                }

                mensajeLucha.setFont(fuente.deriveFont(Font.BOLD, size));
                mensajeLucha.repaint();

                if (count >= 40) {
                    // Transición de desvanecimiento gradual
                    int opacity = 255 - ((count - 40) * 255 / 20);
                    mensajeLucha.setForeground(new Color(255, 236, 0, opacity));

                    if (opacity <= 0) {
                        ((Timer) e.getSource()).stop();
                        mensajeLucha.setVisible(false);
                    }
                }
            }
        });
        timer.start();
        
        //Imagenes
        try{
            ganar = ImageIO.read(getClass().getResourceAsStream("/imagenes/ganar.png"));
            perder = ImageIO.read(getClass().getResourceAsStream("/imagenes/perder.png"));
        }catch(IOException ex){
            System.out.println("Error: imagen no encontrada");
        }
        
        // Creacion de cartas
        cartas = new ArrayList<>();
        
        //Activos en el tablero
        cartasColocadas = new ArrayList<>();
        
        //Enemigos en el tablero
        enemigos = new ArrayList<>();
        
        //Uso de archivos
        leerArchivo();

        inicializarMatriz(); // Llamar a un método para inicializar la matriz con valores
        crearSpritesArriba(); // Llena los sprites arriba
        crearSpritesAbajo(); // Llena los sprites abajo
        crearCartasAleatorias(); //Crea cartas aleatorias
        crearEnemigos(); //Crea enemigos aleatorios
        iniciaMouse(); // Habilitar el manejo del mouse

        //Física
        yf = 500;
        vxf = 1;
        vyf = 1;
        
        yf2 = 500;
        vxf2 = 1;
        vyf2 = 70;
        
        //Banderas de estado final
        win=false;
        lose=false;

        hilo.start(); // ejecuta el ciclo principal del juego

        timer = new Timer(20, e -> {
            flechaArriba1.fisica(0.7f); // Actualiza la posición de la primera flecha
            flechaArriba2.fisica(0.7f); // Actualiza la posición de la segunda flecha
            flechaAbajo1.fisica(0.7f); // Actualiza la posición de la primera flecha
            flechaAbajo2.fisica(0.7f); // Actualiza la posición de la segunda flecha
            balaArriba.fisica(0.7f); // Actualiza la posicion de la bala
            balaAbajo.fisica(0.7f); // Actualiza la posicion de la bala
            repaint(); // Vuelve a dibujar el componente para reflejar los cambios
        });
        timer.start();
    }
    
    private void crearEnemigos() {
        Random random = new Random();
        Random randomC = new Random();

        for (int i = 0; i < 3; i++) {
            Cartas carta = new Cartas(); // Crear una nueva instancia en cada iteración

            int randomId = random.nextInt(30) + 1; // Generar un ID aleatorio entre 1 y 30
            carta.id = randomId;
            
            //Crear coordenadas aleatorias
            int lado = randomC.nextInt(2) + 1;
            int posX=0;
            int posY=0;
            
            //Coordenadas random en intervalos
            if(lado == 1){
                posX = randomC.nextInt(110) + 330;
            }else if(lado == 2){
                posX = randomC.nextInt(100) + 586;
            }
            
            posY = randomC.nextInt(82) + 150;

            carta.x = posX;
            carta.y = posY;

            //Asignar los sprites
            for (int j = 0; j < 30; j++) {
                if (carta.id == spritesAbajo[j].id) {
                    System.out.println(randomId + " " + "sprites ingresados");
                    carta.setSprAbajo(spritesAbajo[j]);
                }
            }
            enemigos.add(carta); //agrega la carta al arreglo
        }
        repaint(); // Repintar la pantalla para mostrar las cartas
    }

    private void crearCartasAleatorias() {
        Random random = new Random();

        int posX = 815;
        int posY = 8;

        for (int i = 0; i < 3; i++) {
            Cartas carta = new Cartas(); // Crear una nueva instancia en cada iteración

            int randomId = random.nextInt(30) + 1; // Generar un ID aleatorio entre 1 y 30
            carta.id = randomId;

            carta.x = posX;
            carta.y = posY;
            posY += 90;

            //Asignar los sprites
            for (int j = 0; j < 30; j++) {
                if (carta.id == spritesArriba[j].id) {
                    System.out.println(randomId + " " + "sprites ingresados");
                    carta.setSprArriba(spritesArriba[j]);
                }
            }
            cartas.add(carta); //agrega la carta al arreglo
        }
        repaint(); // Repintar la pantalla para mostrar las cartas
    }

    private void inicializarMatriz() {
        // Inicializar la matriz con valores
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[i].length; j++) {
                mat[i][j] = i * mat[i].length + j + 1; // calcula el valor correspondiente a cada posición de la matriz
            }
        }
    }

    // Verificar si el mouse está dentro de la matriz
    private boolean dentroMatrizLimites(int x, int y) {
        int matrixStartX = (getWidth() - matrixWidth) / 2;
        int matrixStartY = (getHeight() - matrixHeight) / 2;

        return x >= matrixStartX && x <= matrixStartX + matrixWidth
                && y >= matrixStartY && y <= matrixStartY + matrixHeight;
    }

    // Obtener la fila correspondiente a la posición Y del mouse
    private int getFilaY(int y) {
        int matrixStartY = (getHeight() - matrixHeight) / 2;
        int relativeY = y - matrixStartY;

        return relativeY / cellHeight;
    }

    // Obtener la columna correspondiente a la posición X del mouse
    private int getColumnaX(int x) {
        int matrixStartX = (getWidth() - matrixWidth) / 2;
        int relativeX = x - matrixStartX;

        return relativeX / cellWidth;
    }

    private void pausarTemporizador() {
        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
        }
    }

    private void iniciarTimer() {
        Random random = new Random();
        contador = random.nextInt(3) + 5; // Generar un número aleatorio entre 5 y 7

        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                contador--; // Decrementar el contador cada segundo
                if (contador <= 0) {
                    crearCartasAleatorias(); // Generar 3 nuevas cartas
                    
                    crearEnemigos(); //Generar 3 nuevos enemigos
                                        
                    contador = 0; // Reiniciar el contador
                    pausarTemporizador(); // Detener el temporizador actual, si existe
                }
                repaint(); // Volver a pintar la pantalla para actualizar el temporizador
            }
        }, 1, 1, TimeUnit.SECONDS); // Ejecutar la tarea cada segundo
    }

    private void iniciaMouse() {
        MouseAdapter ma = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                for (Cartas carta : cartas) {
                    if (e.getX() >= carta.x && e.getX() <= carta.x + carta.getCarta().getIconWidth()
                            && e.getY() >= carta.y && e.getY() <= carta.y + carta.getCarta().getIconHeight()) {
                        Cartas.arrastrada = carta; // Asignar la carta a la variable estática arrastrada
                        carta.mov = true;
                        carta.xm = e.getX();
                        carta.ym = e.getY();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (Cartas.arrastrada != null) {
                    if (dentroMatrizLimites(e.getX(), e.getY())) { // Verificar si el mouse está dentro de la matriz
                        int row = getFilaY(e.getY()); // Obtener la fila correspondiente a la posición Y del mouse
                        int col = getColumnaX(e.getX()); // Obtener la columna correspondiente a la posición X del mouse

                        mat[row][col] = Cartas.arrastrada.id; // Actualizar la matriz con el valor de la carta 

                        // Asignar la columna correspondiente a la carta
                        Cartas.arrastrada.setColumna(col);

                        //Se agrega al arreglo
                        Cartas temp = new Cartas(); //Crea un temporal para copiar
                        temp = Cartas.arrastrada;
                        cartasColocadas.add(temp); // agregamos la carta al array de sprites activos

                        cartas.remove(Cartas.arrastrada); // Eliminar la carta del arraylist
                        Cartas.arrastrada = null; // Eliminar la referencia a la carta arrastrada

                        ventana.reproducirMusica("/Sonidos/Carta.wav");

                        if (cartas.isEmpty() && executorService == null) { // si no hay cartas en el tablero
                            iniciarTimer(); // timer para regenerar cartas
                        }
                    }
                }
                for (Cartas carta : cartas) {
                    if (carta.mov) {
                        carta.mov = false;
                    }
                }
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                xm = e.getX();
                ym = e.getY();
                for (Cartas carta : cartas) {
                    if (carta.mov) {
                        carta.distX = e.getX() - carta.xm; // Diferencia en la coordenada X del mouse
                        carta.distY = e.getY() - carta.ym; // Diferencia en la coordenada Y del mouse
                        carta.x += carta.distX; // Actualizar la posición X de la carta
                        carta.y += carta.distY; // Actualizar la posición Y de la carta
                        carta.xm = e.getX(); // Actualizar la coordenada X del mouse para el siguiente evento
                        carta.ym = e.getY(); // Actualizar la coordenada Y del mouse para el siguiente evento
                    }
                }
                repaint();
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }
    
    //Información de los sprites
    public void crearSpritesArriba() {
        spritesArriba[0] = new Sprite(1, "/sprites/sprite1.png", 82, 67, 10, 18);
        spritesArriba[1] = new Sprite(2, "/sprites/sprite2.png", 50, 52, 10, 13);
        spritesArriba[2] = new Sprite(3, "/sprites/sprite3.png", 80, 77, 10, 10);
        spritesArriba[3] = new Sprite(4, "/sprites/sprite4.png", 88, 84, 10, 17);
        spritesArriba[4] = new Sprite(5, "/sprites/sprite5.png", 67, 62, 10, 7);
        spritesArriba[5] = new Sprite(6, "/sprites/sprite6.png", 75, 76, 10, 2);
        spritesArriba[6] = new Sprite(7, "/sprites/sprite7.png", 76, 80, 10, 3);
        spritesArriba[7] = new Sprite(8, "/sprites/sprite8.png", 77, 95, 10, 12);
        spritesArriba[8] = new Sprite(9, "/sprites/sprite9.png", 106, 94, 10, 23);
        spritesArriba[9] = new Sprite(10, "/sprites/sprite10.png", 60, 61, 10, 8);
        spritesArriba[10] = new Sprite(11, "/sprites/sprite11.png", 88, 82, 10, 16);
        spritesArriba[11] = new Sprite(12, "/sprites/sprite12.png", 76, 82, 10, 8);
        spritesArriba[12] = new Sprite(13, "/sprites/sprite13.png", 70, 82, 10, 13);
        spritesArriba[13] = new Sprite(14, "/sprites/sprite14.png", 87, 81, 10, 8);
        spritesArriba[14] = new Sprite(15, "/sprites/sprite15.png", 71, 72, 10, 12);
        spritesArriba[15] = new Sprite(16, "/sprites/sprite16.png", 67, 68, 10, 8);
        spritesArriba[16] = new Sprite(17, "/sprites/sprite17.png", 89, 87, 10, 8);
        spritesArriba[17] = new Sprite(18, "/sprites/sprite18.png", 62, 82, 10, 8);
        spritesArriba[18] = new Sprite(19, "/sprites/sprite19.png", 87, 92, 10, 8);
        spritesArriba[19] = new Sprite(20, "/sprites/sprite20.png", 75, 85, 10, 8);
        spritesArriba[20] = new Sprite(21, "/sprites/sprite21.png", 70, 76, 10, 8);
        spritesArriba[21] = new Sprite(22, "/sprites/sprite22.png", 100, 98, 10, 8);
        spritesArriba[22] = new Sprite(23, "/sprites/sprite23.png", 100, 93, 10, 8);
        spritesArriba[23] = new Sprite(24, "/sprites/sprite24.png", 125, 113, 10, 8);
        spritesArriba[24] = new Sprite(25, "/sprites/sprite25.png", 90, 97, 10, 5);
        spritesArriba[25] = new Sprite(26, "/sprites/sprite26.png", 92, 89, 10, 8);
        spritesArriba[26] = new Sprite(27, "/sprites/sprite27.png", 97, 111, 10, 8);
        spritesArriba[27] = new Sprite(28, "/sprites/sprite28.png", 50, 47, 10, 8);
        spritesArriba[28] = new Sprite(29, "/sprites/sprite29.png", 85, 92, 10, 8);
        spritesArriba[29] = new Sprite(30, "/sprites/sprite30.png", 87, 111, 10, 5);
    }
    public void crearSpritesAbajo() {
        spritesAbajo[0] = new Sprite(1, "/sprites/spriteA1.png", 82, 67, 10, 9);
        spritesAbajo[1] = new Sprite(2, "/sprites/spriteA2.png", 50, 52, 10, 8);
        spritesAbajo[2] = new Sprite(3, "/sprites/spriteA3.png", 80, 77, 10, 14);
        spritesAbajo[3] = new Sprite(4, "/sprites/spriteA4.png", 88, 84, 10, 12);
        spritesAbajo[4] = new Sprite(5, "/sprites/spriteA5.png", 67, 62, 10, 3);
        spritesAbajo[5] = new Sprite(6, "/sprites/spriteA6.png", 75, 76, 10, 8);
        spritesAbajo[6] = new Sprite(7, "/sprites/spriteA7.png", 76, 80, 10, 8);
        spritesAbajo[7] = new Sprite(8, "/sprites/spriteA8.png", 77, 95, 10, 8);
        spritesAbajo[8] = new Sprite(9, "/sprites/spriteA9.png", 106, 94, 10, 8);
        spritesAbajo[9] = new Sprite(10, "/sprites/spriteA10.png", 60, 61, 10, 8);
        spritesAbajo[10] = new Sprite(11, "/sprites/spriteA11.png", 88, 82, 10, 16);
        spritesAbajo[11] = new Sprite(12, "/sprites/spriteA12.png", 76, 82, 10, 10);
        spritesAbajo[12] = new Sprite(13, "/sprites/spriteA13.png", 70, 82, 10, 12);
        spritesAbajo[13] = new Sprite(14, "/sprites/spriteA14.png", 87, 81, 10, 8);
        spritesAbajo[14] = new Sprite(15, "/sprites/spriteA15.png", 71, 72, 10, 12);
        spritesAbajo[15] = new Sprite(16, "/sprites/spriteA16.png", 67, 69, 10, 8);
        spritesAbajo[16] = new Sprite(17, "/sprites/spriteA17.png", 89, 87, 10, 8);
        spritesAbajo[17] = new Sprite(18, "/sprites/spriteA18.png", 62, 82, 10, 4);
        spritesAbajo[18] = new Sprite(19, "/sprites/spriteA19.png", 87, 92, 10, 8);
        spritesAbajo[19] = new Sprite(20, "/sprites/spriteA20.png", 75, 71, 10, 8);
        spritesAbajo[20] = new Sprite(21, "/sprites/spriteA21.png", 70, 76, 10, 8);
        spritesAbajo[21] = new Sprite(22, "/sprites/spriteA22.png", 100, 98, 10, 8);
        spritesAbajo[22] = new Sprite(23, "/sprites/spriteA23.png", 100, 93, 10, 8);
        spritesAbajo[23] = new Sprite(24, "/sprites/spriteA24.png", 125, 113, 10, 8);
        spritesAbajo[24] = new Sprite(25, "/sprites/spriteA25.png", 90, 97, 10, 8);
        spritesAbajo[25] = new Sprite(26, "/sprites/spriteA26.png", 92, 89, 10, 8);
        spritesAbajo[26] = new Sprite(27, "/sprites/spriteA27.png", 97, 111, 10, 8);
        spritesAbajo[27] = new Sprite(28, "/sprites/spriteA28.png", 50, 52, 10, 8);
        spritesAbajo[28] = new Sprite(29, "/sprites/spriteA29.png", 85, 92, 10, 8);
        spritesAbajo[29] = new Sprite(30, "/sprites/spriteA30.png", 87, 111, 10, 5);
    }

    // Colisiones a las torres
    private void colisionesTorre() {
        Iterator<Cartas> iterator = cartasColocadas.iterator();
        while (iterator.hasNext()) {
            Cartas carta = iterator.next();
            if (carta.sprArriba.recMonito.intersects(torreReyRojo.centro)) {
                if (torreReyRojo.vidaTorre > 0) {
                    //Disminuir el daño a la torre
                    ventana.reproducirMusica("/Sonidos/reyAtacado.wav");
                    carta.chocaIz=false;
                    carta.chocaD = false;
                    carta.chocaR = true;
                    // Disminuir la vida de la torre
                    torreReyRojo.vidaTorre -= carta.daño;
                    carta.vida = 0; // Establecer vida de la carta a cero para que se elimine
                }else if(torreReyRojo.vidaTorre <= 210) {
                    System.out.println("Se muere la torre del rey, fin del juego");
                }
            } else if (carta.sprArriba.recMonito.intersects(torrePrincesaRoja1.centro)) {
                if (torrePrincesaRoja1.vidaTorre > 0) {
                    // Disminuir la vida de la torre
                    ventana.reproducirMusica("/Sonidos/princesaAtacada.wav");
                    torrePrincesaRoja1.vidaTorre -= carta.daño;
                    carta.vida = 0; // Establecer vida de la carta a cero para que se elimine
                }else if(torrePrincesaRoja1.vidaTorre <= 0) {
                        System.out.println("Se muere la torre");
                        carta.chocaIz=true;
                        carta.chocaD = false;
                }
            } else if (carta.sprArriba.recMonito.intersects(torrePrincesaRoja2.centro)) {
                if (torrePrincesaRoja2.vidaTorre > 0) {
                    // Disminuir la vida de la torre
                    ventana.reproducirMusica("/Sonidos/princesaAtacada.wav");
                    torrePrincesaRoja2.vidaTorre -= carta.daño;
                    carta.vida = 0; // Establecer vida de la carta a cero para que se elimine    
                }else if (torrePrincesaRoja2.vidaTorre <= 0) {
                        System.out.println("Se muere la torre");
                        carta.chocaD=true;
                        carta.chocaIz=false;
                }
            } else if (carta.sprArriba.recMonito.intersects(recRio)) {
                carta.vida = 0; // Establecer vida de la carta a cero para que se elimine
                System.out.println("Se cae al rio");
                ventana.reproducirMusica("/Sonidos/Splash.wav");
            }

            // Se remueven del array las cartas con la vida baja o que se salgan del tablero
            if (carta.vida <= 0) {
                iterator.remove();
                System.out.println("Monito muerto");
            }
            
        }
        
        //Colisiones enemigas
        Iterator<Cartas> iterator2 = enemigos.iterator();
        while (iterator2.hasNext()) {
            Cartas carta = iterator2.next();
            if (carta.sprAbajo.recMonito.intersects(torreReyAzul.centro)) {
                if (torreReyAzul.vidaTorre > 0) {
                    //Disminuir el daño a la torre
                    ventana.reproducirMusica("/Sonidos/reyAtacado.wav");
                    carta.chocaIz = false;
                    carta.chocaD = false;
                    carta.chocaR = true;
                    // Disminuir la vida de la torre
                    torreReyAzul.vidaTorre -= carta.daño;
                    carta.vida = 0; // Establecer vida de la carta a cero para que se elimine
                }else if(torreReyAzul.vidaTorre <= 0) {
                    System.out.println("Se muere la torre del rey, fin del juego");
                }
            } else if (carta.sprAbajo.recMonito.intersects(torrePrincesaAzul1.centro)) {
                if (torrePrincesaAzul1.vidaTorre > 0) {
                    // Disminuir la vida de la torre
                    ventana.reproducirMusica("/Sonidos/princesaAtacada.wav");
                    torrePrincesaAzul1.vidaTorre -= carta.daño;
                    carta.vida = 0; // Establecer vida de la carta a cero para que se elimine
                }else if(torrePrincesaAzul1.vidaTorre <= 0) {
                        System.out.println("Se muere la torre");
                        carta.chocaIz=true;
                        carta.chocaD = false;
                }
            } else if (carta.sprAbajo.recMonito.intersects(torrePrincesaAzul2.centro)) {
                if (torrePrincesaAzul2.vidaTorre > 0) {
                    // Disminuir la vida de la torre
                    ventana.reproducirMusica("/Sonidos/princesaAtacada.wav");
                    torrePrincesaAzul2.vidaTorre -= carta.daño;
                    carta.vida = 0; // Establecer vida de la carta a cero para que se elimine    
                }else if (torrePrincesaAzul2.vidaTorre <= 0) {
                        System.out.println("Se muere la torre");
                        carta.chocaD=true;
                        carta.chocaIz=false;
                }
            } else if (carta.sprAbajo.recMonito.intersects(recRio)) {
                carta.vida = 0; // Establecer vida de la carta a cero para que se elimine
                System.out.println("Se cae al rio");
                ventana.reproducirMusica("/Sonidos/Splash.wav");
            }

            // Se remueven del array las cartas con la vida baja o que se salgan del tablero
            if (carta.vida <= 0 || carta.x<250 || carta.x>800) {
                iterator2.remove();
                System.out.println("Monito muerto");
            }
        }
    }

    // colisiones de flechas y balas
    private void colisionesFlechasBalas() {
        Iterator<Cartas> iterator = cartasColocadas.iterator();
        while (iterator.hasNext()) {
            Cartas carta = iterator.next();

            // Colisión con la flecha arriba 1
            if (carta.sprArriba.recMonito.intersects(centroFlechaArriba1) && carta.vida > 0) {
                if (!flechaArriba1.choca) {
                    ventana.reproducirMusica("/Sonidos/Flecha.wav");
                    flechaArriba1.choca = true;
                    carta.vida -= flechaArriba1.getDaño();
                    if (carta.vida <= 0) {
                        iterator.remove();
                        System.out.println("Monito muerto");
                    }
                    flechaArriba1.removerFlecha(); // Remover la flecha
                }
            }

            // Colisión con la flecha arriba 2
            if (carta.sprArriba.recMonito.intersects(centroFlechaArriba2) && carta.vida > 0) {
                if (!flechaArriba2.choca) {
                    ventana.reproducirMusica("/Sonidos/Flecha.wav");
                    flechaArriba2.choca = true;
                    carta.vida -= flechaArriba2.getDaño();
                    if (carta.vida <= 0) {
                        iterator.remove();
                        System.out.println("Monito muerto");
                    }
                    flechaArriba2.removerFlecha(); // Remover la flecha
                }
            }

            // Colisión con la bala arriba
            if (carta.sprArriba.recMonito.intersects(centroBalaArriba) && carta.vida > 0) {
                if (!balaArriba.choca) {
                    ventana.reproducirMusica("/Sonidos/Bala.wav");
                    balaArriba.choca = true;
                    carta.vida -= balaArriba.getDaño();
                    if (carta.vida <= 0) {
                        iterator.remove();
                        System.out.println("Monito muerto");
                    }
                    balaArriba.removerBala();
                }
            }
        }
        
        //Colisiones a enemigos
        Iterator<Cartas> iterator2 = enemigos.iterator();
        while (iterator2.hasNext()) {
            Cartas carta = iterator2.next();

            // Colisión con la flecha arriba 1
            if (carta.sprAbajo.recMonito.intersects(centroFlechaAbajo1) && carta.vida > 0) {
                if (!flechaAbajo1.choca) {
                    ventana.reproducirMusica("/Sonidos/Flecha.wav");
                    flechaAbajo1.choca = true;
                    carta.vida -= flechaAbajo1.getDaño();
                    if (carta.vida <= 0) {
                        iterator.remove();
                        System.out.println("Monito muerto");
                    }
                    flechaAbajo1.removerFlecha(); // Remover la flecha
                }
            }

            // Colisión con la flecha arriba 2
            if (carta.sprAbajo.recMonito.intersects(centroFlechaAbajo2) && carta.vida > 0) {
                if (!flechaAbajo2.choca) {
                    ventana.reproducirMusica("/Sonidos/Flecha.wav");
                    flechaAbajo2.choca = true;
                    carta.vida -= flechaAbajo2.getDaño();
                    if (carta.vida <= 0) {
                        iterator.remove();
                        System.out.println("Monito muerto");
                    }
                    flechaAbajo2.removerFlecha(); // Remover la flecha
                }
            }

            // Colisión con la bala arriba
            if (carta.sprAbajo.recMonito.intersects(centroBalaAbajo) && carta.vida > 0) {
                if (!balaAbajo.choca) {
                    ventana.reproducirMusica("/Sonidos/Bala.wav");
                    balaAbajo.choca = true;
                    carta.vida -= balaAbajo.getDaño();
                    if (carta.vida <= 0) {
                        iterator.remove();
                        System.out.println("Monito muerto");
                    }
                    balaAbajo.removerBala();
                }
            }
        }
    }

   private void fisica(float dt) {
        System.out.println("Fisica");

        //Movimiento equipo azul
        for(Cartas cart:cartasColocadas){
            if(cart.chocaIz == false && cart.chocaD == false && cart.chocaR==false){ //Caminar hacia arriba
                cart.y = cart.y-=vyf*dt/1000;
            }else if(cart.chocaD){ // caminar hacia izquierda
                cart.y = cart.y-=vyf*dt/1000;
                cart.x = cart.x-=vxf*dt;
            }else if(cart.chocaIz){ // caminar hacia derecha
                cart.y = cart.y-=vyf*dt/1000;
                cart.x = cart.x+=vxf+1;
            }else if(cart.chocaR){
                //no se mueve
            }
        }
        
        //Movimiento equipo rojo
        for(Cartas car:enemigos){
            if(car.chocaIz == false && car.chocaD == false && car.chocaR==false){ //Caminar hacia arriba
                car.y = car.y+=vyf2*dt;
            }else if(car.chocaD){ // caminar hacia izquierda
                car.y = car.y+=vyf2*dt;
                car.x = car.x-=vxf*dt;
            }else if(car.chocaIz){ // caminar hacia derecha
                car.y = car.y+=vyf2*dt;
                car.x = car.x+=vxf2;
            }else if(car.chocaR){
                //no se mueve
            }
        }
    }

    private void dibuja() throws Exception { // para sprites que van hacia arriba
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                paintImmediately(0, 0, ANCHO, ALTO);
            }
        });
    }

    @Override
    public void run() { // ciclo principal juego
        long tiempoViejo = System.nanoTime();
        while (true) {
            long tiempoNuevo = System.nanoTime();
            float dt = (tiempoNuevo - tiempoViejo) / 1000000000f;
            tiempoViejo = tiempoNuevo;
            fisica(dt);
            colisionesTorre();
            colisionesFlechasBalas();
            try {
                dibuja();
                Thread.sleep(10);
            } catch (Exception ex) {
                Logger.getLogger(Pantalla.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(fondo.getImage(), 0, 0, getWidth(), getHeight(), this);
        g.setFont(fuente);
        g.setColor(Color.WHITE);
        
        //Coordenadas
        g.drawString(Integer.toString(Math.round(xm))+","+Integer.toString(Math.round(ym)), 19,19);
        
        // Dibujar las cartas
        for (Cartas carta : cartas) {
            ImageIcon imagenCarta = carta.getCarta();
            g.drawImage(imagenCarta.getImage(), carta.x, carta.y, this);
        }

        // Dibujar los sprites activos en el tablero
        for (Cartas carta : cartasColocadas) {
            if (carta.vida > 0) {
                carta.sprArriba.pintar(g, carta.x, carta.y);
                g.setFont(fuente.deriveFont(Font.BOLD, 12));
                g.setColor(Color.WHITE);
                g.drawString(Integer.toString(carta.vida), carta.x, carta.y + carta.getHeight()); //pinta la vida
            }
        }
        
        // Dibujar los enemigos activos
        for (Cartas carta : enemigos) {
            if (carta.vida > 0) {
                carta.sprAbajo.pintar(g, carta.x, carta.y);
                g.drawString(Integer.toString(carta.vida), carta.x, carta.y + carta.getHeight()); //pinta la vida
            }
        }

        int startX = (getWidth() - matrixWidth) / 2; // Coordenada X de inicio para centrar la matriz
        int startY = (getHeight() - matrixHeight) / 2 + 25; // Coordenada Y de inicio para centrar la matriz

//        // Dibujar la matriz
//        for (int row = 0; row < numRows; row++) {
//            for (int col = 0; col < numCols; col++) {
//                int x = startX + col * cellWidth;
//                int y = startY + row * cellHeight;
//
//                // Dibujar el contorno de la celda
//                g.drawRect(x, y, cellWidth, cellHeight);
//            }
//        }

        // Dibujar el temporizador (elixir) en la pantalla
        g.setFont(fuente.deriveFont(Font.BOLD, 22));
        g.setColor(Color.WHITE);
        g.drawString(String.valueOf(contador), 950, 185);

        // Dibujar torres
        torreReyAzul.pintarTorre(g);
        torrePrincesaAzul1.pintarTorre(g);
        torrePrincesaAzul2.pintarTorre(g);
        torreReyRojo.pintarTorre(g);
        torrePrincesaRoja1.pintarTorre(g);
        torrePrincesaRoja2.pintarTorre(g);

        // Dibujar vidas de las torres
        g.setFont(fuente.deriveFont(Font.BOLD, 12));
        g.setColor(Color.WHITE);
        g.drawString(String.valueOf(torreReyRojo.getVidaTorre()), (int) torreReyRojo.centro.getX() + 17, (int) torreReyRojo.centro.getY() - 10);
        g.drawString(String.valueOf(torrePrincesaRoja1.getVidaTorre()), (int) torrePrincesaRoja1.centro.getX() + 17, (int) torrePrincesaRoja1.centro.getY() - 10);
        g.drawString(String.valueOf(torrePrincesaRoja2.getVidaTorre()), (int) torrePrincesaRoja2.centro.getX() + 17, (int) torrePrincesaRoja2.centro.getY() - 10);
        g.drawString(String.valueOf(torreReyAzul.getVidaTorre()), (int) torreReyAzul.centro.getX() + 17, (int) torreReyAzul.centro.getY() + 63);
        g.drawString(String.valueOf(torrePrincesaAzul1.getVidaTorre()), (int) torrePrincesaAzul1.centro.getX() + 17, (int) torrePrincesaAzul1.centro.getY() + 70);
        g.drawString(String.valueOf(torrePrincesaAzul2.getVidaTorre()), (int) torrePrincesaAzul2.centro.getX() + 17, (int) torrePrincesaAzul2.centro.getY() + 70);

        // Dibujar flechas (ataques de princesas)
        g.drawImage(imagenFlechaAbajo1.getImage(), (int) centroFlechaAbajo1.getX(), (int) centroFlechaAbajo1.getY(), this);
        g.drawImage(imagenFlechaAbajo2.getImage(), (int) centroFlechaAbajo2.getX(), (int) centroFlechaAbajo2.getY(), this);

        // Dibujar bala (ataque de rey)
        g.drawImage(imagenBalaAbajo.getImage(), (int) centroBalaAbajo.getX(), (int) centroBalaAbajo.getY(), this);

        // Verificar si alguna carta ha sido colocada en una columna (para los ataques que vienen de arriba)
        for (Cartas carta : cartasColocadas) {
            if (carta.vida > 0) {
                // Verificar si la carta está en la columna 1
                if (carta.getColumna() == 0) {
                    g.drawImage(imagenFlechaArriba1.getImage(), (int) centroFlechaArriba1.getX(), (int) centroFlechaArriba1.getY(), this);
                } // Verificar si la carta está en la columna 2
                else if (carta.getColumna() == 1) {
                    g.drawImage(imagenBalaArriba.getImage(), (int) centroBalaArriba.getX(), (int) centroBalaArriba.getY(), this);
                } // Verificar si la carta está en la columna 3
                else if (carta.getColumna() == 2) {
                    g.drawImage(imagenFlechaArriba2.getImage(), (int) centroFlechaArriba2.getX(), (int) centroFlechaArriba2.getY(), this);
                }
            }
        }
        
        // Verificar si alguna carta ha sido colocada en una columna (para los ataques que vienen de abajo)
        for (Cartas cart : enemigos) {
            if (cart.vida > 0) {
                // Verificar si la carta está en la columna 1
                if (cart.getColumna() == 0) {
                    g.drawImage(imagenFlechaAbajo1.getImage(), (int) centroFlechaAbajo1.getX(), (int) centroFlechaAbajo1.getY(), this);
                } // Verificar si la carta está en la columna 2
                else if (cart.getColumna() == 1) {
                    g.drawImage(imagenBalaAbajo.getImage(), (int) centroBalaAbajo.getX(), (int) centroBalaAbajo.getY(), this);
                } // Verificar si la carta está en la columna 3
                else if (cart.getColumna() == 2) {
                    g.drawImage(imagenFlechaAbajo2.getImage(), (int) centroFlechaAbajo2.getX(), (int) centroFlechaAbajo2.getY(), this);
                }
            }
        }
        
        //Trofeos
        g.drawString(Integer.toString(trofeos), 122,367);
        
        //Coronas (evitando que se haga un bucle)
        if(torrePrincesaRoja1.vidaTorre<=0 && torrePrincesaRoja1.contada==false){
            contadorAzul+=1;
            torrePrincesaRoja1.contada=true;
        }else if(torrePrincesaRoja2.vidaTorre<=0 && torrePrincesaRoja2.contada==false){
            contadorAzul+=1;
            torrePrincesaRoja2.contada=true;
        }else if(torrePrincesaAzul1.vidaTorre<=0 && torrePrincesaAzul1.contada==false){
            contadorRojo+=1;
            torrePrincesaAzul1.contada=true;
        }else if(torrePrincesaAzul2.vidaTorre<=0 && torrePrincesaAzul2.contada==false){
            contadorRojo+=1;
            torrePrincesaAzul2.contada=true;
        }

        //Dibuja las coronas conseguidas
        g.drawString(Integer.toString(contadorRojo), 760, 223);
        g.drawString(Integer.toString(contadorAzul), 760, 380);
        
        //Fin del juego 
        if(torreReyRojo.vidaTorre<=0){
            g.drawImage(ganar, 0, 0, getWidth(), getHeight(), this);
            if(win==false){
                ventana.detenerMusica();
                ventana.reproducirMusica("/Sonidos/Ganador.wav");
                numero= trofeos +1000;
                sobreescribirArchivo(numero);
                win=true;
            }
        }else if(torreReyAzul.vidaTorre<=0){
            g.drawImage(perder, 0, 0, getWidth(), getHeight(), this);
            if(win==false){
                ventana.detenerMusica();
                ventana.reproducirMusica("/Sonidos/Perdedor.wav");
                win=true;
            }
        }
    }
    
    private void leerArchivo(){
        try {
            scanner = new Scanner(f);
            trofeos = scanner.nextInt();
            System.out.println("El numero leido es: " + trofeos);
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado: " + e.getMessage());
        }
    }
    
    private static void sobreescribirArchivo(int num) {
        try {
            FileWriter fileWriter = new FileWriter("src/archivos/trofeos.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // Escribir el número en el archivo
            bufferedWriter.write(String.valueOf(num));

            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println("Ocurrió un error al sobreescribir el archivo: " + e.getMessage());
        }
    }
    
}
