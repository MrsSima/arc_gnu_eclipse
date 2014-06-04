/*******************************************************************************
 * Copyright (c) 2000, 2004 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.embeddedcdt;

public interface LaunchConfigurationConstants {

	static final String LAUNCH_ID = "com.arc.embeddedcdt"; //$NON-NLS-1$
	static final String ATTR_DEBUGGER_CONFIG = LAUNCH_ID + ".debugger_config";
	static final String ATTR_DEBUGGER_INIT_TARGET = LAUNCH_ID + ".debugger_init_target";
	static final String ATTR_DEBUGGER_APP_CONSOLE =LAUNCH_ID + ".debugger_app_console";
	static final boolean ATTR_DEBUGGER_APP_CONSOLE_DEFAULT = false;
	//String ATTR_DEBUGGER_COMMANDS_INIT =  LAUNCH_ID + ".debugger_init_commands"; //$NON-NLS-1$
	String ATTR_DEBUGGER_COMMANDS_INIT =  LAUNCH_ID + ".debugger_init_commands"; //$NON-NLS-1$
	String ATTR_DEBUGGER_COMMANDS_RUN =  LAUNCH_ID + ".debugger_run_commands"; //$NON-NLS-1$
	String ATTR_DEBUGGER_COMMANDS_LAUNCH =  LAUNCH_ID + ".debugger_lauch_commands"; //$NON-NLS-1$
	String ATTR_DEBUGGER_EXTERNAL_TOOLS =  LAUNCH_ID + ".debugger_external_tools"; //$NON-NLS-1$
	String ATTR_DEBUGGER_COM_OPENOCD_PORT =  LAUNCH_ID + ".debugger_com_oepnocd_port"; //$NON-NLS-1$
	String ATTR_DEBUGGER_COM_ASHLING_PORT =  LAUNCH_ID + ".debugger_com_ashling_port"; //$NON-NLS-1$
	String ATTR_DEBUGGER_EXTERNAL_TOOLS_OPENOCD_PATH =  LAUNCH_ID + ".debugger_external_tools_oepnocd_path"; //$NON-NLS-1$
	String ATTR_DEBUGGER_EXTERNAL_TOOLS_ASHLING_PATH =  LAUNCH_ID + ".debugger_external_tools_ashling_path"; //$NON-NLS-1$
	String ATTR_DEBUGGER_EXTERNAL_TOOLS_NSIM_PATH =  LAUNCH_ID + ".debugger_external_tools_nsim_path"; //$NON-NLS-1$
	String ATTR_DEBUGGER_EXTERNAL_OPENOCD_DEFAULT = LAUNCH_ID + ".debugger_external_openocd_default";
	String ATTR_DEBUGGER_EXTERNAL_ASHLING_DEFAULT = LAUNCH_ID + ".debugger_external_ashling_default";
	String ATTR_DEBUGGER_EXTERNAL_NSIM_DEFAULT = LAUNCH_ID + ".debugger_external_nsim_default";
	String ATTR_DEBUGGER_TERMINAL_DEFAULT =LAUNCH_ID + ".debugger_putty_default";
	String ATTR_DEBUGGER_GDB_ADDRESS =  LAUNCH_ID + ".debugger_gdb_address"; //$NON-NLS-1$
	String ATTR_NSIM_PROP_FILE =  LAUNCH_ID + ".nsim_prop_file"; //$NON-NLS-1$
	String ATTR_NSIM_TCF_FILE =  LAUNCH_ID + ".nsim_tcf_file"; //$NON-NLS-1$
	
}
