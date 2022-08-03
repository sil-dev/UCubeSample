/**
 * Copyright (C) 2011-2016, YouTransactor. All Rights Reserved.
 * <p>
 * Use of this product is contingent on the existence of an executed license
 * agreement between YouTransactor or one of its sublicensee, and your
 * organization, which specifies this software's terms of use. This software
 * is here defined as YouTransactor Intellectual Property for the purposes
 * of determining terms of use as defined within the license agreement.
 */
package com.sil.ucubesdk.rpc;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.sil.ucubesdk.BluetoothConnexionManager;
import com.sil.ucubesdk.LogManager;
import com.sil.ucubesdk.Tools;
import com.sil.ucubesdk.rpc.command.EnterSecureSessionCommand;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.sil.ucubesdk.BluetoothConnexionManager.*;

/**
 * @author gbillard on 3/11/16.
 */
public class RPCManager {

    public static String lg = "";
    private IConnexionManager connexionManager;
    private OutputStream out;
    private boolean secureSession = false;
    private byte sequenceNumber = 0;
    private Map<Short, IRPCMessageHandler> messageHandlerByCommandId = new HashMap<>();
    //private ConcurrentHashMap<Short, IRPCMessageHandler> messageHandlerByCommandId = new ConcurrentHashMap<>();

    //static Context context;

/*	private RPCManager(Context ct) {
		this.context = ct;
	}*/

    private RPCManager() {
    }

