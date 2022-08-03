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
import com.sil.ucubesdk.Tools;
import com.sil.ucubesdk.UCubeCallBacks;
import com.sil.ucubesdk.rpc.Constants;
import com.sil.ucubesdk.rpc.DeviceInfos;
import com.sil.ucubesdk.rpc.EMVApplicationDescriptor;
import com.sil.ucubesdk.rpc.MyConst;
import com.sil.ucubesdk.rpc.command.BuildCandidateListCommand;
import com.sil.ucubesdk.rpc.command.DisplayChoiceCommand;
import com.sil.ucubesdk.rpc.command.DisplayMessageCommand;
import com.sil.ucubesdk.rpc.command.GetInfosCommand;
import com.sil.ucubesdk.rpc.command.GetPlainTagCommand;
import com.sil.ucubesdk.rpc.command.GetSecuredTagCommand;
import com.sil.ucubesdk.rpc.command.InitTransactionCommand;
import com.sil.ucubesdk.rpc.command.TransactionFinalizationCommand;
import com.sil.ucubesdk.rpc.command.TransactionProcessCommand;
import com.sil.ucubesdk.rpc.command.WaitCardRemovalCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author shankar on 5/18/16.
 */
public class ICCPaymentService extends AbstractPaymentService {

    private boolean running;
    public static List<EMVApplicationDescriptor> candidateList;
    private Context ct;
    private UCubeCallBacks uCubeCallBacks;

    public ICCPaymentService(PaymentContext context, Context activity) {
        super(context);
        this.ct = activity;
    }

    public ICCPaymentService(PaymentContext context, Context activity, UCubeCallBacks uCubeCallBacks) {
        super(context);
        this.ct = activity;
        this.uCubeCallBacks = uCubeCallBacks;
    }

    @Override
    protected void start() {
        LogManager.debug(this.getClass().getSimpleName(), "start");

        running = true;

        buildCandidateList();
    }

    private void buildCandidateList() {
        LogManager.debug(this.getClass().getSimpleName(), "buildCandidateList");

        final BuildCandidateListCommand cmd = new BuildCandidateListCommand();
        cmd.execute(new ITaskMonitor() {
            @Override
            public void handleEvent(TaskEvent event, Object... params) {
                switch (event) {
                    case FAILED:
                        Log.d("kst1", "" + cmd.getResponseStatus());
                        Log.d("kst2", "" + Tools.bytesToHex(cmd.getResponseData()));
                        if (cmd.getResponseStatus().toString().equals("-300")) {
                            //end(PaymentState.UNSUPPORTED_CARD);
                            MyConst.setResponseStatus(cmd.getResponseStatus().shortValue());

                            if (MyConst.getTxnattempt() == 2) {
                                //end(PaymentState.DECLINED);
                                Log.d("Card state ubnsupported", ">>>>>>>>>");
                                end(PaymentState.UNSUPPORTED_CARD);
                            } else {
                                end(PaymentState.SWIPE_CARD);
                                Log.d("Card state in swipe", ">>>>>>>>>");
                            }
                        } else if (cmd.getResponseStatus().toString().equals("-350")) {
                            end(PaymentState.CARD_BLOCKED);
                        } else {
                            end(PaymentState.ERROR);
                        }


                        break;

                    case SUCCESS:
                        candidateList = cmd.getCandidateList();
                        Log.d("Application List", String.valueOf(candidateList.size()));
                        selectApplication();
                        break;
                }
            }
        });
    }

