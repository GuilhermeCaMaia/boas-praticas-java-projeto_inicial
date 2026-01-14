package br.com.alura.service;

import br.com.alura.client.ClientHttpConfiguration;
import br.com.alura.model.AbrigoModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.http.HttpResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbrigoServiceTest {
    private ClientHttpConfiguration client = mock(ClientHttpConfiguration.class);
    private AbrigoService abrigoService = new AbrigoService(client);
    private HttpResponse<String> response = mock(HttpResponse.class);
    private AbrigoModel abrigo = new AbrigoModel("Teste", "61981880392", "abrigo_alura@gmail.com");

    @Test
    public void deveVerificarQuandoHaAbrigo() throws IOException, InterruptedException {
        abrigo.setId(0L);
        String expectedAbrigosCadastrados = "Abrigos cadastrados:";
        String expectedIdENome = "0 - Teste";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        System.setOut(printStream);

        when(response.body()).thenReturn("[{"+abrigo.toString()+"}]");
        when(client.dispararRequisicaoGet(anyString())).thenReturn(response);

        abrigoService.listarAbrigo();

        String[] lines = baos.toString().split(System.lineSeparator());
        String actualAbrigosCadastrados = lines[0];
        String actualIdENome = lines[1];

        Assertions.assertEquals(expectedAbrigosCadastrados, actualAbrigosCadastrados);
        Assertions.assertEquals(expectedIdENome, actualIdENome);
    }

    @Test
    public void deveVerificarQuandoNaoHaAbrigo() throws IOException, InterruptedException {
        abrigo.setId(0L);
        String expected = "Não há abrigos cadastrados";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        System.setOut(printStream);

        when(response.body()).thenReturn("[]");
        when(client.dispararRequisicaoGet(anyString())).thenReturn(response);

        abrigoService.listarAbrigo();

        String[] lines = baos.toString().split(System.lineSeparator());
        String actual = lines[0];

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void deveCadastrarAbrigoComSucesso() throws IOException, InterruptedException {
        // ENTRADA SIMULADA (3 linhas!)
        String input =
                "Abrigo Teste\n" +
                        "61999999999\n" +
                        "teste@email.com\n";

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // CAPTURA DO CONSOLE
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        // MOCKS
        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("Abrigo salvo com sucesso");

        when(client.dispararRequisicaoPost(
                anyString(),
                any(AbrigoModel.class)
        )).thenReturn(response);

        // EXECUÇÃO
        abrigoService.cadastrarAbrigo();

        // ASSERTS
        String output = baos.toString();

        Assertions.assertTrue(output.contains("Abrigo cadastrado com sucesso!"));
        Assertions.assertTrue(output.contains("Abrigo salvo com sucesso"));
    }

    @Test
    public void deveExibirErroAoCadastrarAbrigo() throws IOException, InterruptedException {
        // ---------- Entrada simulada ----------
        String input = String.join(System.lineSeparator(),
                "Abrigo Erro",
                "000000000",
                "erro@email.com"
        );

        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // ---------- Captura do console ----------
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        // ---------- Mocks ----------
        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(400);
        when(response.body()).thenReturn("Dados inválidos");

        when(client.dispararRequisicaoPost(
                anyString(),
                any(AbrigoModel.class)
        )).thenReturn(response);

        // ---------- Execução ----------
        abrigoService.cadastrarAbrigo();

        // ---------- Verificação ----------
        String output = baos.toString();

        Assertions.assertTrue(output.contains("Erro ao cadastrar o abrigo:"));
    }

}
