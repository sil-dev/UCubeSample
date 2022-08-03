/*
 * Copyright (C) 2016, YouTransactor. All Rights Reserved.
 *
 * Use of this product is contingent on the existence of an executed license
 * agreement between YouTransactor or one of its sublicensee, and your
 * organization, which specifies this software's terms of use. This software
 * is here defined as YouTransactor Intellectual Property for the purposes
 * of determining terms of use as defined within the license agreement.
 */
package com.sil.ucubesdk.rpc.command;

import android.util.Log;

import com.sil.ucubesdk.TLV;
import com.sil.ucubesdk.rpc.Constants;
import com.sil.ucubesdk.rpc.RPCCommand;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gbillard on 5/12/16.
 */
public class GetPlainTagCommand extends RPCCommand {

	private int[] requestedTagList;
	private Map<Integer, byte[]> result;
	private boolean isMSR = false;
	public GetPlainTagCommand() {
		super(Constants.GET_PLAIN_TAG_VALUE_COMMAND);
	}

	public GetPlainTagCommand(int[] requestedTagList) {
		this();
		this.requestedTagList = requestedTagList;
	}

	public GetPlainTagCommand(int[] requestedTagList, boolean isMSR) {
		this();
		this.isMSR = true;
		this.requestedTagList = requestedTagList;
	}

	public void setRequestedTagList(int[] requestedTagList) {
		this.requestedTagList = requestedTagList;
	}

	public Map<Integer, byte[]> getResult() {
		return result;
	}

	@Override
	protected byte[] createPayload() {
		if (requestedTagList == null) {
			return super.createPayload();
		}

		byte[] payload = new byte[requestedTagList.length * 2 + 2];
		int offset = 0;
		if(isMSR) offset = TLV.writeTagID(Constants.TAG_MSR_ACTION, payload, 0);;
		Log.d("offset",offset+"");
		for (int i = 0; i < requestedTagList.length; i++) {
			offset += TLV.writeTagID(requestedTagList[i], payload, offset);
		}

		payload = Arrays.copyOf(payload, offset);

		return payload;
	}

	@Override
	protected boolean parseResponse() {
		if (requestedTagList != null) {
			if (requestedTagList.length == 0) {
				result = new HashMap<>();
				result.put(requestedTagList[0], response.getData());
			} else {
				result = TLV.parse(response.getData());
			}
		}

		return true;
	}

}
