package br.com.contatos.servlet;

import br.com.contatos.model.Endereco;
import br.com.contatos.service.ViaCepService;
import com.google.gson.Gson;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/consultarCep")
public class CepServlet extends HttpServlet {
    private ViaCepService viaCEPService = new ViaCepService();
    private Gson gson = new Gson(); // Criar uma única instância é mais eficiente

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String cep = request.getParameter("cep");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (cep != null && !cep.trim().isEmpty()) {
                Endereco endereco = viaCEPService.buscarPorCep(cep);

                if (endereco != null) {
                    String json = gson.toJson(endereco);
                    response.getWriter().write(json);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"erro\": \"CEP não encontrado\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"erro\": \"CEP é obrigatório\"}");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Loga o erro no console do IntelliJ
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"erro\": \"Erro interno ao consultar CEP\"}");
        }
    }
}