package br.com.contatos.dao;

import br.com.contatos.model.Cliente;
import br.com.contatos.model.Endereco;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public Cliente salvar(Cliente cliente, Endereco endereco) {
        String sqlCliente = "INSERT INTO Cliente (endereco_id, nome, cpf, data_nascimento) VALUES (?, ?, ?, ?)";
        String sqlEndereco = "INSERT INTO Endereco (cep, logradouro, complemento, numero, bairro, localidade, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.recuperarConexao()) {
            conn.setAutoCommit(false);

            try {
                int idEnderecoGerado;
                try (PreparedStatement psEndereco = conn.prepareStatement(sqlEndereco, Statement.RETURN_GENERATED_KEYS)) {
                    preencherEndereco(psEndereco, endereco);
                    psEndereco.executeUpdate();
                    ResultSet rs = psEndereco.getGeneratedKeys();

                    if (rs.next()) {
                        idEnderecoGerado = rs.getInt(1);
                    } else {
                        throw new SQLException("Erro ao recuperar o ID do endereço.");
                    }
                }
                try (PreparedStatement psCliente = conn.prepareStatement(sqlCliente)) {
                    String cpfFormatado = cliente.cpf().replaceAll("[^0-9]", "");
                    psCliente.setInt(1, idEnderecoGerado);
                    psCliente.setString(2, cliente.nome());
                    psCliente.setString(3, cpfFormatado);
                    psCliente.setDate(4, Date.valueOf(cliente.dataNascimento()));
                    psCliente.executeUpdate();
                }
                conn.commit();
                System.out.println("LOG: Transação concluída com sucesso no banco!");
                return cliente;

            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Erro ao salvar cliente e endereço: " + e.getMessage(), e);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro de conexão com o banco de dados: " + e.getMessage(), e);
        }
    }

    public Cliente editar(Cliente cliente, Endereco endereco) {
        String sqlCliente = "UPDATE Cliente SET endereco_id = ?, nome = ?, cpf = ?, data_nascimento = ? WHERE id = ?";
        String sqlEndereco = "UPDATE Endereco SET cep = ?, logradouro = ?, complemento = ?, numero = ?, bairro = ?, localidade = ?, estado = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.recuperarConexao()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement psEndereco = conn.prepareStatement(sqlEndereco)) {
                    preencherEndereco(psEndereco, endereco);
                    psEndereco.setInt(8, endereco.id());
                    psEndereco.executeUpdate();
                }

                try (PreparedStatement psCliente = conn.prepareStatement(sqlCliente)) {
                    psCliente.setInt(1, cliente.endereco().id());
                    psCliente.setString(2, cliente.nome());
                    psCliente.setString(3, cliente.cpf());
                    psCliente.setDate(4, Date.valueOf(cliente.dataNascimento()));
                    psCliente.setInt(5, cliente.id());
                    psCliente.executeUpdate();
                }


            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Erro ao salvar as alterações em cliente e endereço: " + e.getMessage(), e);
            }
            conn.commit();
            return cliente;

        } catch (SQLException e) {
            throw new RuntimeException("Erro de conexão com o banco de dados: " + e.getMessage(), e);
        }
    }

    public void apagar(int idCli, int idEnd) throws RuntimeException {
        Connection conn = ConnectionFactory.recuperarConexao();
        String sqlContatos = "DELETE FROM contato WHERE cliente_id = ?";
        String sqlCliente = "DELETE FROM Cliente WHERE id = ?";
        String sqlEndereco = "DELETE FROM Endereco WHERE id = ?";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtCont = conn.prepareStatement(sqlContatos)) {
                stmtCont.setInt(1, idCli);
                stmtCont.executeUpdate();
            }

            try (PreparedStatement stmtCli = conn.prepareStatement(sqlCliente)) {
                stmtCli.setInt(1, idCli);
                stmtCli.executeUpdate();
            }

            try (PreparedStatement stmtEnd = conn.prepareStatement(sqlEndereco)) {
                stmtEnd.setInt(1, idEnd);
                stmtEnd.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("Erro ao apagar: " + e.getMessage(), e);
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Cliente> listar() {
        String sql = """
                SELECT c.id AS cli_id, c.nome, c.cpf, c.data_nascimento, c.endereco_id,
                       e.id AS end_id, e.cep, e.logradouro, e.complemento, 
                       e.numero, e.bairro, e.localidade, e.estado
                FROM Cliente c
                INNER JOIN Endereco e ON c.endereco_id = e.id
                ORDER BY c.nome
                """;

        List<Cliente> clientes = new ArrayList<>();

        try (Connection conn = ConnectionFactory.recuperarConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Endereco endereco = new Endereco(
                        rs.getInt("end_id"),
                        rs.getString("cep"),
                        rs.getString("logradouro"),
                        rs.getString("complemento"),
                        rs.getInt("numero"),
                        rs.getString("bairro"),
                        rs.getString("localidade"),
                        rs.getString("estado")
                );

                clientes.add(new Cliente(
                        rs.getInt("cli_id"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getDate("data_nascimento").toLocalDate(),
                        endereco
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar dados: " + e.getMessage(), e);
        }
        return clientes;
    }

    public Cliente buscarPorId(int id) {
        String sql = """
            SELECT c.id AS cli_id, c.nome, c.cpf, c.data_nascimento, c.endereco_id,
                   e.id AS end_id, e.cep, e.logradouro, e.complemento, 
                   e.numero, e.bairro, e.localidade, e.estado
            FROM Cliente c
            INNER JOIN Endereco e ON c.endereco_id = e.id
            WHERE c.id = ?
            """;

        List<Cliente> resultado = executarBusca(sql, String.valueOf(id));

        return resultado.isEmpty() ? null : resultado.get(0);
    }



    public List<Cliente> buscarPorNome(String nome) {
        String sql = """
                SELECT c.id AS cli_id, c.nome, c.cpf, c.data_nascimento, c.endereco_id,
                       e.id AS end_id, e.cep, e.logradouro, e.complemento, 
                       e.numero, e.bairro, e.localidade, e.estado
                FROM Cliente c
                INNER JOIN Endereco e ON c.endereco_id = e.id
                WHERE c.nome LIKE ?
                ORDER BY c.nome
                """;
        return executarBusca(sql, "%" + nome + "%");
    }

    public List<Cliente> buscarPorCpf(String cpf) {
        String sql = """
                SELECT c.id AS cli_id, c.nome, c.cpf, c.data_nascimento, c.endereco_id,
                       e.id AS end_id, e.cep, e.logradouro, e.complemento, 
                       e.numero, e.bairro, e.localidade, e.estado
                FROM Cliente c
                INNER JOIN Endereco e ON c.endereco_id = e.id
                WHERE c.cpf = ?
                """;
        return executarBusca(sql, cpf);
    }

    private List<Cliente> executarBusca(String sql, String parametro) {
        List<Cliente> clientes = new ArrayList<>();

        try (Connection conn = ConnectionFactory.recuperarConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (parametro != null) {
                ps.setString(1, parametro);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idCliente = rs.getInt("cli_id");
                    int idEndereco = rs.getInt("end_id");

                    Endereco endereco = new Endereco(
                            idEndereco,
                            rs.getString("cep"),
                            rs.getString("logradouro"),
                            rs.getString("complemento"),
                            rs.getInt("numero"),
                            rs.getString("bairro"),
                            rs.getString("localidade"),
                            rs.getString("estado")
                    );

                    Cliente cliente = new Cliente(
                            idCliente,
                            rs.getString("nome"),
                            rs.getString("cpf"),
                            rs.getDate("data_nascimento").toLocalDate(),
                            endereco
                    );

                    clientes.add(cliente);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar no banco: " + e.getMessage(), e);
        }
        return clientes;
    }

    private void preencherEndereco(PreparedStatement psEndereco, Endereco endereco) throws SQLException {
        psEndereco.setString(1, endereco.cep());
        psEndereco.setString(2, endereco.logradouro());
        psEndereco.setString(3, endereco.complemento());
        psEndereco.setInt(4, endereco.numero());
        psEndereco.setString(5, endereco.bairro());
        psEndereco.setString(6, endereco.localidade());
        psEndereco.setString(7, endereco.estado());
    }

}
