package vos;

import java.util.List;

public class RFC6 
{	
	private long idUsuario;
	
	private String tipo;
	
	private int totalDiasAlojamiento;
	
	private double totalDinero;
	
	private List<String> caracteristicas;

	public RFC6(long idUsuario, String tipo, int totalDiasAlojamiento, double totalDinero, List<String> caracteristicas) {
		this.idUsuario = idUsuario;
		this.tipo = tipo;
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

	public long getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(long idUsuario) {
		this.idUsuario = idUsuario;
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
