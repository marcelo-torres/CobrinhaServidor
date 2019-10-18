package comunicacao_geral;

//import comunicacao_geral.GerenciadorDeRequisicao;
import java.net.Socket;
import java.util.LinkedList;
//import cliente.RepresentanteJogador;
import controller.ControladorGeral;
import model.Jogador;
import stub.GerenciadorDeCliente;

public class GerenciadorDeConexoes implements GerenciadorDeRequisicao {
    
    private ControladorGeral cg;
    public GerenciadorDeConexoes(ControladorGeral controladorGeral){
        this.cg = controladorGeral;
    }
    
    @Override
    public void gerenciarRequisicao(Socket socket) {
        
        // TODO salvar as threads ativas
        
       // new Thread(new RepresentanteJogador(socket)).start();
       
       GerenciadorDeCliente novoGerenciador = new GerenciadorDeCliente(socket);
       Jogador novoJogador = new Jogador(cg, novoGerenciador);
       novoGerenciador.setJogador(novoJogador);
       
    }
    
    public static class ListaLimitada<E>{
    
        private final int CAPACIDADE;
        private final LinkedList<E> LISTA; 
        
        public ListaLimitada(int capacidade) {
            this.CAPACIDADE = capacidade;
            this.LISTA = new LinkedList<>();
        }
        
        public boolean inserirNoFila(E e) {
            if(this.LISTA.size() > this.CAPACIDADE) {
                return false;
            }
            this.LISTA.addLast(e);
            return true;
        }
        
        public E removerDoComeco() {
            return this.LISTA.getFirst();
        }
        
        public E acessarElemento(int indice) {
            return this.LISTA.get(indice);
        }
        
        public int tamanho() {
            return this.LISTA.size();
        }
    }
}
