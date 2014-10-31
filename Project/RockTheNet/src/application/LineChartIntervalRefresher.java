package application;

import javafx.scene.chart.XYChart;
import controller.RTNController;

public class LineChartIntervalRefresher implements Runnable {

	private int sleepTime;
	private int sec;
	private boolean byteSelected;
	private boolean kiloByteSelected;
	private boolean megaByteSelected;
	private RTNController rtnc;
		
	public LineChartIntervalRefresher(RTNController rtnc) {
		this.sleepTime = 1000;
		this.sec = 0;
		this.byteSelected = true;
		this.rtnc = rtnc;
	}

	/**
	 * 
	 */
	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(this.sleepTime);
				
				if (this.byteSelected) {
					// populating the series with data
					this.rtnc.getSeries().getData().remove(0);
					this.rtnc.getSeries().getData().add(new XYChart.Data(this.sec, 80));
				} else {
					if (this.kiloByteSelected) {
						this.rtnc.getSeries().getData().remove(0);
						this.rtnc.getSeries().getData().add(new XYChart.Data(this.sec, 30));
					} else {
						if (this.megaByteSelected) {
							this.rtnc.getSeries().getData().remove(0);
							this.rtnc.getSeries().getData().add(new XYChart.Data(this.sec, 40));
						}
					}
				}
				
				this.rtnc.getLineChart().getData().add(this.rtnc.getSeries());
				this.sec++;
			}
		} catch (Exception e) {
			System.err
					.println("Beim Setzen des Refresh-Intervalls fuer den LineChart ist ein Fehler aufgetreten!");
		}
	}

	/**
	 * 
	 * @return
	 */
	public int getSleepTime() {
		return sleepTime;
	}

	/**
	 * 
	 * @param sleepTime
	 */
	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}

	public int getSec() {
		return sec;
	}

	public void setSec(int sec) {
		this.sec = sec;
	}

	public boolean isByteSelected() {
		return byteSelected;
	}

	public void setByteSelected(boolean byteSelected) {
		this.byteSelected = byteSelected;
	}

	public boolean isKiloByteSelected() {
		return kiloByteSelected;
	}

	public void setKiloByteSelected(boolean kiloByteSelected) {
		this.kiloByteSelected = kiloByteSelected;
	}

	public boolean isMegaByteSelected() {
		return megaByteSelected;
	}

	public void setMegaByteSelected(boolean megaByteSelected) {
		this.megaByteSelected = megaByteSelected;
	}
}
