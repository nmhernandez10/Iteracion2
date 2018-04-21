package vos;

import org.codehaus.jackson.annotate.JsonProperty;

public class CategoriaHabitacion 
{
	@JsonProperty(value = "id")
	private long id;
	
	@JsonProperty(value = "nombre")
	private String nombre;
	
	@JsonProperty(value = "descripcion")
	private String descripcion;
	
	public CategoriaHabitacion(@JsonProperty(value = "id") long id, 
			@JsonProperty(value = "nombre") String nombre,
			@JsonProperty(value = "descripcion") String descripcion)
	{
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
	}

	public long getId() 
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getNombre()
	{
		return nombre;
	}

	public void setNombre(String nombre) 
	{
		this.nombre = nombre;
	}

	public String getDescripcion()
	{
		return descripcion;
	}

	public void setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
	}	
}
