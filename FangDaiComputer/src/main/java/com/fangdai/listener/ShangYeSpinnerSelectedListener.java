package com.fangdai.listener;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;

import com.fangdai.util.Utils;

import java.math.BigDecimal;


public class ShangYeSpinnerSelectedListener implements OnItemSelectedListener {

	EditText et;
	double lilv;

	public ShangYeSpinnerSelectedListener(EditText et, double lilv) {
		this.et = et;
		this.lilv = lilv;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		et.setText(new BigDecimal(Utils.getLiLv(lilv, position)).setScale(2,
				BigDecimal.ROUND_HALF_DOWN).toString());
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

}