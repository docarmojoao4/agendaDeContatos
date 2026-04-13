package br.com.contatos.service;

import br.com.contatos.dao.ContatoDAO;
import br.com.contatos.model.Contato;
import java.util.List;

public class ContatoService {

    private ContatoDAO contatoDAO = new ContatoDAO();

    public void adicionarContato(Contato contato, int idCliente) {
        if (contato.valor() == null) {
            throw new RuntimeException("O valor do contato (E-mail/Telefone) é obrigatório.");
        }

        String tipoPadronizado = contato.tipo().toUpperCase();
        if (!tipoPadronizado.equals("TELEFONE") && !tipoPadronizado.equals("EMAIL")) {
            throw new RuntimeException("Tipo de contato inválido. Use TELEFONE ou EMAIL.");
        }

        contatoDAO.salvar(contato);
    }

    public List<Contato> listarContatosCliente(int idCliente) {
        return contatoDAO.listar(idCliente);
    }

    public void removerContato(int idContato) {
        contatoDAO.excluir(idContato);
    }
}