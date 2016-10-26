package main.gui.sub;

import main.ArcaneTower;
import main.logic.AT_PARAMS;
import main.session.Session;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.ColorManager;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.FontMaster.FONT;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.TimeMaster;
import main.system.graphics.MigMaster;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Timer;
import java.util.TimerTask;

public class SessionTimer extends G_Panel {

	private static final int CLEAR = 0;
	private static final int RUNNING = 1;
	private static final int PAUSED = 2;
	private Session session;
	long initialTime;
	long timePassed;
	String timeString;
	private long timeStarted;
	private long timeRemaining;
	private Timer timer;
	private long timePaused;
	int status = CLEAR;
	private Font font;

	public SessionTimer(Session session, long initialTimeInMinutes) {
		this.session = session;
		this.initialTime = initialTime * 60000;
		timer = new Timer();
		panelSize = new Dimension(200, 120);
		font = FontMaster.getFont(FONT.NYALA, 24, Font.PLAIN);
	}

	@Override
	public void paint(Graphics g) {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.black);
		g.setFont(font);
		int x = MigMaster.getCenteredTextPosition(timeString, font, getPanelWidth());
		int y = MigMaster.getCenteredTextPositionY(font, getPanelHeight());
		g.drawString(timeString, x, y);
		switch (status) {
			case RUNNING:
				g.setColor(ColorManager.BRONZE);
				g.drawRect(0, 0, getPanelWidth(), getPanelHeight());
				break;
			case PAUSED:
				g.setColor(ColorManager.DEEP_GRAY);
				g.drawRect(0, 0, getPanelWidth(), getPanelHeight());
				break;
		}
	}

	public void refresh() {
		if (timeStarted == 0)
			timeString = getTimeString(initialTime, ":", isShowSeconds());
		else {
			long time = TimeMaster.getTime();
			timePassed = time - timeStarted;
			timePaused = session.getIntParam(AT_PARAMS.TIME_TOTAL_PAUSED);
			timeRemaining = initialTime - timePassed + timePaused;
			timeString = getTimeString(timeRemaining, ":", isShowSeconds());

		}
		repaint();
	}

	public String getTimeString(long time, String delimiter, boolean secondsIncluded) {
		int HOUR_MILLIS = 3600000;
		int hours = (int) (time / HOUR_MILLIS);
		String string = StringMaster.getFormattedTimeString(hours, 2) + delimiter;
		int MINUTE_MILLIS = 60000;
		int minutes = (int) ((time % HOUR_MILLIS) / MINUTE_MILLIS);
		string += StringMaster.getFormattedTimeString(minutes, 2) + delimiter;
		if (secondsIncluded) {
			int SECOND_MILLIS = 1000;
			int seconds = (int) ((time % MINUTE_MILLIS) / SECOND_MILLIS);
			string += StringMaster.getFormattedTimeString(seconds, 2) + delimiter;

		}
		return string.substring(0, string.length() - 1);

	}

	public void reset() {
		// make ready to start() again
		// initialTime = initialTimeOriginal;
	}

	public void resume() {
		if (status == RUNNING)
			return;
		initialTime = timeRemaining;
		start();
	}

	public void pause() {
		if (status == PAUSED)
			return;
		// timeStarted change?
		timer.cancel();
		refresh();
		status = PAUSED;
	}

	public void start() {
		if (status == RUNNING)
			return;
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				refresh();

			}
		};
		if (timeStarted == 0)
			timeStarted = TimeMaster.getTime();
		timer = new Timer();
		timer.schedule(task, 0, getDelay());

		status = RUNNING;
	}

	private long getDelay() {
		return isShowSeconds() ? 1000 : 60000;
	}

	private long getSoundAlertDelay() {
		return isShowSeconds() ? 10000 : 600000;
	}

	private boolean isSoundAlert() {
		return false;
	}

	private boolean isShowSeconds() {
		return ArcaneTower.isTestMode();
	}

}
