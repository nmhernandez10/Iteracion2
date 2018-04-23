package vos;

import org.codehaus.jackson.annotate.JsonProperty;

public class RF9 
{
	@JsonProperty("idEspacio")
	private long idEspacio;
	
	@JsonProperty("fechaDeshabilitacion")
	private String fechaDeshabilitacion;
	
	public RF9(@JsonProperty("idEspacio") long idEspacio, @JsonProperty("fechaDeshabilitacion") String fechaDeshabilitacion)
	{
		this.idEspacio = idEspacio;
		this.fechaDeshabilitacion = fechaDeshabilitacion;
	}
	
	public long getIdEspacio() {
		return idEspacio;
	}

	public void setIdEspacio(long idEspacio) {
		this.idEspacio = idEspacio;
	}

	public String getFechaDeshabilitacion() {
		return fechaDeshabilitacion;
	}

	public void setFechaDeshabilitacion(String fechaDeshabilitacion) {
		this.fechaDeshabilitacion = fechaDeshabilitacion;
	}
}

