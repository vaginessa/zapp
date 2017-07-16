package de.christinecoenen.code.zapp.upnp;


import android.util.Log;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.support.avtransport.callback.GetTransportInfo;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportState;

class GetTransportInfoCommand implements IUpnpCommand {

	private static final String TAG = GetTransportInfoCommand.class.getSimpleName();

	private final UpnpService upnpService;
	private final RendererDevice device;
	private Listener listener;
	private TransportState lastTransportState;

	GetTransportInfoCommand(UpnpService upnpService, RendererDevice device) {
		this.upnpService = upnpService;
		this.device = device;
	}

	public TransportState getTransportState() {
		return lastTransportState;
	}

	@Override
	public void execute() {
		ActionCallback action = new GetTransportInfo(device.getAvTransportService()) {
			@Override
			public void received(ActionInvocation invocation, TransportInfo transportInfo) {
				Log.d(TAG, "received: " + invocation.toString());
				lastTransportState = transportInfo.getCurrentTransportState();
				if (listener != null) {
					listener.onCommandSuccess();
				}
			}

			@Override
			public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
				Log.w(TAG, "Action GetTransportInfo failed on " + device.toString() + ": " + defaultMsg);
				if (listener != null) {
					listener.onCommandFailure(defaultMsg);
				}
			}
		};

		upnpService.getControlPoint().execute(action);
	}

	@Override
	public void setListener(Listener listener) {
		this.listener = listener;
	}
}
