package SoftwareDeployment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class PrimeClient {
  private static final String SERVER_HOST = "localhost";
  private static final int SERVER_PORT = 12345;

  private JFrame frame;
  private JTextField inputField;
  private JTextArea outputArea;

  public PrimeClient() {
    setupGUI();
  }

  private void setupGUI() {
    frame = new JFrame("Client");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(500, 300);


    JPanel panel = new JPanel(new BorderLayout());
    inputField = new JTextField();
    JButton sendButton = new JButton("Check Prime");
    outputArea = new JTextArea();
    outputArea.setEditable(false);


    panel.add(new JLabel("Enter a number to evaluate:"), BorderLayout.NORTH);
    panel.add(inputField, BorderLayout.CENTER);
    panel.add(sendButton, BorderLayout.EAST);
    frame.add(panel, BorderLayout.NORTH);
    frame.add(new JScrollPane(outputArea), BorderLayout.CENTER);


    sendButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String input = inputField.getText();
        if (input.isEmpty()) {
          JOptionPane.showMessageDialog(frame, "Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
          return;
        }
        try {
          int number = Integer.parseInt(input);
          sendNumberToServer(number);
        } catch (NumberFormatException ex) {
          JOptionPane.showMessageDialog(frame, "Invalid number format. Please enter an integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    frame.setVisible(true);
  }

  private void sendNumberToServer(int number) {
    try (
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
    ) {
      out.println(number);
      String response = in.readLine();
      outputArea.append("The number is " + number + "\n");
      outputArea.append("The response from the server is: " + response + "\n");
    } catch (IOException e) {
      JOptionPane.showMessageDialog(frame, "Unable to connect to server. Please try again.", "Connection Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(PrimeClient::new);
  }
}