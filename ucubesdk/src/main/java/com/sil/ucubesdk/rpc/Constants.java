/**
 * Copyright (C) 2016, YouTransactor. All Rights Reserved.
 * <p/>
 * Use of this product is contingent on the existence of an executed license
 * agreement between YouTransactor or one of its sublicensee, and your
 * organization, which specifies this software's terms of use. This software
 * is here defined as YouTransactor Intellectual Property for the purposes
 * of determining terms of use as defined within the license agreement.
 */
package com.sil.ucubesdk.rpc;

/**
 * @author gbillard on 3/15/16.
 */
public final class Constants {

	private Constants() {}

	/**
	 * Command IDs
	 */
	//My tags

	public static final int tag9f26 = 0xDF26;
	public static final int tag9f36 = 0xDF36;//5f,30
	public static final int tag30 = 0x30;
	public static final int tag5f = 0x5F;


	//session constants
	public static final String sessionName = "MySession";

	public static final short GET_INFO_COMMAND                      = 0x5001;
	public static final short SET_INFO_FIELD_COMMAND				= 0x5003;
	public static final short CARD_WAIT_INSERTION_COMMAND           = 0x5020;
	public static final short CARD_WAIT_REMOVAL_COMMAND             = 0x5021;
	public static final short GET_SECURED_TAG_VALUE_COMMAND         = 0x5025;
	public static final short GET_PLAIN_TAG_VALUE_COMMAND           = 0x5026;
	public static final short DISPLAY_WITHOUT_KI_COMMAND            = 0x5040;
	public static final short DISPLAY_LISTBOX_WITHOUT_KI_COMMAND    = 0x5042;
	public static final short ENTER_SECURED_SESSION                 = 0x5101;
	public static final short EXIT_SECURED_SESSION                  = 0x5102;
	public static final short INSTALL_FOR_LOAD_KEY_COMMAND          = 0x5130;
	public static final short INSTALL_FOR_LOAD_COMMAND              = 0x5160;
	public static final short LOAD_COMMAND                          = 0x5161;
	public static final short SIMPLIFIED_ONLINE_PIN                 = 0x5171;
	public static final short BUILD_CANDIDATE_LIST                  = 0x5510;
	public static final short TRANSACTION_INIT                      = 0x5511;
	public static final short TRANSACTION_PROCESS                   = 0x5512;
	public static final short TRANSACTION_FINAL                     = 0x5513;
	public static final short START_NFC_TRANSACTION					= 0x5530;
	public static final short COMPLETE_NFC_TRANSACTION				= 0x5531;

	/**
	 * GET INFO TAGS
	 */
	public static final int TAG_ATMEL_SERIAL                   = 0xC1;
	public static final int TAG_TERMINAL_PN                    = 0xC2;
	public static final int TAG_TERMINAL_SN                    = 0xC3;
	public static final int TAG_TERMINAL_STATE                 = 0xC4;
	public static final int TAG_BATTERY_STATE                  = 0xC5;
	public static final int TAG_POWER_OFF_TIMEOUT              = 0xC6;
	public static final int TAG_FIRMWARE_VERSION               = 0xD1;
	public static final int TAG_SVPP_CHECKSUM                  = 0xD2;
	public static final int TAG_PCI_PED_VERSION                = 0xD3;
	public static final int TAG_PCI_PED_CHECKSUM               = 0xD4;
	public static final int TAG_EMV_L1_VERSION                 = 0xD5;
	public static final int TAG_EMV_L1_CHECKSUM                = 0xD6;
	public static final int TAG_EMV_L2_VERSION                 = 0xD7;
	public static final int TAG_EMV_L2_CHECKSUM                = 0xD8;
	public static final int TAG_BOOT_LOADER_VERSION            = 0xD9;
	public static final int TAG_BOOT_LOADER_CHECKSUM           = 0xDA;
	public static final int TAG_NFC_INFOS                      = 0xE8;
	public static final int TAG_EMV_ICC_CONFIG_VERSION         = 0xEA;
	public static final int TAG_EMV_NFC_CONFIG_VERSION         = 0xEB;
	public static final int TAG_MPOS_MODULE_STATE			   = 0xED;
	public static final int TAG_EMV_L1_NFC_VERSION             = 0xA0;
	public static final int TAG_EMV_L2_NFC_VERSION             = 0xA2;
	public static final int TAG_SYSTEM_FAILURE_LOG_RECORD_1    = 0xCB;
	public static final int TAG_SYSTEM_FAILURE_LOG_RECORD_2    = 0xCC;
	/**
	 * Secure session management constants
	 */
	public static final byte RPC_HEADER_LEN                       = 0x05;
	public static final byte RPC_SECURED_HEADER_CRYPTO_RND_LEN    = 0x01;
	public static final byte RPC_SECURED_HEADER_LEN               = RPC_HEADER_LEN + RPC_SECURED_HEADER_CRYPTO_RND_LEN;
	public static final byte RPC_SRED_MAC_SIZE                    = 0x04;

	/**
	 *  PLAIN & SECURED PROPRIETARY TAGS
	 */
	public static final int TAG_MSR_ACTION            = 0xDF60;
	public static final int TAG_MSR_BIN               = 0xDF61;
	public static final int TAG_CARD_DATA_BLOCK       = 0xDF62;

