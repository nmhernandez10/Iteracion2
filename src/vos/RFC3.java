package vos;

public class RFC3 {
	private long idOperador;

	private double indiceOcupacion;

	public RFC3(long idOperador, double indiceOcupacion){		
		this.idOperador = idOperador;
		this.indiceOcupacion = indiceOcupacion;
	}

	public long getIdOperador() {
		return idOperador;
	}

	public void setIdOperador(long idEspacio) {
		this.idOperador = idEspacio;
	}

	public double getIndiceOcupacion() {
		return indiceOcupacion;
	}

	public void setIndiceOcupacion(double indiceOcupacion) {
		this.indiceOcupacion = indiceOcupacion;
	}
}
