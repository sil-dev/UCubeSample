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

import android.content.Context;
import android.util.Log;

import com.sil.ucubesdk.ITask;
import com.sil.ucubesdk.ITaskMonitor;
import com.sil.ucubesdk.TaskEvent;
import com.sil.ucubesdk.UCubeCallBacks;
import com.sil.ucubesdk.rpc.Constants;
import com.sil.ucubesdk.rpc.MyConst;
import com.sil.ucubesdk.rpc.command.EnterSecureSessionCommand;
import com.sil.ucubesdk.rpc.command.ExitSecureSessionCommand;
import com.sil.ucubesdk.rpc.command.GetInfosCommand;
import com.sil.ucubesdk.rpc.command.WaitCardCommand;

import java.text.MessageFormat;

/**
 * @author gbillard on 5/18/16.
 * <p>
 * this class allow only ICC and MS card use.
 * Nor does it allow amount to be entered on uCube.
 * Use SingleEntryPointPaymentService if you need NFC or amount input on uCube device.
 */
public class PaymentService extends AbstractPaymentService {

    String msg = "";
    protected int cardWaitTimeout = 60;
    protected byte[] enabledReaders;
    protected AbstractPaymentService paymentService;
    protected  UCubeCallBacks uCubeCallBacks;
    Context c;

    public PaymentService(Context ct, PaymentContext paymentContext, UCubeCallBacks uCubeCallBacks, byte[] enabledReaders, String msg) {
        super(paymentContext);
        this.c = ct;
        this.enabledReaders = enabledReaders;
        this.msg = msg;
        this.uCubeCallBacks = uCubeCallBacks;
    }
    public PaymentService(Context ct, PaymentContext paymentContext, byte[] enabledReaders, String msg) {
        super(paymentContext);
        this.c = ct;
        this.enabledReaders = enabledReaders;
        this.msg = msg;
    }

    public void setCardWaitTimeout(int cardWaitTimeout) {
        this.cardWaitTimeout = cardWaitTimeout;
    }

    @Override
    protected void start() {
        new ExitSecureSessionCommand().execute(new ITaskMonitor() {
            @Override
            public void handleEvent(TaskEvent event, Object... params) {
                if (event == TaskEvent.PROGRESS) {
                    return;
                }

                if (MyConst.getResponseStatus() == Constants.STATUS_CARD_MUTE) {
                    displayMessage(context.getString("LBL_swipecard"), new ITaskMonitor() {
                        @Override
                        public void handleEvent(TaskEvent event, Object... params) {
                            switch (event) {
                                case FAILED:
                                    failedTask = (ITask) params[0];
                                    end(PaymentState.ERROR);
                                    break;

                                case SUCCESS:
                                    getInfos();
                                    break;
                            }
                        }
                    });
                } else {
                    displayMessage(context.getString("LBL_wait"), new ITaskMonitor() {
                        @Override
                        public void handleEvent(TaskEvent event, Object... params) {
                            switch (event) {
                                case FAILED:
                                    failedTask = (ITask) params[0];
                                    end(PaymentState.ERROR);
                                    break;

                                case SUCCESS:
                                    getInfos();
                                    break;
                            }
                        }
                    });
                }

            }
        });
    }

    protected void getInfos() {
        new GetInfosCommand(Constants.TAG_FIRMWARE_VERSION).execute(new ITaskMonitor() {
            @Override
            public void handleEvent(TaskEvent event, Object... params) {
                switch (event) {
                    case FAILED:
                        failedTask = (ITask) params[0];
                        end(PaymentState.ERROR);
                        break;

                    case SUCCESS:
                        context.setuCubeInfos(((GetInfosCommand) params[0]).getResponseData());
                        onGetInfos();
                        break;
                }
            }
        });
    }

    protected void onGetInfos() {
        waitCard();
    }

