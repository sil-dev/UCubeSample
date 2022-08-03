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
import android.content.Intent;
import android.util.Log;

import com.sil.ucubesdk.ITaskMonitor;
import com.sil.ucubesdk.LogManager;
import com.sil.ucubesdk.TaskEvent;
import com.sil.ucubesdk.UCubeCallBacks;
import com.sil.ucubesdk.rpc.Constants;
import com.sil.ucubesdk.rpc.DeviceInfos;
import com.sil.ucubesdk.rpc.MyConst;
import com.sil.ucubesdk.rpc.command.DisplayMessageCommand;
import com.sil.ucubesdk.rpc.command.GetInfosCommand;
import com.sil.ucubesdk.rpc.command.GetPlainTagCommand;
import com.sil.ucubesdk.rpc.command.GetSecuredTagCommand;
import com.sil.ucubesdk.rpc.command.SimplifiedOnlinePINCommand;
import com.sil.ucubesdk.rpc.command.WaitCardRemovalCommand;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Arrays;

/**
 * @author gbillard on 5/11/16.
 */
public class MSPaymentService extends AbstractPaymentService {

    private boolean running;
    Context ct;
    UCubeCallBacks uCubeCallBacks;

    public MSPaymentService(PaymentContext context, Context c) {
        super(context);
        this.ct = c;
    }

    public MSPaymentService(PaymentContext context, Context c, UCubeCallBacks uCubeCallBacks) {
        super(context);
        this.ct = c;
        this.uCubeCallBacks = uCubeCallBacks;
    }

    @Override
    protected void start() {
        LogManager.debug(this.getClass().getSimpleName(), "start");
        running = true;
        getSecuredTag();
    }

    private void getSecuredTag() {
        LogManager.debug(this.getClass().getSimpleName(), "getSecuredTag");

        final GetSecuredTagCommand cmd = new GetSecuredTagCommand(context.getRequestedSecuredTagList());
        cmd.execute(new ITaskMonitor() {
            @Override
            public void handleEvent(TaskEvent event, Object... params) {
                switch (event) {
                    case FAILED:
                        end(PaymentState.ERROR);
                        break;

                    case SUCCESS:
                        context.setSecuredTagBlock(cmd.getResponseData());
                        getPlainTag();
                        Log.d("Shankar Res 1:", "value ");
                        //shankar111
                        break;
                }
            }
        });
    }

    public static int[] concat(int[] src1, int[] src2) {
        int[] dest = new int[src1.length + src2.length];
        System.arraycopy(src1, 0, dest, 0, src1.length);
        System.arraycopy(src2, 0, dest, src1.length, src2.length);
        return dest;
    }

    private void getPlainTag() {
        LogManager.debug(this.getClass().getSimpleName(), "getPlainTag");
        GetInfosCommand cmd1 = new GetInfosCommand(
                Constants.TAG_TERMINAL_PN);

        cmd1.execute(new ITaskMonitor() {
            @Override
            public void handleEvent(TaskEvent event, Object... params) {
                switch (event) {
                    case FAILED:
                        Log.d("deviceInfo", "Fail");
                        break;

                    case SUCCESS:
                        DeviceInfos deviceInfos = new DeviceInfos(((GetInfosCommand) params[0]).getResponseData());
                        Log.d("deviceInfo", "Success>>" + deviceInfos.getPartNumber());
                        MyConst.setDevice_sr_no(deviceInfos.getPartNumber());
                        final GetPlainTagCommand cmd = new GetPlainTagCommand(context.getRequestedPlainTagList(), true);
                        cmd.execute(new ITaskMonitor() {
                            @Override
                            public void handleEvent(TaskEvent event, Object... params) {
                                switch (event) {
                                    case FAILED:
                                        end(PaymentState.ERROR);
                                        break;
                                    case SUCCESS:
                                        context.setPlainTagTLV(cmd.getResult());
                                        checkMSRAction();
                                        break;
                                }
                            }
                        });
                        break;
                }
            }
        });

        //////
    }

