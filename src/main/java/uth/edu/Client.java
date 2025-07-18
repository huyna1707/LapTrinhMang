package uth.edu;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {
        try {
            Socket socket = new Socket("localhost", 1234);
            System.out.println("Kết nối thành công đến server");
            socket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        };
    }
}
