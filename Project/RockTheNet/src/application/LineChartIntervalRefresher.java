package application;

import controller.RTNController;

/**
 * 
 * @author Osman Oezsoy
 * @version 2014-10-30
 */
public class LineChartIntervalRefresher implements Runnable {

	private int sleepTime;
	private int sec;
	private RTNController rtnc;
	private int index;

	/**
	 * 
	 * @param rtnc
	 */
	public LineChartIntervalRefresher(RTNController rtnc) {
		this.sleepTime = 1000;
		this.sec = 0;
		this.index = 57;
		this.rtnc = rtnc;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(this.sleepTime);
				this.rtnc.refreshLineChart(this.sec, this.rtnc.getRead2()
						.getMonBytesSec(this.index));
				this.sec++;
			}
		} catch (Exception e) {
			System.err
					.println("Beim Setzen des Refresh-Intervalls fuer den LineChart ist ein Fehler aufgetreten!");
		}
	}

	public int getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}

	public int getSec() {
		return sec;
	}

	public void setSec(int sec) {
		this.sec = sec;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
