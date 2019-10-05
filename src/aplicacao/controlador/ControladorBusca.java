package controlador;

import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import telas.Busca;

public class ControladorBusca extends Controlador{
    
    public ControladorBusca(GerenciadorDeTelas grc) {
        gerenciador = grc;
    }
    
    public void inicializarTelaBusca(){
        tela = new Busca(this);
        tela.setLocationRelativeTo(null);
        tela.setResizable(false);

        criarMapaDeComponentes();
        
        URL url = getClass().getResource("/telas/img/loading.gif");
        Icon icon = new ImageIcon(url);
        
        ((JLabel)getComponente("lbl_loading")).setIcon(icon);
        
        tela.setVisible(true);
    }
    
    public void finalizarTelaBusca(){
        new java.util.Timer().schedule( 
            new java.util.TimerTask() {
                @Override
                public void run() {
                    gerenciador.exibirJogo();
                    tela.setVisible(false);
                    tela = null;
                }
            }, 
            3000 
        );
    }
}