    private void selectApplication() {
        LogManager.debug(this.getClass().getSimpleName(), "selectApplication");

        if (applicationSelectionProcessor == null) {
            Log.d("Shankar", "application selection null");
            applicationSelectionProcessor = new EMVApplicationSelectionTask();
        }

        applicationSelectionProcessor.setContext(context);
        applicationSelectionProcessor.setAvailableApplication(candidateList);

        applicationSelectionProcessor.execute(new ITaskMonitor() {
            @Override
            public void handleEvent(TaskEvent event, Object... params) {
                switch (event) {
                    case FAILED:
                        Log.d("Shankar", "application selection fail");
                        failedTask = applicationSelectionProcessor;
                        notifyMonitor(TaskEvent.FAILED);
                        break;

                    case SUCCESS:
                        Log.d("Shankar", "application selection sucess");
//                        List<EMVApplicationDescriptor> appList = applicationSelectionProcessor.getSelection();
                        List<EMVApplicationDescriptor> appList = candidateList;
                        if (EMVApplicationDescriptor.blocked) {
                            end(PaymentState.APPLICATION_BLOCKED);
                        } else {
                            if (appList.size() == 0) {
                                end(PaymentState.UNSUPPORTED_CARD);
                                Log.d("Shankar", "application selection unsupported card");
                            } else if (appList.size() == 1) {
                                Log.d("Shankar", "application selection init transcation");
                                initTransaction(appList.get(0));
                            } else {
                                Log.d("Shankar", "application selection userApplicationSelection");
                                userApplicationSelection(appList);
                            }
                        }

                }
            }
        });
    }

    private void userApplicationSelection(final List<EMVApplicationDescriptor> appList) {
        LogManager.debug(this.getClass().getSimpleName(), "userApplicationSelection");

        List<String> labelList = new ArrayList<>();

        for (EMVApplicationDescriptor app : appList) {
            labelList.add(app.getLabel());
        }

        new DisplayChoiceCommand(labelList).execute(new ITaskMonitor() {
            @Override
            public void handleEvent(TaskEvent event, Object... params) {
                switch (event) {
                    case FAILED:
                        end(PaymentState.ERROR);
                        break;

                    case SUCCESS:
                        int index = ((DisplayChoiceCommand) params[0]).getSelectedIndex();
                        EMVApplicationDescriptor selected = appList.get(index);
                        //get selected application here to send data with correct card no
                        initTransaction(selected);
                        break;
                }
            }
        });

    }

    private void initTransaction(final EMVApplicationDescriptor app) {
        LogManager.debug(this.getClass().getSimpleName(), "initTransaction using " + Tools.bytesToHex(app.getAid()));
        LogManager.debug(this.getClass().getSimpleName(), "get app name" + app.getLabel());
        MyConst.setAppl_name(app.getLabel());
        LogManager.debug(this.getClass().getSimpleName(), "APPL name" + context.getApplName());
        InitTransactionCommand cmd = new InitTransactionCommand(context.getAmount(), context.getCurrency(), context.getTransactionType(), app);
        cmd.setPreferredLanguageList(context.getPreferredLanguageList());
        cmd.setRequestedTagList(context.getRequestedAuthorizationTagList());
        cmd.setDate(context.getTransactionDate());

        cmd.setCashbackAmount(Double.parseDouble("0"));
        cmd.execute(new ITaskMonitor() {
            @Override
            public void handleEvent(TaskEvent event, Object... params) {
                switch (event) {
                    case FAILED:
                        candidateList.remove(app);
                        selectApplication();
                        break;

                    case SUCCESS:
                        context.setSelectedApplication(app);
                        riskManagement();
                        break;
                }
            }
        });
    }

    @Override
    protected void processTransaction() {
        Log.d("emvShankar", "Process emv trans");
        LogManager.debug(this.getClass().getSimpleName(), "processTransaction");

        //Log.d("SHANKAR TVR",""+Tools.bytesToHex(context.getTvr()));
        final TransactionProcessCommand cmd = new TransactionProcessCommand(context.getTvr());

        String reader = "" + context.getActivatedReader();
        Log.d("get reder val", reader);


        if (reader.equals("17")) {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(context.mBroadcastStringAction);
            broadcastIntent.putExtra("msg", "Enter PIN");
            if(uCubeCallBacks!=null){
                uCubeCallBacks.progressCallback("Enter PIN");
            }
            ct.sendBroadcast(broadcastIntent);

        }

        if (context.getTransactionDate() == null) {
            /* if context.transaction != null, date has been send at initTransaction */
            cmd.setDate(new Date());
        }
        cmd.setApplicationVersion(context.getApplicationVersion());


        cmd.execute(new ITaskMonitor() {
            @Override
            public void handleEvent(TaskEvent event, Object... params) {
                Log.d("Shankar Event", "" + event.toString());
                Log.d("Monali", "" + cmd.getResponseStatus());
                switch (event) {
                    case FAILED:
                        if(cmd.getResponseStatus() != null){
                            end(cmd.getResponseStatus() == Constants.EMV_NOT_ACCEPT ? PaymentState.REFUSED_CARD : PaymentState.ERROR);
                        }else{
                            end(PaymentState.ERROR);
                        }

                        break;

                    case SUCCESS:
                        //EMV Mod By Shankar
                        //doAuthorization();
                        getSecuredTag();
                        break;
                }
            }
        });
    }

