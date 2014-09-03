package org.onlab.onos.of.controller.driver;

import java.util.List;

import org.jboss.netty.channel.Channel;
import org.onlab.onos.of.controller.OpenFlowSwitch;
import org.projectfloodlight.openflow.protocol.OFDescStatsReply;
import org.projectfloodlight.openflow.protocol.OFErrorMsg;
import org.projectfloodlight.openflow.protocol.OFFeaturesReply;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPortDescStatsReply;
import org.projectfloodlight.openflow.protocol.OFVersion;

/**
 * Represents the driver side of an OpenFlow switch.
 * This interface should never be exposed to consumers.
 *
 */
public interface OpenFlowSwitchDriver extends OpenFlowSwitch {

    /**
     * Sets the OpenFlow agent to be used. This method
     * can only be called once.
     * @param agent the agent to set.
     */
    public void setAgent(OpenFlowAgent agent);

    /**
     * Sets the Role handler object.
     * This method can only be called once.
     * @param roleHandler the roleHandler class
     */
    public void setRoleHandler(RoleHandler roleHandler);

    /**
     * Reasserts this controllers role to the switch.
     * Useful in cases where the switch no longer agrees
     * that this controller has the role it claims.
     */
    public void reassertRole();

    /**
     * Handle the situation where the role request triggers an error.
     * @param error the error to handle.
     * @return true if handled, false if not.
     */
    public boolean handleRoleError(OFErrorMsg error);

    /**
     * If this driver know of Nicira style role messages, these should
     * be handled here.
     * @param m the role message to handle.
     * @throws SwitchStateException if the message received was
     *  not a nicira role or was malformed.
     */
    public void handleNiciraRole(OFMessage m) throws SwitchStateException;

    /**
     * Handle OF 1.x (where x > 0) role messages.
     * @param m the role message to handle
     * @throws SwitchStateException if the message received was
     *  not a nicira role or was malformed.
     */
    public void handleRole(OFMessage m) throws SwitchStateException;

    /**
     * Starts the driver specific handshake process.
     */
    public void startDriverHandshake();

    /**
     * Checks whether the driver specific handshake is complete.
     * @return true is finished, false if not.
     */
    public boolean isDriverHandshakeComplete();

    /**
     * Process a message during the driver specific handshake.
     * @param m the message to process.
     */
    public void processDriverHandshakeMessage(OFMessage m);

    /**
     * Announce to the OpenFlow agent that this switch has connected.
     * @return true if successful, false if duplicate switch.
     */
    public boolean connectSwitch();

    /**
     * Activate this MASTER switch-controller relationship in the OF agent.
     * @return true is successful, false is switch has not
     * connected or is unknown to the system.
     */
    public boolean activateMasterSwitch();

    /**
     * Activate this EQUAL switch-controller relationship in the OF agent.
     * @return true is successful, false is switch has not
     * connected or is unknown to the system.
     */
    public boolean activateEqualSwitch();

    /**
     * Transition this switch-controller relationship to an EQUAL state.
     */
    public void transitionToEqualSwitch();

    /**
     * Transition this switch-controller relationship to an Master state.
     */
    public void transitionToMasterSwitch();

    /**
     * Remove this switch from the openflow agent.
     */
    public void removeConnectedSwitch();

    /**
     * Sets the ports on this switch.
     * @param portDescReply the port set and descriptions
     */
    public void setPortDescReply(OFPortDescStatsReply portDescReply);

    /**
     * Sets the features reply for this switch.
     * @param featuresReply the features to set.
     */
    public void setFeaturesReply(OFFeaturesReply featuresReply);

    /**
     * Sets the switch description.
     * @param desc the descriptions
     */
    public void setSwitchDescription(OFDescStatsReply desc);

    /**
     * Gets the next transaction id to use.
     * @return the xid
     */
    public int getNextTransactionId();


    /**
     * Does this switch support Nicira Role messages.
     * @return true if supports, false otherwise.
     */
    public Boolean supportNxRole();

    /**
     * Sets the OF version for this switch.
     * @param ofV the version to set.
     */
    public void setOFVersion(OFVersion ofV);

    /**
     * Sets this switch has having a full flowtable.
     * @param full true if full, false otherswise.
     */
    public void setTableFull(boolean full);

    /**
     * Sets the associated Netty channel for this switch.
     * @param channel the Netty channel
     */
    public void setChannel(Channel channel);

    /**
     * Sets whether the switch is connected.
     *
     * @param connected whether the switch is connected
     */
    public void setConnected(boolean connected);

    /**
     * Checks if the switch is still connected.
     *
     * @return whether the switch is still connected
     */
    public boolean isConnected();

    /**
     * Writes the message to the output stream
     * in a driver specific manner.
     *
     * @param msg the message to write
     */
    public void write(OFMessage msg);

    /**
     * Writes to the OFMessage list to the output stream
     * in a driver specific manner.
     *
     * @param msgs the messages to be written
     */
    public void write(List<OFMessage> msgs);

}
