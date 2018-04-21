package vos;

public class RFC8 {
	private Cliente cliente;
	private int diasAlojamiento;
	private int reservas;
	
	public RFC8(Cliente cliente, int diasAlojamiento, int reservas) {
		this.cliente = cliente;
		this.diasAlojamiento = diasAlojamiento;
		this.reservas = reservas;
	}
	
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	public int getDiasAlojamiento() {
		return diasAlojamiento;
	}
	public void setDiasAlojamiento(int diasAlojamiento) {
		this.diasAlojamiento = diasAlojamiento;
	}
	public int getReservas() {
		return reservas;
	}
	public void setReservas(int reservas) {
		this.reservas = reservas;
	}
}
