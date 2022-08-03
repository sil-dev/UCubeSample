/*
 * Copyright (C) 2016, YouTransactor. All Rights Reserved.
 *
 * Use of this product is contingent on the existence of an executed license
 * agreement between YouTransactor or one of its sublicensee, and your
 * organization, which specifies this software's terms of use. This software
 * is here defined as YouTransactor Intellectual Property for the purposes
 * of determining terms of use as defined within the license agreement.
 */
package com.sil.ucubesdk.mdm.task;

import com.sil.ucubesdk.LogManager;
import com.sil.ucubesdk.TaskEvent;
import com.sil.ucubesdk.mdm.MDMManager;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * @author gbillard on 6/28/16.
 */
public class SendLogsTask extends AbstractMDMTask {

	private InputStream in;
	private String fileType;

	public SendLogsTask() {}

	public SendLogsTask(String fileType, InputStream in) {
		setInputStream(fileType, in);
	}

	public SendLogsTask setInputStream(String fileType, InputStream in) {
		this.fileType = fileType;
		this.in = in;

		return this;
	}

	@Override
	protected void start() {
		HttpURLConnection urlConnection = null;

		try {
			urlConnection = MDMManager.getInstance().initRequest(SEND_LOGS_WS + deviceInfos.getPartNumber() + '/' + deviceInfos.getSerial() + "?type=" + fileType, MDMManager.POST_METHOD);

			urlConnection.setRequestProperty("Content-Type", "application/octet-stream");

			OutputStream output = urlConnection.getOutputStream();
			IOUtils.copy(in, output);

			HTTPResponseCode = urlConnection.getResponseCode();

			if (HTTPResponseCode == 200) {
				notifyMonitor(TaskEvent.SUCCESS);
				return;
			}

			LogManager.debug(GetConfigTask.class.getSimpleName(), "send logs WS error: " + HTTPResponseCode);

		} catch(Exception e) {
			LogManager.debug(GetConfigTask.class.getSimpleName(), "\"send logs WS error", e);

		} finally {
			if(urlConnection != null) {
				urlConnection.disconnect();
			}
		}

		notifyMonitor(TaskEvent.FAILED, this);
	}

	private static final String SEND_LOGS_WS = "/v1/dongle/logs/";

}
