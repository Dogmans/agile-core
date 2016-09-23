package iot.agile.devicemanager.device;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.freedesktop.dbus.exceptions.DBusException;

import iot.agile.Device;
import iot.agile.Protocol;
import iot.agile.object.DeviceDefinition;

public abstract class AgileBLEDevice extends DeviceImp implements Device {

	private static final String BLE = "ble";

	/**
	 * BLE Protocol imp DBus interface id
	 */
	private static final String BLE_PROTOCOL_ID = "iot.agile.protocol.BLE";
	/**
	 * BLE Protocol imp DBus interface path
	 */
	private static final String BLE_PROTOCOL_PATH = "/iot/agile/protocol/BLE";

	/**
	 * Protocol
	 */
	protected static final String BLUETOOTH_LOW_ENERGY = "iot.agile.protocol.BLE";

	protected static final String GATT_SERVICE = "GATT_SERVICE";
	protected static final String GATT_CHARACTERSTICS = "GATT_CHARACTERSTICS";

	protected static final Map<String, SensorUuid> sensors = new HashMap<String, SensorUuid>();

	public AgileBLEDevice(DeviceDefinition devicedefinition) throws DBusException {
		super(devicedefinition);
		this.protocol = BLUETOOTH_LOW_ENERGY;

		String devicePath = AGILE_DEVICE_BASE_BUS_PATH + BLE + devicedefinition.address.replace(":", "");

		dbusConnect(deviceAgileID, devicePath, this);
		deviceProtocol = (Protocol) connection.getRemoteObject(BLE_PROTOCOL_ID, BLE_PROTOCOL_PATH, Protocol.class);
 		logger.debug("Exposed device {} {}", deviceAgileID, devicePath);
	}

	@Override
	public void Connect() throws DBusException {
		try {
			if (protocol.equals(BLUETOOTH_LOW_ENERGY) && deviceProtocol != null) {
				deviceProtocol.Connect(address);
				deviceStatus = CONNECTED;
				logger.info("Device connect {}", deviceID);
			} else {
				logger.debug("Protocol not supported: {}", protocol);
			}

		} catch (DBusException e) {
			logger.error("Failed to disconnect device {}", deviceID);
			throw new DBusException("Failed to disconnect device:" + deviceID);
		}
	}

	public void Disconnect() throws DBusException {
		try {
			if (protocol.equals(BLUETOOTH_LOW_ENERGY) && deviceProtocol != null) {
				deviceProtocol.Disconnect(address);
				deviceStatus = DISCONNECTED;
				logger.info("Device disconnected {}", deviceID);
			} else {
				logger.debug("Protocol not supported: {}", protocol);
			}
		} catch (DBusException e) {
			logger.error("Failed to disconnect device {}", deviceID);
			throw new DBusException("Failed to disconnect device:" + deviceID);
		}
	}

	/**
	 * Checks if there is another active subscription on the given component of
	 * the device
	 * 
	 * @param componentName
	 * @return
	 */
	protected boolean hasotherActiveSubscription(String componentName) {
		return false;
	}

	/**
	 * Given the profile of the component returns the name of the sensor
	 * 
	 * @param uuid
	 * @return
	 */
	protected String getComponent(Map<String, String> profile) {
		return null;
	}

	/**
	 * Given the profile of the component returns the name of the sensor
	 * 
	 * @param uuid
	 * @return
	 */
	@Override
	protected String getComponentName(Map<String, String> profile) {
		String serviceUUID = profile.get(GATT_SERVICE);
		String charValueUuid = profile.get(GATT_CHARACTERSTICS);
		for (Entry<String, SensorUuid> su : sensors.entrySet()) {
			if (su.getValue().serviceUuid.equals(serviceUUID) && su.getValue().charValueUuid.equals(charValueUuid)) {
				return su.getKey();
			}
		}
		return null;
	}

 }
