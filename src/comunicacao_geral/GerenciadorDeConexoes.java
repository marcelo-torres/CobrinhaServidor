package comunicacao_geral;

import java.net.Socket;
import controller.ControladorGeral;
import java.util.concurrent.Semaphore;
import model.Jogador;
import stub.GerenciadorDeCliente;
import stub.comunicacao.GerenciadorDePortas;

public class GerenciadorDeConexoes implements GerenciadorDeRequisicao {
    
    private final GerenciadorDePortas GERENCIADOR_DE_PORTAS;
    
    private int NUMERO_MAXIMO_DE_CONEXOES;
    private final Object LOCKER_NUMERO_DE_CONEXOES = new Object();
    private int numeroDeConexoes;
    
    private ControladorGeral cg;
    public GerenciadorDeConexoes(int numeroMaximoDeConexoes, ControladorGeral controladorGeral, int inicioIntervaloUDP, int fimIntervaloUDP){
        this.NUMERO_MAXIMO_DE_CONEXOES = numeroMaximoDeConexoes;
        this.cg = controladorGeral;
        this.GERENCIADOR_DE_PORTAS = new GerenciadorDePortas(inicioIntervaloUDP, fimIntervaloUDP);
    }
    
    public GerenciadorDeConexoes(int numeroMaximoDeConexoes, ControladorGeral controladorGeral) {
        this.NUMERO_MAXIMO_DE_CONEXOES = numeroMaximoDeConexoes;
        this.cg = controladorGeral;
        this.GERENCIADOR_DE_PORTAS = new GerenciadorDePortas(-1, 0);
    }
    
    private boolean incrementarNumeroConexoes() {
        synchronized(LOCKER_NUMERO_DE_CONEXOES) {
            if(this.numeroDeConexoes < this.NUMERO_MAXIMO_DE_CONEXOES) {
                this.numeroDeConexoes++;
                return true;
            } else {
                return false;
            }
        }
    }
    
    private void decrementarNumeroConexoes() {
        synchronized(LOCKER_NUMERO_DE_CONEXOES) {
            this.numeroDeConexoes--;
        }
    }
    
    @Override
    public void gerenciarRequisicao(Socket socket) {
        
        // TODO salvar as threads ativas
        
        // new Thread(new RepresentanteJogador(socket)).start();
        if(!this.incrementarNumeroConexoes()) {
            try {
                socket.close();
            } catch(Exception e) {
                // Ignora
            }
        }
       
        Jogador novoJogador = new Jogador(cg);
        GerenciadorDeCliente novoGerenciador = new GerenciadorDeCliente(novoJogador, socket, this.GERENCIADOR_DE_PORTAS);
        novoJogador.setControleJogador(novoGerenciador);
       
        novoGerenciador.iniciarStub();
    }
}
