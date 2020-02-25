package es.studium;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;

public class Cliente extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	Socket socket;
	DataInputStream fentrada;
	DataOutputStream fsalida;
	String nombre;
	static JTextField mensaje = new JTextField();
	private JScrollPane scrollpane;
	static JTextArea textarea;
	JButton boton = new JButton("Enviar");
	JButton desconectar = new JButton("Salir");
	boolean repetir = true;
	private JLabel lblInfo = null;

	public Cliente(Socket socket, String nombre) {
		super(" Cliente: " + nombre);
		getContentPane().setBackground(new Color(51, 51, 51));
		mensaje.setHorizontalAlignment(SwingConstants.CENTER);
		mensaje.setBounds(236, 11, 174, 30);

		mensaje.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String texto = nombre + "> " + mensaje.getText();
				try {
					deshabilitar();
					mensaje.setText("");
					fsalida.writeUTF(texto);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		getContentPane().setLayout(null);
		getContentPane().add(mensaje);
		textarea = new JTextArea();
		scrollpane = new JScrollPane(textarea);
		scrollpane.setBounds(10, 50, 400, 300);
		getContentPane().add(scrollpane);
		boton.setFont(new Font("Tahoma", Font.BOLD, 11));
		boton.setBounds(420, 10, 100, 30);
		getContentPane().add(boton);
		desconectar.setBounds(420, 50, 100, 30);
		getContentPane().add(desconectar);
		textarea.setEditable(false);
	
		lblInfo = new JLabel("Introduzca un n\u00FAmero del 1 al 100: ");
		lblInfo.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblInfo.setForeground(new Color(255, 255, 255));
		lblInfo.setBounds(10, 19, 229, 22);
		getContentPane().add(lblInfo);
		boton.addActionListener(this);
		desconectar.addActionListener(this);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.socket = socket;
		this.nombre = nombre;
		try {
			fentrada = new DataInputStream(socket.getInputStream());
			fsalida = new DataOutputStream(socket.getOutputStream());
			String texto = "> Entra en el chat... " + nombre + "\n";
			fsalida.writeUTF(texto);

		} catch (IOException ex) {
			System.out.println("Error de E/S");
			ex.printStackTrace();
			System.exit(0);
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == boton) {
			String texto = nombre + "> " + mensaje.getText();
			deshabilitar();
			mensaje.setText("");
			try {
				fsalida.writeUTF(texto);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == desconectar) {
			String texto = " > Abandona el chat... " + nombre;
			try {
				fsalida.writeUTF(texto);
				fsalida.writeUTF("*");
				repetir = false;
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void ejecutar() {
		String texto = "";
		while (repetir) {
			try {
				texto = fentrada.readUTF();
				textarea.setText(texto);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(null, "Imposible conectar con el servidor \n" + ex.getMessage(),
						"El Servidor se ha desconectado", JOptionPane.ERROR_MESSAGE);
				repetir = false;
			}
		}
		try {
			socket.close();
			System.exit(0);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	void deshabilitar() {
	
		try {
			
			TimeUnit.SECONDS.sleep(3);

		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) throws Exception {
		int puerto = 44444;
		String nombre = JOptionPane.showInputDialog("Introduce tu nombre o nick:");
		Socket socket = null;
		try {
			socket = new Socket("127.0.0.1", puerto);
		} catch (IOException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Imposible conectar con el servidor. \nEs posible que "
							+ "el servidor no esté activo o que esté lleno.\n" + ex.getMessage(),
							"El servidor no está activo", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		if (!nombre.trim().equals("")) {
			Cliente cliente = new Cliente(socket, nombre);
			cliente.setBounds(0, 0, 540, 400);
			cliente.setVisible(true);
			cliente.ejecutar();
		} else {
			System.out.println("El nombre está vacío...");
		}
	}
}