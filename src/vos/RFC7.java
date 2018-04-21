package vos;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

public class RFC7 
{
	/**
	 * Tipo de alojamiento (tipo de espacio) 
	 */
	@JsonProperty(value = "categoria")
	private String categoria;
	
	/**
	 * Unidad de tiempo en días. Para representar semana, se usa 7. Para representar mes, se usa 30 o 31. 
	 */
	private int timeUnit;
	
	/**
	 * La fecha desde la cual la demanda es la mayor. 
	 */
	private Date inicioFechaMayorDemanda;
	
	/**
	 * La fecha hasta la cual la demanda es la mayor. 
	 */
	private Date finFechaMayorDemanda;
	
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
	
	
	
	//NO SE PUEDEN EDITAR LAS ATRIBUTOS CON LOS MÉTODOS 'SET' DADO QUE SÓLO SE USA INSTANCIAS
	//DE ESTA CLASE PARA SOLUCIONAR UN REQUERIMIENTO SEGÚN ALGO ENTRADO POR PARÁMETRO EN EL RECURSO
	//Y EL SERVICIO.
}