package vos;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class ListaRFC9 {
	@JsonProperty(value = "elementos")
	private List<RFC9> elementos;

	public ListaRFC9(@JsonProperty(value = "elementos") List<RFC9> elementos) {
		this.elementos = elementos;
	}

	public List<RFC9> getElementos() {
		return elementos;
	}

	public void setElementos(List<RFC9> elementos) {
		this.elementos = elementos;
	}
}
