package br.com.contatos.servlet;

import br.com.contatos.dao.ClienteDAO;
import br.com.contatos.model.Cliente;
import br.com.contatos.model.Endereco;
import br.com.contatos.service.ClienteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/clientes")
public class ClienteServlet extends HttpServlet {

    private final ClienteDAO clienteDAO = new ClienteDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("DEBUG: ID recebido do formulário: " + request.getParameter("id"));
        request.setCharacterEncoding("UTF-8");

        try {

            String idStr = request.getParameter("id");
            Integer id = (idStr != null && !idStr.isEmpty()) ? Integer.valueOf(idStr) : null;

            String numString = request.getParameter("numero");
            Integer numero = (numString != null && !numString.isEmpty()) ? Integer.valueOf(numString) : null;

            String complemento = request.getParameter("complemento");
            if (complemento == null) complemento = "";
            String idEndStr = request.getParameter("idEndereco");
            Integer idEndereco = (idEndStr != null && !idEndStr.isEmpty()) ? Integer.valueOf(idEndStr) : null;
            Endereco endereco = new Endereco(
                    idEndereco,
                    request.getParameter("cep"),
                    request.getParameter("logradouro"),
                    complemento,
                    numero,
                    request.getParameter("bairro"),
                    request.getParameter("localidade"),
                    request.getParameter("estado")
            );

            String cpfFormatado = request.getParameter("cpf").replaceAll("[^0-9]", "");

            Cliente cliente = new Cliente(
                    id,
                    request.getParameter("nome"),
                    cpfFormatado,
                    LocalDate.parse(request.getParameter("dataNascimento")),
                    endereco
            );

            ClienteService clienteService = new ClienteService();

            if (id != null) {
                clienteService.atualizar(cliente, endereco);
                response.sendRedirect("index.html?status=editado");
            } else {
                clienteService.cadastrar(cliente, endereco);
                response.sendRedirect("index.html?status=sucesso");
            }



        } catch (RuntimeException e) {
            String msg = java.net.URLEncoder.encode(e.getMessage(), "UTF-8");
            response.sendRedirect("index.html?erro=" + msg);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("index.html?status=erro");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapter(java.time.LocalDate.class, (com.google.gson.JsonSerializer<java.time.LocalDate>)
                        (src, typeOfSrc, context) -> new com.google.gson.JsonPrimitive(src.toString()))
                .create();

        try {
            String idParam = request.getParameter("id");
            String buscaParam = request.getParameter("busca");

            if (idParam != null && !idParam.isEmpty()) {
                Integer id = Integer.valueOf(idParam);
                Cliente cliente = clienteDAO.buscarPorId(id);
                if (cliente != null) {
                    response.getWriter().write(gson.toJson(cliente));
                } else {
                    response.setStatus(404);
                }
            } else if (buscaParam != null && !buscaParam.isEmpty()) {
                List<Cliente> listaFiltrada;
                String termoLimpo = buscaParam.replaceAll("\\D", "");

                if (termoLimpo.length() >= 11) {
                    listaFiltrada = clienteDAO.buscarPorCpf(termoLimpo);
                } else {
                    listaFiltrada = clienteDAO.buscarPorNome(buscaParam);
                }
                response.getWriter().write(gson.toJson(listaFiltrada));
            } else {
                List<Cliente> lista = clienteDAO.listar();
                response.getWriter().write(gson.toJson(lista));
            }
        } catch (Exception e) {
            response.setStatus(500);
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idCliente = request.getParameter("id");
        String idEndereco = request.getParameter("idEndereco");

        try {
            if (idCliente != null && idEndereco != null) {
                int idCli = Integer.parseInt(idCliente);
                int idEnd = Integer.parseInt(idEndereco);

                clienteDAO.apagar(idCli, idEnd);
                response.setStatus(200);
            } else {
                response.setStatus(400);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
        }
    }
}