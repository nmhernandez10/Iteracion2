package vos;

import org.codehaus.jackson.annotate.JsonProperty;

public class Vinculo 
{
	@JsonProperty(value = "id")
	private long id;
	
	@JsonProperty(value = "nombre")
	private String vinculo;
	
	@JsonProperty(value = "descripcion")
	private String descripcion;
	
	public Vinculo(@JsonProperty(value = "id") long id, 
			@JsonProperty(value = "nombre") String vinculo,
			@JsonProperty(value = "descripcion") String descripcion)
	{
		this.id = id;
		this.vinculo = vinculo;
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

	public String getVinculo()
	{
		return vinculo;
	}

	public void setVinculo(String vinculo) 
	{
		this.vinculo = vinculo;
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
