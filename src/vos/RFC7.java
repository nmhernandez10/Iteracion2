package vos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class RFC7 
{
	/**
	 * Tipo de alojamiento (tipo de espacio) 
	 */
	@JsonProperty(value = "categoria")
	private String categoria;
	
	/**
	 * Unidad de tiempo en d�as. Para representar semana, se usa 7. Para representar mes, se usa 30 o 31. 
	 */
	private String timeUnit;
	
	public RFC7(@JsonProperty(value = "categoria") String categoria, @JsonProperty(value = "timeUnit") String timeUnit) {
		this.categoria = categoria;
		this.timeUnit = timeUnit;
	}

	public String getCategoria() {
		return categoria;
	}

	public String getTimeUnit() {
		return timeUnit;
	}
	
	//NO SE PUEDEN EDITAR LAS ATRIBUTOS CON LOS M�TODOS 'SET' DADO QUE S�LO SE USA INSTANCIAS
	//DE ESTA CLASE PARA SOLUCIONAR UN REQUERIMIENTO SEG�N ALGO ENTRADO POR PAR�METRO EN EL RECURSO
	//Y EL SERVICIO.
}