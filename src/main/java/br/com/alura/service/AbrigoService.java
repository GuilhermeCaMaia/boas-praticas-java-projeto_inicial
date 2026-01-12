package br.com.alura.service;

import br.com.alura.client.ClientHttpConfiguration;
import br.com.alura.model.AbrigoModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class AbrigoService {
    private ClientHttpConfiguration client;

    public AbrigoService(ClientHttpConfiguration client){
        this.client = client;
    }

    public void listarAbrigo() throws IOException, InterruptedException {
        String uri = "http://localhost:8080/abrigos";
        HttpResponse<String> response = client.dispararRequisicaoGet(uri);
        String responseBody = response.body();
        AbrigoModel[] abrigos = new ObjectMapper().readValue(responseBody, AbrigoModel[].class);
        List<AbrigoModel> abrigosList = Arrays.stream(abrigos).toList();
        if (abrigosList.isEmpty()){
            System.out.println("Não há abrigos cadastrados");
        } else {
            mostraAbrigo(abrigosList);
        }
    }

    private void mostraAbrigo(List<AbrigoModel> abrigos){
        System.out.println("Abrigos cadastrados:");
        for (AbrigoModel abrigoModel : abrigos) {
            long id = abrigoModel.getId();
            String nome = abrigoModel.getNome();
            System.out.println(id + " - " + nome);
        }
    }

    public void cadastrarAbrigo() throws IOException, InterruptedException {
        System.out.println("Digite o nome do abrigo:");
        String nome = new Scanner(System.in).nextLine();
        System.out.println("Digite o telefone do abrigo:");
        String telefone = new Scanner(System.in).nextLine();
        System.out.println("Digite o email do abrigo:");
        String email = new Scanner(System.in).nextLine();

        AbrigoModel abrigo = new AbrigoModel(nome, telefone, email);

        String uri = "http://localhost:8080/abrigos";
        HttpResponse<String> response = client.dispararRequisicaoPost(uri, abrigo);
        int statusCode = response.statusCode();
        String responseBody = response.body();
        if (statusCode == 200) {
            System.out.println("Abrigo cadastrado com sucesso!");
            System.out.println(responseBody);
        } else if (statusCode == 400 || statusCode == 500) {
            System.out.println("Erro ao cadastrar o abrigo:");
            System.out.println(responseBody);
        }
    }
}