    protected void waitCard() {
        //d.setMessage("Insert/Swipe/Tap Card");
        //todo Display Insert Msg
        displayMessage(MessageFormat.format("" + msg, context.getCurrency().getLabel(),
                context.getFormatedAmount()), new ITaskMonitor() {

            @Override
            public void handleEvent(TaskEvent event, Object... params) {
                if (event == TaskEvent.PROGRESS) {
                    return;
                }

                new WaitCardCommand(enabledReaders, cardWaitTimeout).execute(new ITaskMonitor() {
                    @Override
                    public void handleEvent(TaskEvent event, Object... params) {
                        WaitCardCommand cmd = (WaitCardCommand) params[0];

                     /*   Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(context.mBroadcastStringAction);
                        broadcastIntent.putExtra("msg", "SWIPE/INSERT Card");
                        c.sendBroadcast(broadcastIntent);*/

                        switch (event) {
                            case FAILED:
                                try {
                                    Log.d("kst", "" + cmd.getResponseStatus().shortValue());
                                    if (cmd.getResponseStatus() != null && cmd.getResponseStatus().shortValue() == Constants.CANCELLED_STATUS) {
                                        end(PaymentState.CANCELLED);
                                        //cmd.getResponseStatus().shortValue() == Constants.EMV_NOT_SUPPORT ||
                                    } else if (cmd.getResponseStatus().shortValue() == Constants.EMV_NOT_SUPPORT || cmd.getResponseStatus().shortValue() == Constants.STATUS_CARD_MUTE ||
                                            cmd.getResponseStatus().shortValue() == Constants.STATUS_E_PAY_CARD_BLOCK) {
                                        MyConst.setResponseStatus(cmd.getResponseStatus().shortValue());
                                        if (MyConst.getTxnattempt() == 2) {
                                            //end(PaymentState.DECLINED);
                                            Log.d("Card state ubnsupported", ">>>>>>>>>");
                                            end(PaymentState.UNSUPPORTED_CARD);
                                        } else {
                                            end(PaymentState.SWIPE_CARD);
                                            Log.d("Card state in swipe", ">>>>>>>>>");
                                        }
                                    } else if (cmd.getResponseStatus().shortValue() == Constants.STATUS_CARD_NOT_ACCEPTED) {
                                        MyConst.setResponseStatus(cmd.getResponseStatus().shortValue());
                                        end(PaymentState.UNSUPPORTED_CARD);
                                    } else if (cmd.getResponseStatus().shortValue() == Constants.TAG_BATTERY_STATE) {
                                        MyConst.setResponseStatus(cmd.getResponseStatus().shortValue());
                                        end(PaymentState.TAG_BATTERY_STATE);
                                    } else if (cmd.getResponseStatus().shortValue() == Constants.TAG_POWER_OFF_TIMEOUT) {
                                        MyConst.setResponseStatus(cmd.getResponseStatus().shortValue());
                                        end(PaymentState.TAG_POWER_OFF_TIMEOUT);
                                    } else {
                                        end(PaymentState.CARD_WAIT_FAILED);
                                    }
                                } catch (Exception e) {
                                    end(PaymentState.ERROR);
                                }

                                break;

                            case PROGRESS:
                                break;

                            case CANCELLED:
                                Log.d("SHETTY ", "handleEvent: ");
                                break;

                            case SUCCESS:
                                context.setActivatedReader(cmd.getActivatedReader());

                                displayMessage(context.getString("LBL_wait"), new ITaskMonitor() {
                                    @Override
                                    public void handleEvent(TaskEvent event, Object... params) {
                                        if (event == TaskEvent.PROGRESS) {
                                            return;
                                        }
                                        enterSecureMode();
                                    }
                                });
                                break;
                        }
                    }
                });
            }
        });
    }

    private void enterSecureMode() {
        final EnterSecureSessionCommand cmd = new EnterSecureSessionCommand();
        cmd.execute(new ITaskMonitor() {
            @Override
            public void handleEvent(TaskEvent event, Object... params) {
                switch (event) {
                    case FAILED:
                        failedTask = (ITask) params[0];
                        notifyMonitor(TaskEvent.FAILED, paymentService);
                        break;

                    case SUCCESS:
                        context.setKsn(cmd.getKsn());

                        switch (context.getActivatedReader()) {
                            case Constants.MS_READER:
                                paymentService = new MSPaymentService(context, c,uCubeCallBacks);
                                break;

                            case Constants.ICC_READER:
                                paymentService = new ICCPaymentService(context, c,uCubeCallBacks);
                                paymentService.setApplicationSelectionProcessor(applicationSelectionProcessor);
                                break;

                            case Constants.NFC_READER:
                                // TODO implement NFC payment

                            default:
                                end(PaymentState.ERROR);
                                return;
                        }

                        paymentService.setRiskManagementTask(riskManagementTask);
                        paymentService.setAuthorizationProcessor(authorizationProcessor);

                        paymentService.execute(monitor);
                        break;
                }
            }
        });
    }

/*
	public static void end1(int choice) {
		if (payment_context.getActivatedReader() == Constants.NFC_READER) {
			switch (choice) {
				case 0:
					authResponse = new byte[]{0x30, 0x30};
					break;

				case 1:
					authResponse = new byte[]{0x35, 0x31};
					break;

				case 2:
					authResponse = new byte[]{0x50, 0x50};
					break;
			}

		} else {
			switch (choice) {
				case 0:
					authResponse = new byte[]{(byte) 0x8A, 0x02, 0x30, 0x30};
					break;

				case 1:
					authResponse = new byte[]{(byte) 0x8A, 0x02, 0x30, 0x35};
					break;

				case 2:
					authResponse = new byte[]{(byte) 0x8A, 0x02, 0x39, 0x38};
					break;
			}
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				monitor.handleEvent(TaskEvent.SUCCESS);
			}
		}).start();
	}*/

}
