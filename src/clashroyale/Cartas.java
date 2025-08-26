package clashroyale;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

public class Cartas extends JComponent {
    public int id; // id de carta para extraer imagen
    public int x, y; // posición
    public int xm, ym; // mover con mouse
    public int distX, distY; // calcular distancias
    public boolean mov; // indica si se movió la carta
    public static Cartas arrastrada; // indica si se arrastro la carta
    public int row; // fila de la celda de la matriz
    public int col; // columna de la celda de la matriz
    
    int vida; // vida de la carta
    int daño;
    boolean chocaIz, chocaD, chocaR;
    boolean diagonalIzq;
    
    public Sprite sprArriba; //sprite arriba
    public Sprite sprAbajo; //sprite arriba

    public Cartas() {
        id = 0;
        x = 0;
        y = 0;
        xm = 0;
        ym = 0;
        mov = false;
        row = -1;
        col = -1;
        vida = 100;
        daño = 100;
        chocaIz = false;
        chocaD = false;
        chocaR = false;
    }

    public Cartas(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.xm = x;
        this.ym = y;
        mov = false;
        row = -1;
        col = -1;
        vida = 100;
        chocaIz = false;
        chocaD = false;
        chocaR = false;
    }

    public ImageIcon getCarta() {
        ImageIcon carta = new ImageIcon(getClass().getResource("/imagenes/carta" + id + ".png"));
        return carta;
    }
    
    public void setColumna(int columna) {
        this.col = columna;
    }

    public int getColumna() {
        return col;
    }
    
    public Sprite getSprArriba(int ancho, int alto, int px, int py, int nIm,int velS) {
        //asignar el sprte con el id
        return sprArriba;
    }

    public Sprite getSprAbajo() {
        //asignar el sprte con el id
        return sprAbajo;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    public void setSprArriba(Sprite sprArriba) {
        this.sprArriba = sprArriba;
    }

    public void setSprAbajo(Sprite sprAbajo) {
        this.sprAbajo = sprAbajo;
    }
}