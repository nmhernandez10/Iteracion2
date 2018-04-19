package vos;

import java.util.List;

import org.codehaus.jackson.annotate.*;

/**
 * @author Nicolás Mateo Hernández Rojas - nm.hernandez10@uniandes.edu.co
 * @author David Felipe Niño Romero - df.nino10@uniandes.edu.co Clase que
 *         representa a los Operadores del modelo AlohAndes
 */

public class Operador {
	@JsonProperty(value = "id")
	private long id;

	@JsonProperty(value = "categoria")
	private CategoriaOperador categoria;

	@JsonProperty(value = "nombre")
	private String nombre;

	@JsonProperty(value = "registro")
	private long registro;

	@JsonProperty(value = "documento")
	private long documento;

	@JsonProperty(value = "espacios")
	private List<Long> espacios;

	public Operador(@JsonProperty(value = "id") long id, @JsonProperty(value = "registro") long registro,
			@JsonProperty(value = "nombre") String nombre,
			@JsonProperty(value = "categoria") CategoriaOperador categoria,
			@JsonProperty(value = "espacios") List<Long> espacios,
			@JsonProperty(value = "documento") long documento) {
		this.id = id;
		this.nombre = nombre;
		this.registro = registro;
		this.categoria = categoria;
		this.espacios = espacios;
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

	public CategoriaOperador getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoriaOperador categoria) {
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

	public List<Long> getEspacios() {
		return espacios;
	}

	public void setEspacios(List<Long> espacios) {
		this.espacios = espacios;
	}
}
