package controlador;

import java.awt.Component;
import java.util.HashMap;
import javax.swing.JFrame;

public class Controlador {
    protected JFrame tela;
    protected HashMap mapaDeComponentes;
    protected GerenciadorDeTelas gerenciador;
        
    protected void criarMapaDeComponentes() {
        mapaDeComponentes = new HashMap<String,Component>();
        Component[] components = tela.getContentPane().getComponents();
        for (int i=0; i < components.length; i++) {
                mapaDeComponentes.put(components[i].getName(), components[i]);
        }
    }

    protected Component getComponente(String name) {
        if (mapaDeComponentes.containsKey(name)) {
            return (Component) mapaDeComponentes.get(name);
        }
        else return null;
    }
}
