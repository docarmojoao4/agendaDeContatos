package br.com.contatos.servlet;

import br.com.contatos.dao.ContatoDAO;
import br.com.contatos.dao.ClienteDAO;
import br.com.contatos.model.Cliente;
import br.com.contatos.model.Contato;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet({"/salvarContato", "/listarContatos"})
public class ContatoServlet extends HttpServlet {

    private final ContatoDAO contatoDAO = new ContatoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idClienteStr = request.getParameter("clienteId");

        try {
            if (idClienteStr != null && !idClienteStr.isEmpty()) {
                int idCliente = Integer.parseInt(idClienteStr);
                List<Contato> contatos = contatoDAO.listar(idCliente);


                Gson gson = new com.google.gson.GsonBuilder()
                        .registerTypeAdapter(java.time.LocalDate.class, (com.google.gson.JsonSerializer<java.time.LocalDate>)
                                (src, typeOfSrc, context) -> new com.google.gson.JsonPrimitive(src.toString()))
                        .create();

                String json = gson.toJson(contatos);
                response.getWriter().write(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        try {
            String idClienteStr = request.getParameter("clienteId");

            if (idClienteStr == null || idClienteStr.isEmpty() || idClienteStr.equals("undefined")) {
                response.setStatus(400);
                return;
            }

            int idCliente = Integer.parseInt(idClienteStr);
            String tipo = request.getParameter("tipo");
            String valor = request.getParameter("valor");
            String obs = request.getParameter("observacao") != null ? request.getParameter("observacao") : "";
            Cliente cliente = clienteDAO.buscarPorId(idCliente);

            if (cliente != null) {
                Contato contato = new Contato(null, cliente, tipo, valor, obs);
                contatoDAO.salvar(contato);

                response.setStatus(201);
            } else {
                response.setStatus(404);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
        }
    }
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        try {
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                contatoDAO.excluir(id);
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}