    public synchronized void sendCommand(final RPCCommand command) {
        if (out == null) {
            if (connexionManager == null || !connexionManager.connect()) {
                LogManager.debug(RPCManager.class.getSimpleName(), "unable to connect to device");

                command.setState(RPCCommandStatus.CONNECT_ERROR);

                return;
            }
        }

        LogManager.debug(RPCManager.class.getSimpleName(), "send command ID: 0x" + Integer.toHexString(command.getCommandId()));
        LogManager.debug(RPCManager.class.getSimpleName(), "send command data: 0x" + Tools.bytesToHex(command.getPayload()));

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // Code here will run in UI thread
                lg = "\nsend command ID: 0x" + Integer.toHexString(command.getCommandId());
                lg += "\nsend command data: 0x" + Tools.bytesToHex(command.getPayload());
                MyConst.appendLog(lg);
            }
        });

        messageHandlerByCommandId.put(command.getCommandId(), command);

        command.setState(RPCCommandStatus.SENDING);

        try {
            /* reset sequence number before entering in secure session */
            if (command.getCommandId() == Constants.ENTER_SECURED_SESSION) {
                sequenceNumber = 0;
            }

            if (secureSession && (command.getCommandId() != Constants.EXIT_SECURED_SESSION)) {
                sendSecureCommand(command);
            } else {
                sendInsecureCommand(command);
            }

            out.flush();

            LogManager.debug(RPCManager.class.getSimpleName(), "sent command ID: 0x" + Integer.toHexString(command.getCommandId()));

            command.setState(RPCCommandStatus.SENT);

        } catch (Exception e) {
            LogManager.debug(RPCManager.class.getName(), "send command error for Id: " + Integer.toHexString(command.getCommandId()), e);

            messageHandlerByCommandId.remove(command.getCommandId());

            command.setState(RPCCommandStatus.FAILED);
        }
    }

    public void start(InputStream in, OutputStream out) {
        this.out = out;

        DAEMON = new RPCDaemon(in);

        new Thread(DAEMON).start();

        if (secureSession) {
            new EnterSecureSessionCommand().execute(null);
        }
    }

    public void stop() {
        if (DAEMON != null) {
            DAEMON.stop();
        }

        out = null;

        for (IRPCMessageHandler handler : messageHandlerByCommandId.values()) {
            handler.processMessage(null);
        }

        messageHandlerByCommandId.clear();
    }

    public boolean isReady() {
        return out != null;
    }

    public boolean connect() {
        return connexionManager != null && connexionManager.connect();
    }

    public void setConnexionManager(IConnexionManager connexionManager) {
        this.connexionManager = connexionManager;
    }

    private void sendSecureCommand(RPCCommand command) throws IOException {
        byte[] payload = command.getPayload();

        if (payload.length == 0) {
            sendInsecureCommand(command);
            return;
        }

        byte[] message = new byte[payload.length + Constants.RPC_SECURED_HEADER_LEN + Constants.RPC_SRED_MAC_SIZE];
        int securedLen = payload.length + Constants.RPC_SECURED_HEADER_CRYPTO_RND_LEN + Constants.RPC_SRED_MAC_SIZE;

        int offset = 0;

        message[offset++] = (byte) (securedLen / 0x100);
        message[offset++] = (byte) (securedLen % 0x100);
        message[offset++] = sequenceNumber++;
        message[offset++] = (byte) (command.getCommandId() / 0x100);
        message[offset++] = (byte) (command.getCommandId() % 0x100);

        message[offset++] = (byte) 0x7F; // TODO should be random

        System.arraycopy(payload, 0, message, offset, payload.length);
        offset += payload.length;

        /* Padding the last 4 bytes with 0x00 (SRED OPT) */
        for (int i = 0; i < Constants.RPC_SRED_MAC_SIZE; i++) {
            message[offset++] = 0x00;
        }

        int crc = computeChecksumCRC16(message);

        out.write(Constants.STX);
        IOUtils.write(message, out);
        out.write((byte) (crc / 0x100));
        out.write((byte) (crc % 0x100));
        out.write(Constants.ETX);

        LogManager.debug(RPCManager.class.getSimpleName(), "sent secure message: 0x" + Tools.bytesToHex(message));
        lg = "\nsent secure message: 0x" + Tools.bytesToHex(message);
        MyConst.appendLog(lg);

	/*	if(Tools.bytesToHex(message).contains("5101")||Tools.bytesToHex(message).contains("5510")||Tools.bytesToHex(message).contains("5042")||Tools.bytesToHex(message).contains("5511")||Tools.bytesToHex(message).contains("5512")||Tools.bytesToHex(message).contains("5025")||Tools.bytesToHex(message).contains("5026")||Tools.bytesToHex(message).contains("5513")||Tools.bytesToHex(message).contains("5102")||Tools.bytesToHex(message).contains("5021")){
			lg="\nsent message: 0x" + Tools.bytesToHex(message);
			MyConst.appendLog(lg);
		}*/

    }

    private void sendInsecureCommand(RPCCommand command) {
        try {
            byte[] payload = command.getPayload();

            final byte[] message = new byte[payload.length + Constants.RPC_HEADER_LEN];
            int offset = 0;

            message[offset++] = (byte) (payload.length / 0x100);
            message[offset++] = (byte) (payload.length % 0x100);
            message[offset++] = sequenceNumber++;
            message[offset++] = (byte) (command.getCommandId() / 0x100);
            message[offset++] = (byte) (command.getCommandId() % 0x100);

            System.arraycopy(payload, 0, message, offset, payload.length);

            int crc = computeChecksumCRC16(message);

            out.write(Constants.STX);
            IOUtils.write(message, out);
            out.write((byte) (crc / 0x100));
            out.write((byte) (crc % 0x100));
            out.write(Constants.ETX);

            LogManager.debug(RPCManager.class.getSimpleName(), "sent message: 0x" + Tools.bytesToHex(message));

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    // Code here will run in UI thread
                    lg = "\nsent message: 0x" + Tools.bytesToHex(message);
                    MyConst.appendLog(lg);
                }
            });

			/*lg="sent message: 0x" + Tools.bytesToHex(message);
			MyConst.appendLog(lg);*/
        } catch (Exception e) {

        }
    }

    private void processMessage(final RPCMessage message) {
        LogManager.debug(RPCManager.class.getSimpleName(), "received command ID: 0x" + Integer.toHexString(message.getCommandId()));
        LogManager.debug(RPCManager.class.getSimpleName(), "received command Status: 0x" + Integer.toHexString(message.getStatus()));
        LogManager.debug(RPCManager.class.getSimpleName(), "received command data: 0x" + Tools.bytesToHex(message.getData()));


        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // Code here will run in UI thread
                //5512,5513,5025
                if (Integer.toHexString(message.getCommandId()).equalsIgnoreCase("5512") || Integer.toHexString(message.getCommandId()).equalsIgnoreCase("5513") || Integer.toHexString(message.getCommandId()).equalsIgnoreCase("5025")) {
                    Log.d("SHANKY", "received command ID: 0x" + Integer.toHexString(message.getCommandId()));
                    Log.d("SHANKY", "received command Status: 0x" + Integer.toHexString(message.getStatus()));
                    Log.d("SHANKY", "received command data: 0x" + Tools.bytesToHex(message.getData()));

                    lg = "\nreceived command ID: 0x" + Integer.toHexString(message.getCommandId());
                    MyConst.appendLog(lg);
                    lg = "\nreceived command Status: 0x" + Integer.toHexString(message.getStatus());
                    MyConst.appendLog(lg);
                    lg = "\nreceived command data: 0x" + Tools.bytesToHex(message.getData());
                    MyConst.appendLog(lg);
                }
            }
        });

        if (message.getStatus() == Constants.SUCCESS_STATUS) {
            if (message.getCommandId() == Constants.ENTER_SECURED_SESSION) {
                /* Set Secure Session State*/
                secureSession = true;
            } else if (message.getCommandId() == Constants.EXIT_SECURED_SESSION) {
                secureSession = false;
            }
        }

        IRPCMessageHandler handler = messageHandlerByCommandId.remove(message.getCommandId());

        if (handler == null && messageHandlerByCommandId.size() == 1) {
            /* command ID is 0x8000 in case of error => unable to recognize command */
            handler = messageHandlerByCommandId.values().iterator().next();
            messageHandlerByCommandId.clear();
        }

        if (handler != null) {
            try {
                handler.processMessage(message);
            } catch (Exception e) {
                LogManager.debug(RPCManager.class.getSimpleName(), "RPC command response process error", e);
            }/*finally {
				Log.d("Bluetooth closed", "By command");
			}*/
        }
    }

    public static RPCManager getInstance() {
        return INSTANCE;
    }

    /**
     * this function to calculate the checksum 16bit
     *
     * @param bytes the payload data
     * @return the calculated CRC16
     */
    private static int computeChecksumCRC16(byte bytes[]) {
        int crc = 0x0000;
        int temp;
        int crc_byte;

        for (int byte_index = 0; byte_index < bytes.length; byte_index++) {

            crc_byte = bytes[byte_index];

            if (crc_byte < 0)
                crc_byte += 256;

            for (int bit_index = 0; bit_index < 8; bit_index++) {

                temp = (crc >> 15) ^ (crc_byte >> 7);

                crc <<= 1;
                crc &= 0xFFFF;

                if (temp > 0) {
                    crc ^= 0x1021;
                    crc &= 0xFFFF;
                }

                crc_byte <<= 1;
                crc_byte &= 0xFF;

            }
        }

        return crc;
    }

    /**
     * The size of the biggest packet is the LOAD command and its 2052
     * 2040(block) + 2(length) + 1(isLastBlock) + 2(command_ID) + 7(RPC Headers ETX,STX,SEQ,CRC,PLL) = 2052
     */
    public static final int MAX_RPC_PACKET_SIZE = 2068;

    private static  RPCManager INSTANCE = new RPCManager();

    private static RPCDaemon DAEMON;

    public static void setRPCManager(){
        INSTANCE = new RPCManager();
    }

    private static class RPCDaemon implements Runnable {

        private InputStream in;
        private boolean interrupted = false;

        private RPCDaemon(InputStream in) {
            this.in = in;
        }

        private void stop() {
            interrupted = true;
        }

        @Override
        public void run() {
            byte[] bufferFromStream = new byte[MAX_RPC_PACKET_SIZE];
            byte[] bufferToDeliver = new byte[MAX_RPC_PACKET_SIZE];

            int nb_bytes;
            boolean isPacketComplete = false;
            boolean waiting_for_first_packet = true;
            short expected_length = 0;
            short bufferToDeliverOffset = 0;

            while (!interrupted) {
                try {
                    nb_bytes = in.read(bufferFromStream);

                    if (nb_bytes > 0) {
                        if (waiting_for_first_packet) {
                            if (bufferFromStream[0] != Constants.STX) {
                                isPacketComplete = false;
                                waiting_for_first_packet = true;

                                continue;
                            }

                            expected_length = Tools.makeShort(bufferFromStream[1], bufferFromStream[2]);
                            expected_length += 1 + 2 + 1 + 2 + 3; /* ETX,CRC,STX,CMDID,LENGTH */

                            if (expected_length > MAX_RPC_PACKET_SIZE) {
                                isPacketComplete = false;
                                waiting_for_first_packet = true;

                            } else {
                                waiting_for_first_packet = false;
                            }

                            bufferToDeliverOffset = 0;
                        }

                        System.arraycopy(bufferFromStream, 0, bufferToDeliver, bufferToDeliverOffset, nb_bytes);
                        bufferToDeliverOffset += nb_bytes;
                    }

                    if (bufferToDeliverOffset == expected_length) {
                        if (bufferToDeliver[expected_length - 1] != Constants.ETX) {
                            continue;
                        }

                        isPacketComplete = true;
                        bufferFromStream = new byte[MAX_RPC_PACKET_SIZE];
                    }

                    if (isPacketComplete) {
                        waiting_for_first_packet = true;
                        isPacketComplete = false;

                        LogManager.debug(RPCManager.class.getSimpleName(), "received: " + Tools.bytesToHex(bufferToDeliver));
			/*			if(Tools.bytesToHex(bufferToDeliver).substring(8).contains("5101")||Tools.bytesToHex(bufferToDeliver).substring(8).contains("5510")||Tools.bytesToHex(bufferToDeliver).substring(8).contains("5042")||Tools.bytesToHex(bufferToDeliver).substring(8).contains("5511")||Tools.bytesToHex(bufferToDeliver).substring(8).contains("5512")||Tools.bytesToHex(bufferToDeliver).substring(8).contains("5025")||Tools.bytesToHex(bufferToDeliver).substring(8).contains("5026")||Tools.bytesToHex(bufferToDeliver).substring(8).contains("5513")||Tools.bytesToHex(bufferToDeliver).substring(8).contains("5102")||Tools.bytesToHex(bufferToDeliver).substring(8).contains("5021")){
							lg+="\nreceived command data: 0x" + Tools.bytesToHex(bufferToDeliver).substring(0,Tools.bytesToHex(bufferToDeliver).indexOf("0000000000000000000000"));
							MyConst.appendLog(lg);
						}*/

                        lg = "\nreceived command data: 0x" + Tools.bytesToHex(bufferToDeliver).substring(0, Tools.bytesToHex(bufferToDeliver).indexOf("0000000000000000000000"));
                        MyConst.appendLog(lg);

                        //TODO change by shankar (Change contains->startsWith)
                        if (Tools.bytesToHex(bufferToDeliver).substring(8).startsWith("5025")) {
                            Log.d("Track2OutPut", Tools.bytesToHex(bufferToDeliver));
                            MyConst.setTag5025(Tools.bytesToHex(bufferToDeliver));
                            Log.d("SHANKY", "5025:" + MyConst.getTag5025());
                            String track_act = MyConst.getTag5025().substring(0, MyConst.getTag5025().indexOf("00000"));
                            MyConst.setFallback_tag5025(track_act);
                            Log.d("SHANKY", "Fallback5025:" + MyConst.getTag5025());
                        }
                        if (Tools.bytesToHex(bufferToDeliver).substring(8).startsWith("5512")) {
                            Log.d("SHANKY", "5512:" + Tools.bytesToHex(bufferToDeliver));
                            MyConst.setTag5512(Tools.bytesToHex(bufferToDeliver));
							/*String cvm_result = MyConst.getTag5512().substring(215, 31);
							Log.d("CVM result", cvm_result);*/
                        }
                        if (Tools.bytesToHex(bufferToDeliver).substring(8).startsWith("5513")) {
                            Log.d("SHANKY", "5513:" + Tools.bytesToHex(bufferToDeliver));
                            MyConst.tag_5513_getCVM = Tools.bytesToHex(bufferToDeliver);
                            MyConst.setTag5513(Tools.bytesToHex(bufferToDeliver));
                            String track_act = MyConst.getTag5513().substring(12);
                            MyConst.setTag5513(track_act);
                            Log.d("SHANKY", "Final 5513:" + MyConst.getTag5513());
                            String df03_tag = Tools.bytesToHex(bufferToDeliver).substring(240, 256);
                            String str_df03 = df03_tag.replace("DF03", "9F5B");
                            MyConst.setTagDF03(str_df03);
                            Log.d("SHANKY", "Tag 9F5B value : " + MyConst.getTagDF03());
                        }

                        RPCMessage response = new RPCMessage(bufferToDeliver, INSTANCE.secureSession);

                        /* Reset the buffer to the next message to avoid overwriting problems */
                        Arrays.fill(bufferToDeliver, (byte) 0);

                        INSTANCE.processMessage(response);
                    }

                } catch (Exception e) {
                    if (!interrupted) {
                        if (e instanceof IOException) {
                            //LogManager.debug(RPCManager.class.getName(), "");
                        } else {
                            //LogManager.debug(RPCManager.class.getName(), "RPC socket read  error", e);
                        }

                        INSTANCE.stop();
                    }
                } /*finally {
                    Log.d("Bluetooth closed", "By command");
                }*/
            }

            DAEMON = null;
        }
    }

}
