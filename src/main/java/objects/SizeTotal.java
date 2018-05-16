package objects;

public class SizeTotal {

	private double emptySizeTotal;
	private double tinyTotal;
	private double smallTotal;
	private double mediumTotal;
	private double largeTotal;

	public SizeTotal() {
		setEmptySizeTotal(0);
		setTinyTotal(0);
		setSmallTotal(0);
		setMediumTotal(0);
		setLargeTotal(0);
	}

	public double getEmptySizeTotal() {
		return emptySizeTotal;
	}

	public void setEmptySizeTotal(double emptySizeTotal) {
		this.emptySizeTotal += emptySizeTotal;
	}

	public double getTinyTotal() {
		return tinyTotal;
	}

	public void setTinyTotal(double tinyTotal) {
		this.tinyTotal += tinyTotal;
	}

	public double getSmallTotal() {
		return smallTotal;
	}

	public void setSmallTotal(double smallTotal) {
		this.smallTotal += smallTotal;
	}

	public double getMediumTotal() {
		return mediumTotal;
	}

	public void setMediumTotal(double mediumTotal) {
		this.mediumTotal += mediumTotal;
	}

	public double getLargeTotal() {
		return largeTotal;
	}

	public void setLargeTotal(double largeTotal) {
		this.largeTotal += largeTotal;
	}
	
	public double getTotalSize(){
		return emptySizeTotal + tinyTotal + smallTotal + mediumTotal + largeTotal;
	}

}
