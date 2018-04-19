package vos;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Nicolás Mateo Hernández Rojas - nm.hernandez10@uniandes.edu.co
 * @author David Felipe Niño Romero - df.nino10@uniandes.edu.co Clase que
 *         representa a la lista de Clientes del modelo AlohAndes
 */

public class ListaClientes {
	@JsonProperty(value = "clientes")
	private List<Cliente> clientes;

	public ListaClientes(@JsonProperty(value = "clientes") List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public List<Cliente> getClientes() {
		return clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}
}
