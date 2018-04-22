package vos;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class RF1 {
	@JsonProperty(value = "id")
	private long id;

	@JsonProperty(value = "categoria")
	private String categoria;

	@JsonProperty(value = "nombre")
	private String nombre;

	@JsonProperty(value = "registro")
	private long registro;

	@JsonProperty(value = "documento")
	private long documento;

	public RF1(@JsonProperty(value = "id") long id, @JsonProperty(value = "registro") long registro,
			@JsonProperty(value = "nombre") String nombre,
			@JsonProperty(value = "categoria") String categoria,
			@JsonProperty(value = "documento") long documento) {
		this.id = id;
		this.nombre = nombre;
		this.registro = registro;
		this.categoria = categoria;
		this.documento = documento;
	}

	/**
	 * @return the documento
	 */
	public long getDocumento() {
		return documento;
	}

	/**
	 * @param documento
	 *            the documento to set
	 */
	public void setDocumento(long documento) {
		this.documento = documento;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public long getRegistro() {
		return registro;
	}

	public void setRegistro(long registro) {
		this.registro = registro;
	}
}
