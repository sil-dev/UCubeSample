/**
 * Copyright (C) 2011-2016, YouTransactor. All Rights Reserved.
 * <p>
 * Use of this product is contingent on the existence of an executed license
 * agreement between YouTransactor or one of its sublicensee, and your
 * organization, which specifies this software's terms of use. This software
 * is here defined as YouTransactor Intellectual Property for the purposes
 * of determining terms of use as defined within the license agreement.
 */
package com.sil.ucubesdk.rpc.command;

import com.sil.ucubesdk.rpc.Constants;
import com.sil.ucubesdk.rpc.RPCCommand;

/**
 * @author gbillard on 4/11/16.
 */
public class ExitSecureSessionCommand extends RPCCommand {

    public ExitSecureSessionCommand() {
        super(Constants.EXIT_SECURED_SESSION);
    }

}
