package stub;

import cliente.Jogador;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.regex.Pattern;
import stub.comando.Comando;
import stub.comando.ComandoExibirMensagem;
import stub.comando.gerenciador_de_udp.AtenderPedidoInicioDeAberturaUDP;
import stub.comando.gerenciador_de_udp.ContinuarAberturaUDP;
import stub.comando.gerenciador_de_udp.IniciarPedidoDeAberturaUDP;
import stub.comunicacao.Comunicador;

public class GerenciadorDeCliente extends Stub implements Jogador {
    
    private final Semaphore SEMAFORO_ATICAO_UDP = new Semaphore(0);
    
    private final InetAddress ENDERECO_DO_SERVIDOR;
    
    private final GerenciadorDeConexaoUDPRemota GERENCIADOR_CONEXAO_UDP;
    
    
    public GerenciadorDeCliente(Socket socket) {
    
        super(Comunicador.Modo.SERVIDOR,
                socket.getInetAddress(),
                socket.getPort());
        
        
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
    
    @Override
    public void iniciarPartida() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void desistirDeProcurarPartida() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void encerrarPartida() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void andarParaCima() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void andarParaBaixo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void andarParaEsquerda() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void andarParaDireita() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    private LinkedList<Comando> criarComandosNecessarios() {
        
        LinkedList<Comando> listaDeComandos = new LinkedList<>();
        
        // resolver essa bagaca aqui
        //listaDeComandos.add(new AdversarioSaiu("adversarioSaiu",));
        
        listaDeComandos.add(new ComandoExibirMensagem("exibirMensagem"));
        listaDeComandos.add(new AtenderPedidoInicioDeAberturaUDP("atenderPedidoInicioDeAberturaUDP", this.GERENCIADOR_CONEXAO_UDP));
        listaDeComandos.add(new ContinuarAberturaUDP("continuarAberturaUDP", this.GERENCIADOR_CONEXAO_UDP));
        listaDeComandos.add(new IniciarPedidoDeAberturaUDP("iniciarPedidoDeAberturaUDP", this.GERENCIADOR_CONEXAO_UDP));
        
        return listaDeComandos;
    }
}