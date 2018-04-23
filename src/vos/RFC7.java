package vos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class RFC7 
{
	@JsonProperty(value = "categoria")
	private String categoria;

	@JsonProperty(value = "timeUnit")
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
	
	//NO SE PUEDEN EDITAR LAS ATRIBUTOS CON LOS MÉTODOS 'SET' DADO QUE SÓLO SE USA INSTANCIAS
	//DE ESTA CLASE PARA SOLUCIONAR UN REQUERIMIENTO SEGÚN ALGO ENTRADO POR PARÁMETRO EN EL RECURSO
	//Y EL SERVICIO.
}