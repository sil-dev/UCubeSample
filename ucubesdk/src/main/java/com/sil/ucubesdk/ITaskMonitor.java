/**
 * Copyright (C) 2011-2016, YouTransactor. All Rights Reserved.
 *
 * Use of this product is contingent on the existence of an executed license
 * agreement between YouTransactor or one of its sublicensee, and your
 * organization, which specifies this software's terms of use. This software
 * is here defined as YouTransactor Intellectual Property for the purposes
 * of determining terms of use as defined within the license agreement.
 */

package com.sil.ucubesdk;

/**
 * @author gbillard on 3/11/16.
 */
public interface ITaskMonitor {

	void handleEvent(TaskEvent event, Object... params);

}
