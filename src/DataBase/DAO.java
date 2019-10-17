/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataBase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author a10391204
 */
public class DAO {
    private AcessoBanco acesso;
    private Object lockerDerrotas;
    private Object lockerVitorias;
    private Object lockerEmpates;
    
    public DAO() throws SQLException{
        acesso = AcessoBanco.getAcesso();
    }
    
    public boolean inserirJogador(String nome){
        
        synchronized(acesso){
            
            ResultSet rs = acesso.buscar("SELECT nome FROM jogadores WHERE nome = " + nome);
            try {
                
                if(rs.getString("nome") == null){
                    acesso.executarQuery("INSERT INTO jogadores(nome) VALUES ('"+nome+"')");
                    return true;
                }
                   
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                
            }
            return false;
        }

    }
    
    public double getVD(String nome){
        
        
        double vd = -1;    
        ResultSet rs = acesso.buscar("SELECT (vitorias, derrotas) FROM jogadores WHERE nome = " + nome);
        try {
            
            int vitorias = rs.getInt("vitorias");
            int derrotas = rs.getInt("derrotas");
            
            vd = ((double)vitorias) / ((double)derrotas);

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());

        }
        return vd;
        

    }
    
    public boolean incrementaDerrota(String nome){
        synchronized(lockerDerrotas){
            ResultSet rs = acesso.buscar("SELECT (derrotas) FROM jogadores WHERE nome = " + nome);
            try {
                int derrotas = rs.getInt("derrotas");
                acesso.executarQuery("UPDATE jogadores SET derrotas = " + (derrotas + 1) + " WHERE nome = " + nome);
                return true;
                
            } catch (SQLException ex) {
                
            }
            return false;
        }
    }
    
    public boolean incrementaVitorias(String nome){
        synchronized(lockerVitorias){
            ResultSet rs = acesso.buscar("SELECT (vitorias) FROM jogadores WHERE nome = " + nome);
            try {
                int vitorias = rs.getInt("vitorias");
                acesso.executarQuery("UPDATE jogadores SET vitorias = " + (vitorias + 1) + " WHERE nome = " + nome);
                return true;
                
            } catch (SQLException ex) {
                
            }
            return false;
        }
    }
    
    public boolean incrementaEmpates(String nome){
        synchronized(lockerEmpates){
            ResultSet rs = acesso.buscar("SELECT (empates) FROM jogadores WHERE nome = " + nome);
            try {
                int empates = rs.getInt("empates");
                acesso.executarQuery("UPDATE jogadores SET empates = " + (empates + 1) + " WHERE nome = " + nome);
                return true;
                
            } catch (SQLException ex) {
                
            }
            return false;
        }
    }
    
}
