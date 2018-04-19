package vos;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class ListaRFC1 {
	@JsonProperty(value = "elementos")
	private List<RFC1> elementos;

	public ListaRFC1(@JsonProperty(value = "elementos") List<RFC1> elementos) {
		this.elementos = elementos;
	}

	public List<RFC1> getElementos() {
		return elementos;
	}

	public void setElementos(List<RFC1> elementos) {
		this.elementos = elementos;
	}
}
