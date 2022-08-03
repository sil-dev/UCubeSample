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

/**
 * @author gbillard on 5/12/16.
 */
public enum PaymentState {
	CARD_WAIT_FAILED,
	CANCELLED,
	STARTED,
	CARD_REMOVED,
	CHIP_REQUIRED,
	UNSUPPORTED_CARD,
	REFUSED_CARD,
	ERROR,
	AUTHORIZE,
	DECLINED,
	DECLINED_BY_9F27,
	APPROVED,
	CONN_TIME_OUT,
	APPLICATION_BLOCKED,
	CARD_BLOCKED,
	TRAck_2_error,
	SWIPE_CARD,
	TAG_BATTERY_STATE,
	TAG_POWER_OFF_TIMEOUT,
	REVERSAL;
}
