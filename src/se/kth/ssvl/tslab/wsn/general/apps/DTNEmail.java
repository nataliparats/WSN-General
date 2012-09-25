/*
 *	  This file is part of the Bytewalla Project
 *    More information can be found at "http://www.tslab.ssvl.kth.se/csd/projects/092106/".
 *    
 *    Copyright 2009 Telecommunication Systems Laboratory (TSLab), Royal Institute of Technology, Sweden.
 *    
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *    
 */
package se.kth.ssvl.tslab.wsn.general.apps;

import java.io.UnsupportedEncodingException;

import se.kth.ssvl.tslab.wsn.general.DTNService;
import se.kth.ssvl.tslab.wsn.general.R;
import se.kth.ssvl.tslab.wsn.general.applib.DTNAPIBinder;
import se.kth.ssvl.tslab.wsn.general.applib.DTNAPICode.dtn_api_status_report_code;
import se.kth.ssvl.tslab.wsn.general.applib.DTNAPICode.dtn_bundle_payload_location_t;
import se.kth.ssvl.tslab.wsn.general.applib.DTNAPICode.dtn_bundle_priority_t;
import se.kth.ssvl.tslab.wsn.general.applib.types.DTNBundleID;
import se.kth.ssvl.tslab.wsn.general.applib.types.DTNBundlePayload;
import se.kth.ssvl.tslab.wsn.general.applib.types.DTNBundleSpec;
import se.kth.ssvl.tslab.wsn.general.applib.types.DTNEndpointID;
import se.kth.ssvl.tslab.wsn.general.applib.types.DTNHandle;
import se.kth.ssvl.tslab.wsn.general.servlib.bundling.BundleDaemon;
import se.kth.ssvl.tslab.wsn.general.servlib.naming.EndpointID;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import se.kth.ssvl.tslab.wsn.general.systemlib.util.Logger;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DTNEmail extends Activity {

	/**
	 * Logging TAG for supporting Android logging mechanism
	 */
	private static String TAG = "DTNEmail";

	/**
	 * SendButton reference object
	 */
	private Button SendButton;

	/**
	 * CloseButton reference object
	 */
	private Button closeButton;

	/**
	 * Destination EndpointID reference object
	 */
	private EditText EmailtoEditText;

	/**
	 * Destination EndpointID reference object
	 */
	private EditText SubjectEditText;

	/**
	 * Message Textview reference object
	 */
	private TextView MessageTextView;

	/**
	 * DTNAPIBinder object
	 */
	private DTNAPIBinder dtn_api_binder_;

	// Default DTN send parameters
	/**
	 * Default expiration time in seconds, set to 1 hour
	 */
	private static final int EXPIRATION_TIME = 1 * 60 * 60;

	/**
	 * Set delivery options to don't flag at all
	 */
	private static final int DELIVERY_OPTIONS = 0;

	/**
	 * Set priority to normal sending
	 */
	private static final dtn_bundle_priority_t PRIORITY = dtn_bundle_priority_t.COS_NORMAL;

	/**
	 * The service connection to communicate with DTNService
	 */
	private ServiceConnection conn_;

	/**
	 * The onCreate function overridden from Android Activity. This basically
	 * will associate the display view to dtnsend layout and initialize the
	 * class
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dtnapps_dtnemail);
		init();
	}

	/**
	 * The onDestroy function override form Android Activity. This will free the
	 * DTNAPIBinder resource by unbinding the service
	 */

	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindDTNService();
	}

	/**
	 * Internal Invalid EndpointIDException for this DTNSend Exception. This is
	 * used to pass exception and notify users about invalid input Destination
	 * EID
	 */
	private static class InvalidEndpointIDException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6431328157822474346L;

	}

	/**
	 * Initialization function for this class. This will init userinterface then
	 * bind the DTNService
	 */
	private void init() {
		initUI();
		bindDTNService();

		// if(BundleDaemon.getInstance().local_eid().str().equals("dtn://0023764f5945.bytewalla.com")){
		// DestEIDEditText.setText("dtn://0023764b6795.bytewalla.com/test");
		// }
		// else {
		// DestEIDEditText.setText("dtn://0023764f5945.bytewalla.com/test");
		// }
		MessageTextView.setText(BundleDaemon.getInstance().local_eid().str());
	}

	/**
	 * Initialization for the user interface by referencing with the actual
	 * runtime object and then set appropriate text or events to the runtime
	 * object
	 */
	private void initUI() {
		// DestEIDEditText = (EditText)
		// this.findViewById(R.id.DTNApps_DTNSend_DestEIDEditText);
		EmailtoEditText = (EditText) this
				.findViewById(R.id.DTNApps_DTNEmail_EmailtoEditText);
		SubjectEditText = (EditText) this
				.findViewById(R.id.DTNApps_DTNEmail_SubjectEditText);

		MessageTextView = (TextView) this
				.findViewById(R.id.DTNApps_DTNEmail_MessageTextView);

		SendButton = (Button) this
				.findViewById(R.id.DTNApps_DTNEmail_SendButton);
		SendButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				try {
					// Validate the user input first whether the EID is valid
					// EID
					// checkInputEID();

					// If the validator pass, send the message
					sendMessage();

					new AlertDialog.Builder(DTNEmail.this)
							.setMessage(
									"Sent DTN message to DTN Service successfully ")
							.setPositiveButton("OK", null).show();

				} catch (Exception e) {
					new AlertDialog.Builder(DTNEmail.this)
							.setMessage("Internal error with " + e.getMessage())
							.setPositiveButton("OK", null).show();

				}

			}
		});

		closeButton = (Button) this
				.findViewById(R.id.DTNApps_DTNEmail_CloseButton);
		closeButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				DTNEmail.this.finish();

			}
		});

	}

	/**
	 * Unbind the DTNService to free resource consumed by the binding
	 */
	private void unbindDTNService() {

		unbindService(conn_);
	}

	/**
	 * bind the DTNService to use the API later
	 */
	private void bindDTNService() {

		conn_ = new ServiceConnection() {

			public void onServiceConnected(ComponentName arg0, IBinder ibinder) {
				Logger.getInstance().info(TAG, "DTN Service is bound");
				dtn_api_binder_ = (DTNAPIBinder) ibinder;
			}

			public void onServiceDisconnected(ComponentName arg0) {
				Logger.getInstance().info(TAG, "DTN Service is Unbound");
				dtn_api_binder_ = null;
			}

		};

		Intent i = new Intent(DTNEmail.this, DTNService.class);
		bindService(i, conn_, BIND_AUTO_CREATE);
	}

	/**
	 * major function for send message by calling dtnsend API
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws DTNOpenFailException
	 * @throws DTNAPIFailException
	 */
	private void sendMessage() throws UnsupportedEncodingException,
			DTNOpenFailException, DTNAPIFailException {
		// Getting values from user interface
		String mailto = EmailtoEditText.getText().toString();
		String subject = SubjectEditText.getText().toString();

		String message = MessageTextView.getText().toString();
		message = mailto + "&" + subject + "&" + message;

		byte[] message_byte_array = message.getBytes("US-ASCII");

		// String dest_eid = DestEIDEditText.getText().toString();
		String dest_eid = "dtn://city.bytewalla.com/mail";

		// Setting DTNBundle Payload according to the values
		DTNBundlePayload dtn_payload = new DTNBundlePayload(
				dtn_bundle_payload_location_t.DTN_PAYLOAD_MEM);
		dtn_payload.set_buf(message_byte_array);

		// DTNBundlePayload dtn_payload = new
		// DTNBundlePayload(dtn_bundle_payload_location_t.DTN_PAYLOAD_FILE);
		// dtn_payload.set_file(new File("/sdcard/test3MB.zip"));
		// dtn_payload.set_file(new File("/sdcard/test.htm"));

		// Start the DTN Communication
		DTNHandle dtn_handle = new DTNHandle();
		dtn_api_status_report_code open_status = dtn_api_binder_
				.dtn_open(dtn_handle);
		if (open_status != dtn_api_status_report_code.DTN_SUCCESS)
			throw new DTNOpenFailException();
		try {
			DTNBundleSpec spec = new DTNBundleSpec();

			// set destination from the user input
			spec.set_dest(new DTNEndpointID(dest_eid));

			// set the source EID from the bundle Daemon
			spec.set_source(new DTNEndpointID(BundleDaemon.getInstance()
					.local_eid().toString()));

			// Set expiration in seconds, default to 1 hour
			spec.set_expiration(EXPIRATION_TIME);
			// no option processing for now
			spec.set_dopts(DELIVERY_OPTIONS);
			// Set prority
			spec.set_priority(PRIORITY);

			// Data structure to get result from the IBinder
			DTNBundleID dtn_bundle_id = new DTNBundleID();

			dtn_api_status_report_code api_send_result = dtn_api_binder_
					.dtn_send(dtn_handle, spec, dtn_payload, dtn_bundle_id);

			// If the API fail to execute throw the exception so user interface
			// can catch and notify users
			if (api_send_result != dtn_api_status_report_code.DTN_SUCCESS) {
				throw new DTNAPIFailException();
			}

		} finally {
			dtn_api_binder_.dtn_close(dtn_handle);
		}

	}

	/**
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * 
	 * <!-- Main configuration file for Android DTN Project --> <!-- Its XML
	 * Schema is dtn.config.xsd in the same folder --> <tns:DTNConfiguration
	 * xmlns:tns="http://www.bytewalla.tslab.ssvl.kth.se/DTNConf"
	 * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation=
	 * "http://www.bytewalla.tslab.ssvl.kth.se/DTNConf dtn.config.xsd">
	 * 
	 * <!-- Storage Section --> <!-- Setup your storage here --> <!-- type can
	 * be phone or sdcard --> <!-- quota is the space you will allow DTN to
	 * store bundles in the storage type you set, the unit is Megabytes -->
	 * <tns:StorageSetting type="sdcard" quota="1" storage_path="dtn" />
	 * 
	 * 
	 * <!-- Interfaces Section --> <tns:InterfacesSetting> <!-- Setup your
	 * listening interfaces here --> <tns:Interface id="tcp0"
	 * conv_layer_type="tcp" local_port="4556" /> </tns:InterfacesSetting>
	 * 
	 * 
	 * <!-- Links Section --> <tns:LinksSetting> <!-- Setup your links here -->
	 * </tns:LinksSetting>
	 * 
	 * 
	 * <!-- Routes Section --> <tns:RoutesSetting router_type="prophet"
	 * P_encounter="0.5" P_encounter_first="0.25" delta="0.01" alpha="0.5"
	 * beta="0.9" K="1" queuing="Fifo"> <!-- Setup your routes here -->
	 * </tns:RoutesSetting>
	 * 
	 * <tns:DiscoveriesSetting> <!-- Setup your Discovery here -->
	 * <tns:Discovery id="ipdisc0" address_family="ip"
	 * port="9556"></tns:Discovery> <tns:Announce interface_id="tcp0"
	 * discovery_id="ipdisc0" conv_layer_type="tcp"></tns:Announce>
	 * </tns:DiscoveriesSetting>
	 * 
	 * </tns:DTNConfiguration>
	 * 
	 * 
	 * 
	 */

}