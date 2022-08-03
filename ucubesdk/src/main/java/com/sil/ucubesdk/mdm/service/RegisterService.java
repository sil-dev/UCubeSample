/**
 * Copyright (C) 2011-2016, YouTransactor. All Rights Reserved.
 * <p/>
 * Use of this product is contingent on the existence of an executed license
 * agreement between YouTransactor or one of its sublicensee, and your
 * organization, which specifies this software's terms of use. This software
 * is here defined as YouTransactor Intellectual Property for the purposes
 * of determining terms of use as defined within the license agreement.
 */
package com.sil.ucubesdk.mdm.service;

import android.content.Context;


import com.sil.ucubesdk.ITask;
import com.sil.ucubesdk.ITaskMonitor;
import com.sil.ucubesdk.TaskEvent;
import com.sil.ucubesdk.mdm.MDMManager;
import com.sil.ucubesdk.mdm.task.RegisterTask;
import com.sil.ucubesdk.mdm.task.SendStateTask;
import com.sil.ucubesdk.rpc.command.InstallForLoadKeyCommand;

import java.security.KeyStore;

/**
 * @author gbillard on 4/25/16.
 */
public class RegisterService extends AbstractMDMService {

	private Context context;
	private byte[] cert;
	private KeyStore sslKey;

	public RegisterService(Context context) {
		this.context = context;
	}

	@Override
	protected void onDeviceInfosRetrieved() {
		if (sslKey == null) {
			retrieveDeviceCertificat();

		} else {
			sendState();
		}
	}

	private void retrieveDeviceCertificat() {
		setState(ServiceState.RETRIEVE_DEVICE_CERTIFICAT);

		InstallForLoadKeyCommand cmd = new InstallForLoadKeyCommand();

		cmd.execute(new ITaskMonitor() {
			@Override
			public void handleEvent(TaskEvent event, Object... params) {
				switch(event) {
				case FAILED:
					failedTask = (ITask) params[0];
					notifyMonitor(TaskEvent.FAILED, this);
					return;

				case SUCCESS:
					cert = ((InstallForLoadKeyCommand) params[0]).getFullData();
					registerDevice();
				}
			}
		});
	}

	private void registerDevice() {
		setState(ServiceState.REGISTER_DEVICE);

		RegisterTask task = new RegisterTask(deviceInfos, cert);

		task.execute(new ITaskMonitor() {
			@Override
			public void handleEvent(TaskEvent event, Object... params) {
				switch(event) {
				case FAILED:
					failedTask = (ITask) params[0];
					notifyMonitor(TaskEvent.FAILED, this);
					return;

				case SUCCESS:
					sslKey = (KeyStore) params[0];
					MDMManager.getInstance().setSSLCertificat(context, sslKey);
					sendState();
				}
			}
		});
	}

	private void sendState() {
		new SendStateTask(deviceInfos).execute(new ITaskMonitor() {
			@Override
			public void handleEvent(TaskEvent event, Object... params) {
				notifyMonitor(TaskEvent.SUCCESS, sslKey);
			}
		});
	}

}
