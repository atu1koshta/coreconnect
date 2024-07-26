
package com.unemployed.coreconnect.service;

import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SnmpFetcher {
	// OID for IP-MIB::ipNetToMediaPhysAddress, which gives the MAC addresses of
	// connected devices
	private static final OID oidMac = new OID(".1.3.6.1.2.1.4.22.1.2");
	// OID for IP-MIB::ipNetToMediaNetAddress, which gives the IP addresses of
	// connected devices
	private static final OID oidIp = new OID(".1.3.6.1.2.1.4.22.1.3");

	public ResponseEvent<UdpAddress> fetchSnmpData() throws IOException {
		String defaultGateway = NetworkService.getGatewayIp();

		// Create a transport mapping
		TransportMapping<?> transport = new DefaultUdpTransportMapping();
		transport.listen();

		// Create Snmp instance for SNMP operations
		Snmp snmp = new Snmp(transport);

		// Create Target Address object
		CommunityTarget<UdpAddress> comtarget = new CommunityTarget<UdpAddress>();
		comtarget.setCommunity(new OctetString("public"));
		comtarget.setVersion(SnmpConstants.version2c);
		comtarget.setAddress(new UdpAddress(defaultGateway + "/161")); // replace with your router's IP address
		comtarget.setRetries(2);
		comtarget.setTimeout(1000);

		// Create the PDU object
		PDU pdu = new PDU();
		pdu.add(new VariableBinding(oidMac));
		pdu.add(new VariableBinding(oidIp));
		pdu.setType(PDU.GETBULK);
		pdu.setMaxRepetitions(50); // you may need to adjust this value based on the number of connected devices

		// Send the PDU and receive response
		ResponseEvent<UdpAddress> response = snmp.send(pdu, comtarget);

		// Close the connection
		snmp.close();
		
		return response;
	}

	public String getMacAddressForIp(ResponseEvent<UdpAddress> response, String ipAddress) {
		if (response != null) {
			PDU responsePDU = response.getResponse();
			if (responsePDU != null) {
				List<VariableBinding> variableBindings = new ArrayList<>(responsePDU.getVariableBindings());
				for (int i = 0; i < variableBindings.size(); i++) {
					VariableBinding vb = variableBindings.get(i);
					if (vb.getOid().startsWith(oidIp) && vb.getVariable().toString().equals(ipAddress)) {
						VariableBinding macVb = variableBindings.get(i - 1);
						return macVb.getVariable().toString();
					}
				}
			}
		}
		return null;
	}

}
