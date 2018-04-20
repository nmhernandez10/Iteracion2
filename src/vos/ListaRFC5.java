package vos;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class ListaRFC5 {
	@JsonProperty(value = "elementos")
	private List<RFC5> elementos;

	public ListaRFC5(@JsonProperty(value = "elementos") List<RFC5> elementos) {
		this.elementos = elementos;
	}

	public List<RFC5> getElementos() {
		return elementos;
	}

	public void setElementos(List<RFC5> elementos) {
		this.elementos = elementos;
	}
}