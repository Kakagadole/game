import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JPanel {
    private JButton startButton;
    private JButton ruleButton;

    public Menu(JFrame frame) {
        setLayout(new BorderLayout());

        JLabel backgroundLabel = new JLabel(new ImageIcon(getClass().getResource("BackGround/Space Travel Animation GIF by Planet XOLO.gif")));
        backgroundLabel.setLayout(new GridBagLayout());
        add(backgroundLabel, BorderLayout.CENTER);

        startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 24));
        startButton.setFocusPainted(false);

        ruleButton = new JButton("Rules");
        ruleButton.setFont(new Font("Arial", Font.BOLD, 24));
        ruleButton.setFocusPainted(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(0, 0, 0, 100));
        buttonPanel.setLayout(new GridLayout(1, 2, 10, 10));
        buttonPanel.add(startButton);
        buttonPanel.add(ruleButton);

        backgroundLabel.add(buttonPanel);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.getContentPane().removeAll();
                SpaceInvaders gamePanel = new SpaceInvaders();
                MUSIC gameMusic = new MUSIC();
                gameMusic.gameMusic("SOUND/space-invaders-classic-arcade-game-116826.wav");
                frame.add(gamePanel);
                frame.revalidate();
                frame.repaint();
                gamePanel.requestFocus();
            }
        });

        ruleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRulesWindow();
            }
        });
    }

    public void showRulesWindow() {
        JFrame rulesFrame = new JFrame("Game Rules");
        rulesFrame.setSize(600, 500);
        rulesFrame.setLocationRelativeTo(null);
        rulesFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JTextArea rulesText = new JTextArea();
        rulesText.setEditable(false);
        rulesText.setFont(new Font("Arial", Font.PLAIN, 18));
        rulesText.setText(
                "Space Invaders Rules:\n\n" +
                        "- Use Left & Right Arrows To Move.\n" +
                        "- Press Space Bar To Shoot Lasers.\n" +
                        "- Destroy All Aliens Before They Catch You.\n" +
                        "- You Lose If Aliens Reach You.\n" +
                        "- Try To Get As Many Points As You Can. Good Luck!"
        );

        JScrollPane scrollPane = new JScrollPane(rulesText);
        rulesFrame.add(scrollPane);
        rulesFrame.setVisible(true);
    }
}


