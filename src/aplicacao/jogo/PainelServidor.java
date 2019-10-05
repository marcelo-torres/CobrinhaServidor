 package jogo;

import controlador.ControladorJogo;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JPanel;

@SuppressWarnings("Serial")
 
public class PainelServidor extends JPanel implements Runnable, KeyListener {
    
    // Controlador
    private ControladorJogo controlador;
    
    // Render
    private Graphics2D g2d;
    private BufferedImage image;
    
    // Game Loop
    private Thread thread;
    private boolean running;
    private long targetTime;
    
    // Game Stuff
    Arena arena;
    Pixel cabeca1;
    ArrayList<Pixel> cobra1;
    Pixel cabeca2;
    ArrayList<Pixel> cobra2;
    ArrayList<Pixel> alimentos;
    int velocidade1 = 1;
    int velocidade2 = 1;
    
    // Key Input
    private boolean downAllowed;
    
    public PainelServidor(Arena ar, ControladorJogo ctr) {
        setPreferredSize(new Dimension(ar.getLargura(), ar.getAltura()));
        setFocusable(true);
        requestFocus();
        addKeyListener(this);
        setBackground(new Color(69, 42, 84));
        arena = ar;
        controlador = ctr;
    }
    
    //Método do servidor
    public void atualizaArena(){
        arena.getCobra1().apagaPosicoes();
        for(int i = 0; i < cobra1.size(); i++){
            arena.getCobra1().insertPosicao(cobra1.get(i).getX(), cobra1.get(i).getY());
        }
        
        arena.getCobra2().apagaPosicoes();
        for(int i = 0; i < cobra2.size(); i++){
            arena.getCobra2().insertPosicao(cobra2.get(i).getX(), cobra2.get(i).getY());
        }
        
        ArrayList<EntidadePosicionavel> eAlimentos = new ArrayList<EntidadePosicionavel>();
        for(int i = 0; i < alimentos.size(); i++){
            Pixel alimento = alimentos.get(i);
            eAlimentos.add(alimento.getColor().equals(Color.YELLOW) ?
                            new AlimentoBanana(alimento.getX(), alimento.getY())
                            : new AlimentoPepsi(alimento.getX(), alimento.getY()));
        }
    }
    
    //Método do cliente
    public void atualizaPainel(){
        setCobras();
        setAlimentos();
    }
    
    public void setCobras(){
        cobra1 = new ArrayList<Pixel>();
        cobra2 = new ArrayList<Pixel>();
        
        Cobra cobra = (Cobra)arena.getCobra1();
        
        cabeca1 = new Pixel(arena.getTamanho());
        cabeca1.setColor(cobra.getCor());
        cabeca1.setPosition(cobra.vertices.get(0)[0], cobra.vertices.get(0)[1]);
        cobra1.add(cabeca1);
        
        for(int i = 1; i < cobra.vertices.size(); i++){
            Pixel pixel = new Pixel(arena.getTamanho());
            pixel.setColor(cobra.getCor());
            pixel.setPosition(cobra.vertices.get(i)[0], cobra.vertices.get(i)[1]);
            cobra1.add(pixel);
        }
        
        cobra = (Cobra)arena.getCobra2();

        cabeca2 = new Pixel(arena.getTamanho());
        cabeca2.setColor(cobra.getCor());
        cabeca2.setPosition(cobra.vertices.get(0)[0], cobra.vertices.get(0)[1]);
        cobra2.add(cabeca2);
        
        for(int i = 1; i < cobra.vertices.size(); i++){
            Pixel pixel = new Pixel(arena.getTamanho());
            pixel.setColor(cobra.getCor());
            pixel.setPosition(cobra.vertices.get(i)[0], cobra.vertices.get(i)[1]);
            cobra2.add(pixel);
        }
    }
    
    public void setAlimentos(){
        alimentos = new ArrayList<Pixel>();
        
        for(int i = 0; i < arena.getAlimentos().size(); i++){
            Alimento alimento = (Alimento)arena.getAlimentos().get(i);
            
            Pixel pixel = new Pixel(arena.getTamanho());            
            pixel.setColor(alimento.getCor());
            pixel.setPosition(alimento.getX(), alimento.getY());
            alimentos.add(pixel);
        }
    }
    
    public void setAllowed(boolean a){
        downAllowed = a;
    }
    
    public void moveCobra1(int dx, int dy){
        for(int i = cobra1.size() - 1; i > 0; i--){
            cobra1.get(i).setPosition(cobra1.get(i - 1).getX(), cobra1.get(i - 1).getY());
        }

        cabeca1.move(dx, dy);
    }
    
    public void moveCobra2(int dx, int dy){
        for(int i = cobra2.size() - 1; i > 0; i--){
            cobra2.get(i).setPosition(cobra2.get(i - 1).getX(), cobra2.get(i - 1).getY());
        }

        cabeca2.move(dx, dy);
    }
    
    public int verificaColisaoCobras(){
        if(cabeca1.isCollision(cabeca2) || cabeca2.isCollision(cabeca1))
            return 3;
        
        for(Pixel pixel : cobra1){
            if(pixel.isCollision(cabeca1) || pixel.isCollision(cabeca2)){
                return 1;
            }
        }
        
        for(Pixel pixel : cobra2){
            if(pixel.isCollision(cabeca1) || pixel.isCollision(cabeca2)){
                return 2;
            }
        }
        return 0;
    }
    
