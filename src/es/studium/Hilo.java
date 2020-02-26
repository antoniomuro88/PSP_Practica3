package es.studium;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Hilo extends Thread {
	DataInputStream fentrada;
	Socket socket;

	public Hilo(Socket socket) {
		this.socket = socket;
		try {
			fentrada = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			System.out.println("Error de E/S");
			e.printStackTrace();
		}
	}

	public synchronized void run() {
		Servidor.mensaje.setText("N�mero de conexiones actuales: " + Servidor.ACTUALES);
		String texto = Servidor.textarea.getText();
		EnviarMensajes(texto);
		while (true) {
			String cadena = "";
			try {
				cadena = fentrada.readUTF();
				if (cadena.trim().equals("*")) {
					Servidor.ACTUALES--;
					Servidor.mensaje.setText("N�mero de conexiones actuales: " + Servidor.ACTUALES);
					break;
				} else {

					if (cadena.contains("...")) {
						Servidor.textarea.append(cadena + "\n");
					} else {
						String[] parts = cadena.split("> ");
						String part = parts[0];
						String part1 = parts[1];
						// Si el n�mero ingresado es menor que que el n�mero aleatorio
						if (Integer.parseInt(part1) < Servidor.random) {
							Servidor.textarea.append(
									" "+part + ">" + " ha elegido el n�mero " + part1 + ", pero el n�mero es MAYOR \n");
							// Si el n�mero ingresado es mayor que el n�mero aleatorio
						} else if (Integer.parseInt(part1) > Servidor.random) {
							Servidor.textarea.append(
									" "+part + ">" + " ha elegido el n�mero " + part1 + ", pero el n�mero es MENOR \n");
							// Si el n�mero ingresado es igual al n�mero aleatorio
						} else if (Integer.parseInt(part1) == Servidor.random) {
							// TENEMOS ACERTANTE
							Servidor.textarea
									.append(" "+part + "> " + "ha elegido el n�mero " + part1 + " y �HA ACERTADO! \n\n"+
											"EL N�MERO SECRETO ERA "+part1+"\n"
											+ " **��HURRA POR "+part.toUpperCase()+"!!**\n"
											+ "\n--La aplicaci�n se cerrar� en 10 segundos--\n" );
							texto = Servidor.textarea.getText();
							EnviarMensajes(texto);
							Thread.sleep(10000);
							System.exit(0);
						}
					}
					texto = Servidor.textarea.getText();
					EnviarMensajes(texto);
				}
			} catch (Exception ex) {
				try {
					fentrada.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				ex.printStackTrace();
				break;
			}
		}
	}

	private void EnviarMensajes(String texto) {
		for (int i = 0; i < Servidor.CONEXIONES; i++) {
			Socket socket = Servidor.tabla[i];
			try {
				DataOutputStream fsalida = new DataOutputStream(socket.getOutputStream());
				fsalida.writeUTF(texto);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}