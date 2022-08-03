/**
 * Copyright (C) 2016, YouTransactor. All Rights Reserved.
 * <p>
 * Use of this product is contingent on the existence of an executed license
 * agreement between YouTransactor or one of its sublicensee, and your
 * organization, which specifies this software's terms of use. This software
 * is here defined as YouTransactor Intellectual Property for the purposes
 * of determining terms of use as defined within the license agreement.
 */
package com.sil.ucubesdk.rpc;

import android.util.Log;

import com.sil.ucubesdk.AbstractTask;
import com.sil.ucubesdk.LogManager;
import com.sil.ucubesdk.TaskEvent;

/**
 * @author gbillard on 3/9/16.
 */
public class RPCCommand extends AbstractTask implements IRPCMessageHandler {

    protected RPCCommandStatus state;
    protected short commandId;
    protected boolean ciphered = false;
    protected byte[] payload;
    protected RPCMessage response;

    public RPCCommand(short commandId) {
        Log.d("Command>>", "Amar:" + commandId);
        this.commandId = commandId;
        state = RPCCommandStatus.READY;
    }

    public RPCCommand(short commandId, boolean ciphered) {
        this(commandId);
        this.ciphered = ciphered;
    }

    public short getCommandId() {
        return commandId;
    }

    public Short getResponseStatus() {
        return response == null ? null : response.getStatus();
    }

    public byte[] getResponseData() {
        return response == null ? null : response.getData();
    }

    @Override
    public void start() {
        RPCManager.getInstance().sendCommand(this);
    }

    public byte[] getPayload() {
        if (payload == null) {
            try {
                payload = createPayload();
            } catch (Exception e) {
                LogManager.debug(getClass().getSimpleName(), "create payload error", e);
                notifyMonitor(TaskEvent.FAILED, this);
            }
        }

        return payload;
    }

    @Override
    public void processMessage(RPCMessage message) {
        response = message;

        try {
            if (isValidResponse() && parseResponse()) {
                setState(RPCCommandStatus.SUCCESS);
                return;
            }
        } catch (Exception e) {
            LogManager.debug(getClass().getSimpleName(), "parse response exception", e);
        }

        LogManager.debug(RPCCommand.class.getName(), "RPC command failed. Status: " + (response != null ? response.getStatus() : "none"));

        setState(RPCCommandStatus.FAILED);
    }

    public void setState(RPCCommandStatus newStatus) {
        if (newStatus == state) {
            return;
        }

        state = newStatus;

        switch (newStatus) {
            case CONNECT_ERROR:
            case FAILED:
                notifyMonitor(TaskEvent.FAILED, this);
                break;

            case CANCELED:
                notifyMonitor(TaskEvent.CANCELLED, this);
                break;

            case SUCCESS:
                notifyMonitor(TaskEvent.SUCCESS, this);
                break;

            case SENDING:

            case CONNECT:
                notifyMonitor(TaskEvent.PROGRESS, this);
                break;
        }
    }

    protected boolean isValidResponse() {
        return !(!ciphered && (response == null || response.getStatus() != Constants.SUCCESS_STATUS));
    }

    protected boolean parseResponse() {
        return true;
    }

    protected byte[] createPayload() {
        return new byte[0];
    }

}
