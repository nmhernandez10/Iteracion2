package vos;

import org.codehaus.jackson.annotate.JsonProperty;

public class CategoriaServicio 
{
	@JsonProperty(value = "id")
	private long id;
	
	@JsonProperty(value = "nombre")
	private String categoria;
	
	@JsonProperty(value = "descripcion")
	private String descripcion;
	
	public CategoriaServicio(@JsonProperty(value = "id") long id, 
			@JsonProperty(value = "nombre") String categoria,
			@JsonProperty(value = "descripcion") String descripcion)
	{
		this.id = id;
		this.categoria = categoria;
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

	public String getCategoria()
	{
		return categoria;
	}

	public void setCategoria(String categoria) 
	{
		this.categoria = categoria;
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