    private void checkMSRAction() {
        LogManager.debug(this.getClass().getSimpleName(), "checkMSRAction");

        byte[] bin = context.getPlainTagTLV().get(new Integer(Constants.TAG_MSR_BIN));
        if (bin != null) Log.d("BIN>>", "" + Arrays.toString(bin));

        byte[] actionCode = context.getPlainTagTLV().get(new Integer(Constants.TAG_MSR_ACTION));

        //byte[] a1 = context.getPlainTagTLV().get(new Integer(Constants.service ));

        Log.d("Shankar - actionCode : ", "" + actionCode[0]);
        //	Log.d("serviceCode : ",""+a1[0]) ;

        MyConst.setActioncode(0);

        if (actionCode == null || actionCode.length == 0) {
            end(PaymentState.ERROR);
            return;
        }

        if (Constants.MSR_ACTION_CHIP_REQUIRED_NEW == actionCode[0]) {
            MyConst.setActioncode(3);
            MyConst.isFallBack = true;
        } else if (Constants.MSR_ACTION_CHIP_REQUIRED == actionCode[0]) {
            MyConst.setActioncode(2);
            MyConst.isFallBack = true;
        }
//    MyConst.getResponseStatus() == Constants.EMV_NOT_SUPPORT ||
        if ((MyConst.getResponseStatus() == Constants.STATUS_CARD_MUTE ||
                MyConst.getResponseStatus() == Constants.EMV_NOT_SUPPORT ||
                MyConst.getResponseStatus() == Constants.STATUS_E_PAY_CARD_BLOCK) &&
                (MyConst.getActioncode() == 3 || MyConst.getActioncode() == 2)) {
            actionCode[0] = 0x01;
        } else if (MyConst.getActioncode() == 3 || MyConst.getActioncode() == 2) {
            actionCode[0] = 0x03;
        }

        switch (actionCode[0]) {

            case Constants.MSR_ACTION_CHIP_REQUIRED_NEW:
                Log.d("STIVE", "USE CHIP");
                MyConst.setResponseStatus(Constants.MSR_USE_CHIP);
                if (MyConst.getTxnattempt() == 2) {
                    end(PaymentState.DECLINED_BY_9F27);
                } else {
                    end(PaymentState.CHIP_REQUIRED);
                }
                break;

            case Constants.MSR_ACTION_CHIP_REQUIRED:
                end(PaymentState.CHIP_REQUIRED);
                break;

            case Constants.MSR_ACTION_DECLINE:
                if (MyConst.getTxnattempt() == 2) {
                    end(PaymentState.DECLINED);
                } else {
                    end(PaymentState.SWIPE_CARD);
                }
                break;

            case Constants.MSR_ACTION_NONE:
                Log.d("OnlinePinRequird", "Depends");
			/*if (context.isForceOnlinePIN()) {
				onlinePIN();
			} else {*/
                riskManagement();
                //}
                break;

            case Constants.MSR_ACTION_ONLINE_PIN_REQUIRED:
                Log.d("OnlinePinRequird", "Yes");
                onlinePIN();
                break;

            case Constants.MSR_ACTION_ONLINE_PIN_REQUIRED_OPT:
                Log.d("OnlinePinRequird", "Optional");
                onlinePIN();
                break;

            default:
                end(PaymentState.ERROR);
                break;
        }
    }

    private void onlinePIN() {
        LogManager.debug(this.getClass().getSimpleName(), "onlinePIN");

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(context.mBroadcastStringAction);
        broadcastIntent.putExtra("msg", "Enter PIN");
        if(uCubeCallBacks!=null){
            uCubeCallBacks.progressCallback("Enter PIN");
        }
        ct.sendBroadcast(broadcastIntent);


        String aa = "";
        DecimalFormat form = new DecimalFormat("0.00");
        aa = form.format(Float.parseFloat(context.getAmount() + ""));

        final SimplifiedOnlinePINCommand cmd = new SimplifiedOnlinePINCommand(context.getAmount(), context.getCurrency(), context.getOnlinePinBlockFormat());
        cmd.setPINRequestLabel(MessageFormat.format("Enter PIN", context.getCurrency().getLabel(), aa));
        cmd.setWaitLabel(context.getString("LBL_wait"));
        cmd.execute(new ITaskMonitor() {
            @Override
            public void handleEvent(TaskEvent event, Object... params) {
                switch (event) {
                    case FAILED:
                        end(PaymentState.ERROR);
                        break;

                    case SUCCESS:
                        context.setOnlinePinBlock(cmd.getResponseData());
                        doAuthorization();

                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(context.mBroadcastStringAction);
                        broadcastIntent.putExtra("msg", "Transaction is in Progress");
                        if(uCubeCallBacks!=null){
                            uCubeCallBacks.progressCallback("Transaction is in Progress");
                        }
                        ct.sendBroadcast(broadcastIntent);

                        break;
                }
            }
        });
    }

