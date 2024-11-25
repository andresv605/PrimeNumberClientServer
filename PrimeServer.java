package SoftwareDeployment;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PrimeServer {
  private static final int PORT = 12345;
  private JTextArea logArea;

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new PrimeServer().startServer());
  }

  public void startServer() {
    JFrame frame = new JFrame("Server");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(500, 300);

    logArea = new JTextArea();
    logArea.setEditable(false);
    logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

    JScrollPane scrollPane = new JScrollPane(logArea);
    frame.add(scrollPane, BorderLayout.CENTER);

    LocalDateTime startTime = LocalDateTime.now();
    String startMessage = "Server started at " + startTime.format(DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy"));
    appendLog(startMessage);

    frame.setVisible(true);
    
    new Thread(() -> {
      try (ServerSocket serverSocket = new ServerSocket(PORT)) {
        while (true) {
          Socket clientSocket = serverSocket.accept();
          new Thread(() -> handleClient(clientSocket)).start();
        }
      } catch (IOException e) {
        appendLog("Error starting server: " + e.getMessage());
      }
    }).start();
  }

  private void handleClient(Socket clientSocket) {
    try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
    ) {
      String input;
      while ((input = in.readLine()) != null) {
        appendLog("Number received from client: " + input);
        int number = Integer.parseInt(input);
        String response = isPrime(number) ? "The number is a prime number." : "The number is NOT a prime number.";
        out.println(response);
      }
    } catch (IOException | NumberFormatException e) {
      appendLog("Error handling client: " + e.getMessage());
    }
  }

  private boolean isPrime(int num) {
    if (num <= 1) return false;
    for (int i = 2; i <= Math.sqrt(num); i++) {
      if (num % i == 0) return false;
    }
    return true;
  }

  private void appendLog(String message) {
    SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
  }
}