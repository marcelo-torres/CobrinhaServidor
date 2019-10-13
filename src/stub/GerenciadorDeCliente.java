package stub;

import aplicacao.jogo.Jogador;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import stub.comando.Comando;
import stub.comando.ComandoExibirMensagem;
import stub.comando.controlador_de_partida.AdversarioSaiu;
import stub.comando.gerenciador_de_udp.*;
import stub.comando.jogador.*;
import stub.comunicacao.Comunicador;

public class GerenciadorDeCliente extends Stub {
    
    private final Jogador JOGADOR;
    private final InetAddress ENDERECO_DO_SERVIDOR;
    private final GerenciadorDeConexaoUDPRemota GERENCIADOR_CONEXAO_UDP;
    
    public GerenciadorDeCliente(Jogador jogador, Socket socket) {
        super(Comunicador.Modo.SERVIDOR,
                socket.getInetAddress(),
                socket.getPort());
        
        
        this.JOGADOR = jogador;
        
        //this.CONTROLADOR_CLIENTE = controladorCliente;        
        this.ENDERECO_DO_SERVIDOR = socket.getInetAddress();
        this.GERENCIADOR_CONEXAO_UDP = new GerenciadorDeConexaoUDPRemota(this.MENSAGEIRO, this.ENDERECO_DO_SERVIDOR, this.INTERPRETADOR);
        
        this.INTERPRETADOR.cadastrarComandos(this.criarComandosNecessarios());
        
        super.iniciar(socket);
    }
    
    @Override
    public void receberMensagem(byte[] mensagem) {
        if(mensagem == null) {
            System.out.println("[!] Mano, vc ta jogando uma mensagem nula no interpretador! O que vc tem na cabeça tiw? Programa direito zeh mane");
        }
        
        this.INTERPRETADOR.interpretar(mensagem);
    }
    
    
    /* ########################### CHAMADAS DE RPC ########################## */
    
    
    
    
    
    private LinkedList<Comando> criarComandosNecessarios() {
        
        LinkedList<Comando> listaDeComandos = new LinkedList<>();
        
        // resolver essa bagaca aqui
        //listaDeComandos.add(new AdversarioSaiu("adversarioSaiu",));
        
        listaDeComandos.add(new ComandoExibirMensagem("exibirMensagem"));
        listaDeComandos.add(new AtenderPedidoInicioDeAberturaUDP("atenderPedidoInicioDeAberturaUDP", this.GERENCIADOR_CONEXAO_UDP));
        listaDeComandos.add(new ContinuarAberturaUDP("continuarAberturaUDP", this.GERENCIADOR_CONEXAO_UDP));
        listaDeComandos.add(new IniciarPedidoDeAberturaUDP("iniciarPedidoDeAberturaUDP", this.GERENCIADOR_CONEXAO_UDP));
        
        listaDeComandos.add(new AndarParaBaixo("andarParaBaixo", this.JOGADOR));
        listaDeComandos.add(new AndarParaCima("andarParaCima", this.JOGADOR));
        listaDeComandos.add(new AndarParaDireita("andarParaDireita", this.JOGADOR));
        listaDeComandos.add(new AndarParaEsquerda("andarParaEsquerda", this.JOGADOR));
        listaDeComandos.add(new DesistirDeProcurarPartida("desistirDeProcurarPartida", this.JOGADOR));
        listaDeComandos.add(new EncerrarPartida("encerrarPartida", this.JOGADOR));
        listaDeComandos.add(new IniciarPartida("iniciarPartida", this.JOGADOR));
        
        return listaDeComandos;
    }
}