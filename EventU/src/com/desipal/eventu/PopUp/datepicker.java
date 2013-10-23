package com.desipal.eventu.PopUp;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;

public class datepicker extends DialogFragment implements
		DatePickerDialog.OnDateSetListener {
	EditText campoFecha;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceSateate) {

		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		String dia = Integer.toString(day);
		String mes = Integer.toString(month + 1);
		if (day < 10)
			dia = "0" + dia;
		if (month + 1 < 10)
			mes = "0" + mes;
		// set selected date into textview
		campoFecha.setText(new StringBuilder().append(dia).append("/")
				.append(mes).append("/").append(year));
	}

	public void establecerCampo(EditText campoFecha) {
		this.campoFecha = campoFecha;
	}
}
