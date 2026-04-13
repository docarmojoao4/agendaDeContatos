package br.com.contatos.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    public static Connection recuperarConexao() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/agenda_contatos";
            return DriverManager.getConnection(url, "root", "1904");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver do MySQL não encontrado!", e);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar ao banco!", e);
        }
    }
}
