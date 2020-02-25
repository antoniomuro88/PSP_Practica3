package es.studium;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Font;

public class Servidor extends JFrame implements ActionListener {
	/**
	 *
	 */
	// Crea una id Espec�fica con la que interpreta el problema. Al cambiar de id
	// todos los datos modificados estar�an vac�os
	private static final long serialVersionUID = 1L;
	static ServerSocket servidor;
	static final int PUERTO = 44444;
	static int CONEXIONES = 0;
	static int ACTUALES = 0;
	static int MAXIMO = 10;
	static int random;
	static JTextField mensaje = new JTextField("");
	static JTextField mensaje2 = new JTextField("");
	private JScrollPane scrollpane1;
	static JTextArea textarea;
	JButton salir = new JButton("Salir");
	static Socket[] tabla = new Socket[100];

	public Servidor() {
		super(" SERVIDOR ");
		setSize(1920, 1080);
		setBackground(new Color(255, 0, 0));
		setFont(new Font("Calibri", Font.PLAIN, 12));
		setForeground(new Color(255, 0, 0));
		getContentPane().setBackground(new Color(51, 51, 51));
		setTitle("SERVIDOR");
		getContentPane().setLayout(null);
		mensaje.setBounds(10, 10, 400, 30);
		getContentPane().add(mensaje);
		mensaje.setEditable(false);
		mensaje2.setFont(new Font("Tahoma", Font.BOLD, 12));
		mensaje2.setForeground(new Color(255, 255, 255));
		mensaje2.setBackground(new Color(51, 51, 51));
		mensaje2.setBounds(10, 350, 400, 30);
		getContentPane().add(mensaje2);
		mensaje2.setEditable(false);
		textarea = new JTextArea();
		textarea.setEditable(false);
		scrollpane1 = new JScrollPane(textarea);
		scrollpane1.setBounds(10, 50, 400, 300);
		getContentPane().add(scrollpane1);
		salir.setBounds(420, 10, 100, 30);
		getContentPane().add(salir);
		salir.addActionListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == salir) {
			try {
				servidor.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			System.exit(0);
		}
	}

	public static void main(String args[]) throws Exception {
		servidor = new ServerSocket(PUERTO);
		random = (int) Math.floor(Math.random() * 100 + 1);
		System.out.println("El n�mero secreto del servidor es: " + random);
		System.out.println("Servidor iniciado...");
		Servidor pantalla = new Servidor();
		pantalla.setBounds(0, 0, 545, 430);
		pantalla.setVisible(true);
		mensaje.setText("N�mero de conexiones actuales: " + 0 + " de " + MAXIMO);
		
		// Mientras que las conexiones sean menores al m�ximo, se seguir� permitiendo
		// dejar entrar a los diferentes usuarios
		while (CONEXIONES < MAXIMO) {
			Socket socket;
			try {
				socket = servidor.accept();
			} catch (SocketException setx) {
				// sale por aqu� si pulsamos el bot�n salir
				break;
			}
			// Crear una tabla en la que puedan entrar todas las conexiones que han entrado
			// en nuestro servidor
			tabla[CONEXIONES] = socket;
			// Se a�aden conexiones a los contadores
			CONEXIONES++;
			ACTUALES++;
			// Iniciaremos el hilo que realizar� el juego
			Hilo hilo = new Hilo(socket);
			hilo.start();
		}
		if (!servidor.isClosed()) {
			try {
				mensaje2.setForeground(Color.red);
				mensaje2.setText("Se ha alcanzado el n�mero m�ximo de conexiones: " + CONEXIONES + " de " + MAXIMO);
				servidor.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else {
			System.out.println("Servidor finalizado...");
		}
	}
}