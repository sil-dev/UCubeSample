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


import android.util.Log;

import com.sil.ucubesdk.AbstractService;
import com.sil.ucubesdk.ITask;
import com.sil.ucubesdk.ITaskMonitor;
import com.sil.ucubesdk.LogManager;
import com.sil.ucubesdk.TLV;
import com.sil.ucubesdk.TaskEvent;
import com.sil.ucubesdk.rpc.MyConst;
import com.sil.ucubesdk.rpc.command.DisplayMessageCommand;
import com.sil.ucubesdk.rpc.command.ExitSecureSessionCommand;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Map;

import static com.sil.ucubesdk.BluetoothConnexionManager.isBluetoothOn;

/**
 * @author gbillard on 5/19/16.
 */
abstract public class AbstractPaymentService extends AbstractService implements IPaymentTask {

    protected PaymentContext context;
    protected IApplicationSelectionTask applicationSelectionProcessor;
    protected IRiskManagementTask riskManagementTask;
    protected IAuthorizationTask authorizationProcessor;
    protected boolean authorizationDone;

    public AbstractPaymentService(PaymentContext context) {
        this.context = context;

    }

    protected void riskManagement() {
        LogManager.debug(this.getClass().getSimpleName(), "riskManagement");

        if (riskManagementTask == null) {
            onRiskManagementDone();
            return;
        }

        //Mod By Shankar
        context.setTvr(riskManagementTask.getTVR());
        onRiskManagementDone();

/*		riskManagementTask.setContext(context);
        riskManagementTask.execute(new ITaskMonitor() {
			@Override
			public void handleEvent(TaskEvent event, Object... params) {
				switch (event) {
				case FAILED:
					failedTask = riskManagementTask;
					notifyMonitor(TaskEvent.FAILED);
					break;

				case SUCCESS:
					context.setTvr(riskManagementTask.getTVR());
					onRiskManagementDone();
					break;
				}
			}
		});*/
    }

    protected void onRiskManagementDone() {
        processTransaction();
    }

    public void onAuthorizationDone() {
        Log.d("onAuthorizationResponse", "Enter");
        LogManager.debug(this.getClass().getSimpleName(), "onAuthorizationResponse>>>");

        Map<Integer, byte[]> authResponse = TLV.parse(context.getAuthorizationResponse());

        if (authResponse != null) {
            Log.d("onAuthorizationResponse", "not null");
            byte[] tag8a = authResponse.get(0x8A);

            if (TLV.equalValue(tag8a, new byte[]{0x30, 0x30})) {
                Log.d("onAuthorizationResponse", "EMV Approved");
                end(context.getPaymentStatus());
                //end(PaymentState.APPROVED);
                return;
            }
        }
        end(context.getPaymentStatus());
    }

    public PaymentContext getContext() {
        return context;
    }

    public void setContext(PaymentContext context) {
        this.context = context;
    }

    public void setApplicationSelectionProcessor(IApplicationSelectionTask applicationSelectionProcessor) {
        this.applicationSelectionProcessor = applicationSelectionProcessor;
    }

    public void setRiskManagementTask(IRiskManagementTask riskManagementTask) {
        this.riskManagementTask = riskManagementTask;
    }

    public void setAuthorizationProcessor(IAuthorizationTask authorizationProcessor) {
        this.authorizationProcessor = authorizationProcessor;
    }

    protected void processTransaction() {
        LogManager.debug(this.getClass().getSimpleName(), "processTransaction");
        doAuthorization();
    }


    protected void doAuthorization() {

        LogManager.debug(this.getClass().getSimpleName(), "doAuthorization");

        authorizationDone = true;

        if (authorizationProcessor == null) {
            onAuthorizationDone();
            return;
        }

        context.setPaymentStatus(PaymentState.AUTHORIZE);

        displayMessage(context.getString("LBL_authorization"), new ITaskMonitor() {
            @Override
            public void handleEvent(TaskEvent event, Object... params) {
                switch (event) {
                    case FAILED:
                        Log.d("Shankar Auth", "Auth fail");
                        failedTask = (ITask) params[0];
                        end(PaymentState.ERROR);
                        break;

                    case SUCCESS:
                        Log.d("Shankar Auth", "Auth success");
                        performAuthorization();
                        break;
                }
            }
        });
    }

