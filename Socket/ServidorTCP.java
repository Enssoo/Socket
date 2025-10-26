package Desafio3;

import java.io.*;
import java.net.*;

class TCPServer {
    public static void main(String argv[]) throws Exception {
        ServerSocket welcomeSocket = new ServerSocket(10722);
        System.out.println("Servidor TCP iniciado na porta 10722...");

        Socket connectionSocket = welcomeSocket.accept();
        System.out.println("Cliente conectado: " + connectionSocket.getRemoteSocketAddress());

        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(System.in));

        String clientMessage;

        while (true) {
            clientMessage = inFromClient.readLine();
            if (clientMessage == null) break;

            if (clientMessage.equalsIgnoreCase("QUIT")) {
                System.out.println("Cliente encerrou a conexão.");
                break;
            }

            if (clientMessage.equalsIgnoreCase("FILE")) {
                String fileName = inFromClient.readLine();
                long fileSize = Long.parseLong(inFromClient.readLine());

                FileOutputStream fileOut = new FileOutputStream("recebido_" + fileName);
                InputStream in = connectionSocket.getInputStream();

                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalRead = 0;

                System.out.println("Recebendo arquivo '" + fileName + "' (" + fileSize + " bytes)...");

                while (totalRead < fileSize &&
                       (bytesRead = in.read(buffer, 0, (int)Math.min(buffer.length, fileSize - totalRead))) > 0) {
                    fileOut.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                }

                fileOut.close();
                System.out.println("Arquivo '" + fileName + "' recebido com sucesso!");
                outToClient.writeBytes("Arquivo recebido com sucesso!\n");
                continue;
            }

            System.out.println("Cliente: " + clientMessage);
            System.out.print("Servidor: ");
            String serverMessage = inFromServer.readLine();
            outToClient.writeBytes(serverMessage + '\n');

            if (serverMessage.equalsIgnoreCase("QUIT")) {
                System.out.println("Servidor encerrou a conexão.");
                break;
            }
        }

        connectionSocket.close();
        welcomeSocket.close();
    }
}
