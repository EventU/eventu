package com.desipal.eventu.PopUp;

import java.util.Calendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TimePicker;

public class timepicker extends DialogFragment implements
		TimePickerDialog.OnTimeSetListener {
	private EditText campoHora;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity()));
	}

	@Override
	public void onTimeSet(TimePicker arg0, int horaSel, int minSel) {
		String min = Integer.toString(minSel);
		String hor = Integer.toString(horaSel);
		if (horaSel < 10)
			hor = "0" + hor;
		if (minSel < 10)
			min = "0" + min;
		campoHora.setText(new StringBuilder().append(hor).append(":").append(min));
	}

	public void establecerCampo(EditText campoHora) {
		this.campoHora = campoHora;
	}
}