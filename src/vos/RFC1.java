package vos;

public class RFC1 {
	private long idOperador;

	private double recolectado;

	public RFC1(long idOperador, double recolectado) {
		this.idOperador = idOperador;
		this.recolectado = recolectado;
	}

	public long getIdOperador() {
		return idOperador;
	}

	public void setIdOperador(long idOperador) {
		this.idOperador = idOperador;
	}

	public double getRecolectado() {
		return recolectado;
	}

	public void setRecolectado(double recolectado) {
		this.recolectado = recolectado;
	}
}
