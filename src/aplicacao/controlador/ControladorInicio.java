package controlador;

import javax.swing.JTextField;
import telas.Inicio;

public class ControladorInicio extends Controlador{

    public ControladorInicio(GerenciadorDeTelas grc) {
        gerenciador = grc;
    }
    
    public void inicializarTelaInicio(){
        tela = new Inicio(this);
        tela.setLocationRelativeTo(null);
        tela.setResizable(false);
        
        criarMapaDeComponentes();
        
        ((JTextField)getComponente("txt_nome")).setText(gerenciador.nome_jogador);
        
        tela.setVisible(true);
    }

    public void finalizarTelaInicio(){
        gerenciador.nome_jogador = ((JTextField)getComponente("txt_nome")).getText();
        gerenciador.exibirBusca();
        tela.setVisible(false);
        tela = null;
    }
}