    protected void performAuthorization() {

        Log.d("Shankar Auth", "Perform Auth");
        authorizationProcessor.setContext(context);

	/*	if(MyConst.isFallBack){
			onAuthorizationDone();
		}*/

        authorizationProcessor.execute(new ITaskMonitor() {
            @Override
            public void handleEvent(TaskEvent event, Object... params) {
                switch (event) {
                    case FAILED:
                        Log.d("Shankar Auth", "Perform Auth fail");
                        failedTask = authorizationProcessor;
                        notifyMonitor(TaskEvent.FAILED);
                        break;

                    case SUCCESS:
                        Log.d("Shankar Auth", "Perform Auth Successs");
                        Log.d("Amar00>>", Arrays.toString(authorizationProcessor.getAuthorizationResponse()));
                        context.setAuthorizationResponse(authorizationProcessor.getAuthorizationResponse());

                        displayMessage(context.getString("LBL_wait"), new ITaskMonitor() {
                            @Override
                            public void handleEvent(TaskEvent event, Object... params) {
                                //Mod By Shankar
                                if (event == TaskEvent.PROGRESS) {
                                    Log.d("Shankar", "Auth Return");
                                    return;
                                }
                                Log.d("Shankar", "Auth Return123");
                                onAuthorizationDone();
                            }
                        });
                        break;
                }
            }
        });
    }

    protected void exitSecureSession() {
        LogManager.debug(this.getClass().getSimpleName(), "exitSecureSession");

        new ExitSecureSessionCommand().execute(new ITaskMonitor() {
            @Override
            public void handleEvent(TaskEvent event, Object... params) {
                if (event == TaskEvent.PROGRESS) {
                    return;
                }
                displayResult(null);
            }
        });
    }


    protected void displayResult(ITaskMonitor monitor) {
        LogManager.debug(this.getClass().getSimpleName(), "displayResult");
        String msgKey;

        switch (context.getPaymentStatus()) {
            case APPROVED:
                msgKey = "LBL_approved";
                break;

            case CHIP_REQUIRED:
                msgKey = "LBL_use_chip";
                MyConst.isCardRemoved = true;
                break;

            case UNSUPPORTED_CARD:
                msgKey = "LBL_unsupported_card";
                break;

            case REFUSED_CARD:
                msgKey = "LBL_refused_card";
                break;

            case CARD_WAIT_FAILED:
                msgKey = "LBL_no_card_detected";
                break;

            case CANCELLED:
                msgKey = "LBL_cancelled";
                break;

            case SWIPE_CARD:
                msgKey = "LBL_swipecard";
                MyConst.isCardRemoved = true;
                break;

            case APPLICATION_BLOCKED:
                msgKey = "LBL_application_blocked";
                break;
            case CARD_BLOCKED:
                msgKey = "LBL_card_blocked";
                break;

            case DECLINED_BY_9F27:
                msgKey = "LBL_9f27_declined";
                break;

            default:
                msgKey = "LBL_declined";
                break;
        }

        displayMessage(context.getString(msgKey), monitor);
    }

    protected void end(final PaymentState state) {
        LogManager.debug(this.getClass().getSimpleName(), "end: " + state.name());

        context.setPaymentStatus(state);
        if(!isBluetoothOn()) {
            MyConst.setJSONResponse(getJSONObject("CONNECTION ERROR", 102, "Bluetooth Disconnected."));
        }else{
            if(state==PaymentState.ERROR) {
                MyConst.setJSONResponse(null);
            }
        }
        notifyMonitor(context.getPaymentStatus() != PaymentState.ERROR ? TaskEvent.SUCCESS : TaskEvent.FAILED);

        if (context.getPaymentStatus() != PaymentState.SWIPE_CARD)
            exitSecureSession();
    }

    protected void displayMessage(String msg, ITaskMonitor callback) {
        new DisplayMessageCommand(msg).execute(callback);
    }

    @Override
    protected void notifyMonitor(TaskEvent event, Object... params) {
        super.notifyMonitor(event, this);
    }
    private JSONObject getJSONObject(String message, int responseCode, String responseJson) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Msg", message);
            jsonObject.put("ResponseCode", responseCode);
            jsonObject.put("Response", responseJson);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
