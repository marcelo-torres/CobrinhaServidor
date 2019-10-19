/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.agentes;

import localizacoes.ILocal;
import model.send.Arena;

/**
 *
 * @author Thiago
 */
public interface IJogadorVisaoControladorServidor extends IJogadorProtegido{
    public double getVD();
    public String getNome();
    public void setLocalAtual(ILocal local);
    public void novoQuadro(Arena arena);
    public ILocal getLocalAtual();
    public void oponenteDesistiu();
    public void ganhou();

    public void empatou();

    public void perdeu();

    public void irParaHall();

    public void irParaPartida();

    public void combinando();
}