    public void processaAlimentos(){
        for(Pixel alimento : alimentos){
            if(alimento.isCollision(cabeca1)){
                if(consomeAlimento(alimento)){
                    Pixel e = new Pixel(arena.getTamanho());
                    e.setPosition(-100, -100);
                    e.setColor(cobra1.get(0).getColor());
                    cobra1.add(e);
                }
                else{
                    velocidade1++;
                }
                
            }
            if(alimento.isCollision(cabeca2)){
                if(consomeAlimento(alimento)){
                    Pixel e = new Pixel(arena.getTamanho());
                    e.setPosition(-100, -100);
                    e.setColor(cobra2.get(0).getColor());
                    cobra2.add(e);
                }
                else{
                    velocidade2++;
                }
            }
            
        }
    }
    
    public void trataParedes(){
        if (cabeca1.getX() < 0) cabeca1.setX(arena.getLargura() - 10);
        if (cabeca1.getY() < 0) cabeca1.setY(arena.getAltura() - 10);
        if (cabeca1.getX() > arena.getLargura() - 10) cabeca1.setX(0);
        if (cabeca1.getY() > arena.getAltura() - 10) cabeca1.setY(0);
        
        if (cabeca2.getX() < 0) cabeca2.setX(arena.getLargura() - 10);
        if (cabeca2.getY() < 0) cabeca2.setY(arena.getAltura() - 10);
        if (cabeca2.getX() > arena.getLargura() - 10) cabeca2.setX(0);
        if (cabeca2.getY() > arena.getAltura() - 10) cabeca2.setY(0);
    }
    
    public boolean consomeAlimento(Pixel alimento){
        atualizaArena();
        Color cor = alimento.getColor();
        int index = alimentos.indexOf(alimento);
        arena.getAlimentos().remove(index);
        arena.setNovoAlimento();
        atualizaPainel();
        
        return cor == Color.YELLOW;
    }
    
    public void setFPS(int fps) {
        targetTime = 1000/fps;
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        thread = new Thread(this);
        thread.start();
    }
    
    @Override
    public void run() {
        if(running) return;
        init();
        long startTime;
        long elapsed;
        long wait;
        while(running) {
            startTime = System.nanoTime();
            
            controlador.update();
            requestRender();
            
            elapsed = System.nanoTime() - startTime;
            wait = targetTime - elapsed / 1000000;    
            if(wait > 0) {
                try {
                    Thread.sleep(wait);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void init() {
        image = new BufferedImage(arena.getLargura(), arena.getLargura(), BufferedImage.TYPE_INT_ARGB);
        g2d = image.createGraphics();
        running = true;
        controlador.iniciarJogo();
    }
    
    private void requestRender() {
        render(g2d);
        Graphics g = getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
    }
    
    public void render(Graphics2D g2d) {
        g2d.setColor(new Color(69, 42, 84));
        g2d.fillRect(0, 0, arena.getLargura(), arena.getAltura());
        
        g2d.setColor(cabeca1.getColor());
        for(Pixel e : cobra1) {
            e.render(g2d);
        }
        
        g2d.setColor(cabeca2.getColor());
        for(Pixel e : cobra2) {
            e.render(g2d);
        }
        
        for(Pixel e : alimentos) {
            g2d.setColor(e.getColor());
            e.render(g2d);
        }
        
        g2d.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        if(controlador.isGameOver()){
            g2d.setColor(Color.RED);
            g2d.drawString("GameOver!" , 200, 200);
        }
        

        if(controlador.isInicio()){
            g2d.setColor(Color.WHITE);
            g2d.drawString("Ready!", 200, 200);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        
        switch(k){
            case KeyEvent.VK_UP:
                controlador.setUp(true);
                break;
            case KeyEvent.VK_DOWN:
                if(downAllowed)
                    controlador.setDown(true);
                break;
            case KeyEvent.VK_RIGHT:
                controlador.setRight(true);
                break;
            case KeyEvent.VK_LEFT:
                controlador.setLeft(true);
                break;
            
            case KeyEvent.VK_ENTER:
                controlador.setStart(true);
                break;
            
            case KeyEvent.VK_W:
                controlador.setUp(true);
                break;
            case KeyEvent.VK_S:
                if(downAllowed)
                    controlador.setDown(true);
                break;
            case KeyEvent.VK_D:
                controlador.setRight(true);
                break;
            case KeyEvent.VK_A:
                controlador.setLeft(true);
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        
        switch(k){
            case KeyEvent.VK_UP:
                controlador.setUp(false);
                break;
            case KeyEvent.VK_DOWN:
                if(downAllowed)
                    controlador.setDown(false);
                break;
            case KeyEvent.VK_RIGHT:
                controlador.setRight(false);
                break;
            case KeyEvent.VK_LEFT:
                controlador.setLeft(false);
                break;
            
            case KeyEvent.VK_ENTER:
                controlador.setStart(false);
                break;
            
            case KeyEvent.VK_W:
                controlador.setUp(false);
                break;
            case KeyEvent.VK_S:
                if(downAllowed)
                    controlador.setDown(false);
                break;
            case KeyEvent.VK_D:
                controlador.setRight(false);
                break;
            case KeyEvent.VK_A:
                controlador.setLeft(false);
                break;
            default:
                break;
        }
    }
    
    
    
}
