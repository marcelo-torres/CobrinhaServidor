package stub.comando.jogador;

import model.agentes.IJogador;

public class AndarParaBaixo extends ComandoJogador {
    
    public AndarParaBaixo(String codigo, IJogador jogador) {
        super(codigo, jogador);
    }

    @Override
    public void executar() {
        super.JOGADOR.andarParaBaixo();
    }

    @Override
    public void definirParametros(String... parametros) {
       throw new UnsupportedOperationException("Nenhum parametro necessario");
    }
}