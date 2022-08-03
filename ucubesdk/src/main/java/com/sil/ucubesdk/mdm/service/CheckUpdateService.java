/**
 * Copyright (C) 2011-2016, YouTransactor. All Rights Reserved.
 *
 * Use of this product is contingent on the existence of an executed license
 * agreement between YouTransactor or one of its sublicensee, and your
 * organization, which specifies this software's terms of use. This software
 * is here defined as YouTransactor Intellectual Property for the purposes
 * of determining terms of use as defined within the license agreement.
 */
package com.sil.ucubesdk.mdm.service;



import com.sil.ucubesdk.ITaskMonitor;
import com.sil.ucubesdk.TaskEvent;
import com.sil.ucubesdk.mdm.Config;
import com.sil.ucubesdk.mdm.task.CompareVersionsTask;
import com.sil.ucubesdk.mdm.task.GetConfigTask;

import java.util.List;

/**
 * @author gbillard on 4/5/16.
 */
public class CheckUpdateService extends AbstractMDMService {

	private List<Config> cfgList;
	private List<BinaryUpdate> updateList;
	private boolean updateSameVersion;

	public List<BinaryUpdate> getUpdateList() {
		return updateList;
	}

	public List<Config> getCfgList() {
		return cfgList;
	}

	public void setUpdateSameVersion(boolean updateSameVersion) {
		this.updateSameVersion = updateSameVersion;
	}

	@Override
	protected void start() {
		retrieveDeviceInfos();
	}

	@Override
	protected void onDeviceInfosRetrieved() {
		retrieveDeviceConfig();
	}

	private void retrieveDeviceConfig() {
		setState(ServiceState.RETRIEVE_DEVICE_CONFIG);

		final GetConfigTask task = new GetConfigTask(deviceInfos);

		task.execute(new ITaskMonitor() {
			@Override
			public void handleEvent(TaskEvent event, Object... params) {
				if (event == TaskEvent.PROGRESS) {
					return;
				}

				if (event == TaskEvent.FAILED) {
					failedTask = task;
					notifyMonitor(TaskEvent.FAILED, this);
					return;
				}

				cfgList = (List<Config>) params[0];
				compareVersion();
			}
		});
	}

	private void compareVersion() {
		setState(ServiceState.CHECK_UPDATE);

		CompareVersionsTask task = new CompareVersionsTask(deviceInfos, cfgList);
		task.setUpdateSameVersion(updateSameVersion);

		updateList = task.getUpdateList();

		/* hard coded dependency of NFC config on NFC firmware */
		int nfcCfgFound = -1;
		int nfcFirmwareFound = -1;

		for (int i = 0; i < updateList.size(); i++) {
			BinaryUpdate bin = updateList.get(i);

			switch (bin.getCfg().getType()) {
			case 3:
				nfcFirmwareFound = i;
				break;

			case 5:
				nfcCfgFound = i;
				break;
			}
		}

		if (nfcFirmwareFound != -1) {
			if (nfcCfgFound == -1) {
				for (Config cfg : cfgList) {
					if (cfg.getType() == 5) {
						nfcCfgFound = updateList.size();
						updateList.add(new BinaryUpdate(cfg, updateList.get(nfcFirmwareFound).isMandatory()));
						break;
					}
				}
			}

		}

		/* ensure NFC config will be updated after NFC firmware */
		if (nfcFirmwareFound > nfcCfgFound) {
			BinaryUpdate bin = updateList.get(nfcFirmwareFound);

			updateList.set(nfcFirmwareFound, updateList.get(nfcCfgFound));
			updateList.set(nfcCfgFound, bin);
		}

		notifyMonitor(TaskEvent.SUCCESS, (Object) updateList);
	}

}
