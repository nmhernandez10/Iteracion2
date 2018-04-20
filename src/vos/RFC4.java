package vos;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class RFC4 
{
	@JsonProperty(value = "fechaMenor")
	private String fechaMenor;
	
	private Date fechaMenorDate;
	
	@JsonProperty(value = "fechaMayor")
	private String fechaMayor;
	
	private Date fechaMayorDate;
	
	@JsonProperty(value = "servicios")
	private List<String> servicios;

	public RFC4(@JsonProperty(value = "fechaMenor") String fechaMenor, @JsonProperty(value = "fechaMayor") String fechaMayor, @JsonProperty(value = "servicios") List<String> servicios) {
		this.fechaMenor = fechaMenor;
		this.fechaMayor = fechaMayor;
		this.servicios = servicios;
		
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			this.fechaMenorDate = new Date(format.parse(this.fechaMenor).getTime());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			this.fechaMayorDate = new Date(format.parse(this.fechaMayor).getTime());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getFechaMenor() {
		return fechaMenor;
	}

	public String getFechaMayor() {
		return fechaMayor;
	}

	public List<String> getServicios() {
		return servicios;
	}
	
	
	
	//NO SE PUEDEN EDITAR LAS ATRIBUTOS CON LOS MÉTODOS 'SET' DADO QUE SÓLO SE USA INSTANCIAS
	//DE ESTA CLASE PARA SOLUCIONAR UN REQUERIMIENTO SEGÚN ALGO ENTRADO POR PARÁMETRO EN EL RECURSO
	//Y EL SERVICIO.
}
