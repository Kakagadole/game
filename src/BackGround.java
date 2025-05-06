import javax.swing.*;
import java.awt.*;

public class BackGround extends JPanel {
    private ImageIcon gifIcon;

    public BackGround(String gifPath) {
        gifIcon = new ImageIcon(getClass().getResource(gifPath));
        setLayout(new GridBagLayout());
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gifIcon != null) {
            Image image = gifIcon.getImage();
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }
}