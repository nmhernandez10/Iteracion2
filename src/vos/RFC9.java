package vos;

public class RFC9 {
	private Espacio espacio;
	
	private int mayorPeriodoSinReservas;

	public RFC9(Espacio espacio, int mayorPeriodoSinReservas) {
		this.espacio = espacio;
		this.mayorPeriodoSinReservas = mayorPeriodoSinReservas;
	}

	public Espacio getEspacio() {
		return espacio;
	}

	public void setEspacio(Espacio espacio) {
		this.espacio = espacio;
	}

	public int getMayorPeriodoSinReservas() {
		return mayorPeriodoSinReservas;
	}

	public void setMayorPeriodoSinReservas(int mayorPeriodoSinReservas) {
		this.mayorPeriodoSinReservas = mayorPeriodoSinReservas;
	}	
}
