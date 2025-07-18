package uth.edu;

import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        System.out.println("Server đang lắng nghe");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client kết nối thành công");

            clientSocket.close();
        }
    }
}
