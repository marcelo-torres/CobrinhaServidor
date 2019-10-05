package controlador;

public class GerenciadorDeTelas{
    ControladorBusca ctr_busca;
    ControladorJogo ctr_jogo;
    ControladorInicio ctr_inicio;
    String nome_jogador = "";

    public GerenciadorDeTelas(){
        ctr_busca = new ControladorBusca(this);
        ctr_jogo = new ControladorJogo(this);
        ctr_inicio = new ControladorInicio(this);
        ctr_inicio.inicializarTelaInicio();
    }
    
    public void exibirBusca(){
        ctr_busca.inicializarTelaBusca();
    }
    
    public void exibirJogo(){
        ctr_jogo.inicializarTelaJogo();
    }
    
    public void exibirInicio(){
        ctr_inicio.inicializarTelaInicio();
    }
    
}
