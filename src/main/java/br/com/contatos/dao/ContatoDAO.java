package br.com.contatos.dao;

import br.com.contatos.model.Cliente;
import br.com.contatos.model.Contato;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContatoDAO {

    public void salvar(Contato contato) {
        String sql = "INSERT INTO Contato (tipo, valor, cliente_id) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionFactory.recuperarConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, contato.tipo());
            ps.setString(2, contato.valor());
            ps.setInt(3, contato.cliente().id());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar contato: " + e.getMessage(), e);
        }
    }

    public List<Contato> listar(int idCliente) {
        List<Contato> contatos = new ArrayList<>();
        String sql = "SELECT id, tipo, valor, observacao, cliente_id FROM Contato WHERE cliente_id = ?";

        try (Connection conn = ConnectionFactory.recuperarConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cliente clienteSimplificado = new Cliente(
                            rs.getInt("cliente_id"),
                            null, null, null, null
                    );

                    contatos.add(new Contato(
                            rs.getInt("id"),
                            clienteSimplificado,
                            rs.getString("tipo"),
                            rs.getString("valor"),
                            rs.getString("observacao")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar: " + e.getMessage());
        }
        return contatos;
    }

    public void atualizar(Contato contato) {
        String sql = "UPDATE Contato SET tipo = ?, valor = ?, observacao = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.recuperarConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, contato.tipo());
            ps.setString(2, contato.valor());
            ps.setString(3, contato.observacao());
            ps.setInt(4, contato.id());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar contato: " + e.getMessage(), e);
        }
    }

    public void excluir(int idContato) {
        String sql = "DELETE FROM Contato WHERE id = ?";

        try (Connection conn = ConnectionFactory.recuperarConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idContato);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir contato: " + e.getMessage(), e);
        }
    }
}