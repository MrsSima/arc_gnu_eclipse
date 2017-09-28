/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Synopsys, Inc. - ARC GNU Toolchain support
 *******************************************************************************/

package com.arc.embeddedcdt.dsf;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.cdt.core.parser.util.StringUtil;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.gdb.IGdbDebugConstants;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.cdt.dsf.gdb.service.GDBBackend;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.utils.CommandLineUtil;
import org.eclipse.cdt.utils.spawner.ProcessFactory;
import org.eclipse.cdt.utils.spawner.Spawner;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.osgi.framework.BundleContext;

import com.arc.embeddedcdt.ILaunchPreferences;
import com.arc.embeddedcdt.LaunchConfigurationConstants;
import com.arc.embeddedcdt.LaunchPlugin;
import com.arc.embeddedcdt.dsf.utils.ConfigurationReader;

/**
 * DSF service containing GDB server-related logic:
 *                      commands to start a server,
 *                      commands for GDB to connect to the server,
 *                      process label,
 *                      working directory for the server,
 *                      server start and shutdown methods,
 *                      creating console for the server.
 */
public abstract class GdbServerBackend extends GDBBackend {

    private Process process;
    private DsfSession session;
    protected ILaunchConfiguration launchConfiguration;

    public String[] getCommandLineArray() {
        return CommandLineUtil.argumentsToArray(getCommandLine());
    }

    public abstract String getCommandLine();

    public abstract String getProcessLabel();

    public boolean doLaunchProcess() {
        return true;
    }

    /**
     * Get estimated time in milliseconds that is required for server to startup - before it would
     * start to listen on a TCP port. This value is passed to Thread.sleep(). Without this delay
     * there is a risk that GDB will try to connect before server starts to listen so GDB will exit
     * prematurely.
     *
     * This solution is far from perfect because it uses same delay for all cases, so it might not
     * help in some cases where start is even slower - it would be better to at least make this user
     * configurable, or even better to scan server output for a line that notifies that it is
     * listening. Then that could be handled via some asynchronous DSF magic, I presume. However
     * right we don't have time for fancy solutions, and this gets it working at least in most
     * cases.
     *
     * @return Estimated time in milliseconds for server start listening on TCP socket.
     */
    protected int getStartupDelayEstimate() {
        return LaunchPlugin.getDefault().getPreferenceStore().getInt(ILaunchPreferences.SERVER_STARTUP_DELAY);
    }

    public String getCommandToConnect() {
        return String.format("\ntarget remote %s:%s\nload\n", getHostAddress(), getPortToConnect());
    }

    // For OpenOCD on AXS10x this port might be different from the one in the launch configuration
    protected String getPortToConnect() {
        ConfigurationReader cfgReader = new ConfigurationReader(launchConfiguration);
        return cfgReader.getGdbServerPort();
    }

    protected String getHostAddress() {
        return LaunchConfigurationConstants.DEFAULT_GDB_HOST;
    }

    public File getWorkingDirectory() {
        return null;
    }

    public GdbServerBackend(DsfSession session, ILaunchConfiguration launchConfiguration) {
        super(session, launchConfiguration);
        this.session = session;
        this.launchConfiguration = launchConfiguration;
    }

    @Override
    protected BundleContext getBundleContext() {
        return LaunchPlugin.getDefault().getBundle().getBundleContext();
    }

    @Override
    public void initialize(final RequestMonitor requestMonitor) {
        register( new String[]{ GdbServerBackend.class.getName() },
            new Hashtable<String,String>() );
        if (!doLaunchProcess()) {
            requestMonitor.done();
            return;
        }
        try {
            process = launchGDBProcess(getCommandLineArray(), getWorkingDirectory());
        } catch (CoreException e) {
            e.printStackTrace();
        } finally {
            requestMonitor.done();
        }
    }

    /**
     * Add server process to the launch and assign to the process a label and a command line.
     * 
     * @throws CoreException
     */
    public void initializeServerConsole() throws CoreException, InterruptedException {
        if (doLaunchProcess()) {
            IProcess newProcess = addServerProcess(getProcessLabel());
            newProcess.setAttribute(IProcess.ATTR_CMDLINE, getCommandLine());
        }
    }

    /**
     * Add server process to the launch: show it in the debug tree and add its console.
     * 
     * @param label
     *            Label to assign to the process
     * @return process added to the launch
     * @throws CoreException
     */
    private IProcess addServerProcess(String label) throws CoreException, InterruptedException {
        ILaunch launch = (ILaunch) session.getModelAdapter(ILaunch.class);
        IProcess newProcess = null;
        Process serverProc = getProcess();
        if (serverProc != null) {
            newProcess = DebugPlugin.newProcess(launch, serverProc, label);
        }

        if (getStartupDelayEstimate() > 0) {
            Thread.sleep(getStartupDelayEstimate());
        }

        return newProcess;
    }

    private Process launchGDBProcess(String[] cmdArray, File workingDir) throws CoreException {
        try {
            GdbLaunch launch = (GdbLaunch) session.getModelAdapter(ILaunch.class);
            return ProcessFactory.getFactory().exec(cmdArray, launch.getLaunchEnvironment(), workingDir);
        } catch (IOException e) {
            String message = "Error while launching command: " + StringUtil.join(cmdArray, " ");
            throw new CoreException(new Status(IStatus.ERROR, LaunchPlugin.PLUGIN_ID, -1, message, e));
        }
    }

    @Override
    public void shutdown(RequestMonitor requestMonitor) {
        unregister();
        interrupt();
        requestMonitor.done();
    }

    @Override
    public Process getProcess() {
        return process;
    }

    @Override
    public void interrupt() {
        if (process != null && process instanceof Spawner) {
            Spawner gdbSpawner = (Spawner) process;
            if (getSessionType() == SessionType.REMOTE) {
                gdbSpawner.interrupt();
            } else {
                gdbSpawner.interruptCTRLC();
            }
        }
    }

}
