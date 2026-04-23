package Program;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Narahubung {
    private JFrame frame;
    private JPanel panel;
    private JTextField textField;

    Narahubung() {
        frame = new JFrame("NARAHUBUNG");
        frame.setSize(500, 700);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon image = new ImageIcon("src/resources/images/Desain_Tampilan/Narahubung.jpg");
                    g.drawImage(image.getImage(), 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    // Fallback jika gambar tidak ditemukan
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(0, 0, getWidth(), getHeight());}}};
        panel.setLayout(null);
        panel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        frame.setContentPane(panel);

        // Membuat tombol Back
        createButton( "",23, 30, 58, 35, () -> {
            frame.dispose();
            new Beranda();});
        frame.setVisible(true); frame.setLocationRelativeTo(null);}

    private JPanel createButton(String text, int x, int y, int w, int h, Runnable action) {
        JPanel button = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Gambar background untuk SEMUA tombol (termasuk Back)
                if (!getBackground().equals(new Color(0, 0, 0, 0))) {
                    g2d.setColor(getBackground());
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);}

                // Gambar teks hanya untuk tombol non-Back
                if (!text.trim().isEmpty()) {
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.BOLD, 16));
                    FontMetrics fm = g2d.getFontMetrics();
                    int textX = (getWidth() - fm.stringWidth(text)) / 2;
                    int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    g2d.drawString(text, textX, textY);
                }}};
        button.setBounds(x, y, w, h); button.setOpaque(false);

        // Set background default
        if (text.trim().isEmpty()) {
            button.setBackground(new Color(0, 0, 0, 0));
        } else {
            button.setBackground(new Color(0, 0, 255)); }

        button.addMouseListener(new MouseAdapter() {
            private final Color defaultColor = button.getBackground();
            private final Color hoverColor = text.trim().isEmpty() ?
                    new Color(0, 0, 255, 10) : // Biru transparan untuk Back
                    new Color(0, 0, 255, 170);  // Biru lebih solid untuk tombol lain

            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {action.run();}}

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor); button.repaint();}

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(defaultColor);button.repaint();}});

        panel.add(button); return button;}
    public static void main(String[] args) {
        new Narahubung();}
}