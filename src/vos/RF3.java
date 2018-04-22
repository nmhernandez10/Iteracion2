package vos;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class RF3 {
	@JsonProperty(value = "id")
	private long id;

	@JsonProperty(value = "documento")
	private long documento;

	@JsonProperty(value = "nombre")
	private String nombre;

	@JsonProperty(value = "edad")
	private int edad;

	@JsonProperty(value = "direccion")
	private String direccion;

	@JsonProperty(value = "reservas")
	private List<Long> reservas;

	@JsonProperty(value = "vinculo")
	private String vinculo;

	public RF3(@JsonProperty(value = "id") long id, @JsonProperty(value = "documento") long documento,
			@JsonProperty(value = "nombre") String nombre, @JsonProperty(value = "edad") int edad,
			@JsonProperty(value = "direccion") String direccion, @JsonProperty(value = "vinculo") String vinculo) 
	{
		this.id = id;
		this.documento = documento;
		this.nombre = nombre;
		this.edad = edad;
		this.direccion = direccion;
		this.vinculo = vinculo;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getIdentificacion() {
		return documento;
	}

	public void setIdentificacion(long documento) {
		this.documento = documento;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getEdad() {
		return edad;
	}

	public void setEdad(int edad) {
		this.edad = edad;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getVinculo() {
		return vinculo;
	}

	public void setVinculo(String vinculo) {
		this.vinculo = vinculo;
	}
}