    @Override
    protected void displayResult(final ITaskMonitor monitor) {
        LogManager.debug(this.getClass().getSimpleName(), "displayResult");

        final ITaskMonitor logMonitor = new ITaskMonitor() {
            @Override
            public void handleEvent(TaskEvent event, Object... params) {
                if (event == TaskEvent.PROGRESS) {
                    return;
                }
                logTransaction(monitor);
            }
        };
        //super.displayResult(logMonitor);

        if (!running) {
            context.setPaymentStatus(PaymentState.CARD_REMOVED);
            super.displayResult(logMonitor);
            return;
        }

        if (MyConst.getResponseStatus() == Constants.MSR_USE_CHIP) {
            new DisplayMessageCommand(context.getString("LBL_use_chip")).execute(new ITaskMonitor() {
                @Override
                public void handleEvent(TaskEvent event, Object... params) {
                    if (event == TaskEvent.PROGRESS) {
                        return;
                    }

                    new WaitCardRemovalCommand().execute(new ITaskMonitor() {
                        @Override
                        public void handleEvent(TaskEvent event, Object... params) {
                            if (event == TaskEvent.PROGRESS) {
                                return;
                            }

                            MSPaymentService.super.displayResult(logMonitor);
                        }
                    });
                }
            });
        } else {
            if (context.getPaymentStatus() == PaymentState.APPROVED) {
                new DisplayMessageCommand(context.getString("LBL_approved")).execute(new ITaskMonitor() {
                    @Override
                    public void handleEvent(TaskEvent event, Object... params) {
                        if (event == TaskEvent.PROGRESS) {
                            return;
                        }

                        new WaitCardRemovalCommand().execute(new ITaskMonitor() {
                            @Override
                            public void handleEvent(TaskEvent event, Object... params) {
                                if (event == TaskEvent.PROGRESS) {
                                    return;
                                }

                                //MSPaymentService.super.displayResult(logMonitor);
                            }
                        });
                    }
                });
            } else {
                new DisplayMessageCommand(context.getString("LBL_declined")).execute(new ITaskMonitor() {
                    @Override
                    public void handleEvent(TaskEvent event, Object... params) {
                        if (event == TaskEvent.PROGRESS) {
                            return;
                        }

                        new WaitCardRemovalCommand().execute(new ITaskMonitor() {
                            @Override
                            public void handleEvent(TaskEvent event, Object... params) {
                                if (event == TaskEvent.PROGRESS) {
                                    return;
                                }

                                //MSPaymentService.super.displayResult(logMonitor);
                            }
                        });
                    }
                });
            }

        }

    }

    public void logTransaction(final ITaskMonitor monitor) {
        if (!LogManager.isEnabled()) {
            return;
        }

        final byte[][] logs = new byte[2][];

        new GetInfosCommand(Constants.TAG_SYSTEM_FAILURE_LOG_RECORD_1).execute(new ITaskMonitor() {
            @Override
            public void handleEvent(TaskEvent event, Object... params) {
                switch (event) {
                    case PROGRESS:
                        return;

                    case FAILED:
                        LogManager.debug(AbstractPaymentService.class.getSimpleName(), "retrieve transaction logs 1 errors");
                        break;

                    case SUCCESS:
                        logs[0] = ((GetInfosCommand) params[0]).getResponseData();
                        break;

                    case CANCELLED:
                        return;
                }

                new GetInfosCommand(Constants.TAG_SYSTEM_FAILURE_LOG_RECORD_2).execute(new ITaskMonitor() {
                    @Override
                    public void handleEvent(final TaskEvent event, final Object... params) {
                        switch (event) {
                            case PROGRESS:
                                return;

                            case FAILED:
                                LogManager.debug(AbstractPaymentService.class.getSimpleName(), "retrieve transaction logs 2 errors");
                                break;

                            case SUCCESS:
                                logs[1] = ((GetInfosCommand) params[0]).getResponseData();
                                break;

                            case CANCELLED:
                                return;
                        }

                        LogManager.storeTransactionLog(logs[0], logs[1]);

                        if (monitor != null) {
                            monitor.handleEvent(TaskEvent.SUCCESS);
                        }
                    }
                });
            }
        });
    }
}
