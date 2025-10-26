package Desafio3;

import java.io.*;
import java.net.*;

class TCPClient {
    public static void main(String argv[]) throws Exception {
        System.out.println("Cliente TCP iniciado...");
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        Socket clientSocket = new Socket("localhost", 10722);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        while (true) {
            System.out.println("\n===== MENU =====");
            System.out.println("1 - Enviar mensagem");
            System.out.println("2 - Enviar arquivo");
            System.out.println("3 - Sair");
            System.out.print("Escolha: ");
            String opcao = inFromUser.readLine();

            if (opcao.equals("1")) {
                System.out.print("Cliente: ");
                String sentence = inFromUser.readLine();
                outToServer.writeBytes(sentence + '\n');

                if (sentence.equalsIgnoreCase("QUIT")) {
                    System.out.println("Cliente encerrou a conexão.");
                    break;
                }

                String resposta = inFromServer.readLine();
                if (resposta == null) break;
                System.out.println("Servidor: " + resposta);

            } else if (opcao.equals("2")) {
                System.out.print("Digite o caminho do arquivo: ");
                String caminho = inFromUser.readLine();
                File arquivo = new File(caminho);

                if (!arquivo.exists()) {
                    System.out.println("Arquivo não encontrado!");
                    continue;
                }

                outToServer.writeBytes("FILE\n");
                outToServer.writeBytes(arquivo.getName() + "\n");
                outToServer.writeBytes(arquivo.length() + "\n");

                FileInputStream fileIn = new FileInputStream(arquivo);
                OutputStream out = clientSocket.getOutputStream();

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileIn.read(buffer)) > 0) {
                    out.write(buffer, 0, bytesRead);
                }
                fileIn.close();

                System.out.println("Arquivo enviado com sucesso!");
                System.out.println("Servidor: " + inFromServer.readLine());

            } else if (opcao.equals("3")) {
                outToServer.writeBytes("QUIT\n");
                System.out.println("Cliente encerrou a conexão.");
                break;
            } else {
                System.out.println("Opção inválida!");
            }
        }

        clientSocket.close();
    }
}