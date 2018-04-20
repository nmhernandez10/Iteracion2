package vos;

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
	private int timeUnit;
	

	public RFC7(@JsonProperty(value = "categoria") String categoria, @JsonProperty(value = "timeUnit") int timeUnit) {
		this.categoria = categoria;
		this.timeUnit = timeUnit;
	}

	public String getcategoria() {
		return categoria;
	}

	public int gettimeUnit() {
		return timeUnit;
	}
	
	
	
	//NO SE PUEDEN EDITAR LAS ATRIBUTOS CON LOS M�TODOS 'SET' DADO QUE S�LO SE USA INSTANCIAS
	//DE ESTA CLASE PARA SOLUCIONAR UN REQUERIMIENTO SEG�N ALGO ENTRADO POR PAR�METRO EN EL RECURSO
	//Y EL SERVICIO.
}