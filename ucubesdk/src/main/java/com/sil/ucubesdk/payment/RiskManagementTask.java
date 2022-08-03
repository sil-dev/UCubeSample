/*
 * Copyright (C) 2016, YouTransactor. All Rights Reserved.
 *
 * Use of this product is contingent on the existence of an executed license
 * agreement between YouTransactor or one of its sublicensee, and your
 * organization, which specifies this software's terms of use. This software
 * is here defined as YouTransactor Intellectual Property for the purposes
 * of determining terms of use as defined within the license agreement.
 */
package com.sil.ucubesdk.payment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.sil.ucubesdk.ITaskMonitor;
import com.sil.ucubesdk.TaskEvent;
import com.sil.ucubesdk.Tools;
import com.sil.ucubesdk.rpc.EMVApplicationDescriptor;

import org.apache.commons.lang3.StringUtils;


/**
 * @author gbillard on 6/1/16.
 */
public class RiskManagementTask implements IRiskManagementTask {

	private Activity activity;
	private ITaskMonitor monitor;
	private PaymentContext paymentContext;
	private byte[] tvr;

	public RiskManagementTask(Activity activity) {
		this.activity = activity;
	}

	@Override
	public byte[] getTVR() {
		return tvr;
	}

	@Override
	public PaymentContext getContext() {
		return paymentContext;
	}

	@Override
	public void setContext(PaymentContext context) {
		this.paymentContext = context;
	}

	@Override
	public void execute(ITaskMonitor monitor) {
		this.monitor = monitor;

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);

				builder.setTitle("Risk management");
				builder.setCancelable(false);
				builder.setMessage("Is card stolen ?");

				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						end(new byte[] {0, 0b10000, 0, 0, 0});
					}
				});

				builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						end(new byte[] {0, 0, 0, 0, 0});
					}
				});

				builder.create().show();
			}
		});
	}

	private void end(byte[] tvr) {
		this.tvr = tvr;

		EMVApplicationDescriptor selectedApplication = paymentContext.getSelectedApplication();
		if (selectedApplication != null) {
			String selectedAID = Tools.bytesToHex(selectedApplication.getAid()).substring(0, 10);

			if (StringUtils.equals("A000000003", selectedAID)) {
				paymentContext.setApplicationVersion(140);

			} else if (StringUtils.equals("A000000004", selectedAID)) {
				paymentContext.setApplicationVersion(202);

			} else if (StringUtils.equals("A000000042", selectedAID)) {
				paymentContext.setApplicationVersion(203);

			}
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				monitor.handleEvent(TaskEvent.SUCCESS);
			}
		}).start();
	}

}
