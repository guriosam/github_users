package objects;

public class NatureTotal {

	private double emptyNatureTotal;
	private double managementTotal;
	private double reengineeringTotal;
	private double correctiveEngineeringTotal;
	private double forwardEngineeringTotal;
	private double uncategorizedTotal;

	public NatureTotal() {
		emptyNatureTotal = 0;
		managementTotal = 0;
		reengineeringTotal = 0;
		correctiveEngineeringTotal = 0;
		forwardEngineeringTotal = 0;
		uncategorizedTotal = 0;
	}

	public double getEmptyNatureTotal() {
		return emptyNatureTotal;
	}

	public void setEmptyNatureTotal(double emptyNatureTotal) {
		this.emptyNatureTotal += emptyNatureTotal;
	}

	public double getManagementTotal() {
		return managementTotal;
	}

	public void setManagementTotal(double managementTotal) {
		this.managementTotal += managementTotal;
	}

	public double getReengineeringTotal() {
		return reengineeringTotal;
	}

	public void setReengineeringTotal(double reengineeringTotal) {
		this.reengineeringTotal += reengineeringTotal;
	}

	public double getCorrectiveEngineeringTotal() {
		return correctiveEngineeringTotal;
	}

	public void setCorrectiveEngineeringTotal(double correctiveEngineeringTotal) {
		this.correctiveEngineeringTotal += correctiveEngineeringTotal;
	}

	public double getForwardEngineeringTotal() {
		return forwardEngineeringTotal;
	}

	public void setForwardEngineeringTotal(double forwardEngineeringTotal) {
		this.forwardEngineeringTotal += forwardEngineeringTotal;
	}

	public double getUncategorizedTotal() {
		return uncategorizedTotal;
	}

	public void setUncategorizedTotal(double uncategorizedTotal) {
		this.uncategorizedTotal += uncategorizedTotal;
	}

	public double getTotalNature(){
		return emptyNatureTotal + forwardEngineeringTotal + correctiveEngineeringTotal
				+ reengineeringTotal + uncategorizedTotal + managementTotal;
	}
	
}
