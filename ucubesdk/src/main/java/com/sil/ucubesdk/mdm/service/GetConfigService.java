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

import com.sil.ucubesdk.ITask;
import com.sil.ucubesdk.ITaskMonitor;
import com.sil.ucubesdk.TaskEvent;
import com.sil.ucubesdk.mdm.Config;
import com.sil.ucubesdk.mdm.MDMManager;
import com.sil.ucubesdk.mdm.task.GetConfigTask;

import java.util.List;

/**
 * @author gbillard on 4/25/16.
 */
public class GetConfigService extends AbstractMDMService {

    private List<Config> cfgList;

    public GetConfigService() {
        deviceInfos = MDMManager.getInstance().getDeviceinfos();
    }

    @Override
    protected void onDeviceInfosRetrieved() {
        getDeviceConfig();
    }

    public List<Config> getCfgList() {
        return cfgList;
    }

    private void getDeviceConfig() {
        new GetConfigTask(deviceInfos).execute(new ITaskMonitor() {
            @Override
            public void handleEvent(TaskEvent event, Object... params) {
                switch (event) {
                    case FAILED:
                        failedTask = (ITask) params[0];
                        notifyMonitor(TaskEvent.FAILED, this);
                        break;

                    case SUCCESS:
                        cfgList = (List<Config>) params[0];
                        notifyMonitor(TaskEvent.SUCCESS, cfgList);
                }
            }
        });
    }

}
