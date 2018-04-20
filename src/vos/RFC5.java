package vos;

import java.util.List;

public class RFC5 
{
	private String tipo;
	
	private String categoria;
	
	private int totalDiasAlojamiento;
	
	private double totalDinero;
	
	private List<String> caracteristicas;

	public RFC5(String tipo, String categoria, int totalDiasAlojamiento, double totalDinero, List<String> caracteristicas) {
		this.tipo = tipo;
		this.categoria = categoria;
		this.totalDiasAlojamiento = totalDiasAlojamiento;
		this.totalDinero = totalDinero;
		this.caracteristicas = caracteristicas;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public int getTotalDiasAlojamiento() {
		return totalDiasAlojamiento;
	}

	public void setTotalDiasAlojamiento(int totalDiasAlojamiento) {
		this.totalDiasAlojamiento = totalDiasAlojamiento;
	}

	public double getTotalDinero() {
		return totalDinero;
	}

	public void setTotalDinero(double totalDinero) {
		this.totalDinero = totalDinero;
	}

	public List<String> getCaracteristicas() {
		return caracteristicas;
	}

	public void setCaracteristicas(List<String> caracteristicas) {
		this.caracteristicas = caracteristicas;
	}
}
