package vos;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class ListaRFC3 {
	@JsonProperty(value = "elementos")
	private List<RFC3> elementos;

	public ListaRFC3(@JsonProperty(value = "elementos") List<RFC3> elementos) {
		this.elementos = elementos;
	}

	public List<RFC3> getElementos() {
		return elementos;
	}

	public void setElementos(List<RFC3> elementos) {
		this.elementos = elementos;
	}
}
