import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *  Piano Simulator
 */
public class Piano extends JPanel implements KeyListener  {
    private Synthesizer synthesizer;
    private MidiChannel channel;    
    
    private int octave = 5; 
    private final int KEYS_PER_OCTAVE = 12;
    private String blackKeys = "SD GHJ ";
    private String whiteKeys = "ZXCVBNM";
    private String allKeys = "ZSXDCVGBHNJM"; 
    private boolean[] keyOn = new boolean[allKeys.length()];
    
    public Piano() {
        addKeyListener(this);
        startSynthesizer();
    }

    private void startSynthesizer()  {
        try {
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            channel = synthesizer.getChannels()[0];
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);  
        g.translate(50, 50);      
        g.drawString("Octave: " + octave, 0, -10);
        
        final int KEY_WIDTH = 55;
        final int KEY_HEIGHT =200;
        
        for (int i = 0; i < whiteKeys.length(); ++i) {
            g.setColor(keyOn[allKeys.indexOf(whiteKeys.charAt(i))] ? Color.GRAY : Color.WHITE);
            g.fillRect(i * KEY_WIDTH, 0, KEY_WIDTH, KEY_HEIGHT);
            g.setColor(Color.BLACK);
            g.drawRect(i * KEY_WIDTH, 0, KEY_WIDTH, KEY_HEIGHT);
            g.drawString(" " + whiteKeys.charAt(i), i * KEY_WIDTH + 10, KEY_HEIGHT - 10);
        }

        for (int i = 0; i < blackKeys.length(); ++i) {
            if (blackKeys.charAt(i) == ' ') {
                continue;
            }
            int x = (i + 1) * KEY_WIDTH - KEY_WIDTH / 4;
            g.setColor(keyOn[allKeys.indexOf(blackKeys.charAt(i))] ? Color.GRAY : Color.BLACK);
            g.fillRect(x, 1, KEY_WIDTH/2, KEY_HEIGHT/2);
            g.setColor(Color.WHITE);
            g.drawRect(x, 1, KEY_WIDTH/2, KEY_HEIGHT/2);
            g.drawString(" " + blackKeys.charAt(i), x + 2, KEY_HEIGHT/2-5);
        }
            }
    
    @Override
    public void keyTyped(KeyEvent e) {
    	
    }

    @Override
    public void keyPressed(KeyEvent e) {
        repaint();
        if ("012345678".contains("" + e.getKeyChar())) {
        	octave = e.getKeyCode() - 48;
        }
        
        int keyIndex = allKeys.indexOf((char) e.getKeyCode()); 
        if (keyIndex < 0 || keyOn[keyIndex]) {
            return;
        }
        keyOn[keyIndex] = true;
        channel.noteOn(octave * KEYS_PER_OCTAVE + keyIndex, 90);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        repaint();
        int keyIndex = allKeys.indexOf((char) e.getKeyCode()); 
        if (keyIndex < 0) {
            return;
        }
        keyOn[keyIndex] = false;
        channel.noteOff(octave * KEYS_PER_OCTAVE + keyIndex);
    }

    public static void main(String[] args) {
    	
        SwingUtilities.invokeLater(new Runnable() {
        	
            @Override
            public void run() {
                Piano piano = new Piano();
                JFrame frame = new JFrame();
                frame.setSize(480, 300);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLocationRelativeTo(null);
                frame.getContentPane().add(piano);
                frame.setVisible(true);
                piano.requestFocus();
            }
        });
    }
    
}