	//EMV Tags
	public static final int TAG_5F34               = 0x5F34;
	public static final int TAG_9F09               = 0x9F09;
	public static final int TAG_9F1A               = 0x9F1A;
	public static final int TAG_9F1E               = 0x9F1E;
	public static final int TAG_9F35               = 0x9F35;
	public static final int TAG_9F37               = 0x9F37;
	public static final int TAG_9F22               = 0x9F22;
	public static final int TAG_8F                 = 0x8F;
	public static final int TAG_9F08               = 0x9F08;
	public static final int TAG_9F5B               = 0x9F5B;

	public static final int TAG_9F36               = 0x9F36;
	public static final int TAG_9F34               = 0x9F34;
	public static final int TAG_9F33               = 0x9F33;
	public static final int TAG_9F10               = 0x9F10;
	public static final int TAG_5F2A               = 0x5F2A;
	public static final int TAG_95                 = 0x95;
	public static final int TAG_91                 = 0x91;
	public static final int TAG_9F27               = 0x9F27;
	public static final int TAG_9A                 = 0x9A;
	public static final int TAG_9F26               = 0x9F26;
	public static final int TAG_9F41               = 0x9F41;
	public static final int TAG_9F02               = 0x9F02;
	public static final int TAG_9F03               = 0x9F03;
	public static final int TAG_9F06               = 0x9F06;
	public static final int TAG_82                 = 0x82;
	public static final int TAG_84                 = 0x84;
	public static final int TAG_9C                 = 0x9C;
	public static final int TAG_9B                 = 0x9B;
	public static final int TAG_DF3F               = 0xDF3F;
	public static final int TAG_DF3E               = 0xDF3E;
	public static final int TAG_DF03               = 0xDF03;
	public static final int service         	   = 0x5F;

	/**
	 * RPC Headers
	 */
	public final static byte STX = 0x02;
	public final static byte ETX = 0x03;

	/**
	 * Size of STX/ETX/CRC
	 */
	public final static byte PROTOCOL_BYTES_SIZE             = 0x04;
	public final static byte RPC_STATUS_SIZE                 = 0x02;

	public static final byte SRED_ENCRIPTION_METHOD          = 0x00;
	public static final byte DYNAMIC_ENCRIPTION_METHOD       = 0x01;
	public static final byte PLAIN_DATA_ENCRIPTION_METHOD    = 0x02;

	/**
	 * Card reader's ID
	 */
	public static final byte ICC_READER = 0x11;
	public static final byte NFC_READER = 0x21;
	public static final byte MS_READER = 0x41;

	/**
	 * online PIN block format
	 */
	public static final byte PIN_BLOCK_ISO9564_FORMAT_0 = 0;
	public static final byte PIN_BLOCK_ISO9564_FORMAT_1 = 1;
	public static final byte PIN_BLOCK_ISO9564_FORMAT_3 = 3;

	/**
	 * MSR action code
	 */
	public final static int MSR_ACTION_NONE				   			= 0x00;
	public final static int MSR_ACTION_ONLINE_PIN_REQUIRED 			= 0x01;
	public final static int MSR_ACTION_CHIP_REQUIRED       			= 0x02;
	public final static int MSR_ACTION_CHIP_REQUIRED_NEW       		= 0x03;
	public final static int MSR_ACTION_DECLINE             			= 0x04;
	public final static int MSR_ACTION_ONLINE_PIN_REQUIRED_OPT 		= 0x06;

	public final static short MSR_USE_CHIP 		= 100;



	/**
	 * Return code
	 */
	public static final short SUCCESS_STATUS      = 0;
	public static final short TIMEOUT_STATUS      = -2;
	public static final short CANCELLED_STATUS    = -32;
	public static final short EMV_NOT_SUPPORT     = -300;
	public static final short EMV_NOT_ACCEPT      = -301;
	public static final short STATUS_CARD_MUTE    = -43;
	public static final short STATUS_E_PAY_CARD_BLOCK = -350;
	public static final short STATUS_CARD_NOT_ACCEPTED = -328;
	/**
	 * PIN input correction key action
	 */
	public static byte ERASE_LAST_DIGIT_PIN_INPUT_CORRECTION_KEY_MODE = 0x01;
	public static byte ERASE_ALL_PIN_INPUT_CORRECTION_KEY_MODE = 0x02;

	/**
	 * authorization response code
	 */
	public static final int APPROVED_RESPONSE_CODE = 0x3030;

	/**
	 *
	 */
	public static final int EMV_APPLICATION_CANDIDATE_BLOCK_SIZE = 64;

	/**
	 * transaction type
	 */
	public static final byte DEBIT                     = 0;
	public static final byte WITHDRAWAL                = 1;
	public static final byte REFUND                    = 2;
	public static final byte PURCHASE_CASHBACK         = 9;
	public static final byte CANCELLATION              = 10;
	public static final byte MANUAL_CASH               = 12;
	public static final byte CREDIT                    = 20;
	public static final byte EMV_CVM_CONDITION_CASH    = 23;
	public static final byte INQUIRY                   = 30;
	public static final byte DECLINED                  = 57;
	/**
	 * POS entry mode
	 */
	public static final byte MSR_POS_ENTRY_MODE = 0x02;
	public static final byte ICC_POS_ENTRY_MODE = 0x09;

}
