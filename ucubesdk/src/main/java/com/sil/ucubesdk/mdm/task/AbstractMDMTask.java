/**
 * Copyright (C) 2011-2016, YouTransactor. All Rights Reserved.
 * <p/>
 * Use of this product is contingent on the existence of an executed license
 * agreement between YouTransactor or one of its sublicensee, and your
 * organization, which specifies this software's terms of use. This software
 * is here defined as YouTransactor Intellectual Property for the purposes
 * of determining terms of use as defined within the license agreement.
 */
package com.sil.ucubesdk.mdm.task;

import com.sil.ucubesdk.AbstractTask;
import com.sil.ucubesdk.rpc.DeviceInfos;

/**
 * @author gbillard on 4/5/16.
 */
abstract public class AbstractMDMTask extends AbstractTask {

    protected DeviceInfos deviceInfos;
    protected int HTTPResponseCode;

    public AbstractMDMTask() {
    }

    public AbstractMDMTask(DeviceInfos deviceInfos) {
        setDeviceInfos(deviceInfos);
    }

    public void setDeviceInfos(DeviceInfos deviceInfos) {
        this.deviceInfos = deviceInfos;
    }

    public int getHTTPResponseCode() {
        return HTTPResponseCode;
    }

}
