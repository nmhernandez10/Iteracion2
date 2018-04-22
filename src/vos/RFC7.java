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
	 * Unidad de tiempo en días. Para representar semana, se usa 7. Para representar mes, se usa 30 o 31. 
	 */
	private int timeUnit;
	
	/**
	 * La lista de fechas a retornar. 
	 */
	private List<Date> fechasARetornar;
	
	public RFC7(@JsonProperty(value = "categoria") String categoria, @JsonProperty(value = "timeUnit") int timeUnit) {
		this.categoria = categoria;
		this.timeUnit = timeUnit;
		this.setFechasARetornar(new ArrayList<Date>());
	}

	public String getcategoria() {
		return categoria;
	}

	public int gettimeUnit() {
		return timeUnit;
	}

	/**
	 * @return the fechasARetornar
	 */
	public List<Date> getFechasARetornar() {
		return fechasARetornar;
	}

	/**
	 * @param fechasARetornar the fechasARetornar to set
	 */
	public void setFechasARetornar(List<Date> fechasARetornar) {
		this.fechasARetornar = fechasARetornar;
	}
	
	
	
	//NO SE PUEDEN EDITAR LAS ATRIBUTOS CON LOS MÉTODOS 'SET' DADO QUE SÓLO SE USA INSTANCIAS
	//DE ESTA CLASE PARA SOLUCIONAR UN REQUERIMIENTO SEGÚN ALGO ENTRADO POR PARÁMETRO EN EL RECURSO
	//Y EL SERVICIO.
}