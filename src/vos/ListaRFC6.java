package vos;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class ListaRFC6 {
	@JsonProperty(value = "elementos")
	private List<RFC6> elementos;

	public ListaRFC6(@JsonProperty(value = "elementos") List<RFC6> elementos) {
		this.elementos = elementos;
	}

	public List<RFC6> getElementos() {
		return elementos;
	}

	public void setElementos(List<RFC6> elementos) {
		this.elementos = elementos;
	}
}