    @Override
    public void onAuthorizationDone() {
        finalizeTransaction();
    }

    private void finalizeTransaction() {
        LogManager.debug(this.getClass().getSimpleName(), "finalizeTransaction");
        Log.d("AMAR>>", Arrays.toString(context.getAuthorizationResponse()));
        //final TransactionFinalizationCommand cmd = null;
/*		if(MyConst.getArpc() != null) {
            if (!MyConst.getArpc().contains("null") && MyConst.getArpc().length() > 15) {
				TransactionFinalizationCommand cmd = new TransactionFinalizationCommand(true);
			} else {
				TransactionFinalizationCommand cmd = new TransactionFinalizationCommand(false);
			}
		}*/
        final TransactionFinalizationCommand cmd = new TransactionFinalizationCommand(true);
        cmd.setAuthResponse(context.getAuthorizationResponse());
        cmd.execute(new ITaskMonitor() {
            @Override
            public void handleEvent(TaskEvent event, Object... params) {
                switch (event) {
                    case FAILED:
                        //getPlainTagReturn();

                        //Breaked previously
                        //unbreaked by SHANKY
                        String r = "" + context.getActivatedReader();
                        if (r.equalsIgnoreCase("17") && !MyConst.getArpc().equalsIgnoreCase("FAIL")) {
                            if (!MyConst.getTag5513().substring(2, 6).equalsIgnoreCase("0008") && !MyConst.getTag5513().substring(2, 6).equalsIgnoreCase("0007")) {
                                end(PaymentState.REVERSAL);
                            } else {
                                end(PaymentState.ERROR);
                            }
                        } else {
                            end(PaymentState.ERROR);
                        }
                        ////////////



                        //end(PaymentState.ERROR);

                        break;

                    case SUCCESS:
                        //getPlainTagReturn();
                        context.setTransactionData(cmd.getResponseData());

                        String reader = "" + context.getActivatedReader();
                        if (MyConst.getArpc() != null && reader.equalsIgnoreCase("17") && !MyConst.getArpc().equalsIgnoreCase("FAIL")) {
                            if (MyConst.getTag5513().substring(2, 6).equalsIgnoreCase("0008")) {
                                end(PaymentState.REVERSAL);
                            } else
                           if (MyConst.getTag5513().substring(2, 6).equalsIgnoreCase("0007")) {
                                if (MyConst.getTxnattempt() == 2) {
                                    //end(cmd.getResponseStatus() == 0x07 ? PaymentState.APPROVED : PaymentState.DECLINED);
                                    end(context.getPaymentStatus() == PaymentState.APPROVED ? PaymentState.APPROVED : PaymentState.DECLINED);
                                } else {
                                    //end(cmd.getResponseStatus() == 0x07 ? PaymentState.APPROVED : PaymentState.SWIPE_CARD);
                                    end(context.getPaymentStatus() == PaymentState.APPROVED ? PaymentState.APPROVED : PaymentState.DECLINED);
                                }
                            } else {
                                end(PaymentState.REVERSAL);
                            }
                        } else {
                            if (MyConst.getTxnattempt() == 2) {
                                //end(cmd.getResponseStatus() == 0x07 ? PaymentState.APPROVED : PaymentState.DECLINED);
                                end(context.getPaymentStatus() == PaymentState.APPROVED ? PaymentState.APPROVED : PaymentState.DECLINED);
                            } else {
                                //end(cmd.getResponseStatus() == 0x07 ? PaymentState.APPROVED : PaymentState.SWIPE_CARD);
                                if (context.getPaymentStatus() == PaymentState.DECLINED_BY_9F27) {
                                    end(PaymentState.DECLINED_BY_9F27);
                                }else if(context.getPaymentStatus() == PaymentState.CONN_TIME_OUT){
                                    end(PaymentState.CONN_TIME_OUT);
                                } else {
                                    end(context.getPaymentStatus() == PaymentState.APPROVED ? PaymentState.APPROVED : PaymentState.DECLINED);
                                }
                            }

                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void displayResult(final ITaskMonitor monitor) {
        LogManager.debug(this.getClass().getSimpleName(), "displayResult in ICC Payment service");

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
            Log.d("Pay state remove card", "" + context.getPaymentStatus());
            super.displayResult(logMonitor);
            return;
        }

        new DisplayMessageCommand(context.getString("LBL_remove_card")).execute(new ITaskMonitor() {
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
                        ICCPaymentService.super.displayResult(logMonitor);
                    }
                });
            }
        });

        if (MyConst.isServiceCalled) {
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
                        break;
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
                                break;
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

    private void getSecuredTag() {
        LogManager.debug(this.getClass().getSimpleName(), "getSecuredTag");

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(context.mBroadcastStringAction);
        broadcastIntent.putExtra("msg", "Transaction is in Progress");
        if(uCubeCallBacks!=null){
            uCubeCallBacks.progressCallback("Transaction is in Progress");
        }
        ct.sendBroadcast(broadcastIntent);

        //shankarsavant=serialno
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
                        context.setDevice_sr_no(deviceInfos.getPartNumber());
                        MyConst.setDevice_sr_no(deviceInfos.getPartNumber());

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
                                        Log.d("Shankar Res 1:", "value ");
                                        //getPlainTag();
                                        //onlinePIN();
                                        //doAuthorization();
                                        //shankar111

                                        if (MyConst.getFallback_tag5025().length() < 48) {
                                            end(PaymentState.TRAck_2_error);
                                        } else {
                                            getPlainTag();
                                        }
                                        break;
                                }
                            }
                        });
                        break;
                }
            }
        });
    }

    private void getPlainTag() {
        LogManager.debug(this.getClass().getSimpleName(), "getPlainTag");

        final GetPlainTagCommand cmd = new GetPlainTagCommand(context.getRequestedPlainTagList());

        cmd.execute(new ITaskMonitor() {
            @Override
            public void handleEvent(TaskEvent event, Object... params) {
                Log.d("Eventxx", event.toString());
                Log.d("Statusxx", cmd.getResponseStatus() + "");
                if (cmd.getResponseStatus() != null && cmd.getResponseStatus() == 1) {
                    event = TaskEvent.SUCCESS;
                }
                switch (event) {
                    case FAILED:
                        end(PaymentState.ERROR);
                        break;
                    case SUCCESS:
                        context.setPlainTagTLV(cmd.getResult());
                        //checkMSRAction();
                        doAuthorization();
                        break;
                }
            }
        });


        //shankarsavant=serialno
/*	GetInfosCommand cmd1 = new GetInfosCommand(
                Constants.tag5f);

		cmd1.execute(new ITaskMonitor() {
			@Override
			public void handleEvent(TaskEvent event, Object... params) {
				switch (event) {
					case FAILED:
						Log.d("deviceInfo","Fail");
						break;

					case SUCCESS:
						Log.d("%F", ""+params[0]).getResponseData());
						DeviceInfos deviceInfos = new DeviceInfos(((GetInfosCommand) params[0]).getResponseData());
						Log.d("deviceInfo","Success>>"+deviceInfos.getPartNumber());
						break;
				}
			}
	});*/

        //////

    }

    private void getPlainTagReturn() {
        LogManager.debug(this.getClass().getSimpleName(), "getPlainTagReturn");

        final GetPlainTagCommand cmd = new GetPlainTagCommand(context.getRequestedPlainTagList());

        cmd.execute(new ITaskMonitor() {
            @Override
            public void handleEvent(TaskEvent event, Object... params) {
                Log.d("Eventxx", event.toString());
                Log.d("Statusxx", cmd.getResponseStatus() + "");
                if (cmd.getResponseStatus() != null && cmd.getResponseStatus() == 1) {
                    event = TaskEvent.SUCCESS;
                }
                switch (event) {
                    case FAILED:
                        end(PaymentState.ERROR);
                        break;
                    case SUCCESS:
                        context.setPlainTagTLV(cmd.getResult());
                        break;
                }
            }
        });

    }

}
