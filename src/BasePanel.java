import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.RadialGradientPaint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

public abstract class BasePanel extends JPanel {
    protected GameFrame frame;

    public BasePanel(GameFrame frame) {
        // This constructor gives every screen the same size and base background.
        this.frame = frame;
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(Constants.BACKGROUND_COLOR);
        setOpaque(false);
    }

    private static BufferedImage backgroundImage;
    static {
        try {
            backgroundImage = ImageIO.read(new File("bg.png"));
        } catch (IOException e) {
            backgroundImage = null;
        }
    }

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        if (backgroundImage != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            enableAntialiasing(g2);
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
            g2.dispose();
        } else {
            super.paintComponent(g);
        }
    }

    protected JButton createStyledButton(String text) {
        final float[] hoverProgress = {0f};
        final float[] hoverTarget = {0f};

        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                enableAntialiasing(g2);

                boolean hovered = Boolean.TRUE.equals(getClientProperty("hovered"));
                boolean pressed = getModel().isPressed();

                int drawWidth = getWidth() - 2;
                int drawHeight = getHeight() - 2;
                int drawX = 1;
                int drawY = 1;
                int arc = 14;

                Color topColor = pressed ? new Color(31, 37, 58) : hovered ? new Color(24, 34, 51) : Constants.BUTTON_NORMAL_COLOR;
                Color bottomColor = pressed ? new Color(8, 10, 19) : hovered ? new Color(11, 16, 28) : new Color(8, 10, 19);

                g2.setColor(new Color(0, 0, 0, hovered ? 110 : 85));
                g2.fillRoundRect(drawX + 2, drawY + 4, drawWidth, drawHeight, arc, arc);

                if (hovered || pressed) {
                    g2.setColor(new Color(54, 255, 166, (int) (30 + hoverProgress[0] * 70)));
                    g2.fillRoundRect(drawX - 5, drawY - 5, drawWidth + 10, drawHeight + 10, arc + 8, arc + 8);
                }

                g2.setPaint(new GradientPaint(0, drawY, topColor, 0, drawY + drawHeight, bottomColor));
                g2.fill(new RoundRectangle2D.Float(drawX, drawY, drawWidth, drawHeight, arc, arc));

                if (hovered || pressed) {
                    int alpha = (int) (30 + hoverProgress[0] * 70);
                    g2.setPaint(new GradientPaint(drawX, drawY, new Color(54, 255, 166, alpha),
                            drawX + drawWidth, drawY + drawHeight, new Color(255, 58, 169, alpha / 2)));
                    g2.fill(new RoundRectangle2D.Float(drawX + 3, drawY + 3, drawWidth - 6, drawHeight - 6, arc - 2, arc - 2));
                }

                g2.setColor(hovered ? Constants.ACCENT_GREEN : new Color(255, 255, 255, 115));
                g2.draw(new RoundRectangle2D.Float(drawX, drawY, drawWidth, drawHeight, arc, arc));

                g2.dispose();
                super.paintComponent(g);
            }

            {
                Timer hoverAnimation = new Timer(15, e -> {
                    if (hoverProgress[0] < hoverTarget[0]) {
                        hoverProgress[0] = Math.min(1f, hoverProgress[0] + 0.12f);
                        repaint();
                    } else if (hoverProgress[0] > hoverTarget[0]) {
                        hoverProgress[0] = Math.max(0f, hoverProgress[0] - 0.12f);
                        repaint();
                    }
                });
                hoverAnimation.start();
            }
        };
        button.setPreferredSize(new Dimension(240, 54));
        button.setMinimumSize(new Dimension(240, 54));
        button.setMaximumSize(new Dimension(240, 54));
        button.setFont(createRetroFont(Font.BOLD, 17));
        button.setForeground(Constants.TEXT_COLOR);
        button.setBackground(Constants.BUTTON_NORMAL_COLOR);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(null);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new HoverMouseListener(button, hoverTarget));

        return button;
    }

    private static class HoverMouseListener extends MouseAdapter {
        private final JButton button;
        private final float[] hoverTarget;

        HoverMouseListener(JButton button, float[] hoverTarget) {
            this.button = button;
            this.hoverTarget = hoverTarget;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            button.putClientProperty("hovered", Boolean.TRUE);
            hoverTarget[0] = 1f;
            ((JButton) e.getSource()).repaint();
            button.repaint();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            button.putClientProperty("hovered", Boolean.FALSE);
            hoverTarget[0] = 0f;
            ((JButton) e.getSource()).repaint();
            button.repaint();
        }
    }

    protected void drawGlowText(Graphics2D g, String text, int y, Font font, Color color, Color glowColor) {
        g.setFont(font);
        int textWidth = g.getFontMetrics().stringWidth(text);
        int x = (Constants.WINDOW_WIDTH - textWidth) / 2;

        for (int i = 8; i >= 2; i -= 2) {
            g.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 16));
            g.drawString(text, x - i / 2, y);
            g.drawString(text, x + i / 2, y);
            g.drawString(text, x, y - i / 2);
            g.drawString(text, x, y + i / 2);
        }

        g.setColor(color);
        g.drawString(text, x, y);
    }

    protected void drawAmbientOverlays(Graphics2D g, double phase) {
        Graphics2D g2 = (Graphics2D) g.create();
        enableAntialiasing(g2);

        int cyanX = (int) (Constants.WINDOW_WIDTH * (0.25 + 0.06 * Math.sin(phase * 0.7)));
        int pinkX = (int) (Constants.WINDOW_WIDTH * (0.74 + 0.05 * Math.cos(phase * 0.6)));
        g2.setComposite(AlphaComposite.SrcOver.derive(0.42f));
        g2.setPaint(new RadialGradientPaint(cyanX, 150, 420,
                new float[] {0f, 1f},
                new Color[] {new Color(54, 255, 166, 90), new Color(54, 255, 166, 0)}));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setPaint(new RadialGradientPaint(pinkX, 560, 460,
                new float[] {0f, 1f},
                new Color[] {new Color(255, 58, 169, 75), new Color(255, 58, 169, 0)}));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setComposite(AlphaComposite.SrcOver.derive(0.11f));
        g2.setColor(Color.WHITE);
        for (int y = (int) (phase * 18) % 8; y < getHeight(); y += 8) {
            g2.drawLine(0, y, getWidth(), y);
        }

        g2.dispose();
    }

    protected void drawCard(Graphics2D g, int x, int y, int width, int height, Color fillColor, Color borderColor) {
        g.setColor(new Color(0, 0, 0, 120));
        g.fillRoundRect(x + 6, y + 8, width, height, 28, 28);

        g.setColor(fillColor);
        g.fillRoundRect(x, y, width, height, 28, 28);

        g.setColor(borderColor);
        g.setStroke(new java.awt.BasicStroke(2f));
        g.drawRoundRect(x, y, width, height, 28, 28);
    }

    protected void drawCenteredText(Graphics2D g, String text, int y, Font font, Color color) {
        g.setFont(font);
        g.setColor(color);
        int textWidth = g.getFontMetrics().stringWidth(text);
        int x = (Constants.WINDOW_WIDTH - textWidth) / 2;
        g.drawString(text, x, y);
    }

    protected Font createRetroFont(int style, int size) {
        return new Font(Constants.RETRO_FONT_FAMILY, style, size);
    }

    protected void enableAntialiasing(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
}
