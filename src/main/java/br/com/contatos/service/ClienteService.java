package br.com.contatos.service;
import br.com.contatos.dao.ClienteDAO;
import br.com.contatos.model.Cliente;
import br.com.contatos.model.Endereco;
import java.time.LocalDate;
import java.util.List;

public class ClienteService {

    private ClienteDAO clienteDAO = new ClienteDAO();

    public void cadastrar(Cliente cliente, Endereco endereco) {
        if (cliente.dataNascimento().isAfter(LocalDate.now())) {
            throw new RuntimeException("A data de nascimento não pode ser uma data futura.");
        }

        String cpfNormalizado = cliente.cpf().replaceAll("[^0-9]", "");

        if (!clienteDAO.buscarPorCpf(cpfNormalizado).isEmpty()) {
            throw new RuntimeException("Este CPF já está cadastrado no sistema.");
        }

        clienteDAO.salvar(cliente, endereco);
    }



    public void atualizar(Cliente cliente, Endereco endereco) {
        if (cliente.dataNascimento().isAfter(LocalDate.now())) {
            throw new RuntimeException("A data de nascimento não pode ser uma data futura.");
        }

        List<Cliente> clientesNoBanco = clienteDAO.buscarPorCpf(cliente.cpf());

        if (!clientesNoBanco.isEmpty()) {
            Cliente existente = clientesNoBanco.get(0);
            if (!existente.id().equals(cliente.id())) {
                throw new RuntimeException("Este CPF já está cadastrado para outro cliente.");
            }
        }
        clienteDAO.editar(cliente, endereco);
    }

    public void remover(int idCliente, int idEndereco) {
        clienteDAO.apagar(idCliente, idEndereco);
    }

    public List<Cliente> listarTodos() {
        return clienteDAO.listar();
    }

    public List<Cliente> buscarPorNome(String nome) {
        return clienteDAO.buscarPorNome(nome);
    }

    public Cliente buscarPorId(int id) {
        Cliente cliente = clienteDAO.buscarPorId(id);
        if (cliente == null) {
            throw new RuntimeException("Cliente não encontrado.");
        }
        return cliente;
    }
}