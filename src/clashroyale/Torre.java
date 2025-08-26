package clashroyale;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Torre {
    public int bando;
    public int x, y;
    public int vidaTorre;
    static final int ANCHO = 80;
    static final int ALTO = 40;
    public Rectangle centro;
    public BufferedImage torreImg;
    public BufferedImage torreDestruida;
    public boolean contada;
    
    public Torre(int bando, int x, int y, int vida, String rutaImagen) {
        this.bando = bando;
        this.x = x;
        this.y = y;
        this.vidaTorre = vida;
        this.contada = false;
        if(bando==2){
            this.centro = new Rectangle(x, y, ANCHO, ALTO);
        }else if(bando==1){
            this.centro = new Rectangle(x, y+30, ANCHO, ALTO);
        }
        
        try{
            this.torreImg = ImageIO.read(getClass().getResourceAsStream(rutaImagen));
            this.torreDestruida = ImageIO.read(getClass().getResourceAsStream("/imagenes/destruida3.png"));
        }catch(IOException ex){
            System.out.println("Error: imagen no encontrada");
        }
    }
    
    public void pintarTorre(Graphics g){
        if(vidaTorre>0){
            g.drawImage(torreImg, x, y, null);
            //g.drawRect(centro.x,centro.y ,centro.width , centro.height);
        }else if(vidaTorre<=0){
            g.drawImage(torreDestruida, x, y, null);
        }
    }
    
    public Rectangle2D getTorreRec() {
        return centro;
    }
    
    public BufferedImage getTorreImagen() {
        return torreImg;
    }
    
    public int getVidaTorre() {
        return vidaTorre;
    }
        
}