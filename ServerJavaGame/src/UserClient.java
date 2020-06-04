import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class UserClient extends JFrame{
    private int height;
    private int width;
    private int userId;

    private ClientSideConnection csc;

    private JPanel panel1;
    private JTextArea chatWindow;
    private JTextField userTextField;
    private JButton sendButton;


    public UserClient(int width, int height){
        this.width=width;
        this.height=height;

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                csc.sendData("User#"+userId+": "+userTextField.getText());
                userTextField.setText("");
            }
        });
    }

    public void setUpGUI(){
        setSize(width,height);
        setTitle("User #"+userId);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatWindow.setEditable(false);
        sendButton.setText("send");
        setContentPane(panel1);


        setVisible(true);

    }

    public void connectToServer(){
        csc=new ClientSideConnection();
        Thread t=new Thread(csc);
        t.start();
    }

    private class ClientSideConnection implements Runnable{
        private Socket socket;
        private ObjectInputStream dataIn;
        private ObjectOutputStream dataOut;

        public ClientSideConnection(){
            System.out.println("----client----");
            try {
                socket=new Socket("localhost", 51734);
                dataOut=new ObjectOutputStream(socket.getOutputStream());
                dataIn=new ObjectInputStream(socket.getInputStream());
                userId=dataIn.readInt();
                System.out.println("connected to server as User# "+userId);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void sendData(Serializable data){
            try {
                dataOut.writeObject(data);
                dataOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    chatWindow.append((String) dataIn.readObject()+"\n");
                }
            } catch (IOException |ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args){
        UserClient u=new UserClient(500,400);
        u.connectToServer();
        u.setUpGUI();
    }
}