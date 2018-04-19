package vos;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Nicolás Mateo Hernández Rojas - nm.hernandez10@uniandes.edu.co
 * @author David Felipe Niño Romero - df.nino10@uniandes.edu.co Clase que
 *         representa a la lista de Operadores del modelo AlohAndes
 */

public class ListaOperadores {
	@JsonProperty(value = "operadores")
	private List<Operador> operadores;

	public ListaOperadores(@JsonProperty(value = "operadores") List<Operador> operadores) {
		this.operadores = operadores;
	}

	public List<Operador> getOperadores() {
		return operadores;
	}

	public void setOperadores(List<Operador> operadores) {
		this.operadores = operadores;
	}
}
