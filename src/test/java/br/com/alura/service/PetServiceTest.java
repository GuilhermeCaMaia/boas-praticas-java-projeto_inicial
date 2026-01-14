package br.com.alura.service;

import br.com.alura.client.ClientHttpConfiguration;
import br.com.alura.model.AbrigoModel;
import br.com.alura.model.PetModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PetServiceTest {
    private ClientHttpConfiguration client = mock(ClientHttpConfiguration.class);
    private PetService petService = new PetService(client);
    private AbrigoService abrigoService = new AbrigoService(client);
    private HttpResponse<String> response = mock(HttpResponse.class);
    private PetModel pet = new PetModel("CACHORRO", "Teste", "raca", 5, "teste", 25.5f);
    private AbrigoModel abrigo = new AbrigoModel("Teste", "61981880392", "abrigo_alura@gmail.com");

    @Test
    public void deveListarPetsDoAbrigo() throws IOException, InterruptedException {

        // Entrada simulada (1 nextLine)
        String input = "1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Captura do console
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        System.setOut(printStream);

        // resposta JSON (igual API)
        String jsonResponse = """
        [
          {
            "id": 1,
            "tipo": "Cachorro",
            "nome": "Rex",
            "raca": "Labrador",
            "idade": 3
          }
        ]
        """;

        when(response.body()).thenReturn(jsonResponse);
        when(client.dispararRequisicaoGet(anyString())).thenReturn(response);

        petService.listarPetsDoAbrigo();

        // Verificações
        String output = baos.toString();

        Assertions.assertTrue(output.contains("Pets cadastrados:"));
        Assertions.assertTrue(output.contains("1 - Cachorro - Rex - Labrador - 3 ano(s)"));
    }

    @Test
    public void deveCadastrarPetsDoAbrigo() throws IOException, InterruptedException {
        // 🔹 Entrada simulada (2 nextLine)
        String input =
                "1\n" +
                        "pets.csv\n";

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // 🔹 Captura do console
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        // 🔹 Arquivo REAL
        Files.writeString(
                Paths.get("pets.csv"),
                "cachorro,Rex,Poodle,5,Marrom,10.5\n"
        );

        // 🔹 Mock HTTP
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(201);

        when(client.dispararRequisicaoPost(anyString(), any()))
                .thenReturn(response);

        // 🔹 Execução
        petService.importarPetsDoAbrigo();

        // 🔹 Verificação
        String output = baos.toString();

        Assertions.assertTrue(output.contains("Pet cadastrado com sucesso: Rex"));
//        String expected = "Pet cadastrado com sucesso: Rex";
//        String input = "1\n";
//        System.setIn(new ByteArrayInputStream(input.getBytes()));
//
//        String input2 = "pets.csv";
//        System.setIn(new ByteArrayInputStream(input2.getBytes()));
//
//        String csv = """
//            cachorro,Rex,Poodle,5,Marrom,10.5
//        """;
//
//        InputStream inputStream = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
//        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//
//        petService.importarPetsDoAbrigo();
//
//        List<String> linhas = reader.lines().toList();
//        Assertions.assertEquals(3, linhas.size());
    }
}
