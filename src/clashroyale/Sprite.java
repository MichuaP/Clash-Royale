package clashroyale;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class Sprite extends JComponent {

    private int width;
    private int height;
    private int frame = 0;

    private BufferedImage image;
    private int tw, th;
    int l = 0, vel, numImagen;

    //Prueba colisiones
    Rectangle recMonito;

    int x;
    int y;
    int id;
     
    //Constructor
    public Sprite(int id, String spriteName, int spriteWidth, int spriteHeight, int vel, int numImagen) {
        this.vel = vel;
        this.numImagen = numImagen;
        this.id = id;
        this.recMonito = new Rectangle();
        try {
            image = ImageIO.read(getClass().getResourceAsStream(spriteName));
            width = spriteWidth;
            height = spriteHeight;
            tw = image.getWidth() / width;
            th = image.getHeight() / height;

        } catch (IOException ex) {
            //...
        }
    }

    public void setFrame(int index) {
        frame = index;
    }

    // Pinta lo que se ponga adentro
    public void pintar(Graphics g, int x, int y) {

        int i = frame % tw;
        int j = frame / tw;

        g.drawImage(image, x, y, x + width, y + height, i * width, j * height, (i + 1) * width, (j + 1) * height, null);
        System.out.println("imprimiendo sprite");

        recMonito.setLocation(x, y);
        recMonito.setSize(width, height);

        //g.drawRect(x, y, recMonito.width, recMonito.height);

        setFrame((l++) / vel);//para la velocidad

        //para el numero, se multiplica el número de la división anterior por el numero de imagenes que hay
        if (l >= vel * numImagen) {
            l = 0;
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getId() {
        return id;
    }
}
