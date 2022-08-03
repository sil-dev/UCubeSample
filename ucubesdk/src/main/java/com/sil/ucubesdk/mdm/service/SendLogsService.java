/*
 * Copyright (C) 2016, YouTransactor. All Rights Reserved.
 *
 * Use of this product is contingent on the existence of an executed license
 * agreement between YouTransactor or one of its sublicensee, and your
 * organization, which specifies this software's terms of use. This software
 * is here defined as YouTransactor Intellectual Property for the purposes
 * of determining terms of use as defined within the license agreement.
 */
package com.sil.ucubesdk.mdm.service;


import com.sil.ucubesdk.ITaskMonitor;
import com.sil.ucubesdk.LogManager;
import com.sil.ucubesdk.TaskEvent;
import com.sil.ucubesdk.mdm.MDMManager;
import com.sil.ucubesdk.mdm.task.SendLogsTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author gbillard on 6/28/16.
 */
public class SendLogsService extends AbstractMDMService {

    private boolean deleteAfterSend = true;

    public SendLogsService() {
        deviceInfos = MDMManager.getInstance().getDeviceinfos();
    }

    public SendLogsService(boolean deleteAfterSend) {
        this();
        this.deleteAfterSend = deleteAfterSend;
    }

    @Override
    protected void onDeviceInfosRetrieved() {
        setState(ServiceState.SEND_LOGS);

        File tmp = null;

        try {
            tmp = File.createTempFile("uCubelib-logs", ".zip");
            OutputStream out = new FileOutputStream(tmp);

            LogManager.getLogs(out);

            out.close();

            final SendLogsTask task = new SendLogsTask("zip", new FileInputStream(tmp));
            task.setDeviceInfos(deviceInfos);
            task.execute(new ITaskMonitor() {
                @Override
                public void handleEvent(TaskEvent event, Object... params) {
                    switch (event) {
                        case FAILED:
                            failedTask = task;
                            notifyMonitor(TaskEvent.FAILED, this);
                            break;

                        case SUCCESS:
                            onSuccessfulSend();
                            break;
                    }
                }
            });

        } catch (IOException e) {
            notifyMonitor(TaskEvent.FAILED, this);
        } finally {
            if (tmp != null && tmp.exists()) {
                tmp.delete();
            }
        }
    }

    private void onSuccessfulSend() {
        if (deleteAfterSend) {
            LogManager.deleteLogs();
        }

        notifyMonitor(TaskEvent.SUCCESS);
    }

}
