package vos;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class ListaRFC8 {
	@JsonProperty(value = "elementos")
	private List<RFC8> elementos;

	public ListaRFC8(@JsonProperty(value = "elementos") List<RFC8> elementos) {
		this.elementos = elementos;
	}

	public List<RFC8> getElementos() {
		return elementos;
	}

	public void setElementos(List<RFC8> elementos) {
		this.elementos = elementos;
	}
}
